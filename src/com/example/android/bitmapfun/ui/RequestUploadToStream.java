package com.example.android.bitmapfun.ui;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedByteArray;


import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class RequestUploadToStream extends RetrofitSpiceRequest<ConnexusStream.List, ConnexusApi> {
	private long streamId = 0;
	//private final TypedFile image;
	private TypedByteArray tba;


    public RequestUploadToStream(long sId, byte[] bytes) {
        super(ConnexusStream.List.class, ConnexusApi.class);
        streamId = sId;
        tba = new TypedByteArray("image/jpeg", bytes);
        //image = new TypedFile("image/jpeg", img);
    }

    @Override
    public ConnexusStream.List loadDataFromNetwork() throws Exception {
        //return getService().uploadToStream("UploadServletAPI", streamId, tba);
        return getService().uploadToStream(streamId, tba);
    }

}