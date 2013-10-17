package com.example.android.bitmapfun.ui;

import retrofit.http.*;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public interface ConnexusApi {

    @GET("/AllStreamsServletAPI")
    ConnexusStream.List allStreams();
    
    @GET("/AllStreamsServletAPI")
    ConnexusStream.List selectStreams(@EncodedQuery("startDate") long startDate, @EncodedQuery("endDate") long endDate, @EncodedQuery("maxNumberToReturn") int maxNumberToReturn);
    
    @GET("/SingleStreamServletAPI")
   ConnexusImage.List singleStream(@EncodedQuery("streamId") long streamId);
    
    @Multipart
    @POST("/UploadServletAPI")
    //ConnexusStream.List uploadToStream(@EncodedQuery("streamId") long streamId, @EncodedQuery("image") TypedFile image);
    //@POST("/{uploadUrl}")
    //ConnexusStream.List uploadToStream(@EncodedPath("uploadUrl") String uploadUrl, @Part("streamId") long streamId, @Part("image") TypedByteArray image);
    ConnexusStream.List uploadToStream(@EncodedQuery("streamId") long streamId, @Part("image") TypedByteArray image);
}