package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.data.DataUpdater;
import hk.org.msf.android.data.RSSDatabase;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.data.RSSEntry;
import hk.org.msf.android.utils.ImageFetcher;
import hk.org.msf.android.utils.XMLParser;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class NewsList extends Activity implements OnItemClickListener, OnItemLongClickListener {

	public static NewsList self;

	private ArrayList<RSSEntry> newsEntryList;

	private ListView newsList;
	private ViewFlipper newsFlipper;
	private WebView webView;
	private TextView moreNews;

	public static final String news_id = "id of the news";

	private ProgressDialog progress;

	private RSSDatabase db;

	private Hashtable<String, Bitmap> imageHash;

	private NewsAdapter adapter;

	private Thread prepareImageThread;

	private int currentPos = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.news_list);

		self = this;
		imageHash = new Hashtable<String, Bitmap>();

		newsList = (ListView) findViewById(R.id.NewsList);
		newsList.setDivider(new ColorDrawable(Color.rgb(150, 150, 150)));
		newsList.setDividerHeight(1);
		newsList.setOnItemClickListener(this);
        newsList.setOnItemLongClickListener(this);

	    newsFlipper = (ViewFlipper) findViewById(R.id.NewsFlipper);

	    db = RSSDatabase.getDatabaseInstance(null);
	    newsEntryList = db.getRSSEntryList(RSSDatabaseHelper.NEWS);

        boolean listEmpty = newsEntryList.isEmpty();
        boolean noCoon = !DataUpdater.isOnline(self);
        
        if (!(listEmpty && noCoon)) {
		    showLoadingMessage();
	        prepareImageThread = new Thread(new PrepareImage());
	        prepareImageThread.start();
        } else {
        	Toast toast = Toast.makeText(self, 
        			"No network connection available, please check you network status", Toast.LENGTH_SHORT);
        	toast.show();
        }
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    imageHash.clear();
	}

    /**
     * Show a loading dialog:
     */
	private void showLoadingMessage() {
		progress = ProgressDialog.show(this, "", 
				self.getResources().getString(R.string.loading), true);
		progress.setCancelable(true);
	}

	class PrepareImage implements Runnable {
		@Override
		public void run() {
			adapter = new NewsAdapter();
			ArrayList<String> urls = new ArrayList<String>();

			waitForDatabaseReady();
			newsEntryList = db.getRSSEntryList(RSSDatabaseHelper.NEWS);

			for(int i = 0; i < newsEntryList.size(); i++) {
				if(newsEntryList.get(i).image != null) {
					urls.add(newsEntryList.get(i).image);
				}
			}

			newsHandler.sendEmptyMessage(0);
			ImageFetcher.fetchImage(urls, RSSDatabaseHelper.NEWS);
		}
	}

	/**
	 * if the data have not been inserted into the database, wait until it is done.
	 */
	private void waitForDatabaseReady() {
		while(!db.updateFinished(RSSDatabaseHelper.NEWS)) {
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
	}

	/**
	 * This method is called by ImageFetcher when one more image has been loaded
	 * @param pos
	 * @param image
	 */
	public void addImage(int pos, Bitmap image) {
		imageHash.put(newsEntryList.get(pos).url, image);
		refreshHandler.sendEmptyMessage(0);
	}

	/**
	 * Handle the message when the data parsed from RSS fields are ready
	 */
	private Handler newsHandler = new Handler() {
		public void handleMessage(Message msg) {
			// createFooterView();
	        newsList.setAdapter(adapter);
			progress.dismiss();
		}
	};

	/**
	 * Handle the message from addImage(), which is called by ImageFetcher
	 */
	private Handler refreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			newsList.invalidateViews();
		}
	};
    
	/**
	 * Create the footer that shows "more" at the bottom of the list view
	 */
	/*
	private void createFooterView() {
    	
		moreNews = new TextView(this);
		moreNews.setText(getApplicationContext().getResources().getString(R.string.click_for_more));
		moreNews.setTextSize(20f);
		moreNews.setTextColor(Color.rgb(255, 255, 255));
		moreNews.setTypeface(null, Typeface.BOLD);
		moreNews.setGravity(Gravity.CENTER);
		moreNews.setPadding(0, 6, 0, 6);
		moreNews.setBackgroundColor(Color.rgb(150, 150, 150));

		moreNews.setOnClickListener(new OnClickListener() {
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

		        newsFlipper.addView(webView);
		        newsFlipper.showNext();

		        webView.loadUrl(getApplicationContext().getResources().getString(R.string.link_news_url));	

		        webView.setWebViewClient(new WebViewClient() {
		        	@Override
		        	public void onPageFinished(WebView view, String url) {
		        		progress.dismiss();
		        	}
		        });

			}
		});
		newsList.addFooterView(moreNews);
	}
	*/

    private class NewsAdapter extends BaseAdapter {
    	
    	private LayoutInflater inflater;
    	
    	public NewsAdapter() {
    		inflater = getLayoutInflater();
    	}
    	
    	/**
    	 * map the position of the entry to the position of the image
    	 * @param position of the entry
    	 * @return position of the image
    	 */
    	private int mapNewsToImage(int position) {
    		int posOfImage = -1;
    		for(int i = 0; i < position + 1; i++) {
    			if(newsEntryList.get(i).image != null) {
    				posOfImage++;
    			}
    		}
    		return posOfImage;
    	}

		@Override
		public int getCount() {
			return newsEntryList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.news_item, null);

			LinearLayout ll = (LinearLayout) v.findViewById(R.id.news_text_layout);

			TextView newsTitle = (TextView) v.findViewById(R.id.news_title);
			newsTitle.setText(XMLParser.teaseTitle(newsEntryList.get(position).title));

			TextView newsPubdate = (TextView) v.findViewById(R.id.news_date);
			newsPubdate.setText(newsEntryList.get(position).date);

			ImageView iv = (ImageView)v.findViewById(R.id.news_image);

			iv.setBackgroundColor(Color.BLACK);

			String url = newsEntryList.get(position).image;

			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();

			if(url != null) { // if there is an image attached with the piece of news
				if(imageHash.size() > mapNewsToImage(position)) {
					Bitmap bmp = imageHash.get(newsEntryList.get(mapNewsToImage(position)).url);
					if(bmp != null) {
						iv.setImageBitmap(bmp);
						/*
						ll.setLayoutParams(new RelativeLayout.LayoutParams(
								(int)(screenWidth * 0.55), RelativeLayout.LayoutParams.WRAP_CONTENT));
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        		90, (int)(bmp.getHeight() * 90 / bmp.getWidth()));
                        lp.addRule(RelativeLayout.RIGHT_OF, R.id.news_text_layout);
						iv.setLayoutParams(lp);
						*/
					} else {
						iv.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
					}
			    }
			}
			return v;
		}
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		openItem(position);
	}
    
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(newsEntryList.get(position).title);
        
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new ViewGroup.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
        		ViewGroup.LayoutParams.FILL_PARENT));
        
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
        		LinearLayout.LayoutParams.WRAP_CONTENT));
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
		intent.putExtra(Intent.EXTRA_SUBJECT, newsEntryList.get(position).title);
		intent.putExtra(Intent.EXTRA_TEXT, newsEntryList.get(position).url);
		startActivity(Intent.createChooser(intent, "Share with"));
	}

	public void openItem(final int position) {
    	currentPos = position;
    	
        LinearLayout webLayout = new LinearLayout(this);
        webLayout.setOrientation(LinearLayout.VERTICAL);
        webLayout.setBackgroundColor(Color.WHITE);
        webLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT
                )
        );
        
        webView = new WebView(this);
        
        WebSettings webSettings = webView.getSettings();
        
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        
        webView.setLayoutParams(
        		new LinearLayout.LayoutParams(
                		LinearLayout.LayoutParams.FILL_PARENT, 
                		LinearLayout.LayoutParams.WRAP_CONTENT
                )
		);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 view.loadUrl(url);
                 return true;
             }
        });
        webView.setInitialScale(120);
        webView.loadDataWithBaseURL("http://www.msf.org.hk/", 
                "<h2><font color=\"#EE0000\">" + newsEntryList.get(position).title + "</font></h2>"
                + "<p>" + newsEntryList.get(position).date + "</p>"
                + newsEntryList.get(position).content, 
                "text/html", "UTF-8", null);
        
        newsFlipper.addView(webView);
        newsFlipper.showNext();
    }

	@Override
	public void onBackPressed() {
		checkState();
	}

	/**
	 * Refresh the list to show the most up-to-date entries:
	 */
	public void refreshList() {
		if (!prepareImageThread.isAlive()) {
	    	newsList.removeFooterView(moreNews);
			showLoadingMessage();
	        waitForDatabaseReady();
	        prepareImageThread = new Thread(new PrepareImage());
	        prepareImageThread.start();
		}
	}

	public void checkState() {
		if (newsFlipper.getChildCount() > 1) {
			if(webView.canGoBack()) {
				webView.goBack();
			} else {
				newsFlipper.showPrevious();
				newsFlipper.removeViewAt(1);
			}
		} else {
			// NewsList.self.finish();
			super.onBackPressed();
			this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}

	/*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (newsFlipper.getChildCount() == 1) { // in the list view
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.options_menu, menu);
		} else { // in the web view
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.share_menu, menu);
		}
		return true;
	}
	*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.share_item:
			shareItem(currentPos);
			return true;
        case R.id.menu_msf:
        	intent = new Intent(NewsList.this, MSFView.class);
        	startActivity(intent);
            return true;
        case R.id.menu_settings:
            intent = new Intent(NewsList.this, Preferences.class);
            startActivity(intent);
            this.finish();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}