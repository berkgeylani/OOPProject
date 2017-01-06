package similarity;

public class SimilarityAlghoritm {
	private SimilarityStrategy similarityAlg;

	   public SimilarityAlghoritm(SimilarityStrategy similarityAlg){
	      this.similarityAlg = similarityAlg;
	   }

	   public double calculateSimilarity(int firstPaper, int secondPaper){
	      return similarityAlg.similarity(firstPaper, secondPaper);
	   }
}
