package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.utils.MySettings;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity implements OnItemClickListener {
    
    private GridView mainMenuGrid;
    
    private String [] menuTypes;
    private int [] menuImages;
    
    private static MainMenu self;
    
    private AlertDialog dialog;
    
    /**
     * Called only when the application is started:
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        self = this;
        
    }
    
    /**
     * Every time when the activity is brought to foreground, this method is called:
     */
    @Override
    public void onResume() {
    
        super.onResume();
        
        updateApplicationLanguage();
        
        setContentView(R.layout.main_menu);        
        
        menuTypes = new String [] {
                getApplicationContext().getResources().getString(R.string.latest_news),
                getApplicationContext().getResources().getString(R.string.blogs),
                getApplicationContext().getResources().getString(R.string.frontline_vision),
                getApplicationContext().getResources().getString(R.string.videos),
                getApplicationContext().getResources().getString(R.string.msf),
                getApplicationContext().getResources().getString(R.string.menu_settings)
        };
        
        menuImages = new int [] {
                R.drawable.menu_news,
                R.drawable.menu_blog,
                R.drawable.menu_vision,
                R.drawable.menu_video,
                R.drawable.menu_about,
                R.drawable.menu_setting
        };
    
        ImageView mainMenuImage = (ImageView)findViewById(R.id.menu_top_image);
        
        MySettings settings = MySettings.getMySettings();
        String langPref = settings.getLangPref();
        
        if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
            mainMenuImage.setImageResource(R.drawable.logo_cn);
        } else {
            mainMenuImage.setImageResource(R.drawable.logo_hk);
        }
                
        mainMenuGrid = (GridView)findViewById(R.id.main_menu_grid);
        mainMenuGrid.setAdapter(new MainMenuAdapter());
        mainMenuGrid.setOnItemClickListener(this);
    }
    
    class MainMenuAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 6;
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
            LayoutInflater inflater = getLayoutInflater();
            View v = inflater.inflate(R.layout.main_menu_item, null);
            
            TextView menuItemType = (TextView)v.findViewById(R.id.menu_item_type);
            menuItemType.setText(menuTypes[position]);
            
            ImageView menuItemImage = (ImageView)v.findViewById(R.id.menu_item_image);
            menuItemImage.setImageResource(menuImages[position]);
            
            return v;
        }
    }

    /**
     * Update the language for the application when the activity is resumed,
     * so that the changes of language will take effect as soon as the user
     * exits the preference screen
     */
    public void updateApplicationLanguage() {
        
        MySettings settings = MySettings.getMySettings();
        settings.configurePrefs(this);
        
        String langPref = settings.getLangPref();
        
        //set the application language according to the class MySettings:
        Configuration config = new Configuration();
        
        if(langPref.equals(MySettings.ENGLISH)) {
            config.locale = Locale.ENGLISH;
        } else if(langPref.equals(MySettings.SIMPLIFIED_CHINESE)) {
            config.locale = Locale.CHINA;
        } else if(langPref.equals(MySettings.TRADITIONAL_CHINESE)) {
            config.locale = Locale.TAIWAN;
        }
        
        //set the configuration:
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
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
        // Handle item selection
    	Intent intent;
        switch (item.getItemId()) {
        case R.id.menu_msf:
        	intent = new Intent(MainMenu.this, MSFView.class);
        	startActivity(intent);
            return true;
        case R.id.menu_settings:
            intent = new Intent(MainMenu.this, Preferences.class);
            startActivity(intent);
            this.finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent i;
        
        switch(position) {
        case 0:
            i = new Intent(MainMenu.this, MainTabs.class);
            i.putExtra("tab", 0);
            this.startActivity(i);
            break;
        case 1:
            i = new Intent(MainMenu.this, MainTabs.class);
            i.putExtra("tab", 1);
            this.startActivity(i);
            break;
        case 2:
            i = new Intent(MainMenu.this, MainTabs.class);
            i.putExtra("tab", 2);
            this.startActivity(i);
            break;
        case 3:
            i = new Intent(MainMenu.this, MainTabs.class);
            i.putExtra("tab", 3);
            this.startActivity(i);
            break;
        case 4:
            i = new Intent(MainMenu.this, MSFView.class);
            this.startActivity(i);
            break;
        case 5:
        	this.showLangSelectDialog();
            break;
        }
        self.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
    }
    
    public static void finishManiMenu() {
    	self.finish();
    }
    
}
