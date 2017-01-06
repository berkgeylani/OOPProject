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
import java.util.Set;

import database.PaperDAO;
import database.PaperMongo;
import database.PaperPOJO;
import similarity.SimilarityAlghoritm;
import similarity.SorensenDice;

public class InitializeHelper {
	private static final String CSV_FILE_PATH = "/home/francium/Downloads/ACM.csv";
	private static final String COLLECTION_NAME_POJO = "paper";
	private static final String COLLECTION_NAME_STATISTICS = "statistics";
	private static PaperDAO paperDAO = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);

	public Map<String, Integer> mergeMaps(ArrayList<Map<String, Integer>> maps) {
		Set<String> keys = new HashSet<String>();
		for (Map<String, Integer> map : maps)
			keys.addAll(map.keySet());

		Map<String, Integer> result = new HashMap<String, Integer>();
		for (String key : keys) {
			Integer value = 0;
			for (Map<String, Integer> map : maps)
				if (map.containsKey(key))
					value += map.get(key);
			result.put(key, value);
		}
		return result;
	}

	public void findMost5SimilarPaper(int searchingId) {
		PaperDAO db = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);
		List<PaperPOJO> allPapers = db.getAllPapers();
		Map<Integer, Double> similarityMap = new HashMap<>();
		SimilarityAlghoritm similarityAlghoritm = new SimilarityAlghoritm(new SorensenDice());
		for (int i = 0; i < allPapers.size(); i++) {
			int id = allPapers.get(i).getId();
			similarityMap.put(id, similarityAlghoritm.calculateSimilarity(searchingId, id));
		}
		similarityMap.entrySet().stream() // streame çevir
				.sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).limit(5).forEach((k) -> {
					if (k.getValue() != 1)
						System.out
								.println(k.getKey() /* + ": " + k.getValue() */);
				});
	}

	public void initialize() throws IOException {
		paperDAO.clearDatabase();
		Random rn = new Random();
		File file = new File("/home/francium/Desktop/pdfPapers/idAtached");
		File[] listFiles = file.listFiles();
		FileUtility fileUtility = new FileUtility();
		PdfTextExtractor pdfTextExtractor = new PdfTextExtractor();
		// ArrayList<Integer> readCSV = fileUtility.readCSV(CSV_FILE_PATH);
		// for (Integer integer : readCSV) {
		// pdfTextExtractor.getDownloadLink(integer.intValue());
		// }
		String pdfContent;
		int count = listFiles.length < 10 ? listFiles.length : 10;
		for (int i = 0; i < count; i++) {
			// 0 dan 10 a 10 farklı rakam seçmeli
			File filePath = listFiles[rn.nextInt(listFiles.length)];
			String pdfId = filePath.getName().split("\\.")[0];
			pdfContent = pdfTextExtractor.pdfTextExtractor(filePath);
			String[] searchRow = fileUtility.searchCSV(CSV_FILE_PATH, pdfId);
			// "Id","title","authors","venue","year" <-- content ekleyip pojoya
			// eklencek
			PaperPOJO paperPOJO = new PaperPOJO(new Integer(searchRow[0]).intValue(), searchRow[1], searchRow[2],
					searchRow[3], new Integer(searchRow[4]), pdfContent);
			try {
				paperDAO.addPaper(paperPOJO);
				// pdf paperpojonun içersine kaydedildi.
				// content dahil.
				StatisticsCreator statisticCreator = new StatisticsCreator(paperPOJO.getContent());
				// şu anda mongoya gidip eklemesi kaldı istatistikleri
				Map<String, Integer> result = new LinkedHashMap<>();
				statisticCreator.getWordList().entrySet().stream() // streame
																	// çevir
						.sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).limit(30)
						.forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
				paperDAO.addStatisticsAsJson(new Integer(pdfId).intValue(), statisticCreator.getTriGramList(), result);
				System.out.println(i + "\t" + pdfId);
			} catch (Exception e) {
				i--;
				continue;
			}
		}
	}
}
