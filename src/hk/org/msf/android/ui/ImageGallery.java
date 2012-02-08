package hk.org.msf.android.ui;

import hk.org.msf.android.R;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.utils.ImageFetcher;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageGallery extends Activity {
	
	private ArrayList<Bitmap> images;
	private Gallery gallery;
	private int infoPos;
	private int picturePos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.gallery);
		
		images = ImageFetcher.getImage(RSSDatabaseHelper.IMAGE);
		
		Intent intent = getIntent();
		
		if (intent !=null && intent.getExtras()!=null) {
			infoPos = intent.getExtras().getInt("info_pos");
            picturePos = intent.getExtras().getInt("pic_pos");
        }
		
		gallery = (Gallery)findViewById(R.id.gallery_view);
		gallery.setPadding(10, 10, 10, 10);
		gallery.setAdapter(new ImageAdapter());
		gallery.setSelection(picturePos);
	}

	private class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
	        
	    public ImageAdapter() {
			TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
			a.recycle();
			inflater = getLayoutInflater();
	    }
	
		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View v = inflater.inflate(R.layout.gallery_item, null);
			
			final int text_pos = position + infoPos - picturePos;
			
			TextView imageTitle = (TextView)v.findViewById(R.id.image_title);
			imageTitle.setText(ImageGrid.getImages().get(text_pos).title);
			imageTitle.getBackground().setAlpha(100);
			
			ImageView galleryImage = (ImageView)v.findViewById(R.id.image_view);
			galleryImage.setImageBitmap(images.get(position));
			galleryImage.setAdjustViewBounds(true);
			
			TextView galleryDiscription = (TextView)v.findViewById(R.id.image_description);
			galleryDiscription.setText(ImageGrid.getImages().get(text_pos).content);
			galleryDiscription.getBackground().setAlpha(100);
			
			TextView imageShareButton = (TextView)v.findViewById(R.id.image_share);
			imageShareButton.getBackground().setAlpha(140);
			imageShareButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, ImageGrid.getImages().get(text_pos).title);
					intent.putExtra(Intent.EXTRA_TEXT, ImageGrid.getImages().get(text_pos).url);
					startActivity(Intent.createChooser(intent, "Share with"));
				}
			});
			return v;
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.share_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share_item:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, ImageGrid.getImages().get(picturePos).title);
			intent.putExtra(Intent.EXTRA_TEXT, ImageGrid.getImages().get(picturePos).url);
			startActivity(Intent.createChooser(intent, "Share with"));
			return true;
        case R.id.menu_msf:
        	intent = new Intent(ImageGallery.this, MSFView.class);
        	startActivity(intent);
            return true;
        case R.id.menu_settings:
            intent = new Intent(ImageGallery.this, Preferences.class);
            startActivity(intent);
            this.finish();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
