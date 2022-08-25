package java_HTMLtoPDF;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.delicacies.matching.InvalidDataException;

class RegexHandler { // no need public as it's the same package as Graphing.java where it's used
	// The consolidated function that does everything we need
	public static String reformat_text(String string_to_reformat) {
//		String test = "€Št,\nâ ˜ rœédiš? c 2fé™"; // TESTED WORKING: \n, whitespace, i'll i'm I'm don't wasn't wouldn't i'd I'd it's it'd who's what's that's aren't we're 'derp' '"' + "derp" + '"'
		String test2 = remove_nbsp(string_to_reformat);
//    	String test3 = replace_comma(test2);
//    	String test4 = replace_dash(test3);
//    	String test5 = replace_single_inverted_comma(test4);
//    	String test6 = replace_double_inverted_comma(test5);
//    	String test7 = convert_UTF8(test6); // remove nonASCII after converting to UTF8, as there may be some extra weird characters
//    	String test8 = remove_nonASCII(test7);
    	System.out.println("Reformatted_output: " + test2);
    	return test2;
	    }
	
	
	// Note: the below function removes all class tags if needed
	public static String replace_inline_css_without_class(String string_to_reformat, String tag_name, String css_code) {
		String replaced_01 = "";
		String replaced_02 = "";
		
		// Add a space -> for easier replaceAll later (shortens 1 step)
		final String setup_regex = "(<" + tag_name + ">)";
		final String setup_regex_replacement = "(<" + tag_name + " >)";
		
		replaced_01 = string_to_reformat.replaceAll(setup_regex, setup_regex_replacement);
		System.out.println("\nreplaced_01" + replaced_01);
		
		final String regex_already_have_styles = "(?<=\\<" + tag_name + " )([^<]*?)(?=\\>)"; // instead of "\\b)(.*?)(?=\\>)" // Note: ([^<]*?) means all except <; because (.*?) would have still captured ">Videos</h1" in "<h1>Videos</h1>"
		final String already_have_styles_replacement = "style=\"" + css_code + "\"";
		Pattern p1 = Pattern.compile(regex_already_have_styles);
		Matcher m1 = p1.matcher(replaced_01);
		if (m1.find()) {
			replaced_02 = replaced_01.replaceAll(regex_already_have_styles, already_have_styles_replacement);
			String matched_strings = m1.group();
			System.out.println("\n" + m1.groupCount() + " Old " + tag_name + " styles: \n" + matched_strings);
			System.out.println("\n" + m1.groupCount() + " New " + tag_name + " styles: \n" + already_have_styles_replacement);
		} else {
			replaced_02 = replaced_01;
		}
		return replaced_02;
	}
	
	public static String replace_inline_css_with_class(String string_to_reformat, String tag_name, String css_code, String class_name) {
		String replaced_01 = "";
		String replaced_02 = "";
		
		// Add a space -> for easier replaceAll later (shortens 1 step)
		final String regex_already_have_classes_without_style = "(<" + tag_name + " class=\"" + class_name + "\">)";
		final String already_have_classes_without_style_replacement = "(<" + tag_name + " class=\"" + class_name + "\" >)";
		
		replaced_01 = string_to_reformat.replaceAll(regex_already_have_classes_without_style, already_have_classes_without_style_replacement);
		System.out.println("\nreplaced_01" + replaced_01);
		
		final String regex_already_have_classes_with_style = "(?<=<" + tag_name + " class=\"" + class_name + "\" )([^<]*?)(?=>)"; // instead of "\\b)(.*?)(?=\\>)"
		final String already_have_classes_replacement = "style=\"" + css_code + "\"";
		
		Pattern p1 = Pattern.compile(regex_already_have_classes_with_style);
		Matcher m1 = p1.matcher(replaced_01);
		if (m1.find()) { // Do not use .matches() - see https://stackoverflow.com/questions/4450045/difference-between-matches-and-find-in-java-regex
			replaced_02 = replaced_01.replaceAll(regex_already_have_classes_with_style, already_have_classes_replacement);
			String matched_strings = m1.group();
			System.out.println("\n" + m1.groupCount() + " Old " + tag_name + " styles with class" + class_name + " styles: \n" + matched_strings);
			System.out.println("\n" + m1.groupCount() + " New " + tag_name + " styles with class" + class_name + " styles: \n" + already_have_classes_replacement);
		} else {
			replaced_02 = replaced_01;
		}
		return replaced_02;
	}
	
	
	// The component functions that make up the function above
	// Regex to handle	
		final static String UTF_setting = "UTF-8";
		// See https://en.wikipedia.org/wiki/ASCII#Printable_characters for ASCII Charcter Set
		// Note: \x0a is "\n" (line feed), and \x21 is " " (whitespace).
		final static String regex_nbsp = "(?:&nbsp)";
		
		final static String regex_dash_filter = "\\x2d"; // - ; — is \\u2014, which will be removed by the ASCII filter function later
		final static String regex_dash_replacement = ""; // we will eventually replace all "A2B5" back to dashes when uploading into bot
		
		final static String regex_comma_filter = "\\x2c";
		final static String regex_comma_replacement = "A3B1"; // we will eventually replace all "A3B1" back to commas when uploading into bot
		
		final static String regex_single_inverted_comma_filter = "[\\x27?]"; // added "?" because once you save as CSV from XLSX, some single inverted commas convert into "?"
		
		final static String regex_double_inverted_comma_filter = "\\x22";
		final static String regex_double_inverted_comma_replacement = "A2B5";
		
		public static String remove_nbsp(String string_to_reformat) {
			String replaced = string_to_reformat.replaceAll(regex_nbsp, ""); // 2nd argument is the replacement text for each block/letter that matches
//			System.out.println("Replaced_nonASCII_output: " + replaced);
				return replaced;
		    }
}
