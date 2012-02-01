package hk.org.msf.android.utils;

public class Miscellaneous {
	/**
	 * limit the length of the title
	 */
	public static String teaseTitle (String title) {
		String [] words = title.split(" ");
		if (words.length > 10) {
            title = "";
			for (int i = 0; i < 10; i++) {
				title += words[i] + " ";
			}
			title += "...";
		}
		return title;
	}
}