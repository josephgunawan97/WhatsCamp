package com.projects.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.config.UIConfig;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.projects.whatscamp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

	String photoUrl;
	ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.activity_imageviewer);
		setTitle(R.string.app_name);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		photoUrl = this.getIntent().getStringExtra("photoUrl");

        mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(10);
		mPagerAdapter = new ScreenSlidePagerAdapter(this, photoUrl);
		mPager.setAdapter(mPagerAdapter);
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	public class ScreenSlidePagerAdapter extends PagerAdapter {

		private Context mContext;
		String photos;

		public ScreenSlidePagerAdapter(Context context) {
			mContext = context;
		}

		public ScreenSlidePagerAdapter(Context context, String photo) {
			mContext = context;
			this.photos = photo;
		}

		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View layout = inflater.inflate(R.layout.imageviewer_entry, collection, false);

			Log.e("PHOTO_URL", photos);

			final SubsamplingScaleImageView imgViewPhoto = (SubsamplingScaleImageView) layout.findViewById(R.id.imgViewPhoto);
			imgViewPhoto.setDebug(true);
//			imgViewPhoto.setParallelLoadingEnabled(true);
			imgViewPhoto.recycle();

			final TextView tvLoadingImage = (TextView) layout.findViewById(R.id.tvLoadingImage);
			tvLoadingImage.setVisibility(View.VISIBLE);

			final ImageView imgView = new ImageView(mContext);

			Picasso.with(ImageViewerActivity.this).load(photos).memoryPolicy(MemoryPolicy.NO_CACHE )
					.networkPolicy(NetworkPolicy.NO_CACHE).into(imgView, new Callback() {
				@Override
				public void onSuccess() {
					Log.e("Picasso Image", "Success");
					Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
					imgViewPhoto.setImage(ImageSource.bitmap(bitmap));
					tvLoadingImage.setVisibility(View.GONE);
				}

				@Override
				public void onError() {
					Log.e("Picasso Image", "Error loading image");
					imgViewPhoto.setImage(ImageSource.resource(UIConfig.IMAGE_PLACEHOLDER));
					tvLoadingImage.setVisibility(View.GONE);
				}
			});

			collection.addView(layout);
			return layout;
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View) view);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		// Handle action bar actions click
		switch (item.getItemId()) {
			default:
				finish();
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(android.view.Menu menu) {
		// if nav drawer is opened, hide the action items
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onDestroy()  {
		super.onDestroy();
	}

}
