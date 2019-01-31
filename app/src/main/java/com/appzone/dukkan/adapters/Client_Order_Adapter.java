package com.appzone.dukkan.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzone.dukkan.R;
import com.appzone.dukkan.activities_fragments.activity_home.client_home.fragment.fragment_my_order.Fragment_Client_Current_Order;
import com.appzone.dukkan.activities_fragments.activity_home.client_home.fragment.fragment_my_order.Fragment_Client_New_Order;
import com.appzone.dukkan.activities_fragments.activity_home.client_home.fragment.fragment_my_order.Fragment_Client_Previous_Order;
import com.appzone.dukkan.models.OrdersModel;
import com.appzone.dukkan.share.TimeAgo;
import com.appzone.dukkan.tags.Tags;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class Client_Order_Adapter extends RecyclerView.Adapter<Client_Order_Adapter.MyHolder> {

    private Context context;
    private List<OrdersModel.Order> orderList;
    private Fragment fragment;

    public Client_Order_Adapter(Context context, List<OrdersModel.Order> orderList, Fragment fragment) {
        this.context = context;
        this.orderList = orderList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_current_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        OrdersModel.Order order = orderList.get(position);
        holder.BindData(order);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrdersModel.Order order = orderList.get(holder.getAdapterPosition());
                if (order!=null)
                {
                    if (fragment instanceof Fragment_Client_New_Order)
                    {
                        Fragment_Client_New_Order fragment_client_new_order = (Fragment_Client_New_Order) fragment;
                        fragment_client_new_order.setItemData(order,holder.getAdapterPosition());

                    }else if (fragment instanceof Fragment_Client_Current_Order)
                    {
                        Fragment_Client_Current_Order fragment_client_current_order = (Fragment_Client_Current_Order) fragment;
                        fragment_client_current_order.setItemData(order,holder.getAdapterPosition());

                    }else if (fragment instanceof Fragment_Client_Previous_Order)
                    {
                        Fragment_Client_Previous_Order fragment_client_previous_order = (Fragment_Client_Previous_Order) fragment;
                        fragment_client_previous_order.setItemData(order,holder.getAdapterPosition());


                    }
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_order_number,tv_order_total,tv_order_status,tv_date,tv_created_date;
        private ImageView image_order_state;
        public MyHolder(View itemView) {
            super(itemView);
            tv_order_number = itemView.findViewById(R.id.tv_order_number);
            tv_order_total = itemView.findViewById(R.id.tv_order_total);
            tv_order_status = itemView.findViewById(R.id.tv_order_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_created_date = itemView.findViewById(R.id.tv_created_date);
            tv_created_date.setVisibility(View.GONE);
            image_order_state = itemView.findViewById(R.id.image_order_state);

        }

        public void BindData(OrdersModel.Order order)
        {
            if (fragment instanceof Fragment_Client_New_Order)
            {
                image_order_state.setBackgroundResource(R.drawable.add_bg_gradient);
                image_order_state.setImageResource(R.drawable.clock_white);
                if (order.getStatus()==0)
                {
                    tv_order_status.setText(R.string.not_approved);

                }
            }else if (fragment instanceof Fragment_Client_Current_Order)
            {
                image_order_state.setBackgroundResource(R.drawable.add_bg_gradient);
                image_order_state.setImageResource(R.drawable.clock_white);

                switch (order.getStatus())
                {
                    case Tags.status_delegate_accept_order:
                        tv_order_status.setText(R.string.delegate_accept_order);
                        break;
                    case Tags.status_delegate_collect_order:
                        tv_order_status.setText(R.string.collecting_order);

                        break;
                    case Tags.status_delegate_already_collect_order:
                        tv_order_status.setText(R.string.order_collected);

                        break;
                    case Tags.status_delegate_delivering_order:
                        tv_order_status.setText(R.string.delivering_order);

                        break;

                }

            }else if (fragment instanceof Fragment_Client_Previous_Order)
            {
                image_order_state.setBackgroundResource(R.drawable.circle_fill_primary);
                image_order_state.setImageResource(R.drawable.correct_white);
                tv_order_status.setText(R.string.done2);

            }

            Paper.init(context);
            String lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

            String order_date = order.getCreated_at();
            SimpleDateFormat originalDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",new Locale(lang));
            try {
                Date parse = originalDate.parse(order_date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);
                String d = TimeAgo.getTimeAgo(calendar.getTimeInMillis(),context);
                tv_date.setText(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_order_number.setText("#"+order.getId());
            tv_order_total.setText(order.getTotal()+" "+context.getString(R.string.rsa));


        }
    }
}
