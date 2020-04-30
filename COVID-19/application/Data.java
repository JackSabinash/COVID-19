package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class Data to hold arrays of the data to be used by the main program from csv files.
 * 
 * @param String filename is a param for the instructor for the filename in the local directory 
 * containing the csv in the correct format.
 */
public class Data {
  String filename;
  ArrayList<String> dates;
  ArrayList<Country> countryList;

  Data(String filename) throws IOException {
    dates = new ArrayList<String>();
    countryList = new ArrayList<Country>();
    this.filename = filename;
    parseData();
  }

  /**
   * parseData() parses the csv file given to the constructor for the Data class.
   * @throws IOException 
   * 
   */
  public void parseData() throws IOException {
    // Input file which needs to be parsed
    String fileToParse = filename;
    BufferedReader fileReader = null;

    // Delimiter used in CSV file
    final String DELIMITER = ",";
      String line = "";
      // Create the file reader
      fileReader = new BufferedReader(new FileReader(fileToParse));

      // Read the file line by line
      while ((line = fileReader.readLine()) != null) {
        // Get all tokens available in line
        String[] tokens = line.split(DELIMITER);

        // gets dates
        if (tokens[0].contentEquals("Province/State")) {
          for (int i = 3; i < tokens.length; ++i) {
            dates.add(tokens[i]);
          }
        }
        // else set up new country
        else {
          Country temp;
          if (tokens[0].isBlank()) {
            temp = new Country(tokens[1]);
          } else {
            temp = new Country(tokens[0] + ", " + tokens[1]);
          }
          for (int i = 2; i < tokens.length; ++i) {
            temp.num.add(tokens[i]);
          }
          countryList.add(temp);
        }
      }
    
  }
}
