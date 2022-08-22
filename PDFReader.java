import java.net.HttpURLConnection;
import java.net.URL;

import sun.net.www.URLConnection;

public class PDFReader {
	public static Document HTML_Jsoup_parse(int url_index) {
		URL url = new URL( "https://medicine.nus.edu.sg/edutech/masteringpsychiatry_200108/#/reader/chapter/"
	+ (String) url_index);
		
		URLConnection connection = url.openConnection();
        try {
		    URL url = new URL("http://example.com/file.pdf");
	        connection = (HttpURLConnection) url.openConnection();
	        connection.connect();
	        String inputHTML = connection.getInputStream();
	        // expect HTTP 200 OK, so we don't mistakenly save error report
	        // instead of the file
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	            return "Server returned HTTP " + connection.getResponseCode() 
	            + " " + connection.getResponseMessage(); 
	        }
	        Document document = Jsoup.parse(inputHTML, "UTF-8");
	        // document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
	        return document;
        } catch (Exception e) {
	        return e.toString();
        } finally {
	        try {
	            if (input != null)
	                input.close();
		    } catch (IOException ignored) {
		    }
    
            if (connection != null)
            	connection.disconnect();
        	        }
        }
	
	
	public static void main(String[] args) {
		final String outputPdfPath = "E:\LG Documents\JavaProjects\Java_HTMLtoPDF";³
		InputStream input = null;
		OutputStream output = null;
        HttpURLConnection connection = null;
        // Download the file
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
