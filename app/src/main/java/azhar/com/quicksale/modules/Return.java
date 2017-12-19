package azhar.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import azhar.com.quicksale.adapter.ReturnAdapter;
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
public class Return implements DateListener {

    private List<TakenModel> returnList = new ArrayList<>();


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
                .whereEqualTo(Constants.TAKEN_PROCESS, Constants.CLOSED)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    TextView noReturn = itemView.findViewById(R.id.no_view);
                    RecyclerView returnView = itemView.findViewById(R.id.rv_return);

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        DialogUtils.dismissProgressDialog();
                        if (e != null) {
                            Log.w("Error", "Listen failed.", e);
                            return;
                        }

                        assert value != null;
                        for (DocumentSnapshot document : value) {
                            Log.d(activity.getString(R.string.result),
                                    document.getId() + " => " + document.getData());
                            HashMap<String, Object> takenDetails = (HashMap<String, Object>) document.getData();
                            returnList.add(new TakenModel(document.getId(), takenDetails));

                        }
                        if (returnList.size() > 0) {
                            noReturn.setVisibility(View.GONE);
                            returnView.setVisibility(View.VISIBLE);
                            populateReturn();
                        } else {
                            noReturn.setVisibility(View.VISIBLE);
                            returnView.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void populateReturn() {

        ReturnAdapter adapter = new ReturnAdapter(activity, returnList);
        RecyclerView returnView = itemView.findViewById(R.id.rv_return);
        returnView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        returnView.setLayoutManager(layoutManager);
        returnView.setItemAnimator(new DefaultItemAnimator());
        returnView.setAdapter(adapter);
    }

    @Override
    public void getDate(String date) throws ParseException {
        changeMapToList(date);
    }
}
