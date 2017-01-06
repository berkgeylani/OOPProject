package similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import database.PaperDAO;
import database.PaperMongo;

public class Jaccard implements SimilarityStrategy {
	

	private static final String COLLECTION_NAME_POJO = "paper";
	private static final String COLLECTION_NAME_STATISTICS = "statistics";
	
    /**
     * Compute jaccard index: |A inter B| / |A union B|.
     * @param firstPaper
     * @param secondPaper
     * @return
     */
	@Override
	public final double similarity(final int firstPaper, final int secondPaper) {
//        Map<String, Integer> profile1 = getProfile(s1);
//        Map<String, Integer> profile2 = getProfile(s2);
    	PaperDAO paperDb = new PaperMongo(COLLECTION_NAME_POJO, COLLECTION_NAME_STATISTICS);
    	Map<String, Integer> profile1 = paperDb.getPaperTrigramStatistics(firstPaper);
    	Map<String, Integer> profile2 = paperDb.getPaperTrigramStatistics(secondPaper);
		
		Set<String> union = new HashSet<String>();
        union.addAll(profile1.keySet());
        union.addAll(profile2.keySet());
//
        int inter = 0;
//
        for (String key : union) {
            if (profile1.containsKey(key) && profile2.containsKey(key)) {
                inter++;
            }
        }
//
        return 1.0 * inter / union.size();
    }

    /**
     * Distance is computed as 1 - similarity.
     * @param s1
     * @param s2
     * @return
     */
    public final double distance(final int s1, final int s2) {
        return 1.0 - similarity(s1, s2);
    }
}
