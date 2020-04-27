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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author gabrielle
 *
 */
public class Main extends Application {

	private List<String> args;

	private static final int WINDOW_WIDTH = 430;
	private static final int WINDOW_HEIGHT = 385;
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
		// search button event handler
		search.setOnAction((e) -> {
			// adds the secondary stage for graph window
			Stage secondaryStage = new Stage();
			BorderPane root2 = new BorderPane();

			// country to be searching data from
			String country = (String) combo.getValue();

			// adds chart to root pane
			VBox graph = graph(country, cb1.isSelected(), cb2.isSelected(), cb3.isSelected());
			root2.setCenter(graph);

			// adds and makes visible the second scene
			Scene secondScene = new Scene(root2, 600, WINDOW_HEIGHT);
			secondaryStage.setTitle(APP_TITLE + " Graph");
			secondaryStage.setScene(secondScene);
			secondaryStage.show();
		});

		// adds elements to v box for user graph selection
		VBox v_left = new VBox();
		v_left.getChildren().addAll(graph_ops, h1, h2, h3, search);

		// adds user selection options to left pane
		root.setLeft(v_left);

		// vbox for additional options
		VBox options = new VBox();

		Label l1 = new Label("Additional options: ");
		
		Button load = new Button("LOAD different data files");
		Button change = new Button("CHANGE data for specified date");
		// Button remove = new Button("REMOVE data for specified ");

		// Label load = new Label("LOAD different data files: ");
		// Button b3 = new Button("Submit");
		// TextField t3 = new TextField();
		// t3.setPromptText("< Type data file path here >");
		// op3.getChildren().addAll(load, t3, b3);

		options.getChildren().addAll(l1, load, change);

		// adds graphing and additional options
		v_left.getChildren().addAll(options);
		root.setLeft(v_left);

		// creates main window
		Scene userScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

		// scene change to load new data file
		Scene loadScene = loadData(primaryStage, userScene);
		load.setOnAction((e) -> {
			primaryStage.setScene(loadScene);
		});

		// scene change to update info
		Scene changeScene = changeData(primaryStage, userScene);
		change.setOnAction((e) -> {
			primaryStage.setScene(changeScene);
		});

		// styling
		userScene.getStylesheets().add("application.css");
		options.getStyleClass().add("vbox1");
		v_left.getStyleClass().add("vbox");
		l1.getStyleClass().add("label");
		hcombo.getStyleClass().add("hbox");
		//search.getStyleClass().add("button");
		root.getStyleClass().add("pane");

		// Add the stuff and set the primary stage
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(userScene);
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
						c_data.getData().add(new XYChart.Data(confirmDates.get(i), Float.parseFloat(c.num.get(i))));
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
						r_data.getData().add(new XYChart.Data(recovDates.get(i), Float.parseFloat(c.num.get(i))));
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
						d_data.getData().add(new XYChart.Data(deathDates.get(i), Float.parseFloat(c.num.get(i))));
					}
				}
			}

			lineChart.getData().addAll(d_data);
		}

		lineChart.setTitle("COVID-19 Spread Data for " + country);
		VBox chart = new VBox(lineChart);

		return chart;
	}

	private Scene changeData(Stage primaryStage, Scene userScene) {
		BorderPane changeRoot = new BorderPane();

		// button to go back to main scene
		Button back = new Button("Back to Options");
		changeRoot.setTop(back);

		// vertical box to hold options for changing data
		VBox changeBox = new VBox();
		Text add = new Text("Update COVID-19 Case Information");
		add.setFont(Font.font("Arial", FontWeight.BOLD, 16));

		// text fields to get users update info
		TextField date = new TextField();
		TextField confirmed = new TextField();
		TextField recovered = new TextField();
		TextField dead = new TextField();
		date.setPromptText("< mm/dd/yy >");
		confirmed.setPromptText("< Number of Cases >");
		recovered.setPromptText("< Number of Cases >");
		dead.setPromptText("< Number of Cases >");

		// horizontal boxes to organize text fields
		HBox dateBox = new HBox();
		Label da = new Label("Date (mm/dd/yy): ");
		dateBox.getChildren().addAll(da, date);

		HBox cBox = new HBox();
		Label c = new Label("Change confirmed cases to: ");
		cBox.getChildren().addAll(c, confirmed);

		HBox rBox = new HBox();
		Label r = new Label("Change recovered cases to: ");
		rBox.getChildren().addAll(r, recovered);

		HBox dBox = new HBox();
		Label de = new Label("Change number of deaths to: ");
		dBox.getChildren().addAll(de, dead);

		// updates vertical box contents to hold all information for updating
		changeBox.getChildren().addAll(add, dateBox, cBox, rBox, dBox);

		// hbox for removing a date and its data
		HBox removeBox = new HBox();
		Label remove = new Label("Remove data for specified date (mm/dd/yy): ");
		TextField removeData = new TextField();
		removeData.setPromptText("< mm/dd/yy >");
		removeBox.getChildren().addAll(remove, removeData);

		// button to finalize changes
		Button submit = new Button("Submit changes to data");

		// updates vertical box with changes and removal options
		VBox changes = new VBox();
		changes.getChildren().addAll(add, dateBox, cBox, rBox, dBox, removeBox, submit);

		changeRoot.setLeft(changes);

		back.setOnAction(e -> {
			primaryStage.setScene(userScene);
		});

		Scene changeScene = new Scene(changeRoot, 550, WINDOW_HEIGHT);
		changeScene.getStylesheets().add("application.css");
		changes.getStyleClass().add("vbox");

		return changeScene;
	}

	private Scene loadData(Stage primaryStage, Scene userScene) {
		BorderPane loadRoot = new BorderPane();

		// button to go back to main scene
		Button back = new Button("Back to Options");
		loadRoot.setTop(back);

		// vertical box to hold load information
		VBox loadBox = new VBox();
		Text data = new Text("Upload new data file for COVID-19 Spread");

		data.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		Button submit_upload = new Button("Upload data");

		// horizontal box to hold text field info
		HBox fileBox = new HBox();
		Label file = new Label("New data file path: ");
		TextField t = new TextField();
		t.setPromptText("< File Path >");
		fileBox.getChildren().addAll(file, t);

		loadBox.getChildren().addAll(data, fileBox, submit_upload);
		loadRoot.setLeft(loadBox);

		Scene loadScene = new Scene(loadRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
		loadScene.getStylesheets().add("application.css");
		loadBox.getStyleClass().add("vbox");
	
		back.setOnAction(e -> {
			primaryStage.setScene(userScene);
		});

		return loadScene;
	}
}
