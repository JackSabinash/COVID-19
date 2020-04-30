/**
 * Country class for storing data for each country
 * 
 * @author Jack Sabinash
 * @author Gabi Bigalke
 */

package application;

import java.util.ArrayList;

/**
 * Data type for storing the name and data set for each country.
 * 
 * @param countryName constructor param used as the identifier for the country and contents
 */
public class Country {
  String countryName;
  ArrayList<String> num;

  Country(String countryName) {
    this.countryName = countryName;
    num = new ArrayList<String>();
  }
}
