package application;

import java.util.ArrayList;

// Country data type
public class Country {
  String countryName;
  ArrayList<String> num;

  Country(String countryName) {
    this.countryName = countryName;
    num = new ArrayList<String>();
  }
}
