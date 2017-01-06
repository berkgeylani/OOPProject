package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PaperDAO {
	public List<PaperPOJO> getAllPapers();
	public void addPaper(PaperPOJO paperPojo);
//	public void updatePaper(PaperPOJO paperPojo);
	public void deletePaper(int paperId);
	public PaperPOJO findPaper(int id);
	public void addStatisticsAsJson(int paperId,Map<String, Integer> trigramMap,Map<String, Integer> wordList);
	public Map<String, Integer> getPaperTrigramStatistics(int id);
	public Map<String, Integer> getPaperWordStatistics(int id);
	public void clearDatabase();
	public ArrayList<PaperPOJO> findPaperByAtrributes(String name, int year);
	public Map<Integer, Integer> findByKeyWord(String keyword);
}
