package com.example.android.bitmapfun.ui;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.android.bitmapfun.R;
import com.example.android.bitmapfun.util.*;
import com.google.gson.Gson;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;

import java.net.URLEncoder;
 
public class GetImageActivity extends SpiceBaseActivity {
 
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CAMERA = 2;
    public static final String EXTRA_IMAGE_DATA_STREAM_NAME = "extra_image_data_stream_name";
    public static final String EXTRA_IMAGE_DATA_STREAM_ID = "extra_image_data_stream_id";
    private final static String BASE_URL = "http://1-3.balaurbun2013.appspot.com/UploadServletAPI";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
 
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String selectedImagePath;
	private String mCurrentPhotoPath;
    private ImageView img;
    private long streamId;
    private String streamName;
    private String message;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_image_activity);
 
        Intent i = getIntent();
        streamName = i.getStringExtra(EXTRA_IMAGE_DATA_STREAM_NAME);        
        streamId = i.getLongExtra(EXTRA_IMAGE_DATA_STREAM_ID, 0);
        
        TextView text = (TextView)findViewById(R.id.textView2);
        text.setText(streamName);
        EditText edittext = (EditText)findViewById(R.id.editText1);
        message = edittext.getText().toString();
        
        img = (ImageView)findViewById(R.id.ImageView01);
        img.setDrawingCacheEnabled(true);
        img.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        ((Button) findViewById(R.id.Button01))
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                    }
                });
        
        ((Button) findViewById(R.id.button_upload_photo))
        		.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	uploadImgToStream();
		                Intent i = new Intent(v.getContext(), StreamImageGridActivity.class);
		                Bundle b = new Bundle();
		                b.putLong(StreamImageGridActivity.EXTRA_STREAM_DATA_STREAM_ID, streamId);
		                b.putString(StreamImageGridActivity.EXTRA_STREAM_DATA_STREAM_NAME, streamName);
		                i.putExtra(StreamImageGridActivity.EXTRA_IMAGE_DATA_URL, b);
		                startActivity(i);
		            }
		        });
        
		Button picBtn = (Button) findViewById(R.id.button_camera);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

    }
    
	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		
	}

    
    public void uploadImgToStream() {
		FileInputStream in;
        BufferedInputStream buf;
        byte[] bMapArray;
        String tstJson;
        
        EditText edittext = (EditText)findViewById(R.id.editText1);
        message = edittext.getText().toString();
        if (message == null || message.equals(""))
        	message = "empty";
        String comment = "empty";;
		try {
			comment = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String apiUrl = new StringBuilder(BASE_URL).append("?")
    	        .append("streamId")
    	        .append("=")
    	        .append(String.valueOf(streamId))
    	        .append("&")
    	        .append("streamName")
    	        .append("=")
    	        .append("Dogs")
    	        .append("&")
    	        .append("comments")
    	        .append("=")
    	        .append(comment)
    	        .toString();
    			
    	//Convert bitmap to byte array
    	Bitmap bitmap = img.getDrawingCache(true);
   	    try {
   	    	if(bitmap != null) {
   	    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
   	    		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
   	    		bMapArray = bos.toByteArray();  
   	    		bos.close();
   	    	} else {
       	    	File file = new File(selectedImagePath);
				in = new FileInputStream(file);
	            buf = new BufferedInputStream(in);
	            bMapArray = new byte[buf.available()];
	            buf.read(bMapArray);
       	    	//byte[] bMapArray= Files.toByteArray(file);				       	    		
   	    	}
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

    		Image sendImg = new Image( currentLocation.getLatitude(), currentLocation.getLongitude(), bMapArray );
    		Gson gson = new Gson();
    		tstJson = gson.toJson(sendImg);
       		makeHTTPPOSTRequest(apiUrl, tstJson);
            
            
            
        	//RequestUploadToStream mRequest = new RequestUploadToStream(6330232038490112L, bMapArray);
        	//System.out.println("mRequest : " + mRequest.toString());
        	//mSpiceManager.execute(mRequest, new RequestUploadListener());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
    }
    
    public static void makeHTTPPOSTRequest(String apiUrl, String tstJson) {
        try {
            HttpClient c = new DefaultHttpClient();        
            HttpPost p = new HttpPost(apiUrl);        
            //p.setEntity(new StringEntity(tstJson, ContentType.create("application/json")));
            p.setEntity(new StringEntity(tstJson));
            HttpResponse r = c.execute(p);
            System.out.println("makeHTTPPOSTRequest: " + r.toString());

            // used to process response, if any
            /* 
            BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
               JSONParser j = new JSONParser();
               JSONObject o = (JSONObject)j.parse(line);
               Map response = (Map)o.get("response");
               System.out.println(response.get("somevalue"));
            }
	    */
        }
        // catch(ParseException e) {
        //    System.out.println(e);
        // }
        catch(IOException e) {
            System.out.println(e);
        }                        
    }    

    public String getUriPath(Uri uri) 
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = null;
            if (requestCode == SELECT_PICTURE) {
                if(data != null)
                	selectedImageUri = data.getData();
            }
            if (requestCode == SELECT_CAMERA) {
    			File f = new File(mCurrentPhotoPath);
    		    selectedImageUri = Uri.fromFile(f);
            }
            if (selectedImageUri != null) {
	            selectedImagePath = getUriPath(selectedImageUri);
	            System.out.println("Image Path : " + selectedImagePath + selectedImageUri.getEncodedPath() + selectedImageUri.getHost());
	            img.setImageURI(selectedImageUri);
	            System.out.println("Image Path img: " + String.valueOf(img.getHeight()));
	            img.setAdjustViewBounds(true);
	            int mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size); 
	            img.setMaxHeight(mImageThumbSize);
	            img.setMaxWidth(mImageThumbSize);
            }
        }
    }
 
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    private final class RequestUploadListener implements RequestListener<ConnexusStream.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // Something bad happened
        }

        @Override
        public void onRequestSuccess(final ConnexusStream.List result) {
        	//GetImageActivity.this.showResults(result);
        }
    }

	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}


	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		
		try {
			f = createImageFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}

		startActivityForResult(takePictureIntent, SELECT_CAMERA);
	}
        
	Button.OnClickListener mTakePicOnClickListener = 
			new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		};
    
    /**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}

}