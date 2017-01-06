package textprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import database.PaperDAO;
import database.PaperMongo;
import database.PaperPOJO;
import htmlextractor.ExtractorOfOSYM;
import similarity.SimilarityAlghoritm;
import similarity.SorensenDice;

public class Main {
	private static final String COLLECTION_NAME_POJO = "paper";
	private static final String COLLECTION_NAME_STATISTICS = "statistics";
	private static PaperDAO paperDAO = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);

	public static void main(String[] args) throws IOException {
		InitializeHelper initializeHelper = new InitializeHelper();
		initializeHelper.initialize();
		// makale alındı su anda kullanıcıya makalelerin gösterilmesi
		PaperDAO db = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);
		Scanner reader = new Scanner(System.in);
		System.out.println("1-)En sık geçen trigramları listele\n" + "2-)Id'ye göre arama yapmak için\n"
				+ "3-)Keyworde gore arama yapmak için\n" + "4-)Authora göre arama yapmak için\n"
				+ "5-)Yıla göre arama yapmak için(>1900)\n" + "6-)Osym de bulunan sınavlar ile ilgil bilgi için");
		switch (reader.nextInt()) {
		case 1:
			System.out.println("En sık geçen 30 trigram:\n");
			ArrayList<Map<String, Integer>> mapCarrier = new ArrayList<>();
			List<PaperPOJO> allPapers = db.getAllPapers();
			for (int i = 0; i < allPapers.size(); i++) {
				mapCarrier.add(db.getPaperWordStatistics(allPapers.get(i).getId()));
			}
			initializeHelper.mergeMaps(mapCarrier).entrySet().stream() // streame
																		// çevir
					.sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).limit(25).forEach(System.out::println);
			// trigramlar alınacak merge edlip yazılcak.
			break;
		case 2:
			System.out.println("İstediğiniz projenin id'sini yazınız.");
			int paperId = reader.nextInt();
			PaperPOJO findPaper = paperDAO.findPaper(paperId);
			System.out.println("Paper Id: \t" + findPaper.getId());
			System.out.println("Title: \t" + findPaper.getTitle());
			System.out.println("Author: \t" + findPaper.getAuthor());
			System.out.println("Content: \t" + findPaper.getContent());
			System.out.println("Benzer Makaleler: \n");
			initializeHelper.findMost5SimilarPaper(paperId);
			break;
		case 3:
			System.out.println("Aramak istediğiniz keywordu yazınız.");
			String searchedKeyword = reader.next();
			Map<Integer, Integer> findByKeyWord = db.findByKeyWord(searchedKeyword);
			System.out.println("Aranan keywordun sırasıyla makale id'si, \"" + searchedKeyword
					+ "\" kelimesinin kaç kez geçtiği.");
			System.out.println("Id\tCount");
			findByKeyWord.forEach((k, v) -> {
				System.out.println(k + "\t" + v);
			});
			break;
		case 4:
			System.out.println("Aramak istediğiniz ismi yazınız.");
			String paperAuthor = reader.next();
			ArrayList<PaperPOJO> findPaperByAtrributesAuthor = db.findPaperByAtrributes(paperAuthor, -1);
			for (int i = 0; i < findPaperByAtrributesAuthor.size(); i++) {
				System.out.println("Id:\t" + findPaperByAtrributesAuthor.get(i).getId() + "\tTitle:\t"
						+ findPaperByAtrributesAuthor.get(i).getTitle());
			}
			break;
		case 5:
			System.out.println("Aramak istediğiniz yılı yazınız.");
			int paperYear = reader.nextInt();
			ArrayList<PaperPOJO> findPaperByAtrributesYear = db.findPaperByAtrributes(null, paperYear);
			for (int i = 0; i < findPaperByAtrributesYear.size(); i++) {
				System.out.println("Id" + findPaperByAtrributesYear.get(i).getId() + "Title:\t"
						+ findPaperByAtrributesYear.get(i).getTitle() + "\t"
						+ findPaperByAtrributesYear.get(i).getAuthor());
			}
			break;
		case 6:
			new ExtractorOfOSYM().extract(5);
		default:
			System.out.println("çıkıldı.");
			break;
		}

	}

}
