/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bitmapfun.ui;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.http.entity.StringEntity;

import com.example.android.bitmapfun.BuildConfig;
import com.example.android.bitmapfun.util.Utils;
import com.google.android.gms.common.AccountPicker;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Simple FragmentActivity to hold the main {@link ImageGridFragment} and not much else.
 */
public class SearchStreamGridActivity extends SpiceBaseActivity {
    private static final String TAG = "SearchStreamGridActivity";
    //private RequestAllStreams mRequest;
    private RequestSelectStreams mRequest;
    public static final String STREAM_SEARCH = "search";
    private String searchCriteria;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        searchCriteria = i.getStringExtra(STREAM_SEARCH);
        
        try {
            Date date1 = new SimpleDateFormat("MM/dd/yy").parse("10/15/10");
            Date date2 = new SimpleDateFormat("MM/dd/yy").parse("10/15/13");
    		mRequest = new RequestSelectStreams(date1.getTime(), date2.getTime(), 15);
          } catch (ParseException e) {
            e.printStackTrace();
          }
	}
    
    @Override
    public void onStart() {
        super.onStart();
        //mSpiceManager.execute(mRequest, new ConnexusStreamRequestListener());
        mSpiceManager.execute(mRequest, new ConnexusStreamRequestListener());
    }

    private void showResults(ConnexusStream.List streams) {
    	ConnexusStream.List selectedStreams = new ConnexusStream.List();
    	
    	if ( searchCriteria != null) {
     		//Collections.sort(streams);
     		int i = 0;
     		for (ConnexusStream s : streams ) {
     			if (s.name.contains(searchCriteria)) {
     	    		selectedStreams.add(s);    			
     			} 
     		} 
    	} else {
    		selectedStreams = streams;
    	}
    	
    	
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, SearchStreamGridFragment.newInstance((ConnexusStream.List)selectedStreams), TAG);
            ft.commit();
        }
    }

    private final class ConnexusStreamRequestListener implements RequestListener<ConnexusStream.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // Something bad happened
        }

        @Override
        public void onRequestSuccess(final ConnexusStream.List result) {
        	SearchStreamGridActivity.this.showResults(result);
        }
    }
}
