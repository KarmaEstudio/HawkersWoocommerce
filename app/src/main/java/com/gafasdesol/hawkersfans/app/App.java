package com.gafasdesol.hawkersfans.app;


import android.content.Context;
import android.support.multidex.MultiDexApplication;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.onesignal.OneSignal;
import com.gafasdesol.hawkersfans.databases.DB_Handler;
import com.gafasdesol.hawkersfans.databases.DB_Manager;
import com.gafasdesol.hawkersfans.models.order_model.OrderDetails;
import com.gafasdesol.hawkersfans.models.drawer_model.Drawer_Items;
import com.gafasdesol.hawkersfans.models.banner_model.BannerDetails;
import com.gafasdesol.hawkersfans.models.category_model.CategoryDetails;
import com.gafasdesol.hawkersfans.models.device_model.AppSettingsDetails;
import com.gafasdesol.hawkersfans.models.user_model.UserDetails;


/**
 * App extending Application, is used to save some Lists and Objects with Application Context.
 **/


public class App extends MultiDexApplication {

    // Application Context
    private static Context context;
    private static DB_Handler db_handler;


    private List<Drawer_Items> drawerHeaderList;
    private Map<Drawer_Items, List<Drawer_Items>> drawerChildList;
    
    
    private AppSettingsDetails appSettings = null;
    private List<BannerDetails> bannersList = new ArrayList<>();
    private List<CategoryDetails> categoriesList = new ArrayList<>();
    
    
    private UserDetails userDetails = new UserDetails();
    private OrderDetails orderDetails = new OrderDetails();


    @Override
    public void onCreate() {
        super.onCreate();

        // set App Context
        context = this.getApplicationContext();

        // initialize DB_Handler and DB_Manager
        db_handler = new DB_Handler();
        DB_Manager.initializeInstance(db_handler);
    
        
        // initialize OneSignal
        OneSignal.startInit(this)
                .filterOtherGCMReceivers(true)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();
    }



    //*********** Returns Application Context ********//

    public static Context getContext() {
        return context;
    }




    public List<Drawer_Items> getDrawerHeaderList() {
        return drawerHeaderList;
    }

    public void setDrawerHeaderList(List<Drawer_Items> drawerHeaderList) {
        this.drawerHeaderList = drawerHeaderList;
    }

    public Map<Drawer_Items, List<Drawer_Items>> getDrawerChildList() {
        return drawerChildList;
    }

    public void setDrawerChildList(Map<Drawer_Items, List<Drawer_Items>> drawerChildList) {
        this.drawerChildList = drawerChildList;
    }
    
    
    
    public AppSettingsDetails getAppSettingsDetails() {
        return appSettings;
    }
    
    public void setAppSettingsDetails(AppSettingsDetails appSettings) {
        this.appSettings = appSettings;
    }
    
    
    public List<BannerDetails> getBannersList() {
        return bannersList;
    }
    
    public void setBannersList(List<BannerDetails> bannersList) {
        this.bannersList = bannersList;
    }
    
    public List<CategoryDetails> getCategoriesList() {
        return categoriesList;
    }
    
    public void setCategoriesList(List<CategoryDetails> categoriesList) {
        this.categoriesList = categoriesList;
    }
    
    
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public UserDetails getUserDetails() {
        return userDetails;
    }
    
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
    
}

