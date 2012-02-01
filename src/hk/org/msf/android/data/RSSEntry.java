package hk.org.msf.android.data;

public class RSSEntry {
	public String entry_type;
	public String title;
	public String content;
	public String date;
	public String url;
	public String otherInfo;
	public String image;
	
	public RSSEntry(String e, String t, String c, String d, String u, String o, String i) {
		entry_type = e;
		title = t;
		content = c;
		date = d;
		url = u;
		otherInfo = o;
		image = i;
	}
}
