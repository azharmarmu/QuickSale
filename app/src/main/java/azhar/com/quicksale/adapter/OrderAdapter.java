package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.CreateOrderActivity;
import azhar.com.quicksale.api.FireBaseAPI;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.model.OrderModel;

/**
 * Created by azharuddin on 24/7/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context context;
    private List<OrderModel> orderList;
    private static List<String> selectedSalesMan = new ArrayList<>();

    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new OrderAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderModel order = orderList.get(position);
        final HashMap<String, Object> orderMap = order.getOrderMap();

        String orderName = "Customer Name : " + orderMap.get("customer_name").toString() + "\n";

        if (orderMap.containsKey("customer_gst"))
            orderName += "Customer GST : " + orderMap.get("customer_gst").toString() + "\n";

        if (orderMap.containsKey("sales_man_name"))
            orderName += "Sales Man : " + orderMap.get("sales_man_name").toString();

        holder.orderName.setText(orderName);
        if (orderMap.get("process").toString().equalsIgnoreCase(Constants.START)) {
            holder.orderViewEdit.setText("Edit");
        } else {
            holder.orderViewEdit.setText("View");
        }

        holder.orderViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, CreateOrderActivity.class);
                editIntent.putExtra("key", order.getKey());
                editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(editIntent);
            }
        });

        holder.orderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireBaseAPI.orderDBRef.child(order.getKey()).removeValue();
                orderList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderName, orderViewEdit, orderDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.name);
            orderViewEdit = itemView.findViewById(R.id.view_edit);
            orderDelete = itemView.findViewById(R.id.delete);
        }
    }

}
