package hk.org.msf.android.utils;

import hk.org.msf.android.R;
import hk.org.msf.android.data.RSSDatabase;
import hk.org.msf.android.data.RSSDatabaseHelper;
import hk.org.msf.android.data.RSSEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

/**
 * The XMLParser parses XML files for news, blogs, images and videos on YouTube using XMLPullParser
 */
public abstract class XMLParser {
	
	protected static RSSDatabase db;
	
	private static String newsFeedURL;
	private static String blogFeedURL;
	private static String imageFeedURL;
	private static String youtubeVideoFeedURL;
	
	/**
	 * The public function of the XMLParser, parse the XML files
	 * @param a instance of a Context used to instansiate the database
	 * @param the specified type of the entry
	 */
	public static void parseRSSFeeds(Context context, String entryType)
			throws URISyntaxException, ClientProtocolException, IOException, XmlPullParserException {
		
		db = RSSDatabase.getDatabaseInstance(context);
		
        newsFeedURL = context.getResources().getString(R.string.msf_newsurl);
        blogFeedURL = context.getResources().getString(R.string.msf_blogsurl);
        imageFeedURL = context.getResources().getString(R.string.msf_imagesurl);
		youtubeVideoFeedURL = context.getResources().getString(R.string.youtube_video_url);
		
		InputStream is = null;
		
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			is = getInputStream(newsFeedURL);
			parseMSFNews(is);
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			is = getInputStream(blogFeedURL);
			parseMSFBlogs(is);
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			is = getInputStream(imageFeedURL);
			parseMSFImages(is);
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			is = getInputStream(youtubeVideoFeedURL);
			parseYouTubeEntries(is);
		}
		is.close();
	}

	/**
	 * Make a HttpGet and get the input stream of a RSS URL
	 * @param the URL of the page to be parsed
	 * @return an InputStream containing the page
	 */
    private static InputStream getInputStream(String url) throws ClientProtocolException, IOException {
    	
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpget = new HttpGet(url);
    	HttpResponse response = httpclient.execute(httpget);
    	return response.getEntity().getContent();
    }

    /**
     * Parse the news on the MSF website
     * @param the InputStream containing the page
     */
	private static void parseMSFNews(InputStream is) throws XmlPullParserException, IOException {
		
		int depth = 0;
		String parserName = null;
		
		String title = null;
		String link = null;
		String date = null;
		String content = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new InputStreamReader(is));
		
		for(int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
			depth = parser.getDepth();
			try {
				switch(e) {
				case XmlPullParser.START_TAG:
					parserName = parser.getName();
					break;
				case XmlPullParser.TEXT:
					String value = parser.getText();
					if(depth == 4) {
						if(parserName.equals("title")) {
							title = value;
						} else if(parserName.equals("link")) {
							link = value;
						} else if(parserName.equals("pubDate")) {
							date = parseDate(value, RSSDatabaseHelper.NEWS);
						} else if(parserName.equals("description")) {
							content = value;
						}
					}
					break;
				}
				if(title != null && link != null && date != null && content != null) {
					db.insertRSSEntry(new RSSEntry(RSSDatabaseHelper.NEWS, title, content,
							date, "http://www.msf.org.hk/" + link, 
							"", parseForNewsImage(content)));
					title = null;
					link = null;
					date = null;
					content = null;
				}					
			} catch(Exception ex) {

			}
		}
	}
	
    /**
     * Parse the blogs on the MSF website
     * @param the InputStream containing the page
     */
	private static void parseMSFBlogs(InputStream is) throws XmlPullParserException, IOException {
		
		int depth = 0;
		String parserName = null;
		
		String title = null;
		String link = null;
		String date = null;
		String author = null;
		String content = null;
		String image = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new InputStreamReader(is));
		
		for(int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
			depth = parser.getDepth();
			try {
				switch(e) {
				case XmlPullParser.START_TAG:
					parserName = parser.getName();
					break;
				case XmlPullParser.TEXT:
					String value = parser.getText();
					if(depth == 4) {
						if(parserName.equals("title")) {
							title = value;
						} else if(parserName.equals("link")) {
							link = value;
						} else if(parserName.equals("encoded")) {
							content = value;
                            if (content.contains("img")) {
                                String pattern = "<img(.*)? src=\"(.*)?\" alt=";
                                Pattern re = Pattern.compile(pattern);
                                Matcher m = re.matcher(content);
                                if (m.find()) {
                                    image = m.group(2);
                                }
                            }
						} else if(parserName.equals("pubDate")) {
							date = parseDate(value, RSSDatabaseHelper.BLOG);
						} else if(parserName.equals("creator")) {
							author = value;
						}
					}
					break;
				}
				if(title != null && link != null && date != null && author != null && content != null) {
					db.insertRSSEntry(new RSSEntry(RSSDatabaseHelper.BLOG, title, content,
							date, link, author, image));
					title = null;
					link = null;
					date = null;
					author = null;
					content = null;
					image = null;
				}
			} catch(Exception ex) {}
		}
	}
	
    /**
     * Parse the images on the MSF website
     * @param the InputStream containing the page
     */
	private static void parseMSFImages(InputStream is) throws XmlPullParserException, IOException {
		
		int depth = 0;
		String parserName = null;
		
		String title = null;
		String link = null;
		String date = null;
		String content = null;
		String image = null;
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new InputStreamReader(is));
		
		for(int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
			depth = parser.getDepth();
			try {
				switch(e) {
				case XmlPullParser.START_TAG:
					parserName = parser.getName();
					break;
				case XmlPullParser.TEXT:
					String value = parser.getText();
					if(depth == 4) {
						if(parserName.equals("title")) {
							title = value;
						} else if(parserName.equals("link")) {
							link = value;
						} else if(parserName.equals("description")) {
							content = value;
						} else if(parserName.equals("pubDate")) {
							date = parseDate(value, RSSDatabaseHelper.IMAGE);
						} else if(parserName.equals("encoded")) {
							image = parseForImageURL(value);
						}
					}
					break;
				}
				if(title != null && link != null && date != null && content != null && image != null) {
					db.insertRSSEntry(new RSSEntry(RSSDatabaseHelper.IMAGE, title, content,
							date, link, "", image));
					title = null;
					link = null;
					date = null;
					content = null;
					image = null;
				}
			} catch(Exception ex) {

			}
		}
	}
	
    /**
     * Parse the videos on YouTube
     * @param the InputStream containing the page
     */
	private static void parseYouTubeEntries(InputStream is) throws XmlPullParserException, IOException {
		
		int depth = 0;
		String date = null;
		String title = null;
		String player = null;
		String thumbNail = null;
		
		String parserName = null;
		
		String backupTitle = "";
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new InputStreamReader(is));
		
		for(int e = parser.getEventType(); e != XmlPullParser.END_DOCUMENT; e = parser.next()) {
			depth = parser.getDepth();
			try {
				switch(e) {
				case XmlPullParser.START_TAG:
					parserName = parser.getName();
					if(depth == 4) {
						if(parserName.equals("player")) {
							player = parser.getAttributeValue(null, "url");
						} else if(parserName.equals("thumbnail")) {
							thumbNail = parser.getAttributeValue(null, "url");
							if(!title.equals(backupTitle)) {
								String youtubeID = parseForYouTubeID(player);
								db.insertRSSEntry(new RSSEntry(RSSDatabaseHelper.VIDEO, title, youtubeID,
										date, "http://www.youtube.com/watch?v=" + youtubeID, "", thumbNail));
							}
							backupTitle = title;
							continue;
						}
					}
				break;
				case XmlPullParser.TEXT:
					String value = parser.getText();
					if(depth == 3) {
						if(parserName.equals("published")) {
							date = parseDate(value, RSSDatabaseHelper.VIDEO);
						} else if(parserName.equals("title")) {
							title = value;
						}
					}
					break;
				}
			} catch(Exception ex) {
				
			}
		}
	}
	
	/**
	 * Parse the image URL from a block of text
	 * @param the block of text containing the image URL
	 * @return the image URL parsed
	 */
	private static String parseForNewsImage(String content) {
		
		String pattern = "images/press/s(.*)." +
				"(jpg|JPG|jpeg|JPEG|png|bmp)";
		Pattern re = Pattern.compile(pattern);
		Matcher m = re.matcher(content);
		if(m.find()) {
			String s = "http://www.msf.org.hk/" + m.group(0);
			String [] ss = s.split("\"");
			return ss[0];
		}
		return null;
	}
	
	/**
	 * This method is used only for the URLs of image, because the image URL does not have explicit
	 * field that can be used without parsing. Thus this method applied regular expression to get the
	 * URL from a complicated string.
	 * @param a string that contents the URL of the image
	 * @return the URL of the image
	 */
	private static String parseForImageURL(String content) {
		
		String pattern = "http://www.msf.org.hk/photo/wp-content/uploads/" +
				"(.*).(jpg|JPG|jpeg|JPEG|png|bmp)";
		Pattern re = Pattern.compile(pattern);
		Matcher m = re.matcher(content);
		if(m.find()) {
			String [] ss = m.group(0).split("\"");
			return ss[0];
		}
		return null;
	}
	
	/**
	 * parse the YouTube player to get the YouTube ID
	 * @param player the YouTube URL of the webpage which displays the video
	 * @return the parsed ID of the video
	 */
	private static String parseForYouTubeID(String player) {
		
		String [] ss = player.split("v=");
		String [] ss2 = ss[1].split("&feature");
		return ss2[0];
	}
	
	/**
	 * This method parse the date and format its according the entry type
	 * @param the original date string fetched from the webpage
	 * @param the type of the entry
	 * @return the formatted string representing the date
	 */
	private static String parseDate(String date, String entryType) {
		
		if(entryType.equals(RSSDatabaseHelper.NEWS)) {
			//get rid of the redundant info: time and time zone
			String pattern = "(.*) 20[0-9][0-9]";
			Pattern re = Pattern.compile(pattern);
			
			Matcher m = re.matcher(date);
			if(m.find()) {
				date = m.group(0);
			} else {
				return date;
			}
			return ConvertDateInLetterMonthFormat(date);
			
		} else if(entryType.equals(RSSDatabaseHelper.BLOG)) {
			String pattern = "(.*) 20[0-9][0-9]";
			Pattern re = Pattern.compile(pattern);
			
			Matcher m = re.matcher(date);
			if(m.find()) {
				return m.group(0);
			} else {
				return date;
			}
			
		} else if(entryType.equals(RSSDatabaseHelper.IMAGE)) {
			String pattern = "(.*) 20[0-9][0-9]";
			Pattern re = Pattern.compile(pattern);
			
			Matcher m = re.matcher(date);
			if(m.find()) {
				return m.group(0);
			} else {
				return date;
			}
			
		} else if(entryType.equals(RSSDatabaseHelper.VIDEO)) {
			
			String pattern = "(.*)-(.*)-(.*)T";
			Pattern re = Pattern.compile(pattern);
			Matcher m = re.matcher(date);
			
			if(m.find()) {
				return ConvertDateInLetterMonthFormat(m.group(3) + " " + m.group(2) + " " + m.group(1));
			} else {
				return date;
			}
		}
		return null;
	}
	
	/**
	 * Convert the date from "09 08 2011" format to "09 Aug 2011" format
	 * @param date formatted in "09 08 2011" style
	 * @return date formatted in "09 Aug 2011" style
	 */
	private static String ConvertDateInLetterMonthFormat(String date) {
		
		InputStream is = new ByteArrayInputStream(date.getBytes());
		Scanner scanner = new Scanner(is);
		String dayStr = scanner.next();
		
		int month = Integer.parseInt(scanner.next());
		
		String monthStr = new String();
		
		switch(month) {
		case 1:
			monthStr = "Jan";
			break;
		case 2:
			monthStr = "Feb";
			break;
		case 3:
			monthStr = "Mar";
			break;
		case 4:
			monthStr = "Apr";
			break;
		case 5:
			monthStr = "May";
			break;
		case 6:
			monthStr = "Jun";
			break;
		case 7:
			monthStr = "Jul";
			break;
		case 8:
			monthStr = "Aug";
			break;
		case 9:
			monthStr = "Sep";
			break;
		case 10:
			monthStr = "Oct";
			break;
		case 11:
			monthStr = "Nov";
			break;
		case 12:
			monthStr = "Dec";
			break;
		default:
			monthStr = String.valueOf(month);	
		}
		String yearStr = scanner.next();
		
		return dayStr + " " + monthStr + " " + yearStr;
	}
}
