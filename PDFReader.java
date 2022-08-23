import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // Note that Element ?may also be imported from the iTextPDF module

public class PDFReader {
	public static Document HTML_Jsoup_parse(int url_index) {
		URL url = new URL( "https://medicine.nus.edu.sg/edutech/masteringpsychiatry_200108/#/reader/chapter/"
	+ Integer.toString(url_index));
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // cast URLConnection to HttpURLConnection - standard way
		InputStream inputHTML = connection.getInputStream();
        try {
	        connection = (HttpURLConnection) url.openConnection();
	        connection.getResponseCode();
	        connection.connect();
	        // expect HTTP 200 OK, so we don't mistakenly save error report
	        // instead of the file
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	        	throw new LinkNotAccessible("Server returned HTTP " + connection.getResponseCode() 
	            + " " + connection.getResponseMessage());
//	            return "Server returned HTTP " + connection.getResponseCode() 
//	            + " " + connection.getResponseMessage(); 
	        }
	        Document document = Jsoup.parse(inputHTML, "UTF-8");
	        // document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
	        return document;
        } catch (Exception e) {
	        return e.toString();
        } finally {
	        try {
	            if (inputHTML != null)
	            	inputHTML.close();
		    } catch (IOException ignored) {
		    }
            if (connection != null)
            	connection.disconnect();
        	        }
        }
	
	public static Element return_Element(String href, Document document) {
		Element element = document.head()
		return element;
	}
	
	
	public static void main(String[] args) {
		final String outputPdfPath = "E:/LG Documents/JavaProjects/Java_HTMLtoPDF";
		InputStream input = null;
		OutputStream output = null;
        HttpURLConnection connection = null;
        // Download the file
        Document document = HTML_Jsoup_parse(1);
        try {
    		output = new FileOutputStream(outputPdfPath);
        } catch (Exception e) {
	        return e.toString();
        } finally {
	        try {
	            if (output != null)
	                output.close();
		    } catch (IOException ignored) {
		    }
        }
        
	}

}

class LinkNotAccessible extends Exception { 
    public LinkNotAccessible(String errorMessage) {
        super(errorMessage);
    }
}
