package java_HTMLtoPDF;
import java_HTMLtoPDF.RegexHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // Note that Element ?may also be imported from the iTextPDF module
import org.jsoup.parser.Parser;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFReader {
	public static void main(String[] args) {
		// NOTE: Use the right version of ChromeDriver - it must match your version in Google Chrome. See https://chromedriver.chromium.org/downloads/version-selection
		LocalDateTime date_now_utc = LocalDateTime.now();
		final String date_now_string = date_now_utc.format(DateTimeFormatter.ofPattern("dd-MM-yy HH-mm-ss"));
		System.out.println("Date now is " + date_now_string);
		
//		final String inputHTMLPath = "E:/LG Documents/JavaProjects/tempHTMLs/102.html";
		final String currentprojectPath = System.getProperty("user.dir") + "/src/main/java/java_HTMLtoPDF/";
		System.out.println("currentprojectPath = \n" + currentprojectPath);
		final String cssquery_section_to_output = "section.k-section parsed";
		String base_url = "https://medicine.nus.edu.sg/edutech/masteringpsychiatry_200108/#/reader/chapter/";
		final int start_page = 310;
		final int end_page = 310;
		int connection_timeout_millisecond = 3000;
		
		final String path_to_driver = "chromedriver_win32/chromedriver.exe";
		final String XPATH_section_to_print = "//section[@class = \"k-section parsed\"]";
		final long implicit_wait_seconds = 112;
		
		// NOTE: currentprojectPath + cstylesheet_02_loc AND "file://" + currentprojectPath + cstylesheet_02_loc DOES NOT WORK
		System.out.println("Internal css path: " + currentprojectPath + "internal.css");
		final String css_internal = readUsingFiles(currentprojectPath + "internal.css");
		System.out.println("*****Read internal css File to String Using Files Class*****\n" + css_internal);
		
		// See https://webscraping.pro/java-selenium-headless-chrome-jsoup-to-scrape-data-of-the-web/
		System.setProperty("webdriver.chrome.driver", currentprojectPath + path_to_driver);
		
		// Set up the driver
		ChromeOptions chromeOptions = new ChromeOptions();
//		chromeOptions.setBinary("/path/to/other/chrome/binary");
//		chromeOptions.addArguments("--headless");
//		chromeOptions.addArguments("--enable-javascript");
//		chromeOptions.addArguments("lang=en");
		// I have added some arguments to chromeOptions in the code. The driver threw exceptions without them.
//		chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
//		chromeOptions.addArguments("start-maximized"); // open Browser in maximized mode
		chromeOptions.addArguments("disable-infobars"); // disabling infobars
		chromeOptions.addArguments("--disable-extensions"); // disabling extensions
		chromeOptions.addArguments("--disable-gpu"); // applicable to windows os only
		chromeOptions.addArguments("--no-sandbox"); // Bypass OS security model
		ChromeDriver driver = new ChromeDriver(chromeOptions);
		driver.manage().window().maximize();
		driver.switchTo();
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit_wait_seconds)); // See https://www.selenium.dev/documentation/webdriver/waits/ // Mixing explicit waits and implicit waits will cause unintended consequences, namely waits sleeping for the maximum time even if the element is available or condition is true.
		//REFERENCE FOR VARIABLES: htmlToPDF_fromToPages(ChromeDriver driver, int start_page, int end_page, String base_url, String currentprojectPath, String css_internal, String css_external, String XPATH_section_to_print)
		htmlToPDF_fromToPages(driver, start_page, end_page, base_url, currentprojectPath, XPATH_section_to_print);
	}
	
	public static String get_external_css_filepath(int page_number) { // external css are named by the following template: external - Chp<integer>.css
		String css_external = "";
		if (0 <= page_number && page_number <= 5) {
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp0" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (6 <= page_number && page_number <= 18) {
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp1" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (19 <= page_number && page_number <= 30) {
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (31 <= page_number && page_number <= 56) {
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp3" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (57 <= page_number && page_number <= 100) {
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp4" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (101 <= page_number && page_number <= 124) { // chp5
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp1" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (125 <= page_number && page_number <= 146) { // chp6
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (147 <= page_number && page_number <= 198) { // chp7
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp3" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (199 <= page_number && page_number <= 210) { // chp8
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp4" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (211 <= page_number && page_number <= 220) { // chp9
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp1" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (221 <= page_number && page_number <= 241) { // chp10
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (242 <= page_number && page_number <= 261) { // chp11
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp3" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (262 <= page_number && page_number <= 284) { // chp12
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp4" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (285 <= page_number && page_number <= 298) { // chp13
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp1" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (299 <= page_number && page_number <= 309) { // chp14
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (page_number == 310) { // chp14
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2pg310" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (311 <= page_number && page_number <= 318) { // chp14
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (319 <= page_number && page_number <= 336) { // chp15
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp3" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (337 <= page_number && page_number <= 393) { // chp16
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp4" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (394 <= page_number && page_number <= 410) { // chp17
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp1" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (411 <= page_number && page_number <= 420) { // chp18
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp2" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (421 <= page_number && page_number <= 430) { // chp19
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp3" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		else if (431 <= page_number && page_number <= 450) { // chp20
			css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/externalChp4" + ".css\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		}
		return css_external;
	}
	
	public static String padLeftZeros(String inputString, int length) {
	    if (inputString.length() >= length) {
	        return inputString;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < length - inputString.length()) {
	        sb.append('0');
	    }
	    sb.append(inputString);

	    return sb.toString();
	}
	
	public static void htmlToPDF_fromToPages(ChromeDriver driver, int start_page, int end_page, String base_url, String currentprojectPath, String XPATH_section_to_print) {
		WebElement firstResult;
		for (int i = start_page; i <=end_page; i++) {
			System.out.println("\n----NEW PAGE: Current page being printed: Page " + i + "\n");
			try {
				driver.get(base_url + Integer.toString(i));
				TimeUnit.SECONDS.sleep(2);
				try 
			    { // to deal with the alerts
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    System.out.println("Alert was present and accepted");
			    }   // try 
			    catch (NoAlertPresentException Ex) { 
			    	System.out.println("\nAlert not found! continuing with script");
			    }   // catch 
				firstResult = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_section_to_print)));
			} catch (Exception e) {
				e.printStackTrace();
				i--; // as continue will go on to the next iteration of the loop
				System.out.println("\nException thrown, i value after min using 1: Page " + i + "\n");
				driver.navigate().refresh();
				continue;
			}
			Document html = Jsoup.parse(firstResult.getAttribute("outerHTML"), "", Parser.htmlParser()); // See https://www.browserstack.com/guide/get-html-source-of-web-element-in-selenium-webdriver
//			Document html_with_css_internal = HTML_insertbefore(html, css_internal);
			
			String css_external = get_external_css_filepath(i);
			Document html_with_css_external = HTML_insertbefore(html, css_external);
			
			html_with_css_external.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // XHMTL format; fixes no /link end tag error. See 
			System.out.println("HTML Document:\n" + html_with_css_external.html()); // OR Document html = Jsoup.parse(driver.getPageSource()); // getting HTML code from ChromeDriver
			
//			String html_replaced_inline_html_01 = RegexHandler.replace_inline_css_without_class(html_02.html(), "body", "color:blue");
//			String html_replaced_inline_html = RegexHandler.replace_inline_css_with_class(html_02.html(), "div", "color:red", "videoWrapper");
//			String html_replaced_inline_html_02 = RegexHandler.replace_inline_css_without_class(html_with_css_external.html(), "p", "font-family: Arial, sans-serif;");
//			System.out.println("html_replaced_inline_html_02:\n" + html_replaced_inline_html_02); 
			String reformatted_str = RegexHandler.reformat_text(html_with_css_external.html()); // REPLACED html_replaced_inline_html_01
			createPdfFile(reformatted_str,  currentprojectPath + "Page " + padLeftZeros(Integer.toString(i), 3) + ".pdf");
		}
		
//        // Download the file using url
//        Document document = HTML_Jsoup_parse(page_number, connection_timeout_millisecond, base_url);
//        Element element = document.head().selectFirst(section_to_output);
//        createPdfFile(element.toString(), currentprojectPath + date_now_string + Pdf_filetype);
		// Offline parsing works but gives <Can't load the XML resource (using TrAX transformer)> error - due to css file not found - tested by removing all lines in text editor with "css" involved.
//		Document document = HTML_offline_parse(inputHTMLPath, base_url); // test with https://stackoverflow.com/questions/12043035/html-to-pdf-using-itext-external-css
//		createPdfFile(document.toString(), currentprojectPath + date_now_string + Pdf_filetype);
	}
	
	
	private static void createPdfFile(String html_content_in_string, String fileName) {
		OutputStream outputStream = null;
		try { // Note that we're wrapping our code in a try block to ensure the output stream is closed - see https://www.baeldung.com/java-html-to-pdf
			
			// BAD CODE: You do NOT need to use a custom Helvetica, unless you're replacing e.g. greek words which does not get replaced with the Helvetica in-built in iText5.
			// See https://stackoverflow.com/questions/66966573/flyingsaucerpdf-greek-alphabet-helvetica for how to add fonts
		    final String FONT_DIR = "src/main/java/java_HTMLtoPDF/fonts/";
		    final String[] FONT_FILES = {  //
		            "Helvetica.ttf", "Helvetica-Bold.ttf", //
		            "Helvetica-BoldOblique.ttf","helvetica-compressed-5871d14b6903a.otf", // 
		            "helvetica-light-587ebe5a59211.ttf", "Helvetica-Oblique.ttf", //
		            "helvetica-rounded-bold-5871d05ead8de.otf" };
			
			outputStream = new FileOutputStream(fileName);
    		ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontRes = renderer.getFontResolver(); // impt for custom fonts! (i.e. not the default fonts like Arial and Times New Roman)
    		
    		// removing the preloaded Helvetica font
    		Field fontFagliesField = ITextFontResolver.class.getDeclaredField("_fontFamilies");
            fontFagliesField.setAccessible(true);
            Map<String, ?> fontFamiliesMap = (Map<String, ?>) fontFagliesField.get(fontRes);
            fontFamiliesMap.remove("Helvetica");
    		
            // BAD CODE: You do NOT need to use a custom Helvetica, unless you're replacing e.g. greek words which does not get replaced with the Helvetica in-built in iText5.
            // After that, I added a new Helvetica font
    		loadFont(fontRes, FONT_FILES, FONT_DIR);
            
            
    	    SharedContext sharedContext = renderer.getSharedContext();
    	    sharedContext.setPrint(true);
    	    sharedContext.setInteractive(false);
    	    
    	    renderer.setDocumentFromString(html_content_in_string); // changed from renderer.setDocument(uri) // Critical thing to note here was, that the base url supplied to setDocumentAsString must be a URI (file:/folder/structure/static/), and must end with a trailing slash ('/'). Otherwise the file and url doesn't get resolved correctly. see https://www.generacodice.com/en/articolo/4178098/font-face-with-flying-saucer
    	    
    	    renderer.layout();
    	    renderer.createPDF(outputStream);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
	        try {
	            if (outputStream != null)
	            	outputStream.close();
		    } catch (IOException ignored) {
		    }
        }
	    System.out.println( "PDF file: '" + fileName + "' created." );
	    }
	
	private static void loadFont(ITextFontResolver poF, String[] FONT_FILES, String FONT_DIR) throws Exception {
        for (String lsFontFile : FONT_FILES) {
            File lfFile = new File(FONT_DIR + lsFontFile);
//        	BaseFont base = BaseFont.createFont(FONT_DIR + lsFontFile, BaseFont.IDENTITY_H, true); // TODO: delete
//            System.out.println("Absolute path: " + lfFile.getAbsolutePath());
            poF.addFont(lfFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED); // Default fonts like Arial and Helvetica cannot be embedded by default (i.e. the font is stored as code for pdf file readers to interpret. However, now we can as we have the .ttf files.
        }
    }
	
	private static Document HTML_insertbefore(Document document, String css_code) {
		Element head = document.head();
		head.append(css_code); // OR document.selectFirst("html").child(0).before(css_code);
		return document;
	}
	
//	private static Document HTML_offline_parse(String fileName, String basefileName) {
//	Document document = null;
//	try {
//        document = Jsoup.parse(String(fileName), basefileName); 
//     // See https://stackoverflow.com/questions/65959750/does-outputsettings-charset-also-change-meta-content-type for the functions below
//        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // specify that we want XML output // As the next step, we'll use jsoup to convert the above HTML file to a jsoup Document to render XHTML.
////        document.charset(StandardCharsets.UTF_8); // update the charset - also adds the <?xml encoding> instruction
////        document.select("meta[content~=charset]").remove(); // Remove the obsolete HTML meta tags
//    } catch (IOException ignored) {
//    }
	
//        System.out.println("\n---------------Document parsed:---------------\n" + document.toString());
//    return document;
//    }

//private static Document HTML_Jsoup_parse(int url_index, int connection_timeout_millisecond, String base_url) {
//	URL url = null;
//	try {
//		url = new URL( base_url
//+ Integer.toString(url_index));
//		System.out.println(url.toString());
//	} catch (MalformedURLException e) {
//		e.printStackTrace();
//	}
//	String url_string = base_url
//+ Integer.toString(url_index);
//	Document document = null;
//	InputStream inputHTML = null;
//	HttpURLConnection connection = null;
//    try {
//    	// TODO uncomment
////    	connection = (HttpURLConnection) url.openConnection(); // cast URLConnection to HttpURLConnection - standard way
////		inputHTML = connection.getInputStream();
////        connection = (HttpURLConnection) url.openConnection();
////        connection.getResponseCode();
////        connection.connect();
////        // expect HTTP 200 OK, so we don't mistakenly save error report
////        // instead of the file
////        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
////        	throw new LinkNotAccessible("Server returned HTTP " + connection.getResponseCode() 
////            + " " + connection.getResponseMessage());
//////            return "Server returned HTTP " + connection.getResponseCode() 
//////            + " " + connection.getResponseMessage(); 
////        }
//        Map<String, String> cookies_map = new HashMap();
//        cookies_map.put("incap_ses_1220_2116053", "KTaVUOsIJnbEwWUrT1DuEJP3BGMAAAAAvhaU/6tN11Xm7F+GWi8VdA==");
//        cookies_map.put("incap_ses_165_2116053", "dVbmAe5YASAYkV3VsjJKAsv1BGMAAAAAUmVR9ifid72ZeTElbQeu3A==");
//        cookies_map.put("incap_ses_165_1988262", "yIU4OoOXrwiJkV3VsjJKAsv1BGMAAAAABfz6XB7fGw/lrX3iElQCHQ==");
//        cookies_map.put("incap_ses_968_2116053", "IIVZeigRC03U3i+zhgdvDcT2BGMAAAAAoqSqU3x3jexjYrxyPkGfBQ==");
//        cookies_map.put("nlbi_1988262_2049781", "XDJeMsEYV0NcijDal1sq3wAAAAAOm9JO9a2+cNt46gsR7EKp");
//        cookies_map.put("nlbi_1988262", "fBnKQBVMwkWQ3lMJgBguRAAAAABScCSapleh35kmpYyck3cL");
//        cookies_map.put("nlbi_1988262_1963038", "GXRjExWAaxoAyjWBl1sq3wAAAAAtJ/gJdAQJrskHPNyiXR/P");
//        cookies_map.put("nlbi_1988262_1968855", "Z5bPEGDJdwfAR1Bvl1sq3wAAAAD1PBIamJ2vvBt6LZAGiKk3");
//        cookies_map.put("nlbi_2116053", "poKlItY6KBnPUUMnF0PJmAAAAAAydDXCD6nWSFsRKFvTLcOf");
//        cookies_map.put("nlbi_2246921", "Fgk4CRlYex5b5EzNDuMRywAAAAAPcSbAVh2C0tCmF0j6T+fL");
//        cookies_map.put("nlbi_2208316", "Iw2rfS8GKktdFYSHzupxbQAAAABA3toTfejqFTeaTF8ybibo");
//        cookies_map.put("nlbi_2116053_1969743", "+6qUFzkO4h6k0T9dDckrsQAAAABLVknzD+u65gQlq/54fIgS");
        
        // NEW PROBLEM: Jsoup cannot access URL as we would need to be able to run Kotobee reader web app. "The Kotobee reader web application needs to be run through a web server" error. - FIXED WITH CHROMEDRIVER & SELENIUM
//        document = Jsoup.connect(url_string)
        		// pretend to be a human to bypass the captcha. See https://stackoverflow.com/questions/38338418/java-jsoup-html-parsing-robot-index-bot-detection-noindex
//        		.header("cookie", "incap_ses_1220_2116053=dVbmAe5YASAYkV3VsjJKAsv1BGMAAAAAUmVR9ifid72ZeTElbQeu3A==")
//        		.cookies(cookies_map)
//        		.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36") 
//        		.timeout(connection_timeout_millisecond)
//        		.get(); // IO Exception thrown // see https://jsoup.org/cookbook/input/load-document-from-url for other cool Connection modifiers to build specific requests: .data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(3000).post(); 
        // Note: Jsoup.parse is only used if you have an offline html file
//        document = Jsoup.parse(inputHTML.toString(), "UTF-8");	
//    } catch (Exception e) {
//    	e.printStackTrace();
//    } finally {
//        try {
//            if (inputHTML != null)
//            	inputHTML.close();
//	    } catch (IOException ignored) {
//	    }
//        if (connection != null)
//        	connection.disconnect();
//    	        }
        
     // See https://stackoverflow.com/questions/65959750/does-outputsettings-charset-also-change-meta-content-type for the functions below
//        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // specify that we want XML output // As the next step, we'll use jsoup to convert the above HTML file to a jsoup Document to render XHTML.
//        document.charset(StandardCharsets.UTF_8); // update the charset - also adds the <?xml encoding> instruction
//        document.select("meta[content~=charset]").remove(); // Remove the obsolete HTML meta tags
//        System.out.println("\n---------------Document connected:---------------\n" + document.toString());
//    return document;
//}
	
	private static String readUsingFiles(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

//class LinkNotAccessible extends Exception { 
//    public LinkNotAccessible(String errorMessage) {
//        super(errorMessage);
//    }
//}
