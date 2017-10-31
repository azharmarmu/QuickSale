package marmu.com.quicksale.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import marmu.com.quicksale.R;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.utils.Constants;
import marmu.com.quicksale.utils.DialogUtils;
import marmu.com.quicksale.utils.Permissions;
import marmu.com.quicksale.sms.SMSReceiver;
import marmu.com.quicksale.sms.SMSListener;
import marmu.com.quicksale.model.AdminModel;

public class LoginActivity extends AppCompatActivity {
    List<AdminModel> adminList = new ArrayList<>();
    EditText etPhone, etOTP;

    String phoneVerificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Permissions.SMS(LoginActivity.this);

        etPhone = (EditText) findViewById(R.id.et_sales_man_phone);
        etOTP = (EditText) findViewById(R.id.et_sales_man_otp);

        //Listening for OTP
        SMSReceiver.bindListener(new SMSListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                etOTP.setText(messageText);
                String code = etOTP.getText().toString();
                etOTP.setSelection(code.length());
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationID, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void changeMapToList() {
        HashMap<String, Object> admin = FireBaseAPI.admin;
        adminList.clear();

        for (String key : admin.keySet()) {
            adminList.add(new AdminModel((String) admin.get(key)));
        }
    }

    public void login(View view) {
        if (Permissions.SMS(LoginActivity.this)) {
            String phone = etPhone.getText().toString();
            String otp = etOTP.getText().toString();

            if (!otp.equals("")) {
                DialogUtils.showProgressDialog(LoginActivity.this, "Loading...");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationID, otp);
                signInWithPhoneAuthCredential(credential);
            } else {
                changeMapToList();
                boolean isUserExists = false;

                for (int i = 0; i < adminList.size(); i++) {
                    AdminModel model = adminList.get(i);
                    if (model.getPhone().equalsIgnoreCase(phone)) {
                        isUserExists = true;
                        break;
                    } else {
                        isUserExists = false;
                    }
                }

                if (phone.length() == 10) {
                    if (isUserExists) {
                        DialogUtils.showProgressDialog(LoginActivity.this, "Loading...");
                        phoneNumberVerification("+91" + phone);
                    } else {
                        Toast.makeText(getApplicationContext(), "Not an validate admin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    etPhone.setError("Invalid number");
                    etPhone.requestFocus();
                }
            }
        }
    }

    public void phoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //Instant verification or Auto-retrieval.
                        Log.d("Success", "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("Failed", "onVerificationFailed", e);

                        DialogUtils.dismissProgressDialog();

                        etPhone.setVisibility(View.VISIBLE);
                        etOTP.setVisibility(View.GONE);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(getApplicationContext(), "Invalid request", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getApplicationContext(),
                                    "The SMS quota for the project has been exceeded",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d("OTP Code", "onCodeSent:" + verificationId);

                        DialogUtils.dismissProgressDialog();

                        etPhone.setVisibility(View.GONE);
                        etOTP.setVisibility(View.VISIBLE);

                        phoneVerificationID = verificationId;
                    }


                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        Constants.AUTH.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener((new OnCompleteListener<AuthResult>() {
                    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithCredential:success");
                            Intent landingActivity = new Intent(LoginActivity.this, LandingActivity.class);
                            landingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(landingActivity);
                            finish();
                        } else {
                            Log.w("Failed", "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),
                                        "Verification code is wrong",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }));
    }
}
