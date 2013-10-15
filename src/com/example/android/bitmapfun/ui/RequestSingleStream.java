package com.example.android.bitmapfun.ui;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class RequestSingleStream extends RetrofitSpiceRequest<ConnexusImage.List, ConnexusApi> {
	private long streamId = 0;

	public RequestSingleStream(long sId) {
        super(ConnexusImage.List.class, ConnexusApi.class);
        streamId = sId;
    }

    @Override
    public ConnexusImage.List loadDataFromNetwork() throws Exception {
		return getService().singleStream(streamId);
    }

}