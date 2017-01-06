package similarity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import database.PaperDAO;
import database.PaperMongo;

public class SorensenDice implements SimilarityStrategy {


	private static final String COLLECTION_NAME_POJO = "paper";
	private static final String COLLECTION_NAME_STATISTICS = "statistics";
	
    /**
     * Similarity is computed as 2 * |A inter B| / (|A| + |B|).
     * @param firstPaper
     * @param secondPaper
     * @return
     */
	@Override
    public final double similarity(final int firstPaper, final int secondPaper) {
    	PaperDAO paperDb = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);
    	Map<String, Integer> profile1 = paperDb.getPaperTrigramStatistics(firstPaper);
    	Map<String, Integer> profile2 = paperDb.getPaperTrigramStatistics(secondPaper);
		
		Set<String> union = new HashSet<String>();
        union.addAll(profile1.keySet());
        union.addAll(profile2.keySet());

        int inter = 0;

        for (String key : union) {
            if (profile1.containsKey(key) && profile2.containsKey(key)) {
                inter++;
            }
        }

        return 2.0 * inter / (profile1.size() + profile2.size());
    }

    public double distance(int s1, int s2) {
        return 1 - similarity(s1, s2);
    }
}