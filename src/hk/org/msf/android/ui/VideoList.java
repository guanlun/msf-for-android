package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.data.RSSDatabase;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.data.RSSEntry;
import hk.org.msf.android.utils.ImageFetcher;
import hk.org.msf.android.utils.MySettings;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class VideoList extends Activity implements OnItemClickListener {

	public static VideoList self;
	
	private ViewFlipper videoFlipper;
	private ListView videoList;
	private WebView webView;
	private TextView moreVideos;
	
	public static ArrayList<RSSEntry> videoEntryList;
	
	private ProgressDialog progress;
	
	private RSSDatabase db;
	
	private Hashtable<String, Bitmap> imageHash;
	
	private VideoAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.video_list);
		
		self = this;
		imageHash = new Hashtable<String, Bitmap>();
		db = RSSDatabase.getDatabaseInstance(null);
		
		videoList = (ListView)findViewById(R.id.VideoList);
		videoList.setDivider(new ColorDrawable(Color.rgb(150, 150, 150)));
		videoList.setDividerHeight(1);
		videoFlipper = (ViewFlipper)findViewById(R.id.VideoFlipper);
		
		videoList.setOnItemClickListener(this);
		showLoadingMessage();
		new Thread(new PrepareImage()).start();
	}
	
	/**
	 * When the View is destroyed, clear the bitmap list to avoid possible memory leak
	 */
	@Override
	public void onDestroy() {
        imageHash.clear();
        super.onDestroy();
	}
	
	public void showLoadingMessage() {
		progress = ProgressDialog.show(this, "", 
				self.getResources().getString(R.string.loading), true);
		progress.setCancelable(true);
	}
	
	class PrepareImage implements Runnable {

		@Override
		public void run() {
			videoEntryList = db.getRSSEntryList(RSSDatabaseHelper.VIDEO);
			adapter = new VideoAdapter();
			ArrayList<String> urls = new ArrayList<String>();
			
			waitForDatabaseReady();
			
			for(int i = 0; i < videoEntryList.size(); i++) {
				urls.add(videoEntryList.get(i).image);
			}

			videoHandler.sendEmptyMessage(0);
			ImageFetcher.fetchImage(urls, RSSDatabaseHelper.VIDEO);
		}
	}
	
	/**
	 * if the data have not been inserted into the database, wait until it is done.
	 */
	private void waitForDatabaseReady() {
		
		int timeCount = 0;
		
		while(!db.updateFinished(RSSDatabaseHelper.VIDEO)) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
				
			}
			db.refreshEntryList(RSSDatabaseHelper.VIDEO);
			videoEntryList = db.getRSSEntryList(RSSDatabaseHelper.VIDEO);
			timeCount++;
			//But we will not wait for too long time, when a certain time limit is reached, end it
			if(timeCount == 20) {
				break;
			}
		}
	}

	private void createFooterView() {
		
		moreVideos = new TextView(this);
		moreVideos.setText(getApplicationContext().getResources().getString(R.string.click_for_more));
		moreVideos.setTextSize(20f);
		moreVideos.setTextColor(Color.rgb(255, 255, 255));
		moreVideos.setTypeface(null, Typeface.BOLD);
		moreVideos.setGravity(Gravity.CENTER);
		moreVideos.setPadding(0, 6, 0, 6);
		moreVideos.setBackgroundColor(Color.rgb(150, 150, 150));
		
		moreVideos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showLoadingMessage();
				
				webView = new WebView(self);
				
				WebSettings webSettings = webView.getSettings();
				
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.setLayoutParams(
        		new ViewFlipper.LayoutParams(
        				ViewFlipper.LayoutParams.FILL_PARENT,
        				ViewFlipper.LayoutParams.FILL_PARENT
        				)
        		);
        
        webView.setWebViewClient(new WebViewClient() {
        	@Override
        	 public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		 view.loadUrl(url);
        		 return true;
        	 }
        });
        
        videoFlipper.addView(webView);
        videoFlipper.showNext();
        
        if(MySettings.getMySettings().getVideoSource().equals(MySettings.YOUTUBE)) {
        	webView.loadUrl(getApplicationContext().getResources().getString(R.string.youtube_site));
        } else {
        	webView.loadUrl(getApplicationContext().getResources().getString(R.string.youku_site));
        }
        
        webView.setWebViewClient(new WebViewClient() {
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		progress.dismiss();
        	}
        });
			}
		});
    videoList.addFooterView(moreVideos);
	}
	
	/**
	 * Add an image to the ArrayList of bitmaps
	 * @param pos The position of the image
	 * @param image The image to be added
	 */
	public void addImage(int pos, Bitmap image) {
		imageHash.put(videoEntryList.get(pos).url, image);
		refreshHandler.sendEmptyMessage(0);
	}
	
	private Handler videoHandler = new Handler() {
		public void handleMessage(Message msg) {
			createFooterView();
			videoList.setAdapter(adapter);
			progress.dismiss();
		}
	};
	
	private Handler refreshHandler = new Handler() {
	  public void handleMessage(Message msg) {
	    videoList.invalidateViews(); // update view
	  }
	};
	
	private class VideoAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;

		public VideoAdapter() {
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return videoEntryList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View coverView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.video_item, null);
			ImageView videoImageThumb = (ImageView) v.findViewById(R.id.videoThumbImg);
			videoImageThumb.setBackgroundColor(Color.BLACK);
			ProgressBar pBar = (ProgressBar) v.findViewById(R.id.video_progress);
			
			if (imageHash.size() > position) {
				videoImageThumb.setImageBitmap(imageHash.get(videoEntryList.get(position).url));
				pBar.setVisibility(View.INVISIBLE);
			}
			
			TextView vt = (TextView)v.findViewById(R.id.video_title);
			TextView vd = (TextView)v.findViewById(R.id.video_date);
			
			vt.setText(videoEntryList.get(position).title);
			vd.setText(videoEntryList.get(position).date);

			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		openItem(position);
	}
	
	public void shareItem(int position) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, videoEntryList.get(position).title);
		intent.putExtra(Intent.EXTRA_TEXT, videoEntryList.get(position).url);
		startActivity(Intent.createChooser(intent, "Share with"));
	}
	
	public void openItem(int position) {
		MySettings settings = MySettings.getMySettings();
		String videoSource = settings.getVideoSource();
		
		if(videoSource.equals(MySettings.YOUTUBE)) {
			String youtubeID = videoEntryList.get(position).content;
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID));
			startActivity(i);
		} else if(videoSource.equals(MySettings.YOUKU)) {
			String url = videoEntryList.get(position).content;
			Intent i = new Intent(VideoList.this, WebViewDisplay.class);
			i.putExtra("url", url);
			startActivity(i);
		}
	}
	
	@Override
	public void onBackPressed() {
		checkState();
	}
	
	/**
	 * Refresh the list to show the most up-to-date entries:
	 */
	public void refreshList() {
		videoList.removeFooterView(moreVideos);
		showLoadingMessage();
		waitForDatabaseReady();
		new Thread(new PrepareImage()).start();
	}
	
	public void checkState() {
		
		if (videoFlipper.getChildCount() > 1) {
			if(webView.canGoBack()) {
				webView.goBack();
			} else {
				videoFlipper.showPrevious();
				videoFlipper.removeViewAt(1);
			}
		} else {
			VideoList.self.finish();
		}
	}
}
