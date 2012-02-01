package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainTabs extends TabActivity implements OnTabChangeListener {
	
	/**
	 * The Variables for View
	 */
	private ImageButton backButton;
	private ImageButton refreshButton;
	private TabHost mainTab;
	private TextView mainTitle;
	
	private String [] tabTitles;
	
	private final static int[] tabTitlesDrawable = {
		R.drawable.icon_news,
		R.drawable.icon_blog,
		R.drawable.icon_vision,
		R.drawable.icon_video
	};
	
	/**
	 * Called when the activity is first created. 
	 */
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	
	    super.onCreate(savedInstanceState);
	    
    	setContentView(R.layout.tabs);
    	
	    tabTitles = new String [] {
		    	this.getString(R.string.latest_news),
		    	this.getString(R.string.blogs),
		    	this.getString(R.string.frontline_vision),
		    	this.getString(R.string.videos),
		};
	    
	    mainTitle = (TextView) this.findViewById(R.id.MainTitle);
	    
	    backButton = (ImageButton) this.findViewById(R.id.BackButton);
	    backButton.getBackground().setAlpha(0);
        backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (mainTab.getCurrentTab()) {
				case 0:
					NewsList.self.onBackPressed();
					break;
				case 1:
					BlogList.self.onBackPressed();
					break;
				case 2:
					ImageGrid.self.onBackPressed();
					break;
				case 3:
					VideoList.self.onBackPressed();
					break;
				}
			}
        });
        
        refreshButton = (ImageButton) this.findViewById(R.id.refresh_button);
        refreshButton.getBackground().setAlpha(0);
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
	    
	    mainTab = this.getTabHost();
	    this.BuildTabs();
	    
	    mainTab.setOnTabChangedListener(this);
	    mainTab.setCurrentTab(1);
	    
	    Intent i = getIntent();
	    mainTab.setCurrentTab(i.getIntExtra("tab", 0));
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
    public void BuildTabs() {
    	
    	TabSpec spec;
    	/**
    	 * Latest News
    	 */
   		spec = mainTab.newTabSpec(tabTitles[0])
   						.setIndicator(makeTab(tabTitles[0], tabTitlesDrawable[0]))
   						.setContent(new Intent(this, NewsList.class));
   		mainTab.addTab(spec);
   		/**
   		 * Blogs
   		 */
   		spec = mainTab.newTabSpec(tabTitles[1])
						.setIndicator(makeTab(tabTitles[1], tabTitlesDrawable[1]))
						.setContent(new Intent(this, BlogList.class));
   		mainTab.addTab(spec);
   		
   		/**
   		 * Videos 
   		 */
   		spec = mainTab.newTabSpec(tabTitles[2])
						.setIndicator(makeTab(tabTitles[2], tabTitlesDrawable[2]))
						.setContent(new Intent(this, ImageGrid.class));
		mainTab.addTab(spec);
		
		/**
   		 * Photos
   		 */
   		spec = mainTab.newTabSpec(tabTitles[3])
						.setIndicator(makeTab(tabTitles[3], tabTitlesDrawable[3]))
						.setContent(new Intent(this, VideoList.class));
		mainTab.addTab(spec);
   	
    }
    
    private View makeTab(final String Text, int drawable) {
    	
    	LayoutInflater inflater = getLayoutInflater();
    	View view = inflater.inflate(R.layout.tab_button, null);
    	TextView tabButton = (TextView) view.findViewById(R.id.tabsText);
    	tabButton.setText(Text);
    	tabButton.setCompoundDrawablesWithIntrinsicBounds(null, 
    			getResources().getDrawable(drawable), null, null);
    	return view;
    }
    
    /**
	 * Called when the menu is shown on the screen
	 * Different from onCreateOptionsMenu because that is only called when menu is shown for the first time
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_about:
			Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.about_dialog);
			dialog.show();
			return true;
		case R.id.menu_settings:
			Intent settingsActivity = new Intent(MainTabs.this, Preferences.class);
			startActivity(settingsActivity);
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
