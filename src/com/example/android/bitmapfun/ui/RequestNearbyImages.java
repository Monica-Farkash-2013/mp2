package com.example.android.bitmapfun.ui;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class RequestNearbyImages extends RetrofitSpiceRequest<ConnexusImage.List, ConnexusApi> {
	private double latitude = 0;
	private double longitude = 0;

	public RequestNearbyImages(double lat, double lng) {
        super(ConnexusImage.List.class, ConnexusApi.class);
        latitude = lat;
        longitude = lng;
    }

    @Override
    public ConnexusImage.List loadDataFromNetwork() throws Exception {
		return getService().nearbyImages(latitude, longitude);
    }

}