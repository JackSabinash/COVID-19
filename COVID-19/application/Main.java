/**
 * 
 */
package application;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.event.*;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
		CheckBox cb;
		CheckBox confirmed;
		CheckBox recovered;
		CheckBox deaths;
		
		MyHandler(Button button, CheckBox cb1, CheckBox cb2, CheckBox cb3) {
			this.confirmed = cb1;
			this.recovered = cb2;
			this.deaths = cb3;
			this.button = button;
		}

		public void handle(ActionEvent e) {
			
			// user clicked search button... create graph based on user selections
			if (button.getText().equals("Search")) {
				// check which check boxes were selected by user
				if(confirmed.isSelected()) {
					// get confirmed data and make as a series
				} 
				if(recovered.isSelected()) {
					// get recovered data and make as a series
				} 
				if(deaths.isSelected()) {
					// get death data and make as a series
				}
				// adds the secondary stage for graph window
				Stage secondaryStage = new Stage();
				BorderPane root2 = new BorderPane();

				// adds chart to root pane
				VBox graph = graph();
				root2.setCenter(graph);
				
				// adds and makes visible the second scene
				Scene secondScene = new Scene(root2, WINDOW_WIDTH, WINDOW_HEIGHT);
				secondaryStage.setTitle(APP_TITLE + " Graph");
				secondaryStage.setScene(secondScene);
				secondaryStage.show();

			}
		}
	}

	private List<String> args;

	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 400;
	private static final String APP_TITLE = "COVID-19 Spread";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		args = this.getParameters().getRaw();

		// main layout is Border Pane example (top,left,center,right,bottom)
		BorderPane root = new BorderPane();

		// country to search data from
		String country[] = { "Country A", "Country B", "Country C", "Country D" };

		// create a combo box
		ComboBox<String> combo = new ComboBox<String>(FXCollections.observableArrayList(country));

		// label for combo box
		Label combo_label = new Label("Select County: ");

		// horizontal box to hold label and combo box of countries
		HBox hcombo = new HBox();
		hcombo.getChildren().addAll(combo_label, combo);

		// create check boxes to select which data to display
		CheckBox cb1 = new CheckBox("Total Confirmed Cases");
		CheckBox cb2 = new CheckBox("Total Recovered Cases");
		CheckBox cb3 = new CheckBox("Total Number of Deaths");

		// h box for check boxes
		HBox h1 = new HBox(cb1);
		HBox h2 = new HBox(cb2);
		HBox h3 = new HBox(cb3);
		

		// search button
		Button search = new Button("Search");

		// create the handler instance for search button & check boxes
		MyHandler search_handler = new MyHandler(search, cb1, cb2, cb3);
		search.setOnAction(search_handler);

		// add check boxes to v box
		VBox v_left = new VBox();
		v_left.getChildren().addAll(hcombo, h1, h2, h3, search);
		
		root.setLeft(v_left);

		/*
		 * // adds vertical box to left side of root pane VBox userSelections =
		 * userSelect(); userSelections.getChildren().addAll(search);
		 * root.setLeft(userSelections);
		 */

		// adds chart to right side of root pane
		// VBox graph = graph();
		// root.setRight(graph);

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
	private VBox graph() {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Date");
		yAxis.setLabel("Total Number");
		LineChart lineChart = new LineChart(xAxis, yAxis);

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Recovered Cases");

		series1.getData().add(new XYChart.Data("1/1/2020", 1));
		series1.getData().add(new XYChart.Data("1/15/2020", 5));
		series1.getData().add(new XYChart.Data("2/1/2020", 13));
		series1.getData().add(new XYChart.Data("2/15/2020", 22));
		series1.getData().add(new XYChart.Data("3/1/2020", 37));

		lineChart.getData().addAll(series1);

		lineChart.setTitle("COVID-19 Spread Data");
		VBox chart = new VBox(lineChart);

		return chart;
	}

}
