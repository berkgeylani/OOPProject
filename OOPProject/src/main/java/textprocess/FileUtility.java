package textprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtility {
	public ArrayList<Integer> readCSV(String csvFile) {
		if(csvFile==null || csvFile.isEmpty())
			return null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<Integer> paperId = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i == 0) {
					i++;
					continue;
				}
				// use comma as separator
				String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				paperId.add(new Integer(row[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paperId;
	}

	public String[] searchCSV(String csvFile, String id) {
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<Integer> paperId = new ArrayList<>();
		String[] searchedRow = null;
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i == 0) {
					i++;
					continue;
				}
				// use comma as separator
				String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				if (row[0].equals(id)) {
					searchedRow = row;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchedRow;
	}
}
