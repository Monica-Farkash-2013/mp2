package com.example.android.bitmapfun.ui;

import java.util.Collections;

import com.example.android.bitmapfun.BuildConfig;
import com.example.android.bitmapfun.util.Utils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class NearbyImageGridActivity extends SpiceBaseActivity {
    private static final String TAG = "NearbyImageGridActivity";
    private RequestNearbyImages mRequest;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
           Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

    }
    
	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		
        mRequest = new RequestNearbyImages(currentLocation.getLatitude(), currentLocation.getLongitude());
        mSpiceManager.execute(mRequest, new ConnexusImageRequestListener());	
	}
    
    @Override
    public void onStart() {
        super.onStart();
    }

    private void showResults(ConnexusImage.List images) {

    	Collections.sort(images, new ConnexusImage.DistanceComparator());

    	if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, NearbyImageGridFragment.newInstance(images), TAG);
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
        	NearbyImageGridActivity.this.showResults(result);
        }
    }

}
