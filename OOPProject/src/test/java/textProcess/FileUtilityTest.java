package textProcess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import textProcessUtils.FileUtilityTestConstans;
import textprocess.FileUtility;

public class FileUtilityTest {

	@Test
	public void readCSVInvalidInvalidNullInput() {
		// Setup
		FileUtility fileUtility = new FileUtility();
		// Assert
		assertEquals(null, fileUtility.readCSV(null));
	}

	@Test
	public void readCSVInvalidInvalidEmptyInput() {
		// Setup
		FileUtility fileUtility = new FileUtility();
		// Assert
		assertEquals(null, fileUtility.readCSV(""));
	}
	
	@Test
	public void readCSVInvalidInvalidInput() {
		// Setup
		FileUtility fileUtility = new FileUtility();
		// Assert
		assertEquals(FileUtilityTestConstans.READ_CSV_OUTPUT, fileUtility.readCSV("/home/francium/Downloads/ACM.csv").toString());
	}

}
