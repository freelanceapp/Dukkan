package com.appzone.dukkan.activities_fragments.activity_home.delegate_home.fragment.fragment_delegate_orders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.appzone.dukkan.R;
import com.appzone.dukkan.activities_fragments.activity_order_details.activity.OrderDetailsActivity;
import com.appzone.dukkan.activities_fragments.activity_home.delegate_home.DelegateHomeActivity;
import com.appzone.dukkan.adapters.Delegate_Order_Adapter;
import com.appzone.dukkan.models.OrdersModel;
import com.appzone.dukkan.models.UserModel;
import com.appzone.dukkan.remote.Api;
import com.appzone.dukkan.singletone.UserSingleTone;
import com.appzone.dukkan.tags.Tags;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Delegate_New_Order extends Fragment {

    private ProgressBar progBar;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private List<OrdersModel.Order> orderList;
    private Delegate_Order_Adapter delegate_order_adapter;
    private LinearLayout ll_no_order;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private DelegateHomeActivity activity;
    private int selectedPos;
    private ListenerUpdateFragmentDelegateCurrentOrder listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_previous_order,container,false);
        initView(view);
        return view;
    }
    public static Fragment_Delegate_New_Order newInstance()
    {
        return new Fragment_Delegate_New_Order();
    }

    private void initView(View view)
    {
        activity = (DelegateHomeActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        orderList = new ArrayList<>();
        ll_no_order = view.findViewById(R.id.ll_no_order);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        delegate_order_adapter = new Delegate_Order_Adapter(getActivity(),orderList,this);
        recView.setAdapter(delegate_order_adapter);
        getOrders();
    }
    private void getOrders()
    {
        Api.getService()
                .getOrders(userModel.getToken(), Tags.order_new)
                .enqueue(new Callback<OrdersModel>() {
                    @Override
                    public void onResponse(Call<OrdersModel> call, Response<OrdersModel> response) {
                        Log.e("code",response.code()+"_");
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            activity.dismissSnackBar();
                            orderList.clear();
                            if (response.body()!=null&&response.body().getData()!=null)
                            {
                                orderList.addAll(response.body().getData());
                                if (orderList.size()>0)
                                {
                                    ll_no_order.setVisibility(View.GONE);
                                    delegate_order_adapter.notifyDataSetChanged();

                                }else
                                    {
                                        ll_no_order.setVisibility(View.VISIBLE);
                                    }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersModel> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            activity.CreateSnackBar(getString(R.string.something));
                            Log.e("Error",t.getMessage());

                        }catch (Exception e){}
                    }
                });
    }


    public void setItem(OrdersModel.Order order, int pos) {
        this.selectedPos = pos;
        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra("order",order);
        intent.putExtra("order_type",Tags.order_new);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data!=null)
        {
            boolean isAccepted= data.getBooleanExtra("accepted",false);
            if (isAccepted)
            {
                orderList.remove(selectedPos);
                delegate_order_adapter.notifyItemRemoved(selectedPos);

                if (orderList.size()>0)
                {
                    ll_no_order.setVisibility(View.GONE);

                }else
                {
                    ll_no_order.setVisibility(View.VISIBLE);
                }
                listener.onUpdated();
            }else
                {
                    orderList.remove(selectedPos);
                    delegate_order_adapter.notifyItemRemoved(selectedPos);
                    if (orderList.size()>0)
                    {
                        ll_no_order.setVisibility(View.GONE);

                    }else
                    {
                        ll_no_order.setVisibility(View.VISIBLE);
                    }
                }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (DelegateHomeActivity) context;
        listener = activity;
    }

    public interface ListenerUpdateFragmentDelegateCurrentOrder
    {
        void onUpdated();
    }
}