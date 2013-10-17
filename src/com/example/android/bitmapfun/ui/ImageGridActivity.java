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
public class ImageGridActivity extends SpiceBaseActivity {
    private static final String TAG = "ImageGridActivity";
    //private RequestAllStreams mRequest;
    private RequestSelectStreams mRequest;
    static final int REQUEST_ACCOUNT_PICKER = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        //mRequest = new RequestAllStreams();
        try {
            Date date1 = new SimpleDateFormat("MM/dd/yy").parse("10/15/10");
            Date date2 = new SimpleDateFormat("MM/dd/yy").parse("10/15/13");
    		mRequest = new RequestSelectStreams(date1.getTime(), date2.getTime(), 15);
          } catch (ParseException e) {
            e.printStackTrace();
          }

        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},         
        		true, null, null, null, null); startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);    
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == RESULT_OK) {
	    	switch (requestCode) {
	    		case REQUEST_ACCOUNT_PICKER:
	    			if (data != null && data.getExtras() != null) {
	    				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						Toast t = Toast.makeText(ImageGridActivity.this, "Welcome  " + accountName, Toast.LENGTH_LONG);
						t.setGravity(Gravity.CENTER, 0, -300);
			            t.show();
					}
				}
				break;
	    	}
    	}
        mSpiceManager.execute(mRequest, new ConnexusStreamRequestListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        //mSpiceManager.execute(mRequest, new ConnexusStreamRequestListener());
    }

    private void showResults(ConnexusStream.List streams) {
        // Do stuff
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, ImageGridFragment.newInstance(streams), TAG);
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
        	ImageGridActivity.this.showResults(result);
        }
    }
}
