package azhar.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.LandingActivity;
import azhar.com.quicksale.activity.SetTakenActivity;
import azhar.com.quicksale.adapter.TakenAdapter;
import azhar.com.quicksale.api.TakenApi;
import azhar.com.quicksale.listeners.DateListener;
import azhar.com.quicksale.model.TakenModel;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DateUtils;
import azhar.com.quicksale.utils.DialogUtils;

/**
 * Created by azharuddin on 25/7/17.
 */

@SuppressWarnings("unchecked")
@SuppressLint("SimpleDateFormat")
public class Taken implements DateListener {

    private List<TakenModel> takenList = new ArrayList<>();

    private Activity activity;
    private View itemView;
    private TextView datePicker;

    public void evaluate(LandingActivity activity, View itemView) {

        try {

            this.activity = activity;
            this.itemView = itemView;

            initViews();

            new DateUtils().dateListener(this);
            new DateUtils().currentDate(datePicker);
            new DateUtils().datePicker(activity, datePicker);

            changeMapToList(datePicker.getText().toString());
            createTaken();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        datePicker = itemView.findViewById(R.id.et_date_picker);
    }

    private void changeMapToList(String pickedDate) {
        DialogUtils.showProgressDialog(activity, activity.getString(R.string.loading));
        FirebaseFirestore.getInstance()
                .collection(Constants.TAKEN)
                .whereEqualTo(Constants.TAKEN_DATE, pickedDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    LottieAnimationView noTaken = itemView.findViewById(R.id.no_view);
                    RecyclerView takenView = itemView.findViewById(R.id.rv_taken);

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        DialogUtils.dismissProgressDialog();
                        noTaken.playAnimation();
                        if (e != null) {
                            Log.w("Error", "Listen failed.", e);
                            return;
                        }

                        TakenApi.taken.clear();
                        takenList.clear();
                        assert value != null;
                        for (DocumentSnapshot document : value) {
                            Log.d(activity.getString(R.string.result),
                                    document.getId() + " => " + document.getData());
                            HashMap<String, Object> takenDetails = (HashMap<String, Object>) document.getData();
                            takenList.add(new TakenModel(document.getId(), takenDetails));

                            //Update Taken globally
                            TakenApi.taken.put(document.getId(), takenDetails);
                        }
                        if (takenList.size() > 0) {
                            noTaken.setVisibility(View.GONE);
                            takenView.setVisibility(View.VISIBLE);
                            populateTaken();
                        } else {
                            noTaken.setVisibility(View.VISIBLE);
                            takenView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void populateTaken() {
        TakenAdapter adapter = new TakenAdapter(activity, takenList);
        RecyclerView takenView = itemView.findViewById(R.id.rv_taken);
        takenView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        takenView.setLayoutManager(layoutManager);
        takenView.setItemAnimator(new DefaultItemAnimator());
        takenView.setAdapter(adapter);
    }

    private void createTaken() {
        TextView createOrder = itemView.findViewById(R.id.btn_create_taken);
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SetTakenActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public void getDate(String date) {
        changeMapToList(date);
    }
}
