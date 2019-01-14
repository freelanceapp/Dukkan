package com.appzone.dukkan.activities_fragments.home_activity.client_home.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appzone.dukkan.R;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.Fragment_Client_Profile;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.Fragment_Offers;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.Fragment_Search;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.Fragment_SubCategory;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_Date_Time;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_Delivery_Address;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_Map;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_MyCart;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_Payment_Confirmation;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_cart.Fragment_Review_Purchases;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_home.Fragment_Home;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_home.sub_fragments.Fragment_Charging_Cards;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_home.sub_fragments.Fragment_Food_Department;
import com.appzone.dukkan.activities_fragments.home_activity.client_home.fragment.fragment_my_order.Fragment_Client_Orders;
import com.appzone.dukkan.activities_fragments.product_details.activity.ProductDetailsActivity;
import com.appzone.dukkan.activities_fragments.sign_in_activity.SignInActivity;
import com.appzone.dukkan.language_helper.LanguageHelper;
import com.appzone.dukkan.models.MainCategory;
import com.appzone.dukkan.models.SimilarProductModel;
import com.appzone.dukkan.models.UserModel;
import com.appzone.dukkan.remote.Api;
import com.appzone.dukkan.services.ServiceUpdateLocation;
import com.appzone.dukkan.share.Common;
import com.appzone.dukkan.singletone.OrderItemsSingleTone;
import com.appzone.dukkan.singletone.UserSingleTone;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements Fragment_Date_Time.Date_Time_Listener,Fragment_Map.AddressListener{
    private FragmentManager fragmentManager;
    private String current_lang = "";
    private AHBottomNavigation ah_bottom_nav;
    private Fragment_Home fragment_home;
    private Fragment_Offers fragment_offers;
    private Fragment_SubCategory fragment_subCategory;
    private Fragment_Client_Orders fragment_client_orders;
    private Fragment_Client_Profile fragment_client_profile;
    private Fragment_Search fragment_search;
    ////////////////////////////////////////
    private Fragment_MyCart fragment_myCart;
    private Fragment_Review_Purchases fragment_review_purchases;
    private Fragment_Delivery_Address fragment_delivery_address;
    private Fragment_Payment_Confirmation fragment_payment_confirmation;
    private Fragment_Date_Time fragment_date_time;
    ///////////////////////////////////////
    private Fragment_Food_Department fragment_food_department;
    private Fragment_Charging_Cards fragment_charging_cards;
    ///////////////////////////////////////
    private Fragment_Map fragment_map;
    private final String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 11;
    private final int gps_req = 12;
    private OrderItemsSingleTone orderItemsSingleTone;
    ////////////////////////////////////////
    private MainCategory mainCategory;
    ////////////////////////////////////////
    private Snackbar snackbar;
    private Intent intentService;
    private AlertDialog gpsDialog;
    private LocationManager locationManager;
    private View root;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private String time_type="",delivery_cost="";
    private double order_lat=0.0,order_lng=0.0;
    @Override
    protected void attachBaseContext(Context base) {
        Paper.init(base);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        super.attachBaseContext(LanguageHelper.onAttach(base,current_lang));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }

    private void initView()
    {
        Paper.init(this);
        current_lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        LanguageHelper.setLocality(this,current_lang);

        userSingleTone =  UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        root = findViewById(R.id.root);
        fragmentManager = getSupportFragmentManager();
        orderItemsSingleTone = OrderItemsSingleTone.newInstance();
        ah_bottom_nav = findViewById(R.id.ah_bottom_nav);

        ah_bottom_nav.setDefaultBackgroundColor(ContextCompat.getColor(this,R.color.white));
        ah_bottom_nav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ah_bottom_nav.setTitleTextSizeInSp(13,14);
        ah_bottom_nav.setForceTint(true);
        ah_bottom_nav.setColored(false);
        ah_bottom_nav.setAccentColor(ContextCompat.getColor(this,R.color.colorPrimary));
        ah_bottom_nav.setInactiveColor(ContextCompat.getColor(this,R.color.gray_text));

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.home),R.drawable.bottom_nav_home,R.color.gray_text);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.offers),R.drawable.bottom_nav_offer,R.color.gray_text);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.cart),R.drawable.bottom_nav_cart,R.color.gray_text);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.my_order),R.drawable.bootom_nav_list,R.color.gray_text);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.me),R.drawable.bottom_nav_user,R.color.gray_text);

        ah_bottom_nav.addItem(item1);
        ah_bottom_nav.addItem(item2);
        ah_bottom_nav.addItem(item3);
        ah_bottom_nav.addItem(item4);
        ah_bottom_nav.addItem(item5);

        ah_bottom_nav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                UpdateBottomNavPos(position);

                switch (position)
                {
                    case 0:
                        DisplayFragmentHome();
                        break;
                    case 1:
                        DisplayFragmentOffer();
                        break;
                    case 2:
                        DisplayFragmentMyCart();
                        break;
                    case 3:
                        DisplayFragmentClientOrders();
                        break;
                    case 4:
                        DisplayFragmentClientProfile();

                        /*if (userModel!=null)
                        {
                            DisplayFragmentClientProfile();

                        }else
                            {
                                Common.CreateUserNotSignInAlertDialog(HomeActivity.this,getString(R.string.si_su));
                            }*/
                        break;
                }
                return false;
            }
        });

        DisplayFragmentHome();



    }
    public void UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
    }

    public void UpdateCartNotification(int count)
    {
        if (count > 0 )
        {
            AHNotification.Builder builder = new AHNotification.Builder();
            builder.setBackgroundColor(ContextCompat.getColor(this,R.color.green_text));
            builder.setTextColor(ContextCompat.getColor(this,R.color.white));
            builder.setText(String.valueOf(count));

            ah_bottom_nav.setNotification(builder.build(),2);


        }else
            {
                AHNotification.Builder builder = new AHNotification.Builder();
                builder.setBackgroundColor(ContextCompat.getColor(this,R.color.green_text));
                builder.setTextColor(ContextCompat.getColor(this,R.color.white));
                builder.setText("");
                ah_bottom_nav.setNotification(builder.build(),2);
            }
    }
    private void UpdateBottomNavPos(int pos)
    {
        ah_bottom_nav.setCurrentItem(pos,false);
    }

    public void DisplayFragmentHome()
    {


        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_search!=null&&fragment_search.isAdded())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_map!=null&&fragment_map.isAdded())
        {
            fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_date_time!=null&&fragment_date_time.isAdded())
        {
            fragmentManager.popBackStack("fragment_date_time",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_home==null)
        {
            fragment_home = Fragment_Home.newInstance();
        }

        if (fragment_home.isAdded())
        {
            if (!fragment_home.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_home).commit();
                DisplayFragmentFood_Department();
                UpdateBottomNavPos(0);
            }
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_home,"fragment_home").addToBackStack("fragment_home").commit();
                DisplayFragmentFood_Department();
                UpdateBottomNavPos(0);
            }


            if (fragment_offers!=null&&fragment_offers.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_offers).commit();
            }

        if (fragment_myCart!=null&&fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myCart).commit();
        }

        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }

        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }









    }
    private void DisplayFragmentOffer()
    {
        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_search!=null&&fragment_search.isAdded())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_map!=null&&fragment_map.isAdded())
        {
            fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_date_time!=null&&fragment_date_time.isAdded())
        {
            fragmentManager.popBackStack("fragment_date_time",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_offers==null)
        {
            fragment_offers = Fragment_Offers.newInstance();
        }

        if (fragment_offers.isAdded())
        {
            if (!fragment_offers.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_offers).commit();
                UpdateBottomNavPos(1);
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_offers,"fragment_offers").addToBackStack("fragment_offers").commit();
            UpdateBottomNavPos(1);
        }


        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }
        if (fragment_myCart!=null&&fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myCart).commit();
        }
        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }
        /*if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_subCategory).commit();
        }*/




    }
    public void DisplayFragmentClientOrders()
    {
        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_search!=null&&fragment_search.isAdded())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_map!=null&&fragment_map.isAdded())
        {
            fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_date_time!=null&&fragment_date_time.isAdded())
        {
            fragmentManager.popBackStack("fragment_date_time",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_client_orders==null)
        {
            fragment_client_orders = Fragment_Client_Orders.newInstance();
        }

        if (fragment_client_orders.isAdded())
        {
            if (!fragment_client_orders.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_client_orders).commit();
                UpdateBottomNavPos(3);
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_client_orders,"fragment_client_orders").addToBackStack("fragment_client_orders").commit();
            UpdateBottomNavPos(3);
        }

        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }

        if (fragment_offers!=null&&fragment_offers.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_offers).commit();
        }

        if (fragment_myCart!=null&&fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myCart).commit();
        }
        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }

    }
    public void DisplayFragmentClientProfile()
    {

        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_search!=null&&fragment_search.isAdded())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_map!=null&&fragment_map.isAdded())
        {
            fragmentManager.popBackStack("fragment_map",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_date_time!=null&&fragment_date_time.isAdded())
        {
            fragmentManager.popBackStack("fragment_date_time",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        if (fragment_client_profile==null)
        {
            fragment_client_profile = Fragment_Client_Profile.newInstance();
        }

        if (fragment_client_profile.isAdded())
        {
            if (!fragment_client_profile.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_client_profile).commit();
                UpdateBottomNavPos(4);
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_client_profile,"fragment_client_profile").addToBackStack("fragment_client_profile").commit();
            UpdateBottomNavPos(4);
        }


        if (fragment_offers!=null&&fragment_offers.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_offers).commit();
        }

        if (fragment_myCart!=null&&fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myCart).commit();
        }

        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }
        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }

    }
    public void DisplayFragmentSearch()
    {
        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fragment_search = Fragment_Search.newInstance();


        if (fragment_search.isAdded())
        {
            if (!fragment_search.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_search).commit();
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_search,"fragment_search").addToBackStack("fragment_search").commit();
        }


        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }
        if (fragment_myCart!=null&&fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myCart).commit();
        }
        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }
        if (fragment_offers!=null&&fragment_offers.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_offers).commit();
        }
    }
    ////////////////////////////////////
    private void DisplayFragmentMyCart()
    {
        if (fragment_subCategory!=null&&fragment_subCategory.isAdded())
        {
            fragmentManager.popBackStack("fragment_subCategory",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_search!=null&&fragment_search.isAdded())
        {
            fragmentManager.popBackStack("fragment_search",FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (fragment_myCart==null)
        {
            fragment_myCart = Fragment_MyCart.newInstance();

        }

        if (fragment_myCart.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_myCart).commit();
            DisplayFragmentReview_Purchases();
            UpdateBottomNavPos(2);

        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_myCart,"fragment_myCart").addToBackStack("fragment_myCart").commit();
            DisplayFragmentReview_Purchases();

            UpdateBottomNavPos(2);
        }


        if (fragment_home!=null&&fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home).commit();
        }

        if (fragment_offers!=null&&fragment_offers.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_offers).commit();
        }

        if (fragment_client_orders!=null&&fragment_client_orders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_client_profile!=null&&fragment_client_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_client_profile).commit();
        }




    }
    public void DisplayFragmentReview_Purchases()
    {
        fragment_review_purchases = Fragment_Review_Purchases.newInstance(orderItemsSingleTone.getOrderItemList());

        if (fragment_review_purchases.isAdded())
        {
            if (!fragment_review_purchases.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_review_purchases).commit();
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_my_cart_container,fragment_review_purchases,"fragment_review_purchases").addToBackStack("fragment_review_purchases").commit();
        }



        if (fragment_delivery_address!=null&&fragment_delivery_address.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_delivery_address).commit();
        }

        if (fragment_payment_confirmation!=null&&fragment_payment_confirmation.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_payment_confirmation).commit();
        }

    }
    public void DisplayFragmentDelivery_Address()
    {
        if (fragment_delivery_address==null)
        {
            fragment_delivery_address = Fragment_Delivery_Address.newInstance();
        }

        if (fragment_delivery_address.isAdded())
        {
            if (!fragment_delivery_address.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_delivery_address).commit();
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_my_cart_container,fragment_delivery_address,"fragment_delivery_address").addToBackStack("fragment_delivery_address").commit();
        }


        if (fragment_review_purchases!=null&&fragment_review_purchases.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_review_purchases).commit();
        }
        if (fragment_payment_confirmation!=null&&fragment_payment_confirmation.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_payment_confirmation).commit();
        }
        fragment_myCart.UpdateBasketUI();



    }
    public void DisplayFragmentPayment_Confirmation()
    {
        if (fragment_payment_confirmation==null)
        {
            fragment_payment_confirmation = Fragment_Payment_Confirmation.newInstance();
        }

        if (fragment_payment_confirmation.isAdded())
        {
            if (!fragment_payment_confirmation.isVisible())
            {
                fragmentManager.beginTransaction().show(fragment_payment_confirmation).commit();
            }
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_my_cart_container,fragment_payment_confirmation,"fragment_payment_confirmation").addToBackStack("fragment_payment_confirmation").commit();
        }


        if (fragment_review_purchases!=null&&fragment_review_purchases.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_review_purchases).commit();
        }
        if (fragment_delivery_address!=null&&fragment_delivery_address.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_delivery_address).commit();
        }
        fragment_myCart.UpdateCarUI();


    }
    public void DisplayFragmentFood_Department()
    {
        if (fragment_food_department==null)
        {
            fragment_food_department = Fragment_Food_Department.newInstance();
        }
        if (fragment_food_department.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_food_department).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_home_sub_fragment_container,fragment_food_department,"fragment_food_department").addToBackStack("fragment_food_department").commit();
            }

            if (fragment_charging_cards!=null && fragment_charging_cards.isAdded() && fragment_charging_cards.isVisible())
            {
                fragmentManager.beginTransaction().hide(fragment_charging_cards).commit();
            }
    }
    public void DisplayFragmentCharging_Cards()
    {
        if (fragment_charging_cards==null)
        {
            fragment_charging_cards = Fragment_Charging_Cards.newInstance();
        }
        if (fragment_charging_cards.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_charging_cards).commit();
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_sub_fragment_container,fragment_charging_cards,"fragment_charging_cards").addToBackStack("fragment_charging_cards").commit();
        }

        if (fragment_food_department!=null && fragment_food_department.isAdded() && fragment_food_department.isVisible())
        {
            fragmentManager.beginTransaction().hide(fragment_food_department).commit();
        }
    }

    public void DisplayFragmentMap()
    {
        fragment_map = Fragment_Map.newInstance();

        fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_map,"fragment_map").addToBackStack("fragment_map").commit();

        if (fragment_delivery_address!=null&&fragment_delivery_address.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_delivery_address).commit();
        }

        checkLocationPermission();

    }

    public void DisplayFragmentDateTime()
    {
        fragment_date_time = Fragment_Date_Time.newInstance();

        fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_date_time,"fragment_date_time").addToBackStack("fragment_date_time").commit();

        if (fragment_delivery_address!=null&&fragment_delivery_address.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_delivery_address).commit();
        }


    }
    ///////////////////////////////////////////////////
    public void DisplayFragmentSubCategory(MainCategory.MainCategoryItems mainCategoryItems)
    {
        fragment_subCategory = Fragment_SubCategory.newInstance(mainCategoryItems);


        if (fragment_subCategory.isAdded())
            {
                fragmentManager.beginTransaction().show(fragment_subCategory).commit();
            }else
                {
                    fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_subCategory,"fragment_subCategory").addToBackStack("fragment_subCategory").commit();

                }

                if (fragment_home!=null&&fragment_home.isAdded())
                {
                    fragmentManager.beginTransaction().hide(fragment_home).commit();
                }

    }

    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,fineLoc)== PackageManager.PERMISSION_GRANTED)
        {

            if (isGpsOpen())
            {
                StartLocationUpdate();

            }else
                {
                    CreateGpsDialog();
                }

        }else
            {
                String [] perm = {fineLoc};

                ActivityCompat.requestPermissions(this,perm,loc_req);
            }

    }

    private void StartLocationUpdate()
    {
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

        intentService = new Intent(this, ServiceUpdateLocation.class);
        startService(intentService);
    }
    private void StopLocationUpdate()
    {
        if (intentService!=null)
        {
            stopService(intentService);
        }
    }
    ///////////////////////////////////
    public void setMainCategory (MainCategory mainCategory)
    {
        this.mainCategory = mainCategory;

    }
    public List<MainCategory.Products> getSimilarProducts(String main_category_id,String sub_category_id,String product_id)
    {
        final List<MainCategory.Products> productsList = new ArrayList<>();

        if (mainCategory!=null&&mainCategory.getData().size()>0)
        {
            for (MainCategory.MainCategoryItems mainCategoryItems : mainCategory.getData())
            {
                if (mainCategoryItems!=null)
                {
                    if (mainCategoryItems.getId().equals(main_category_id))
                    {
                        for (MainCategory.SubCategory subCategory : mainCategoryItems.getSub_categories())
                        {
                            if (sub_category_id!=null)
                            {
                                if (subCategory.getId().equals(sub_category_id))
                                {
                                    for (MainCategory.Products products : subCategory.getProducts())
                                    {
                                        if (products!=null)
                                        {
                                            if (!products.getId().equals(product_id))
                                            {
                                                productsList.add(products);
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }

                }
            }
        }else
        {
            Api.getService()
                    .getSimilarProducts(product_id,main_category_id,sub_category_id)
                    .enqueue(new Callback<SimilarProductModel>() {
                        @Override
                        public void onResponse(Call<SimilarProductModel> call, Response<SimilarProductModel> response) {
                            if (response.isSuccessful())
                            {
                                productsList.addAll(response.body().getProducts());
                            }
                        }

                        @Override
                        public void onFailure(Call<SimilarProductModel> call, Throwable t) {
                            try {
                                CreateSnackBar(getString(R.string.something));
                                Log.e("Error",t.getMessage());
                            }catch (Exception e){

                            }
                        }
                    });
        }

        return productsList;
    }

    ///////////////////////////////////

    private void CreateGpsDialog()
    {
        gpsDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();
        View view = LayoutInflater.from(this).inflate(R.layout.gps_layout,null);
        Button btn_allow = view.findViewById(R.id.btn_allow);
        Button btn_deny = view.findViewById(R.id.btn_deny);
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsDialog.dismiss();
                openGps();

            }
        });

        btn_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsDialog.dismiss();
            }
        });

        gpsDialog.setView(view);
        gpsDialog.setCanceledOnTouchOutside(false);
        gpsDialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog_animation;
        gpsDialog.show();
    }

    private void openGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,gps_req);
    }

    private boolean isGpsOpen()
    {
        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    ///////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToLocationUpdate(Location location)
    {
        Log.e("ddd",location.getLatitude()+"_");
        if (fragment_map != null && fragment_map.isAdded())
        {
            if (fragment_map.isMapReady)
            {
               fragment_map.UpdateLocation(location);
            }
        }
    }
    ///////////////////////////////////
    public void NavigateToProductDetailsActivity(MainCategory.Products product, List<MainCategory.Products> similarProducts)
    {


        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product",product);
        intent.putExtra("similar_products", (Serializable) similarProducts);
        startActivityForResult(intent,1122);

        DisplayFragmentHome();
    }
    ////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment:fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == gps_req)
        {
            if (isGpsOpen())
            {
                StartLocationUpdate();
            }else
            {
                CreateGpsDialog();
            }
        }else if (requestCode == 1122 && resultCode == RESULT_OK)
        {
            UpdateCartNotification(orderItemsSingleTone.getItemsCount());
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment:fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == loc_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (isGpsOpen())
                    {
                        StartLocationUpdate();
                    }else
                    {
                        CreateGpsDialog();
                    }
                }else
                    {
                        CreateToast(getString(R.string.gps_perm_denied));
                    }
            }
        }
    }

    public void Back()
    {

        if (fragment_home!=null && fragment_home.isAdded()&& fragment_home.isVisible())
        {
            fragmentManager.popBackStack();
            NavigateToSignInActivity();
        }else if (fragment_map!=null&&fragment_map.isVisible()) {
            fragmentManager.popBackStack("fragment_map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().show(fragment_myCart).commit();
            fragmentManager.beginTransaction().show(fragment_delivery_address).commit();

        }else if (fragment_date_time!=null&&fragment_date_time.isVisible()) {
            fragmentManager.popBackStack("fragment_date_time", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().show(fragment_myCart).commit();
            fragmentManager.beginTransaction().show(fragment_delivery_address).commit();

        }
        else
            {
                DisplayFragmentHome();
            }
    }

    private void NavigateToSignInActivity() {
        Intent intent  = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void CreateToast(String msg)
    {
        Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
    }

    public void CreateSnackBar(String msg)
    {
        snackbar = Common.CreateSnackBar(this,root,msg);
        snackbar.show();
    }

    public void dismissSnackBar()
    {
        if (snackbar!=null)
        {
            snackbar.dismiss();

        }
    }

    public void ChangeLanguage(String lang)
    {
        Log.e("lng",lang);
        Paper.book().write("lang",lang);
        current_lang = lang;
        LanguageHelper.setLocality(this,lang);
        refreshActivity();
    }

    private void refreshActivity()
    {

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onDate_Time_Set(String time_type , String delivery_cost) {

        this.time_type = time_type;
        this.delivery_cost = delivery_cost;

        fragmentManager.popBackStack("fragment_date_time", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().show(fragment_myCart).commit();
        fragment_delivery_address.UpdateDate_Time(time_type,delivery_cost);
        fragmentManager.beginTransaction().show(fragment_delivery_address).commit();


    }
    @Override
    public void onAddressSet(String address, double lat, double lng) {
        this.order_lat = lat;
        this.order_lng = lng;

        fragmentManager.popBackStack("fragment_map", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction().show(fragment_myCart).commit();
        fragment_delivery_address.UpdateAddress(address);
        fragmentManager.beginTransaction().show(fragment_delivery_address).commit();
    }

    @Override
    public void onBackPressed() {
        Back();
    }

    @Override
    protected void onDestroy() {
        if (intentService!=null)
        {
            StopLocationUpdate();
        }

        orderItemsSingleTone.ClearCart();

        super.onDestroy();

    }



}
