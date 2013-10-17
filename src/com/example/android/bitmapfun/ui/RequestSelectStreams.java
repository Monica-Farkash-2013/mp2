package com.example.android.bitmapfun.ui;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class RequestSelectStreams extends RetrofitSpiceRequest<ConnexusStream.List, ConnexusApi> {
	long sd = 0;
	long ed = 0;
	int no = 0;

	public RequestSelectStreams(long sD, long eD, int nM) {
        super(ConnexusStream.List.class, ConnexusApi.class);
        sd = sD;
        ed = eD;
        no = nM;
    }

    @Override
    public ConnexusStream.List loadDataFromNetwork() throws Exception {
       return getService().selectStreams(sd, ed, no);
    }

}