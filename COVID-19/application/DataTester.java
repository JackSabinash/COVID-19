package application;

public class DataTester {

  public static void main(String[] args) {
    Data data = new Data("confirmed.csv");
    System.out.println(data.countryList.get(0).countryName);
    System.out.print(data.countryList.get(0).num.get(0));
  }

}
