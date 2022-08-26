package java_HTMLtoPDF;
import java_HTMLtoPDF.RegexHandler;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // Note that Element ?may also be imported from the iTextPDF module
import org.jsoup.parser.Parser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
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
		final int start_page = 9;
		final int end_page = 9;
		int connection_timeout_millisecond = 3000;
		
		final String path_to_driver = "chromedriver_win32/chromedriver.exe";
		final String XPATH_section_to_print = "//section[@class = \"k-section parsed\"]";
		final long implicit_wait_seconds = 112;
		
		final String cstylesheet_01_loc = "internal.css";
		final String cstylesheet_02_loc = "external.css";
		
		// NOTE: currentprojectPath + cstylesheet_02_loc AND "file://" + currentprojectPath + cstylesheet_02_loc DOES NOT WORK
		System.out.println("Internal css path: " + currentprojectPath + cstylesheet_01_loc);
		final String css_internal = readUsingFiles(currentprojectPath + cstylesheet_01_loc);
		System.out.println("*****Read internal css File to String Using Files Class*****\n" + css_internal);
		final String css_external = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "src/main/java/java_HTMLtoPDF/" + cstylesheet_02_loc + "\" media=\"print\"></link>"; //maybe can append "?c=21902" // type=\"text/css\" //  + "</link>" doesn't work 
		
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
//		chromeOptions.addArguments("disable-infobars"); // disabling infobars
//		chromeOptions.addArguments("--disable-extensions"); // disabling extensions
//		chromeOptions.addArguments("--disable-gpu"); // applicable to windows os only
//		chromeOptions.addArguments("--no-sandbox"); // Bypass OS security model
		ChromeDriver driver = new ChromeDriver(chromeOptions);
		driver.manage().window().maximize();
		driver.switchTo();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit_wait_seconds)); // See https://www.selenium.dev/documentation/webdriver/waits/
		//REFERENCE FOR VARIABLES: htmlToPDF_fromToPages(ChromeDriver driver, int start_page, int end_page, String base_url, String currentprojectPath, String css_internal, String css_external, String XPATH_section_to_print)
		htmlToPDF_fromToPages(driver, start_page, end_page, base_url, currentprojectPath, css_internal, css_external, XPATH_section_to_print);
	}
	
	
	public static void htmlToPDF_fromToPages(ChromeDriver driver, int start_page, int end_page, String base_url, String currentprojectPath, String css_internal, String css_external, String XPATH_section_to_print) {
		for (int i = start_page; i < (end_page + 1); i++) {
			driver.get(base_url + Integer.toString(start_page));
			WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(999)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_section_to_print)));
			
			Document html = Jsoup.parse(firstResult.getAttribute("outerHTML"), "", Parser.htmlParser()); // See https://www.browserstack.com/guide/get-html-source-of-web-element-in-selenium-webdriver
//			Document html_01 = HTML_insertbefore(html, css_internal);
			Document html_02 = HTML_insertbefore(html, css_external);
			
			html_02.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // XHMTL format; fixes no /link end tag error. See 
			System.out.println("HTML Document:\n" + html_02.html()); // OR Document html = Jsoup.parse(driver.getPageSource()); // getting HTML code from ChromeDriver
			
//			String html_replaced_inline_html_01 = RegexHandler.replace_inline_css_without_class(html_02.html(), "body", "color:blue");
//			String html_replaced_inline_html = RegexHandler.replace_inline_css_with_class(html_02.html(), "div", "color:red", "videoWrapper");
//			String html_replaced_inline_html_02 = RegexHandler.replace_inline_css_without_class(html_replaced_inline_html_01, )
//			System.out.println("html_replaced_inline_html_02:\n" + html_replaced_inline_html_02); 
			String reformatted_str = RegexHandler.reformat_text(html_02.html()); // REPLACED html_replaced_inline_html_01
			createPdfFile(reformatted_str,  currentprojectPath + "page " + Integer.toString(i) + ".pdf");
		}
		
//        // Download the file using url
//        Document document = HTML_Jsoup_parse(page_number, connection_timeout_millisecond, base_url);
//        Element element = document.head().selectFirst(section_to_output);
//        createPdfFile(element.toString(), currentprojectPath + date_now_string + Pdf_filetype);
		// Offline parsing works but gives <Can't load the XML resource (using TrAX transformer)> error - due to css file not found - tested by removing all lines in text editor with "css" involved.
//		Document document = HTML_offline_parse(inputHTMLPath, base_url); // test with https://stackoverflow.com/questions/12043035/html-to-pdf-using-itext-external-css
//		createPdfFile(document.toString(), currentprojectPath + date_now_string + Pdf_filetype);
		driver.quit();
	}
	
	
	private static void createPdfFile(String html_content_in_string, String fileName) {
		OutputStream outputStream = null;
		try { // Note that we're wrapping our code in a try block to ensure the output stream is closed - see https://www.baeldung.com/java-html-to-pdf
			outputStream = new FileOutputStream(fileName);
    		ITextRenderer renderer = new ITextRenderer();
    	    SharedContext sharedContext = renderer.getSharedContext();
    	    sharedContext.setPrint(true);
    	    sharedContext.setInteractive(false);
    	    renderer.setDocumentFromString(html_content_in_string); // changed from renderer.setDocument(uri)
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
