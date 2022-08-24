import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // Note that Element ?may also be imported from the iTextPDF module
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PDFReader {
	public static void main(String[] args) {
		// NOTE: Use the right version of ChromeDriver - it must match your version in Google Chrome. See https://chromedriver.chromium.org/downloads/version-selection
		LocalDateTime date_now_utc = LocalDateTime.now();
		final String date_now_string = date_now_utc.format(DateTimeFormatter.ofPattern("dd-MM-yy HH-mm-ss"));
		System.out.println("Date now is " + date_now_string);
		
		final String inputHTMLPath = "E:/LG Documents/JavaProjects/tempHTMLs/102.html";
		final String outputPdfPath = "E:/LG Documents/JavaProjects/Java_HTMLtoPDF/";
		final String cssquery_section_to_output = "section.k-section parsed";
		final String Pdf_filetype = ".pdf";
		String base_url = "https://medicine.nus.edu.sg/edutech/masteringpsychiatry_200108/#/reader/chapter/";
		int page_number = 112;
		int connection_timeout_millisecond = 3000; 
		
		final String path_to_driver = "C:/Users/elgen/Documents/JavaProjects/Java_HTMLtoPDF/chromedriver_win32/chromedriver.exe";
		final String XPATH_section_to_print = "//section[@class = \"k-section parsed\"]";
		final long implicit_wait_seconds = 1;
		
		// See https://webscraping.pro/java-selenium-headless-chrome-jsoup-to-scrape-data-of-the-web/
		System.setProperty("webdriver.chrome.driver", path_to_driver);
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
		driver.get(base_url + Integer.toString(page_number));
		WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(999)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_section_to_print)));
		
		Document html = Jsoup.parse(firstResult.getAttribute("outerHTML")); // See https://www.browserstack.com/guide/get-html-source-of-web-element-in-selenium-webdriver
//		Document html = Jsoup.parse(driver.getPageSource()); // getting HTML code from ChromeDriver
		System.out.println("HTML Document:\n" + html.toString());
//		Element htmlelement = html.selectFirst(cssquery_section_to_output);
		createPdfFile(html.toString(), outputPdfPath + date_now_string + Pdf_filetype);
		
//        // Download the file
//        Document document = HTML_Jsoup_parse(page_number, connection_timeout_millisecond, base_url);
//        Element element = document_select_section(document, section_to_output);
//        createPdfFile(element.toString(), outputPdfPath + date_now_string + Pdf_filetype);
		
		// FOR TESTING ONLY
		// Offline parsing works but gives <Can't load the XML resource (using TrAX transformer)> error - due to css file not found - tested by removing all lines in text editor with "css" involved.
		
//		Document document = HTML_offline_parse(inputHTMLPath, base_url); // test with https://stackoverflow.com/questions/12043035/html-to-pdf-using-itext-external-css
//		createPdfFile(document.toString(), outputPdfPath + date_now_string + Pdf_filetype);
		driver.quit();
	}
	
	
//	private static Document HTML_offline_parse(String fileName, String basefileName) {
//		Document document = null;
//		try {
//	        document = Jsoup.parse(String(fileName), basefileName); 
//	     // See https://stackoverflow.com/questions/65959750/does-outputsettings-charset-also-change-meta-content-type for the functions below
//	        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // specify that we want XML output // As the next step, we'll use jsoup to convert the above HTML file to a jsoup Document to render XHTML.
////	        document.charset(StandardCharsets.UTF_8); // update the charset - also adds the <?xml encoding> instruction
////	        document.select("meta[content~=charset]").remove(); // Remove the obsolete HTML meta tags
//        } catch (IOException ignored) {
//        }
//	        System.out.println("\n---------------Document parsed:---------------\n" + document.toString());
//        return document;
//        }
	
	private static Document HTML_Jsoup_parse(int url_index, int connection_timeout_millisecond, String base_url) {
		URL url = null;
		try {
			url = new URL( base_url
+ Integer.toString(url_index));
			System.out.println(url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String url_string = base_url
+ Integer.toString(url_index);
		Document document = null;
		InputStream inputHTML = null;
		HttpURLConnection connection = null;
        try {
        	// TODO uncomment
//        	connection = (HttpURLConnection) url.openConnection(); // cast URLConnection to HttpURLConnection - standard way
//			inputHTML = connection.getInputStream();
//	        connection = (HttpURLConnection) url.openConnection();
//	        connection.getResponseCode();
//	        connection.connect();
//	        // expect HTTP 200 OK, so we don't mistakenly save error report
//	        // instead of the file
//	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//	        	throw new LinkNotAccessible("Server returned HTTP " + connection.getResponseCode() 
//	            + " " + connection.getResponseMessage());
////	            return "Server returned HTTP " + connection.getResponseCode() 
////	            + " " + connection.getResponseMessage(); 
//	        }
	        Map<String, String> cookies_map = new HashMap();
	        cookies_map.put("incap_ses_1220_2116053", "KTaVUOsIJnbEwWUrT1DuEJP3BGMAAAAAvhaU/6tN11Xm7F+GWi8VdA==");
	        cookies_map.put("incap_ses_165_2116053", "dVbmAe5YASAYkV3VsjJKAsv1BGMAAAAAUmVR9ifid72ZeTElbQeu3A==");
	        cookies_map.put("incap_ses_165_1988262", "yIU4OoOXrwiJkV3VsjJKAsv1BGMAAAAABfz6XB7fGw/lrX3iElQCHQ==");
	        cookies_map.put("incap_ses_968_2116053", "IIVZeigRC03U3i+zhgdvDcT2BGMAAAAAoqSqU3x3jexjYrxyPkGfBQ==");
	        cookies_map.put("nlbi_1988262_2049781", "XDJeMsEYV0NcijDal1sq3wAAAAAOm9JO9a2+cNt46gsR7EKp");
	        cookies_map.put("nlbi_1988262", "fBnKQBVMwkWQ3lMJgBguRAAAAABScCSapleh35kmpYyck3cL");
	        cookies_map.put("nlbi_1988262_1963038", "GXRjExWAaxoAyjWBl1sq3wAAAAAtJ/gJdAQJrskHPNyiXR/P");
	        cookies_map.put("nlbi_1988262_1968855", "Z5bPEGDJdwfAR1Bvl1sq3wAAAAD1PBIamJ2vvBt6LZAGiKk3");
	        cookies_map.put("nlbi_2116053", "poKlItY6KBnPUUMnF0PJmAAAAAAydDXCD6nWSFsRKFvTLcOf");
	        cookies_map.put("nlbi_2246921", "Fgk4CRlYex5b5EzNDuMRywAAAAAPcSbAVh2C0tCmF0j6T+fL");
	        cookies_map.put("nlbi_2208316", "Iw2rfS8GKktdFYSHzupxbQAAAABA3toTfejqFTeaTF8ybibo");
	        cookies_map.put("nlbi_2116053_1969743", "+6qUFzkO4h6k0T9dDckrsQAAAABLVknzD+u65gQlq/54fIgS");
	        
	        // NEW PROBLEM TODO: Jsoup cannot access URL as we would need to be able to run Kotobee reader web app. "The Kotobee reader web application needs to be run through a web server" error.
	        document = Jsoup.connect(url_string)
	        		// pretend to be a human to bypass the captcha. See https://stackoverflow.com/questions/38338418/java-jsoup-html-parsing-robot-index-bot-detection-noindex
//	        		.header("cookie", "incap_ses_1220_2116053=dVbmAe5YASAYkV3VsjJKAsv1BGMAAAAAUmVR9ifid72ZeTElbQeu3A==")
	        		.cookies(cookies_map)
	        		.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36") 
	        		.timeout(connection_timeout_millisecond)
	        		.get(); // IO Exception thrown // see https://jsoup.org/cookbook/input/load-document-from-url for other cool Connection modifiers to build specific requests: .data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(3000).post(); 
	        // Note: Jsoup.parse is only used if you have an offline html file
//	        document = Jsoup.parse(inputHTML.toString(), "UTF-8");
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
	        try {
	            if (inputHTML != null)
	            	inputHTML.close();
		    } catch (IOException ignored) {
		    }
            if (connection != null)
            	connection.disconnect();
        	        }
	        
	     // See https://stackoverflow.com/questions/65959750/does-outputsettings-charset-also-change-meta-content-type for the functions below
	        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml); // specify that we want XML output // As the next step, we'll use jsoup to convert the above HTML file to a jsoup Document to render XHTML.
//	        document.charset(StandardCharsets.UTF_8); // update the charset - also adds the <?xml encoding> instruction
//	        document.select("meta[content~=charset]").remove(); // Remove the obsolete HTML meta tags
	        System.out.println("\n---------------Document connected:---------------\n" + document.toString());
        return document;
	}
	
	private static Element document_select_section(Document document, String html_section) {
		Element element = document.head().selectFirst(html_section);
		return element;
	}
	
	private static void createPdfFile(String url, String fileName) {
		OutputStream outputStream = null;
		try { // Note that we're wrapping our code in a try block to ensure the output stream is closed - see https://www.baeldung.com/java-html-to-pdf
			outputStream = new FileOutputStream(fileName);
    		ITextRenderer renderer = new ITextRenderer();
    	    SharedContext sharedContext = renderer.getSharedContext();
    	    sharedContext.setPrint(true);
    	    sharedContext.setInteractive(false);
    	    renderer.setDocument(url); // changed from renderer.setDocumentFromString(htmldoc); with input String htmldoc
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
}

class LinkNotAccessible extends Exception { 
    public LinkNotAccessible(String errorMessage) {
        super(errorMessage);
    }
}
