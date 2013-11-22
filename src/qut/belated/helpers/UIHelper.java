package qut.belated.helpers;

public class UIHelper {
	
	
	private static final char NO_SPACE_LINE_BREAK = '\u00A0';
	public static String ensureNoLineBreaks(String text)
	{
		text = text.replace('\n', NO_SPACE_LINE_BREAK);
		return text.replace(' ', NO_SPACE_LINE_BREAK);
	}
}
