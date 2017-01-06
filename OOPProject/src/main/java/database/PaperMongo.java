package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

public class PaperMongo implements PaperDAO {
	private static MongoClient mongoClient_INSTANCE;

	private static final String LOCALHOST = "localhost";

	private static final String MONGO_DB_NAME = "mydb";

	private String collectionNamePOJO;

	private String collectionNameStatistics;

	/**
	 * how to use? insertOne -> herhangi fonksiyon
	 * getCollection(mongoClient).insertOne
	 * 
	 * @param client
	 * @return
	 */
	public MongoCollection<Document> getCollection(MongoClient client, String mongoCollectionName) {
		MongoCollection<Document> collection = client.getDatabase(MONGO_DB_NAME).getCollection(mongoCollectionName);
		return collection;
	}

	public static MongoClient getInstance() {
		if (mongoClient_INSTANCE == null) {
			synchronized (MongoClient.class) {
				if (mongoClient_INSTANCE == null) { // yes double check
					mongoClient_INSTANCE = new MongoClient(new ServerAddress(LOCALHOST));
				}
			}
		}
		return mongoClient_INSTANCE;
	}

	public PaperMongo(String collectionNamePojo, String collectionNameStatistics) {
		this.collectionNamePOJO = collectionNamePojo;
		this.collectionNameStatistics = collectionNameStatistics;
		// getCollection(getInstance(),this.collectionNamePOJO).drop();
		// getCollection(getInstance(),this.collectionNameStatistics).drop();
	}

	@Override
	public void addStatisticsAsJson(int paperId, Map<String, Integer> trigramMap, Map<String, Integer> wordList)
			throws MongoException {
		getCollection(getInstance(), this.collectionNameStatistics).insertOne(new Document().append("_id", paperId)
				.append("wordList", wordList).append("triGramStatistic", trigramMap));
	}

	private ArrayList<PaperPOJO> findIterableToArrayListForPaperInfo(FindIterable<Document> iterable) {
		ArrayList<PaperPOJO> dataAl = new ArrayList<>();
		MongoCursor<Document> cursor = iterable.iterator();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			dataAl.add(new PaperPOJO(document.getInteger("_id"), document.getString("title"),
					document.getString("authors"), document.getString("venue"), document.getInteger("year"),
					document.getString("content")));
		}
		return dataAl;
	}

	private List<Document> findIterableToArrayListForPaperStatistics(FindIterable<Document> iterable) {
		List<Document> dataAl = new ArrayList<>();
		MongoCursor<Document> cursor = iterable.iterator();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			dataAl.add(document);
		}
		return dataAl;
	}

	@Override
	public void addPaper(PaperPOJO paperPojo) throws MongoException {
		MongoClient mongoClient = getInstance();
		String author = paperPojo.getAuthor();
		getCollection(mongoClient, this.collectionNamePOJO).insertOne(new Document().append("_id", paperPojo.getId())
				.append("title", paperPojo.getTitle()).append("authors", author).append("venue", paperPojo.getVenue())
				.append("year", paperPojo.getYear()).append("content", paperPojo.getContent()));
	}

	@Override
	public List<PaperPOJO> getAllPapers() {
		MongoClient mongoClient = getInstance();
		ArrayList<PaperPOJO> allPapers = findIterableToArrayListForPaperInfo(
				getCollection(mongoClient, this.collectionNamePOJO).find());
		return allPapers;
	}

	// @Override
	// public void updatePaper(PaperPOJO paperPojo) {
	//
	// }

	@Override
	public void deletePaper(int paperId) {
		MongoClient mongoClient = getInstance();
		Document filter = new Document().append("_id", paperId);
		DeleteResult deleteResult = getCollection(mongoClient, this.collectionNamePOJO).deleteOne(filter);
	}

	@Override
	public PaperPOJO findPaper(int paperId) {
		MongoClient mongoClient = getInstance();
		Document filter = new Document().append("_id", paperId);
		ArrayList<PaperPOJO> findIterableToArrayList = findIterableToArrayListForPaperInfo(
				getCollection(mongoClient, this.collectionNamePOJO).find(filter));
		return findIterableToArrayList.get(0);
	}
	@Override
	public Map<Integer, Integer> findByKeyWord(String keyword) {
		MongoClient mongoClient = getInstance();
		Map<Integer, Integer> searchedWordMap = new HashMap<>();
        Map<Integer, Integer> result = new LinkedHashMap<>();
		if (keyword != null && !keyword.isEmpty()) {
			List<Document> findIterableToArrayListForPaperStatistics = findIterableToArrayListForPaperStatistics(
					getCollection(mongoClient, this.collectionNameStatistics).find());
			for (int i = 0; i < findIterableToArrayListForPaperStatistics.size(); i++) {
				Document document = findIterableToArrayListForPaperStatistics.get(i);
				Map<String, Integer> wordMap = jsonObjectToMap(
						new JsonParser().parse(document.toJson()).getAsJsonObject().get("wordList").getAsJsonObject());
				if (wordMap.containsKey(keyword)) {
					searchedWordMap.put(document.getInteger("_id"), wordMap.get(keyword));
				}
			}
		}
		searchedWordMap.entrySet().stream() // streame çevir
		.sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
		.limit(5)
		.forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
		return result;
	}
	@Override
	public ArrayList<PaperPOJO> findPaperByAtrributes(String name, int year) {
		ArrayList<PaperPOJO> findIterableToArrayList = null;
		MongoClient mongoClient = getInstance();
		Document filter = new Document();
		boolean shouldBeFiltered = false;
		if (name != null && !name.isEmpty()) {
			shouldBeFiltered = true;
			filter.append("authors", Pattern.compile(Pattern.quote(name)));
		}
		if (year > 1900) {
			filter.append("year", year);
			shouldBeFiltered = true;
		}
		if (shouldBeFiltered) {
			findIterableToArrayList = findIterableToArrayListForPaperInfo(
					getCollection(mongoClient, this.collectionNamePOJO).find(filter));
		}
		return findIterableToArrayList;
	}

	public Map<String, Integer> jsonObjectToMap(JsonObject jsonObject) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		Iterator<Entry<String, JsonElement>> keysItr = jsonObject.entrySet().iterator();
		while (keysItr.hasNext()) {
			Entry<String, JsonElement> jsonAttribute = keysItr.next();
			map.put(jsonAttribute.getKey(), jsonAttribute.getValue().getAsInt());
		}
		return map;
	}

	public ArrayList<String> JsonArraytoArrayList(JsonArray jsonArray) {
		ArrayList<String> wordList = new ArrayList<>();
		int count = jsonArray.size();
		for (int i = 0; i < count; i++) {
			wordList.add(jsonArray.get(i).getAsString());
		}
		return wordList;
	}

	@Override
	public Map<String, Integer> getPaperTrigramStatistics(int id) {
		// burada trigram ını alıcaz.
		MongoClient mongoClient = getInstance();
		Document filter = new Document().append("_id", id);
		Document findIterableToArrayListForPaperStatistics = findIterableToArrayListForPaperStatistics(
				getCollection(mongoClient, this.collectionNameStatistics).find(filter)).get(0);
		Map<String, Integer> map = jsonObjectToMap(
				new JsonParser().parse(findIterableToArrayListForPaperStatistics.toJson()).getAsJsonObject()
						.get("triGramStatistic").getAsJsonObject());

		return map;
	}

	@Override
	public Map<String, Integer> getPaperWordStatistics(int id) {
		// burada trigram ını alıcaz.
		MongoClient mongoClient = getInstance();
		Document filter = new Document().append("_id", id);
		Document findIterableToArrayListForPaperStatistics = findIterableToArrayListForPaperStatistics(
				getCollection(mongoClient, this.collectionNameStatistics).find(filter)).get(0);
		return jsonObjectToMap(new JsonParser().parse(findIterableToArrayListForPaperStatistics.toJson())
				.getAsJsonObject().get("wordList").getAsJsonObject());
	}

	@Override
	public void clearDatabase() {
		MongoClient mongoClient = getInstance();
		getCollection(mongoClient, this.collectionNamePOJO).drop();
		getCollection(mongoClient, this.collectionNameStatistics).drop();
	}

}
