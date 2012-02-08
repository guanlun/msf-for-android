package hk.org.msf.android.utils;

import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.ui.BlogList;
import hk.org.msf.android.ui.ImageGrid;
import hk.org.msf.android.ui.NewsList;
import hk.org.msf.android.ui.VideoList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * FetchImage class, get url, fetch images and store images.
 */
public class ImageFetcher {
	
    private static ArrayList<Bitmap> newsImages;
    private static ArrayList<Bitmap> blogImages;
	private static ArrayList<Bitmap> fVisionImages;
	private static ArrayList<Bitmap> videoImages;
	
	public static final String NEWS_IMAGE_FILE_PREFIX = "news";
    public static final String BLOG_IMAGE_FILE_PREFIX = "blog";
	public static final String FVISION_IMAGE_FILE_PREFIX = "image";
	public static final String VIDEO_IMAGE_FILE_PREFIX = "video";
	
	public static final String DATA_PATH = "/data/data/hk.org.msf.android/";
	public static final String NEWS_PATH = "/data/data/hk.org.msf.android/news";
    public static final String BLOG_PATH = "/data/data/hk.org.msf.android/blog";
	public static final String FVISION_PATH = "/data/data/hk.org.msf.android/image";
	public static final String VIDEO_PATH = "/data/data/hk.org.msf.android/videoImage";

	/**
	 * get images from url, store them in local directory:
	 * @param urls passed in for getting images
	 * @param entryType type of the entry, for news, frontline vision or for videos
	 */
	public static void fetchImage(ArrayList<String> urls, String entryType) {
        int numOfNewImages = 0;
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
            newsImages = new ArrayList<Bitmap>();
			numOfNewImages = downloadNewImages(urls, RSSDatabaseHelper.NEWS);
			deleteOutdatedImages(urls, RSSDatabaseHelper.NEWS);
    	    //now read the images from local directory:
            for (int i = 0; i < urls.size(); ++i) {
                //get the images:
                Bitmap b = BitmapFactory.decodeFile(NEWS_PATH + getFileName(urls.get(i)));
                if (b != null) {
                    //compress the images to save memory:
                    b = Bitmap.createScaledBitmap(b, 
                            (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                    //add the images to a list of bitmaps:
                    newsImages.add(b);
                    NewsList.self.addImage(i, b); // new code
                }
            }
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
		    blogImages = new ArrayList<Bitmap>();
            numOfNewImages = downloadNewImages(urls, RSSDatabaseHelper.BLOG);
            deleteOutdatedImages(urls, RSSDatabaseHelper.BLOG);
            // now read the images from local directory
            for (int i = 0; i < urls.size(); i++) {
                // get the images:
            	Bitmap b = BitmapFactory.decodeFile(BLOG_PATH + getFileName(urls.get(i)));
                if (b != null) {
                    // compress the images to save memory:
                	b = Bitmap.createScaledBitmap(b,
                            (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                    // add the images to a list of bitmaps:
                	blogImages.add(b);
                	BlogList.self.addImage(i, b);
                }
            }
        } else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			fVisionImages = new ArrayList<Bitmap>();
			numOfNewImages = downloadNewImages(urls, RSSDatabaseHelper.IMAGE);
			deleteOutdatedImages(urls, RSSDatabaseHelper.IMAGE);
			
			//now read the images from local directory:
			for (int i = numOfNewImages; i < urls.size(); ++i) {
				//get the images:
				Bitmap b = BitmapFactory.decodeFile(FVISION_PATH + getFileName(urls.get(i)));
				if (b != null) {
					//compress the images to save memory:
					b = Bitmap.createScaledBitmap(b, 
							(int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
					//add the images to a list of bitmaps:
					fVisionImages.add(b);
					ImageGrid.self.addImage(i, b); // new code
				}
			}
			
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
		  videoImages = new ArrayList<Bitmap>();
			numOfNewImages = downloadNewImages(urls, RSSDatabaseHelper.VIDEO);
			deleteOutdatedImages(urls, RSSDatabaseHelper.VIDEO);
			
			//now read the images from local directory:
			for (int i = 0; i < urls.size(); ++i) {
				//get the images:
				Bitmap b = BitmapFactory.decodeFile(VIDEO_PATH + getFileName(urls.get(i)));
				if (b != null) {
					b = Bitmap.createScaledBitmap(b, 
							(int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
					//add the images to a list of bitmaps:
					videoImages.add(b);
					VideoList.self.addImage(i, b);
				}
			}
		}
	}
	
	/**
	 * return the ArrayList that contains the bitmaps:
	 * @param entryType specified entry type:
	 * @return ArrayList of images
	 */
	public static ArrayList<Bitmap> getImage(String entryType) {
        if(entryType.equals(RSSDatabaseHelper.NEWS)) {
            return newsImages;
        } else if (entryType.equals(RSSDatabaseHelper.BLOG)) {
        	return blogImages;
        } else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			return fVisionImages;
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			return videoImages;
		} else {
			return null;
		}
	}
	
	/**
	 * Download the new images that are not in the local directory
	 * @param urls The url list storing the urls of images
	 * @param entryType Type of the entry
	 * @return The number of new images that are downloaded
	 * @throws IOException 
	 */
	private static int downloadNewImages(ArrayList<String> urls, String entryType) {
	    int numOfNewImages = 0; // count the number of new images
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			for(int i = 0; i < urls.size(); i++) {
				File file = new File(NEWS_PATH + getFileName(urls.get(i)));
				if(!file.exists()) {
					downloadFromURL(urls.get(i), NEWS_PATH + getFileName(urls.get(i)));
                    Bitmap b = BitmapFactory.decodeFile(NEWS_PATH + getFileName(urls.get(i)));
                    if (b != null) {
                        //compress the images to save memory:
                        b = Bitmap.createScaledBitmap(b, 
                                (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                        //add the images to a list of bitmaps:
                        newsImages.add(b);
                        NewsList.self.addImage(i, b); // new code
                    }
                    numOfNewImages++;
				}
			}
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			for (int i = 0; i < urls.size(); i++) {
                File file = new File(BLOG_PATH + getFileName(urls.get(i)));
                if (!file.exists()) {
                    downloadFromURL(urls.get(i), BLOG_PATH + getFileName(urls.get(i)));
                    Bitmap b = BitmapFactory.decodeFile(BLOG_PATH + getFileName(urls.get(i)));
                    if (b != null) {
                        //compress the images to save memory:
                        b = Bitmap.createScaledBitmap(b, 
                                (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                        //add the images to a list of bitmaps:
                        blogImages.add(b);
                        BlogList.self.addImage(i, b);
                    }
                    numOfNewImages++;
                } 
			}
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			for(int i = 0; i < urls.size(); i++) {
				File file = new File(FVISION_PATH + getFileName(urls.get(i)));
				if(!file.exists()) {
					downloadFromURL(urls.get(i), FVISION_PATH + getFileName(urls.get(i)));
					Bitmap b = BitmapFactory.decodeFile(FVISION_PATH + getFileName(urls.get(i)));
					if (b != null) {
        	            //compress the images to save memory:
                        b = Bitmap.createScaledBitmap(b, 
                          (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                        //add the images to a list of bitmaps:
                        fVisionImages.add(b);
                        ImageGrid.self.addImage(i, b); // new code
	                }
					numOfNewImages++;
				}
			}
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			for(int i = 0; i < urls.size(); i++) {
				File file = new File(VIDEO_PATH + getFileName(urls.get(i)));
				if(!file.exists()) {
   					downloadFromURL(urls.get(i), VIDEO_PATH + getFileName(urls.get(i)));
                    Bitmap b = BitmapFactory.decodeFile(VIDEO_PATH + getFileName(urls.get(i)));
                    if (b != null) {
                        //compress the images to save memory:
                        b = Bitmap.createScaledBitmap(b, 
                                (int)(0.5 * b.getWidth()), (int)(0.5 * b.getHeight()), false);
                        //add the images to a list of bitmaps:
                        videoImages.add(b);
                        VideoList.self.addImage(i, b); // new code
                    }
					numOfNewImages++;
				}
			}
		}
		return numOfNewImages;
	}
	
	/**
	 * Scan the directory storing the images, if found an image not in the url list, delete it
	 * @param urls urls passed in for deleting images
	 * @param entryType type of the entry, for news, frontline vision or for videos
	 */
	private static void deleteOutdatedImages(ArrayList<String> urls, String entryType) {
		
		File dir = new File(DATA_PATH);
		File [] allFiles = dir.listFiles();
		ArrayList<String> filenameList = new ArrayList<String>();
		
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			for(int i = 0; i < urls.size(); i++) {
				filenameList.add(NEWS_IMAGE_FILE_PREFIX + getFileName(urls.get(i))); //make a new list of filenames
			}
			
			for(int i = 0; i < allFiles.length; i++) {
				if(!filenameList.contains(allFiles[i].getName())) { //if not in the url list
					if(isNewsImage(allFiles[i].getName())) { //if is a image file
						allFiles[i].delete(); //then delete it
					}
				}
			}
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
            for (int i = 0; i < urls.size(); i++) {
                filenameList.add(BLOG_IMAGE_FILE_PREFIX + getFileName(urls.get(i))); // make a new list of filenames
            }
            
            for (int i = 0; i < allFiles.length; i++) {
                if (!filenameList.contains(allFiles[i].getName())) {
                    if (isBlogImage(allFiles[i].getName())) {
                        allFiles[i].delete();
                    }
                }
            }
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			
			for(int i = 0; i < urls.size(); i++) {
				filenameList.add(FVISION_IMAGE_FILE_PREFIX + getFileName(urls.get(i))); //make a new list of filenames
			}
			
			for(int i = 0; i < allFiles.length; i++) {
				if(!filenameList.contains(allFiles[i].getName())) { //if not in the url list
					if(isFVImage(allFiles[i].getName())) { //if is a image file
						allFiles[i].delete(); //then delete it
					}
				}
			}
			
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			
			for(int i = 0; i < urls.size(); i++) {
				filenameList.add(VIDEO_IMAGE_FILE_PREFIX + getFileName(urls.get(i))); //make a new list of filenames
			}
			
			for(int i = 0; i < allFiles.length; i++) {
				if(!filenameList.contains(allFiles[i].getName())) { //if not in the url list
					if(isVideoImage(allFiles[i].getName())) { //if is a video image file
						allFiles[i].delete(); //then delete it
					}
				}
			}
		}
	}
	
	public static void deleteImages(String entryType) {
		File dir = new File(DATA_PATH);
		File [] allFiles = dir.listFiles();
		
		if (entryType.equals(RSSDatabaseHelper.BLOG)) {
			for (int i = 0; i < allFiles.length; i++) {
				if (isBlogImage(allFiles[i].getName())) {
					allFiles[i].delete();
				}
			}
		} else if (entryType.equals(RSSDatabaseHelper.VIDEO)) {
			for (int i = 0; i < allFiles.length; i++) {
				if (isVideoImage(allFiles[i].getName())) {
					allFiles[i].delete();
				}
			}
		}
	}
	
	/**
	 * Check whether file or directory is a news image file:
	 * @param filename
	 */
	private static boolean isNewsImage(String filename) {
		return filename.startsWith(NEWS_IMAGE_FILE_PREFIX);
	}
		
	/**
	 * Check whether file or directory is a blog image file:
	 * @param filename
	 */
	private static boolean isBlogImage(String filename) {
		return filename.startsWith(BLOG_IMAGE_FILE_PREFIX);
	}
	
	/**
	 * Check whether file or directory is an image file:
	 * @param filename
	 */
	private static boolean isFVImage(String filename) {
		return filename.startsWith(FVISION_IMAGE_FILE_PREFIX);
	}
	
	/**
	 * Check whether file or directory is a video image file:
	 * @param filename
	 */
	private static boolean isVideoImage(String filename) {
		return filename.startsWith(VIDEO_IMAGE_FILE_PREFIX);
	}
	
	/**
	 * Fetch one image specified by the given url from the internet:
	 * @param address the url specified
	 * @return the input stream that contains the image file:
	 */
	private static InputStream getImageFromWeb(String address)
			throws MalformedURLException, IOException {
		
		URL url = new URL(address);
		HttpGet httpRequest = null;
		try {
			httpRequest = new HttpGet(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream inStream = bufHttpEntity.getContent();
		
		return inStream; //return an input stream containing the image:
	}

	/**
	 * Download the images and store them in a local directory:
	 * @param imageURL the url of the image to get it from web
	 * @param filename the filename of the image to store it in local directory
	 */
	private static void downloadFromURL(String imageURL, String fileName) {

		File file = new File(fileName);
		/**
		 * call the getImageFromWeb method the get the image:
		 */
		try {
			InputStream is = getImageFromWeb(imageURL);
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
	
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
	
			/**
			 * Output the file:
			 */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Get an appropriate file name for the image, since the url is too long, make a shorter one 
	 * with only the parts after the last slash(/)
	 * @param url the URL which is quite long for a filename
	 * @return the filename parsed from the URL
	 */
	private static String getFileName(String url) {

		String filename1 = new String();
		String filename2 = new String();
		for (int i = 0; i < url.length(); ++i) {
			if (url.charAt(i) == '/') {
				filename2 = filename1;
				filename1 = "";
			} else {
				filename1 += url.charAt(i);
			}
		}
		return filename2 + "_" + filename1;
	}
}
