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
import android.widget.ViewFlipper;

public class BlogList extends Activity implements OnItemClickListener, OnItemLongClickListener {

    public static BlogList self;
    
    private ArrayList<RSSEntry> blogEntryList;
    
    private ListView blogList;
    private WebView webView;
    private ViewFlipper blogFlipper;
    private TextView moreBlogs;
    
    private ProgressDialog progress;
    
    public static final String blog_id = "id of the blog";
    
    private RSSDatabase db;
    
    private Hashtable<String, Bitmap> imageHash;
    
	private BlogAdapter adapter;

	private Thread prepareImageThread;

	private int currentPos = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);
        
        self = this;
        imageHash = new Hashtable<String, Bitmap>();
        
        blogList = (ListView) findViewById(R.id.BlogsList);
        blogList.setDivider(new ColorDrawable(Color.rgb(150, 150, 150)));
        blogList.setDividerHeight(1);
        blogList.setOnItemClickListener(this);
        blogList.setOnItemLongClickListener(this);
        
        blogFlipper = (ViewFlipper) findViewById(R.id.BlogsFlipper);

        db = RSSDatabase.getDatabaseInstance(null);
        blogEntryList = db.getRSSEntryList(RSSDatabaseHelper.BLOG);

        boolean listEmpty = blogEntryList.isEmpty();
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
    
    private void showLoadingMessage() {
        progress = ProgressDialog.show(this, "", 
                self.getResources().getString(R.string.loading), true);
        progress.setCancelable(true);
    }
    
    class PrepareImage implements Runnable {
        @Override
        public void run() {
            adapter = new BlogAdapter();
            ArrayList<String> urls = new ArrayList<String>();
            
            waitForDatabaseReady();
            blogEntryList = db.getRSSEntryList(RSSDatabaseHelper.BLOG);
            
            for (int i = 0; i < blogEntryList.size(); i++) {
                if (blogEntryList.get(i).image != null) {
                    urls.add(blogEntryList.get(i).image);
                }
            }
            
            blogHandler.sendEmptyMessage(0);
            ImageFetcher.fetchImage(urls, RSSDatabaseHelper.BLOG);
        }
    }

    /**
     * if the data have not been inserted into the database, wait until it is done.
     */
    private void waitForDatabaseReady() {
        while(!db.updateFinished(RSSDatabaseHelper.BLOG)) {
            try {
                Thread.sleep(1000);
            } catch(Exception e) {}
        }
    }
    
    public void addImage(int pos, Bitmap image) {
        imageHash.put(blogEntryList.get(pos).url, image);
        refreshHandler.sendEmptyMessage(0);
    }
    
    private Handler blogHandler = new Handler() {
        public void handleMessage(Message msg) {
            createFooterView();
	        blogList.setAdapter(adapter);
			progress.dismiss();
        }
    };

	private Handler refreshHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        blogList.invalidateViews();
	    }
	};
    
    private void createFooterView() {
        moreBlogs = new TextView(this);
        moreBlogs.setText(getApplicationContext().getResources().getString(R.string.click_for_more));
        moreBlogs.setTextSize(20f);
        moreBlogs.setTextColor(Color.rgb(255, 255, 255));
        moreBlogs.setTypeface(null, Typeface.BOLD);
        moreBlogs.setGravity(Gravity.CENTER);
        moreBlogs.setPadding(0, 6, 0, 6);
        moreBlogs.setBackgroundColor(Color.rgb(150, 150, 150));
        
        moreBlogs.setOnClickListener(new OnClickListener() {
            
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
                
                blogFlipper.addView(webView);
                blogFlipper.showNext();
                
                webView.loadUrl(getApplicationContext().getResources().getString(R.string.link_blogs_url));
                
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progress.dismiss();
                    }
                });
            }
        });
        blogList.addFooterView(moreBlogs);
    }
    
    private class BlogAdapter extends BaseAdapter {
        
        private LayoutInflater inflater;
        
        public BlogAdapter() {
            inflater = getLayoutInflater();
        }

    	/**
    	 * map the position of the entry to the position of the image
    	 * @param position of the entry
    	 * @return position of the image
    	 */
    	private int mapBlogToImage(int position) {
    		int posOfImage = -1;
    		for(int i = 0; i < position + 1; i++) {
    			if(blogEntryList.get(i).image != null) {
    				posOfImage++;
    			}
    		}
    		return posOfImage;
    	}

        @Override
        public int getCount() {
            return blogEntryList.size();
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
            View v = inflater.inflate(R.layout.blog_item, null);
            
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.blog_text_layout);
            
            TextView blogTitle = (TextView) v.findViewById(R.id.blog_title);
            blogTitle.setText(XMLParser.teaseTitle(blogEntryList.get(position).title));
            
            TextView blogAuthor = (TextView) v.findViewById(R.id.blog_author);
            blogAuthor.setText(blogEntryList.get(position).otherInfo);
            
            ImageView iv = (ImageView) v.findViewById(R.id.blog_image);
            iv.setBackgroundColor(Color.BLACK);
            
			String url = blogEntryList.get(position).image;
            
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int screenWidth = display.getWidth();
            
            if (url != null) {
				if(imageHash.size() > mapBlogToImage(position)) {
					Bitmap bmp = imageHash.get(blogEntryList.get(mapBlogToImage(position)).url);
                    if (bmp != null) {
    			        iv.setImageBitmap(bmp);
						ll.setLayoutParams(new RelativeLayout.LayoutParams(
								(int)(screenWidth * 0.55), RelativeLayout.LayoutParams.WRAP_CONTENT));
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        		90, (int)(bmp.getHeight() * 90 / bmp.getWidth()));
                        lp.addRule(RelativeLayout.RIGHT_OF, R.id.blog_text_layout);
						iv.setLayoutParams(lp);
                    } else {
						iv.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    }
				}
            } else {
				iv.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            }
            return v;
        }
    }

    /**
     * When an item is clicked, display the content of it using runtime layout in a 
     * WebView.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	openItem(position);
    }
    
    @Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(blogEntryList.get(position).title);
        
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
		intent.putExtra(Intent.EXTRA_SUBJECT, blogEntryList.get(position).title);
		intent.putExtra(Intent.EXTRA_TEXT, blogEntryList.get(position).url);
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
        
        //Display the content of the blog with a WebView:
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
        
        webView.setInitialScale(150);
        webView.loadDataWithBaseURL("http://www.msf.org.hk/", 
                blogEntryList.get(position).content, "text/html", "UTF-8", null);
        
        blogFlipper.addView(webView);
        blogFlipper.showNext();
    }
    
    public ArrayList<RSSEntry> getBlogEntryList() {
        return blogEntryList;
    }
    
    public ListView getBlogList() {
        return blogList;
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
	    	blogList.removeFooterView(moreBlogs);
			showLoadingMessage();
	        waitForDatabaseReady();
	        prepareImageThread = new Thread(new PrepareImage());
	        prepareImageThread.start();
    	}
    }

    public void checkState() {
        if (blogFlipper.getChildCount() > 1) {
            if(webView.canGoBack()) {
                webView.goBack();
            } else {
                blogFlipper.showPrevious();
                blogFlipper.removeViewAt(1);
            }
        } else {
            BlogList.self.finish();
        }
    }
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (blogFlipper.getChildCount() == 1) { // in the list view
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.share_item:
			shareItem(currentPos);
			return true;
        case R.id.menu_msf:
        	intent = new Intent(BlogList.this, MSFView.class);
        	startActivity(intent);
            return true;
        case R.id.menu_settings:
            intent = new Intent(BlogList.this, Preferences.class);
            startActivity(intent);
            this.finish();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}