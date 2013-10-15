package com.example.android.bitmapfun.ui;

import com.example.android.bitmapfun.BuildConfig;
import com.example.android.bitmapfun.util.Utils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class StreamImageGridActivity extends SpiceBaseActivity {
    private static final String TAG = "StreamImageGridActivity";
    private RequestSingleStream mRequest;
    private long streamId;
    private String streamName;
    public static final String EXTRA_IMAGE_DATA_URL = "extra_image_data_url";
    public static final String EXTRA_STREAM_DATA_STREAM_ID = "extra_stream_data_stream_id";
    public static final String EXTRA_STREAM_DATA_STREAM_NAME = "extra_stream_data_stream_name";
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
           Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        Bundle b = i.getBundleExtra(EXTRA_IMAGE_DATA_URL);
        streamName = b.getString(EXTRA_STREAM_DATA_STREAM_NAME);
        streamId =b.getLong(EXTRA_STREAM_DATA_STREAM_ID);
        mRequest = new RequestSingleStream(streamId);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mSpiceManager.execute(mRequest, new ConnexusImageRequestListener());
    }

    private void showResults(ConnexusImage.List images) {
    	if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, StreamImageGridFragment.newInstance(images, streamId, streamName), TAG);
            ft.commit();
        }
        /*
    	Iterator<ConnexusStream> streamsIt = streams.listIterator();
    	while (streamsIt.hasNext()) {
    		if (BuildConfig.DEBUG) {
    			Log.d(TAG, streamsIt.next().coverImageUrl);
			}
    	}
    	*/
    }

    private final class ConnexusImageRequestListener implements RequestListener<ConnexusImage.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // Something bad happened
        }

        @Override
        public void onRequestSuccess(final ConnexusImage.List result) {
        	StreamImageGridActivity.this.showResults(result);
        }
    }

}
