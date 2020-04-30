package application;

import java.io.IOException;

// A simple class for testing the Data class implementation.
public class DataTester {

  public static void main(String[] args) {
    
    Data data;
    try {
      data = new Data("confirmed.csv");
      System.out.println(data.countryList.get(0).countryName);
      System.out.print(data.countryList.get(0).num.get(0));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
