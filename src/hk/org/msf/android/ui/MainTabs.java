package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainTabs extends TabActivity implements OnTabChangeListener {
	
	private MainTabs self;
	
	private TabHost mainTab;
	private TextView mainTitle;
	
	private ImageView shareButton;
	private ImageView refreshButton;
	private ImageView homeButton;
	private ImageView settingButton;
	
	private AlertDialog dialog;
	
	private String [] tabTitles;
	
	private final static int[] tabTitlesDrawable = {
		R.drawable.menu_news,
		R.drawable.menu_blog,
		R.drawable.menu_vision,
		R.drawable.menu_video
	};
	
	/**
	 * Called when the activity is first created. 
	 */
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    self = this;
	    
    	this.setContentView(R.layout.tabs);
    	
	    tabTitles = new String [] {
		    	this.getString(R.string.latest_news),
		    	this.getString(R.string.blogs),
		    	this.getString(R.string.frontline_vision),
		    	this.getString(R.string.videos),
		};
	    
	    mainTitle = (TextView) this.findViewById(R.id.MainTitle);
	    
	    shareButton = (ImageView) this.findViewById(R.id.tabs_bar_share_button);
	    shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mainTab.getCurrentTab()) {
				case 0:
					NewsList.self.share();
					break;
				case 1:
					BlogList.self.share();
					break;
				case 2:
					ImageGrid.self.share();
					break;
				case 3:
					VideoList.self.share();
					break;
				}
			}
			
	    });
	    
	    refreshButton = (ImageView) this.findViewById(R.id.tabs_bar_refresh_button);
        refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mainTab.getCurrentTab()) {
				case 0:
					NewsList.self.refreshList();
					break;
				case 1:
					BlogList.self.refreshList();
					break;
				case 2:
					ImageGrid.self.refreshGrid();
					break;
				case 3:
					VideoList.self.refreshList();
					break;
				}
			}
			
        });
	    
	    homeButton = (ImageView) this.findViewById(R.id.tabs_bar_home_button);
	    homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				self.finish();
			}
			
	    });
	    
	    settingButton = (ImageView) this.findViewById(R.id.tabs_bar_settings_button);
	    settingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				self.showLangSelectDialog();
			}
			
	    });
	    
	    mainTab = this.getTabHost();
	    this.buildTabs();
	    
	    mainTab.setOnTabChangedListener(this);
	    
	    int currTab = getIntent().getIntExtra("tab", 0);
	    mainTab.setCurrentTab(currTab);
	    
	    if (currTab == 0) {
	    	mainTitle.setText(tabTitles[0]);
	    }
	}

	@Override
	public void onTabChanged(String tabId) {
		for (int i = 0; i < tabTitles.length; i++) {
			if (tabTitles[i].equals(tabId)) {
				mainTitle.setText(tabTitles[i]);
				break;
			}
		}
	}
	
	/**
     * Build the Tabs
     */
    public void buildTabs() {
   		TabSpec newsTab = mainTab.newTabSpec(tabTitles[0])
				.setIndicator(makeTab(tabTitles[0], tabTitlesDrawable[0]))
				.setContent(new Intent(this, NewsList.class));

   		TabSpec blogTab = mainTab.newTabSpec(tabTitles[1])
				.setIndicator(makeTab(tabTitles[1], tabTitlesDrawable[1]))
				.setContent(new Intent(this, BlogList.class));
   		
   		TabSpec imageTab = mainTab.newTabSpec(tabTitles[2])
				.setIndicator(makeTab(tabTitles[2], tabTitlesDrawable[2]))
				.setContent(new Intent(this, ImageGrid.class));

   		TabSpec videoTab = mainTab.newTabSpec(tabTitles[3])
				.setIndicator(makeTab(tabTitles[3], tabTitlesDrawable[3]))
				.setContent(new Intent(this, VideoList.class));
   		
		mainTab.addTab(newsTab);
		mainTab.addTab(blogTab);
		mainTab.addTab(imageTab);
		mainTab.addTab(videoTab);
   	
    }
    
    private View makeTab(final String Text, int drawable) {
    	LayoutInflater inflater = getLayoutInflater();
    	View view = inflater.inflate(R.layout.tab_button, null);
    	
    	ImageView image = (ImageView) view.findViewById(R.id.tabsImage);
    	image.setImageResource(drawable);
    	TextView tabButton = (TextView) view.findViewById(R.id.tabsText);
    	tabButton.setText(Text);

    	return view;
    }
    
    /**
	 * Called when the menu is shown on the screen
	 * Different from onCreateOptionsMenu because that is only called when menu is shown for the first time
	 */
    /*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        switch (item.getItemId()) {
        case R.id.menu_msf:
        	intent = new Intent(MainTabs.this, MSFView.class);
        	startActivity(intent);
            return true;
        case R.id.menu_settings:
            intent = new Intent(MainTabs.this, Preferences.class);
            startActivity(intent);
            this.finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
    private void showLangSelectDialog() {
		MySettings settings = MySettings.getMySettings();
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(self);
		
		settings.configurePrefs(getApplicationContext());
		final String langPref = settings.getLangPref();
		
		int sel = 0;
		if(langPref.equals(MySettings.ENGLISH)) {
			sel = 0;
		} else if(langPref.equals(MySettings.TRADITIONAL_CHINESE)) {
			sel = 1;
		} else if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
			sel = 2;
		}
		
    	final CharSequence [] items = { "English", "繁體中文", "简体中文" };
    	AlertDialog.Builder builder = new AlertDialog.Builder(self);
    	builder.setTitle(self.getResources().getString(R.string.language_select));
    	builder.setSingleChoiceItems(items, sel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int pos) {
				SharedPreferences.Editor editor = prefs.edit();
				if (pos == 0) {
					if (!langPref.equals(MySettings.ENGLISH)) {
						editor.putString("langPref", MySettings.ENGLISH);
						editor.commit();
						self.restartApp();
					}
				} else if (pos == 1) {
					if (!langPref.equals(MySettings.TRADITIONAL_CHINESE)) {
						editor.putString("langPref", MySettings.TRADITIONAL_CHINESE);
						editor.commit();
						self.restartApp();
					}
				} else if (pos == 2) {
					if (!langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
						editor.putString("langPref", MySettings.SIMPLIFIED_CHINESE);
						editor.commit();
						self.restartApp();
					}
				}
			}
			
		});
    	
    	dialog = builder.create();
    	dialog.show();
    }
    
    private void restartApp() {
    	Intent intent = new Intent(self, SplashScreen.class);
    	self.startActivity(intent);
    	dialog.dismiss();
    	self.finish();
    	MainMenu.finishManiMenu();
    }
    
}
