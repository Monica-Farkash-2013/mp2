package com.example.android.bitmapfun.ui;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

public class ConnexusService extends RetrofitGsonSpiceService {

    //private final static String BASE_URL = "http://apt-connexus.appspot.com";
    private final static String BASE_URL = "http://1-3.balaurbun2013.appspot.com";

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(ConnexusApi.class);
    }

}