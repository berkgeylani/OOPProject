package htmlextractor;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class ExtractorOfOSYM {
	public boolean extract(int maxAttribute) {
		boolean isSuccess = true;
		Elements tableRows;
		if(maxAttribute<1)
			return false;
		try {
			tableRows = Jsoup.connect("https://ais.osym.gov.tr/")
					.userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52")
					.timeout(30000).get().getElementById("tbl_surec").select("tr");
			System.out.println("Sınav/Tercih Adi\tIslem Tipi\tSinavın Tarihi\tIslem Tarihleri\tDurumu");
			for (int i = 0; i < tableRows.size(); i++) {
				Elements eachAttribute = tableRows.get(i).select("td");
				if (eachAttribute.size() > 1) {
					for (int j = 0; j < maxAttribute; j++) {
						System.out.print(eachAttribute.get(j).text() + "\t");
					}
					System.out.println();
				}
			}
		} catch (IOException e) {
			System.err.println("Can't extract data from osym.");
			e.printStackTrace();
			isSuccess=false;
		}
		return isSuccess;
	}
}
