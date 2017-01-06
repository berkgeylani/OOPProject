package textprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PdfTextExtractor {
	protected  void getDownloadLink(int id) throws IOException {
		String url = "http://dl.acm.org/citation.cfm?id="+id;
		Document websiteAsHtmlCurrent = Jsoup.connect(url)
				.userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52")
				.get();
		String urlOfPDf = websiteAsHtmlCurrent.getElementsByClass("medium-text").select("a").attr("href");
		pdfDownload("http://dl.acm.org/"+urlOfPDf);
	}
	
	protected  void pdfDownload(String Url) throws IOException {
		try{System.out.println("opening connection");
		URL url = new URL(Url);
        URLConnection hc = url.openConnection();
        hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        InputStream in = hc.getInputStream();
		FileOutputStream fos = new FileOutputStream(new File(Url.split("=")[1]+".pdf"));
		System.out.println("reading from resource and writing to file...");
		int length = -1;
		byte[] buffer = new byte[1024];// buffer for portion of data from connection
		while ((length = in.read(buffer)) > -1) {
		    fos.write(buffer, 0, length);
		}
		fos.close();
		in.close();
		System.out.println("File downloaded");}catch (Exception e) {
		}
	}
	
	protected  String pdfTextExtractor(File filePath) throws IOException {
		PDDocument document = PDDocument.load(filePath);
		PDFTextStripper pdfTextStripper = new PDFTextStripper();
		String pdfContent = pdfTextStripper.getText(document);
		document.close();
		return pdfContent;
	}
}
