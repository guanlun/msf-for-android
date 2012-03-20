package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.data.DataUpdater;
import hk.org.msf.android.data.RSSDatabase;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.data.RSSEntry;
import hk.org.msf.android.utils.ImageFetcher;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ImageGrid extends Activity implements OnItemClickListener, OnItemLongClickListener {
	
	public static ImageGrid self;

	private RelativeLayout imageGridLayout;
	private GridView imageGridView;
	private WebView webView;
	// private Button moreImages;
	
	private static ArrayList<RSSEntry> imageEntryList;
	
	private ProgressDialog progress;
	private RSSDatabase db;
	
	private Hashtable<String, Bitmap> imageHash;
	
	private ImageAdapter adapter;
	
	private Thread prepareImageThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.img_grid);
		
		self = this;
		
		imageHash = new Hashtable<String, Bitmap>();
		
		db = RSSDatabase.getDatabaseInstance(self);
		imageEntryList = db.getRSSEntryList(RSSDatabaseHelper.IMAGE);
		
		imageGridLayout = (RelativeLayout)findViewById(R.id.image_grid_layout);
		
		/*
		moreImages = (Button)findViewById(R.id.more_images);
		moreImages.setVisibility(View.INVISIBLE);
		moreImages.setPadding(0, 6, 0, 6);
		moreImages.setTextSize(20f);
		moreImages.setTextColor(Color.rgb(255, 255, 255));
		moreImages.setTypeface(null, Typeface.BOLD);
		
		moreImages.setOnClickListener(new OnClickListener() {
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
		        
		        imageGridLayout.addView(webView);
		        webView.loadUrl(getApplicationContext().getResources()
		        		.getString(R.string.link_images_url));	
		        
		        webView.setWebViewClient(new WebViewClient() {
		        	@Override
		        	public void onPageFinished(WebView view, String url) {
		        		progress.dismiss();
		        	}
		        });
			}
		});
		*/
		
		imageGridView = (GridView)findViewById(R.id.image_gridview);
		imageGridView.setOnItemClickListener(this);
		imageGridView.setOnItemLongClickListener(this);

        boolean listEmpty = imageEntryList.isEmpty();
        boolean noCoon = !DataUpdater.isOnline(self);
        if (!(listEmpty && noCoon)) {
			showLoadingMessage();
			prepareImageThread = new Thread(new PrepareImage());
			prepareImageThread.start();
        }
	}
	
	@Override
	public void onDestroy() {
		imageHash.clear();
        super.onDestroy();
	} 

	public void showLoadingMessage() {
		progress = ProgressDialog.show(this, "", self.getResources().getString(R.string.loading), true);
		progress.setCancelable(true);
	}
	
	public static ArrayList<RSSEntry> getImages() {
        return imageEntryList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		openItem(position);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(imageEntryList.get(position).title);
        
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
        		ViewGroup.LayoutParams.FILL_PARENT
        ));
        
        Button viewButton = new Button(this);
        viewButton.setText(self.getApplication().getResources().getString(R.string.open_view));
        viewButton.setTextColor(Color.WHITE);
        viewButton.setTextSize(18.0f);
        viewButton.setPadding(40, 10, 40, 10);
        viewButton.setBackgroundColor(Color.WHITE);
        viewButton.getBackground().setAlpha(90);
        viewButton.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT));
        viewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openItem(position);
				dialog.dismiss();
			}
        });
        
        Button shareButton = new Button(this);
        shareButton.setText(self.getApplication().getResources().getString(R.string.share));
        shareButton.setTextColor(Color.WHITE);
        shareButton.setTextSize(18.0f);
        shareButton.setPadding(40, 10, 40, 10);
        shareButton.setBackgroundColor(Color.WHITE);
        shareButton.getBackground().setAlpha(90);
        shareButton.setLayoutParams(new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        shareButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		shareItem(position);
				dialog.dismiss();
        	}
        });
        
        ll.addView(viewButton);
        ll.addView(shareButton);
        
        dialog.setContentView(ll);
        dialog.show();
        
        return true;
	}
	
	public void shareItem(int position) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, imageEntryList.get(position).title);
		intent.putExtra(Intent.EXTRA_TEXT, imageEntryList.get(position).url);
		startActivity(Intent.createChooser(intent, "Share with"));
	}
	
	public void openItem(int position) {
		String link = imageEntryList.get(position).url;
		if (imageHash.get(link) != null) {
			int realPos = 0;
			for (int i = 0; i < position; i++) {
				if (imageHash.get(imageEntryList.get(i).url) != null) {
					realPos++;
				}
			}
            Intent intent = new Intent(ImageGrid.this, ImageGallery.class);
            intent.putExtra("info_pos", position);
            intent.putExtra("pic_pos", realPos);
            startActivity(intent);
        }
	}
	
	@Override
	public void onBackPressed() {
		checkState();
	}
	
	/**
	 * Refresh the list to show the most up-to-date entries:
	 */
	public void refreshGrid() {
		if (!prepareImageThread.isAlive()) {
	        imageEntryList = db.getRSSEntryList(RSSDatabaseHelper.IMAGE);
	        imageGridView.setAdapter(adapter);
	        prepareImageThread = new Thread(new PrepareImage());
	        prepareImageThread.start();
		}
	}
	
	public void checkState() {
		if(imageGridLayout.getChildCount() > 2) {
			if(webView.canGoBack()) {
				webView.goBack();
			} else {
				imageGridLayout.removeViewAt(2);
			}
		} else {
			ImageGrid.self.finish();
		}
	}
	
	/**
	 * This method calls the FetchImage to start getting images from the website:
	 */
	private class PrepareImage implements Runnable {
		public void run() {
			adapter = new ImageAdapter();
			ArrayList<String> urls = new ArrayList<String>();
			
			waitForDatabaseReady();
			imageEntryList = db.getRSSEntryList(RSSDatabaseHelper.IMAGE);
			
			for(int i = 0; i < imageEntryList.size(); i++) {
				urls.add(imageEntryList.get(i).image);
			}
			
			imageHandler.sendEmptyMessage(0);
			ImageFetcher.fetchImage(urls, RSSDatabaseHelper.IMAGE); // do the time-consuming task
		}
	}
	
	/**
	 * if the data have not been inserted into the database, wait until it is done.
	 */
	private void waitForDatabaseReady() {
		while(!db.updateFinished(RSSDatabaseHelper.IMAGE)) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
	}
	
	/**
	 * Add an image to the ArrayList of bitmaps
	 * @param pos The position of the image
	 * @param image The image to be added
	 */
	public void addImage(int pos, Bitmap image) {
		imageHash.put(imageEntryList.get(pos).url, image);
		refreshHandler.sendEmptyMessage(0);
	}

	/**
	 * When image fetching is completed, dismiss the progress dialog:
	 */
	private Handler imageHandler = new Handler() {
		public void handleMessage(Message msg) {
			imageGridView.setAdapter(adapter);
			progress.dismiss();
			// moreImages.setVisibility(View.VISIBLE);
		}
	};
	
	/**
	 * When a new image is loaded, invoke the handler to update the imageGridView
	 */
	private Handler refreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			imageGridView.invalidateViews(); // update view
		}
	};

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public ImageAdapter() {
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return imageEntryList.size();
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
			View v = inflater.inflate(R.layout.img_item, null);
			ImageView imageThumb = (ImageView) v.findViewById(R.id.thumb_image);
			ProgressBar pBar = (ProgressBar) v.findViewById(R.id.image_progress);
			
			if (position < imageHash.size()) {
				imageThumb.setImageBitmap(imageHash.get(imageEntryList.get(position).url));
				pBar.setVisibility(View.INVISIBLE);
			}
			
			TextView it = (TextView) v.findViewById(R.id.img_title);
			it.setText(imageEntryList.get(position).title);
			it.getBackground().setAlpha(160);
			return v;
		}
	}
}