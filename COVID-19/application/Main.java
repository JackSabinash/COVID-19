/**
 * 
 */
package application;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.event.*;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author gabrielle
 *
 */
public class Main extends Application {

  class MyHandler implements EventHandler<ActionEvent> {
    Button button;
    ComboBox combo;
    CheckBox confirmed;
    CheckBox recovered;
    CheckBox deaths;

    MyHandler(Button button, ComboBox<String> combo, CheckBox cb1, CheckBox cb2, CheckBox cb3) {
      this.confirmed = cb1;
      this.recovered = cb2;
      this.deaths = cb3;
      this.combo = combo;
      this.button = button;
    }

    MyHandler(Button button) {
      this.button = button;
    }

    public void handle(ActionEvent e) {

      // user clicked search button... create graph based on user selections
      if (button.getText().equals("Graph Data")) {

        // adds the secondary stage for graph window
        Stage secondaryStage = new Stage();
        BorderPane root2 = new BorderPane();

        // country to be searching data from
        String country = (String) combo.getValue();

        // adds chart to root pane
        VBox graph =
            graph(country, confirmed.isSelected(), recovered.isSelected(), deaths.isSelected());
        root2.setCenter(graph);

        // adds and makes visible the second scene
        Scene secondScene = new Scene(root2, WINDOW_WIDTH, WINDOW_HEIGHT);
        secondaryStage.setTitle(APP_TITLE + " Graph");
        secondaryStage.setScene(secondScene);
        secondaryStage.show();

      } else if (button.getText().equals("Add new date.")) {

      } else if (button.getText().equals("Remove a date.")) {

      } else if (button.getText().equals("Load different data files.")) {

      }
    }
  }

  private List<String> args;

  private static final int WINDOW_WIDTH = 600;
  private static final int WINDOW_HEIGHT = 400;
  private static final String APP_TITLE = "COVID-19 Spread";
  Data confirmedData;
  Data deathsData;
  Data recoveredData;

  ArrayList<String> countryList;

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    confirmedData = new Data("confirmed.csv");
    deathsData = new Data("deaths.csv");
    recoveredData = new Data("recovered.csv");


    args = this.getParameters().getRaw();

    // main layout is Border Pane example (top,left,center,right,bottom)
    BorderPane root = new BorderPane();

    // country to search data from
    countryList = new ArrayList<String>();
    for (Country c : confirmedData.countryList) {
      countryList.add(c.countryName);
    }


    // create a combo box
    ComboBox<String> combo = new ComboBox<String>(FXCollections.observableArrayList(countryList));

    // label for combo box
    Label combo_label = new Label("Select County: ");

    // horizontal box to hold label and combo box of countries
    HBox hcombo = new HBox();
    hcombo.getChildren().addAll(combo_label, combo);

    // adds select country to top of pane
    root.setTop(hcombo);

    // create check boxes to select which data to display
    Label graph_ops = new Label("Display data on graph: ");
    CheckBox cb1 = new CheckBox("Total Confirmed Cases");
    CheckBox cb2 = new CheckBox("Total Recovered Cases");
    CheckBox cb3 = new CheckBox("Total Number of Deaths");

    // h box for check boxes
    HBox h1 = new HBox(cb1);
    HBox h2 = new HBox(cb2);
    HBox h3 = new HBox(cb3);


    // search button
    Button search = new Button("Graph Data");

    // create the handler instance for search button & check boxes
    MyHandler search_handler = new MyHandler(search, combo, cb1, cb2, cb3);
    search.setOnAction(search_handler);

    // adds elements to v box for user graph selection
    VBox v_left = new VBox();
    v_left.getChildren().addAll(graph_ops, h1, h2, h3, search);

    // adds user selection options to left pane
    root.setLeft(v_left);

    // vbox for additional options
    VBox options = new VBox();


    Label l1 = new Label("Additional options: ");
    HBox op1 = new HBox();
    Label add = new Label("ADD new data for specified date: ");
    Button b1 = new Button("Submit");
    TextField t1 = new TextField();
    t1.setPromptText("< Type date here >");
    op1.getChildren().addAll(add, t1, b1);

    HBox op2 = new HBox();
    Label remove = new Label("REMOVE data for specified date: ");
    Button b2 = new Button("Submit");
    TextField t2 = new TextField();
    t2.setPromptText("< Type date here >");
    op2.getChildren().addAll(remove, t2, b2);

    HBox op3 = new HBox();
    Label load = new Label("LOAD different data files: ");
    Button b3 = new Button("Submit");
    TextField t3 = new TextField();
    t3.setPromptText("< Type data file path here >");
    op3.getChildren().addAll(load, t3, b3);

    options.getChildren().addAll(l1, op1, op2, op3);
    // change
    ///

    // Button op1 = new Button("Type date here.");
    // Button op2 = new Button("Remove a date.");
    // Button op3 = new Button("Load different data files.");
    // options.getChildren().addAll(l1, t1, op2, op3);

    // creates handler for additional options buttons
    // MyHandler op1_handler = new MyHandler(op1);
    // MyHandler op2_handler = new MyHandler(op2);
    // MyHandler op3_handler = new MyHandler(op3);
    // op1.setOnAction(op1_handler);
    // op2.setOnAction(op2_handler);
    // op3.setOnAction(op3_handler);

    // adds additional options to right pane
    root.setRight(options);


    // creates main window
    Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

    // Add the stuff and set the primary stage
    primaryStage.setTitle(APP_TITLE);
    primaryStage.setScene(mainScene);
    primaryStage.show();
  }

  // graph to show data user selected
  // only show graph once user has clicked 'search' button & then use data files
  // to display info
  private VBox graph(String country, boolean confirmed, boolean recovered, boolean deaths) {
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Date");
    yAxis.setLabel("Total Number");
    LineChart lineChart = new LineChart(xAxis, yAxis);

    // checks which check boxes have been selected by user
    if (confirmed) {
      // create confirmed cases data series
      XYChart.Series<Integer, String> c_data = new XYChart.Series<Integer, String>();
      c_data.setName("Confirmed Cases");
      ArrayList<String> confirmDates = confirmedData.dates;

      for (Country c : confirmedData.countryList) {
        if (c.countryName.contentEquals(country)) {
          for (int i = 0; i < confirmDates.size(); ++i) {
            c_data.getData()
                .add(new XYChart.Data(confirmDates.get(i), Float.parseFloat(c.num.get(i))));
          }
        }
      }

      lineChart.getData().addAll(c_data);
    }
    if (recovered) {
      // create recovered cases data series
      XYChart.Series<Integer, String> r_data = new XYChart.Series<Integer, String>();
      r_data.setName("Recovered Cases");

      ArrayList<String> recovDates = recoveredData.dates;

      for (Country c : recoveredData.countryList) {
        if (c.countryName.contentEquals(country)) {
          for (int i = 0; i < recovDates.size(); ++i) {
            r_data.getData()
                .add(new XYChart.Data(recovDates.get(i), Float.parseFloat(c.num.get(i))));
          }
        }
      }

      lineChart.getData().addAll(r_data);
    }
    if (deaths) {
      // create num of deaths data series
      XYChart.Series<Integer, String> d_data = new XYChart.Series<Integer, String>();
      d_data.setName("Deaths");

      ArrayList<String> deathDates = deathsData.dates;

      for (Country c : deathsData.countryList) {
        if (c.countryName.contentEquals(country)) {
          for (int i = 0; i < deathDates.size(); ++i) {
            d_data.getData()
                .add(new XYChart.Data(deathDates.get(i), Float.parseFloat(c.num.get(i))));
          }
        }
      }

      lineChart.getData().addAll(d_data);
    }

    lineChart.setTitle("COVID-19 Spread Data for " + country);
    VBox chart = new VBox(lineChart);

    return chart;
  }

  private void addNewData() {

  }

  private void removeData() {

  }

  private void loadData() {

  }
}
