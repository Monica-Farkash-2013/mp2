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

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.BuildConfig;
import com.example.android.bitmapfun.R;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.Utils;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class SearchStreamGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "SearchStreamGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final String SEARCH_CRITERIA = "search_criteria";
    private static final String STREAM_LIST = "stream_list";
    
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private  ArrayList<ConnexusStream> streamList;
    private String searchCriteria;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static SearchStreamGridFragment newInstance( ConnexusStream.List streams, String searchCriteria) {
        final SearchStreamGridFragment f = new SearchStreamGridFragment();

        final Bundle args = new Bundle();
        args.putParcelableArrayList(STREAM_LIST, streams);
        f.setArguments(args);
        args.putString(SEARCH_CRITERIA, searchCriteria);
       
        return f;
    }
   
    /**
     * Empty constructor as per the Fragment documentation
     */
    public SearchStreamGridFragment() {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        Bundle b = getArguments();
        streamList = b.getParcelableArrayList(STREAM_LIST);
        searchCriteria = b.getString(SEARCH_CRITERIA);

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.search_image_grid_fragment, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridViewS);
        mGridView.setAdapter(mAdapter);
        
        final EditText editText = (EditText) v.findViewById(R.id.editText1);
        editText.setText(searchCriteria);

        TextView streamText = (TextView) v.findViewById(R.id.textView1);
        streamText.setText(String.valueOf(streamList.size()) +  " results for: " + searchCriteria + ", click on an image to view stream...");

        Button button = (Button) v.findViewById(R.id.button_searchS);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            	String newSearchCriteria = editText.getText().toString();
            	Intent intent = new Intent(getActivity(), SearchStreamGridActivity.class);
            	intent.putExtra(SearchStreamGridActivity.STREAM_SEARCH, newSearchCriteria);
                startActivity(intent);
           }
        });
        
        button = (Button) v.findViewById(R.id.button_streams);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            	Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivity(intent);
           }
        });
        
        
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                        	
                        	final int numColumns = (int) Math.floor(mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        //(mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                            			(mGridView.getWidth() / 4) - mImageThumbSpacing;
                                //mAdapter.setNumColumns(numColumns);
                                mAdapter.setNumColumns(4);
                                mAdapter.setItemHeight(columnWidth);
                                //ViewGroup.LayoutParams params = new GridView.LayoutParams(mGridView.getWidth(), 4*(columnWidth + mImageThumbSpacing));
                                //mGridView.setLayoutParams(params);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                            } /* MM: change to portrait mode 4 rows*/ 
	                        
	                    }
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(16)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), StreamImageGridActivity.class);
        
       //ImageAdapter ad = (ImageAdapter) parent.getAdapter();
       //int cols = ad.getNumColumns();
       int cols = mAdapter.getNumColumns();
       if (BuildConfig.DEBUG) {
			Log.d("Connexus", Long.toString(id));
			Log.d("Connexus", String.valueOf(position));
			Log.d("Connexus", String.valueOf(cols));
			Log.d("Connexus", Long.toString(streamList.get(position - cols).id));
			Log.d("Connexus", streamList.get(position - cols).name);
		}
       
       Bundle b = new Bundle();
       b.putLong(StreamImageGridActivity.EXTRA_STREAM_DATA_STREAM_ID, streamList.get(position - cols).id);
       b.putString(StreamImageGridActivity.EXTRA_STREAM_DATA_STREAM_NAME, streamList.get(position - cols).name);
       i.putExtra(StreamImageGridActivity.EXTRA_IMAGE_DATA_URL, b);
       //i.putExtra(StreamImageGridActivity.EXTRA_IMAGE_DATA_URL, (position - cols));
               
        if (Utils.hasJellyBean()) {
            // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
            // show plus the thumbnail image in GridView is cropped. so using
            // makeScaleUpAnimation() instead.
            ActivityOptions options =
                    ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
            getActivity().startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            // Size + number of columns for top empty row
            //return Images.imageThumbUrls.length + mNumColumns;
            return streamList.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    //null : Images.imageThumbUrls[position - mNumColumns];
    				null : streamList.get(position - mNumColumns).coverImageUrl;
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            View v;
           if (convertView == null) { // if it's not recycled, instantiate and initialize
           	v = LayoutInflater.from(mContext).inflate(R.layout.connexus_view,null);
           	imageView = (ImageView)v.findViewById(R.id.imageView1);
           	v.setLayoutParams(mImageViewLayoutParams);
           	
               TextView tView = (TextView) v.findViewById(R.id.textView1);
               String strName = streamList.get(position - mNumColumns).name;
               tView.setText(strName);
               tView.setEnabled(true);
               tView.setVisibility(View.VISIBLE);

               TextView checkedTextView = (TextView) v.findViewById(R.id.textView2);
               String strTags = streamList.get(position - mNumColumns).tags;
               checkedTextView.setText(strTags);
               checkedTextView.setEnabled(true);
               checkedTextView.setVisibility(View.VISIBLE);

                //imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                //imageView = (ImageView) convertView;
            	v = convertView;
            	imageView = (ImageView)v.findViewById(R.id.imageView1);
            }

           // Check the height matches our calculated column width
           //if (imageView.getLayoutParams().height != mItemHeight) {
           //    imageView.setLayoutParams(mImageViewLayoutParams);
          // }
           if (v.getLayoutParams().height != mItemHeight) {
               v.setLayoutParams(mImageViewLayoutParams);
           }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs 
            //mImageFetcher.loadImage(Images.imageThumbUrls[position - mNumColumns], imageView);
            mImageFetcher.loadImage(streamList.get(position - mNumColumns).coverImageUrl, imageView);
            //return imageView;
            return v;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
