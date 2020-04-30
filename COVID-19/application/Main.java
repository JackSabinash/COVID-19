/**
 * 
 */
package application;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
	private static final int WINDOW_HEIGHT = 430;
	private static final String APP_TITLE = "COVID-19 Spread";
	Data confirmedData;
	Data deathsData;
	Data recoveredData;
	ArrayList<String> countryList;

	/**
	 * Launches GUI start window where the user can decide which options to use
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	/**
	 * Sets up the main user scene that appears upon starting the GUI and allows for
	 * user to interact with buttons and options from that scene.
	 * 
	 * @param primaryStage is the main stage that shows all user options
	 */
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

		Text title = new Text("COVID-19 Data Spread Tracker");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		// create a combo box
		ComboBox<String> combo = new ComboBox<String>(FXCollections.observableArrayList(countryList));

		// label for combo box
		Label combo_label = new Label("Select County: ");

		// horizontal box to hold label and combo box of countries
		HBox hcombo = new HBox();
		hcombo.getChildren().addAll(combo_label, combo);
		VBox top = new VBox();
		top.getChildren().addAll(title, hcombo);

		// adds select country to top of pane
		root.setTop(top);

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
		Button save = new Button("SAVE data to external file");
		options.getChildren().addAll(l1, load, change, save);

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

		// scene change to save to file
		Scene saveScene = saveData(primaryStage, userScene);
		save.setOnAction((e) -> {
			primaryStage.setScene(saveScene);
		});

		// styling
		userScene.getStylesheets().add("application.css");
		options.getStyleClass().add("vbox1");
		v_left.getStyleClass().add("vbox");
		l1.getStyleClass().add("label");
		hcombo.getStyleClass().add("hbox");
		root.getStyleClass().add("pane");
		top.getStyleClass().add("vbox");

		// set up the primary stage
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(userScene);
		primaryStage.show();
	}

	/**
	 * Graph to show data user selected. Only show graph once user has clicked the
	 * 'graph data' button and then uses data files to display requested
	 * information.
	 * 
	 * @param country   that user wants to display graph data for
	 * @param confirmed true if user selected, false if wasn't selected
	 * @param recovered true if user selected, false if wasn't selected
	 * @param deaths    true if user selected, false if wasn't selected
	 */
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

	/**
	 * Creates the scene for when the user selected the change data button under
	 * additional options which allows the user to remove data and alter data for
	 * dates already added.
	 * 
	 * @param primary   stage is main stage where scenes are being shown
	 * @param userScene was the original scene displayed on the primary stage
	 * @return changeData scene that allows user to enter new data and remove
	 */
	private Scene changeData(Stage primaryStage, Scene userScene) {
		BorderPane changeRoot = new BorderPane();

		// button to go back to main scene
		Button back = new Button("Back to Options");
		changeRoot.setTop(back);

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

		Text add = new Text("Update COVID-19 Case Information");
		add.setFont(Font.font("Arial", FontWeight.BOLD, 16));

		// text fields to get users update info
		TextField date = new TextField();
		TextField confirmed = new TextField();
		TextField recovered = new TextField();
		TextField dead = new TextField();
		date.setPromptText("< m/d/yy >");
		confirmed.setPromptText("< Number of Cases >");
		recovered.setPromptText("< Number of Cases >");
		dead.setPromptText("< Number of Cases >");

		// horizontal boxes to organize text fields
		HBox dateBox = new HBox();
		Label da = new Label("Date (m/d/yy): ");
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

		// hbox for removing a date and its data
		HBox removeBox = new HBox();
		Label remove = new Label("Remove data for specified date (m/d/yy): ");
		TextField removeData = new TextField();
		removeData.setPromptText("< m/d/yy >");
		removeBox.getChildren().addAll(remove, removeData);

		// button to finalize changes
		Button submit = new Button("Submit changes to data");

		// updates vertical box with changes and removal options
		VBox changes = new VBox();
		changes.getChildren().addAll(add, hcombo, dateBox, cBox, rBox, dBox, removeBox, submit);

		changeRoot.setLeft(changes);

		// go back button event handler
		back.setOnAction(e -> {
			primaryStage.setScene(userScene);
		});

		// event handler when submit button is clicked
		submit.setOnAction(e -> {
			String country = combo.getValue();

			// remove data isn't blank... user wants to remove a date
			if (!removeData.getText().equals("")) {

				// searches death data for corresponding date
				ArrayList<String> deathDates = deathsData.dates;
				for (Country co : deathsData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < deathDates.size(); ++i) {
							// removes matching date and corresponding data
							if (deathDates.get(i).equals(removeData.getText())) {
								co.num.remove(i);
								deathDates.remove(i);
							}

						}
					}
				}
				// searches recovered data for corresponding date
				ArrayList<String> recovDates = recoveredData.dates;
				for (Country co : recoveredData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < recovDates.size(); ++i) {
							// removes matching date and corresponding data
							if (recovDates.get(i).equals(removeData.getText())) {
								co.num.remove(i);
								recovDates.remove(i);
							}
						}
					}
				}
				// searches confirmed data for corresponding date
				ArrayList<String> confirmDates = confirmedData.dates;
				for (Country co : confirmedData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < confirmDates.size(); ++i) {
							// removes matching date and corresponding data
							if (confirmDates.get(i).equals(removeData.getText())) {
								co.num.remove(i);
								confirmDates.remove(i);
							}
						}
					}
				}
				removeData.clear();
			}

			// user entered date to have data altered
			if (!date.getText().equals("")) {
				// user wants confirmed cases altered
				if (!confirmed.getText().equals("")) {
					// confirmed dates data
					ArrayList<String> confirmDates = confirmedData.dates;
					for (Country co : confirmedData.countryList) {
						if (co.countryName.contentEquals(country)) {
							for (int i = 0; i < confirmDates.size(); ++i) {
								// finds matching date and alters current info
								if (confirmDates.get(i).equals(date.getText())) {
									co.num.set(i, confirmed.getText());
								}
							}
						}
					}
					confirmed.clear();
				}

				// user wants recovered cases altered
				if (!recovered.getText().equals("")) {
					// recovered dates data
					ArrayList<String> recovDates = recoveredData.dates;
					for (Country co : recoveredData.countryList) {
						if (co.countryName.contentEquals(country)) {
							for (int i = 0; i < recovDates.size(); ++i) {
								// finds matching date and alters current info
								if (recovDates.get(i).equals(date.getText())) {
									co.num.set(i, recovered.getText());
								}
							}
						}
					}
					recovered.clear();
				}

				// user wants deaths altered
				if (!dead.getText().equals("")) {
					// deaths data
					ArrayList<String> deathDates = deathsData.dates;
					for (Country co : deathsData.countryList) {
						if (co.countryName.contentEquals(country)) {
							for (int i = 0; i < deathDates.size(); ++i) {
								// finds matching date and alters current info
								if (deathDates.get(i).equals(date.getText())) {
									co.num.set(i, dead.getText());
								}

							}
						}
					}
					dead.clear();
				}
				date.clear();
			}

			primaryStage.setScene(userScene);
		});

		// styling
		Scene changeScene = new Scene(changeRoot, 550, WINDOW_HEIGHT);
		changeScene.getStylesheets().add("application.css");
		changes.getStyleClass().add("vbox");

		return changeScene;
	}

	/**
	 * Loads different csv data files into code.
	 * 
	 * @param primary   stage is main stage where scenes are being shown
	 * @param userScene was the original scene displayed on the primary stage
	 * @return loadData scene that allows user to enter new data files
	 */
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

		// styling
		Scene loadScene = new Scene(loadRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
		loadScene.getStylesheets().add("application.css");
		loadBox.getStyleClass().add("vbox");

		// go back to main user scene
		back.setOnAction(e -> {
			primaryStage.setScene(userScene);
		});

		return loadScene;
	}

	/**
	 * Creates the scene for when the user selected the save data button which
	 * allows user to save the data to an external file of their choice.
	 * 
	 * @param primary   stage is main stage where scenes are being shown
	 * @param userScene was the original scene displayed on the primary stage
	 * @return changeData scene that allows user to enter new data and remove
	 */
	private Scene saveData(Stage primaryStage, Scene userScene) {
		BorderPane saveRoot = new BorderPane();

		// button to go back to main scene
		Button back = new Button("Back to Options");
		saveRoot.setTop(back);

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

		VBox saveBox = new VBox();
		Text data = new Text("Save data files for COVID-19 Spread");
		Text fileInfo = new Text("***File extensions much be .csv, .txt, .json, .xml, or .html***");

		// text formatting
		data.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		fileInfo.setFont(Font.font("Arial", 14));

		Button submit_save = new Button("Create files");

		// horizontal box to get confirmed file
		HBox cBox = new HBox();
		Label cFile = new Label("File to save confirmed case data to: ");
		TextField c = new TextField();
		c.setPromptText("< File Name >");
		cBox.getChildren().addAll(cFile, c);

		// horizontal box to get recovered file
		HBox rBox = new HBox();
		Label rFile = new Label("File to save confirmed case data to: ");
		TextField r = new TextField();
		r.setPromptText("< File Name >");
		rBox.getChildren().addAll(rFile, r);

		// horizontal box to hold text field info
		HBox dBox = new HBox();
		Label dFile = new Label("File to save confirmed case data to: ");
		TextField d = new TextField();
		d.setPromptText("< File Name >");
		dBox.getChildren().addAll(dFile, d);

		saveBox.getChildren().addAll(data, fileInfo, hcombo, cBox, rBox, dBox, submit_save);
		saveRoot.setLeft(saveBox);

		Scene saveScene = new Scene(saveRoot, WINDOW_WIDTH, WINDOW_HEIGHT);

		// styling
		saveScene.getStylesheets().add("application.css");
		saveBox.getStyleClass().add("vbox");

		// go back to main user scene
		back.setOnAction(e -> {
			primaryStage.setScene(userScene);
		});

		// event handler for create files button
		submit_save.setOnAction(e -> {
			String country = combo.getValue();
			// calls method to create files based on user selections
			createFiles(country, c.getText(), r.getText(), d.getText());
		});

		return saveScene;
	}

	private void createFiles(String country, String confirmed, String recovered, String deaths) {
		String outputData = "";
		System.out.println(country);
		System.out.println(confirmed);
		System.out.println(recovered);
		System.out.println(deaths);
		// confirmed file input from user
		if (!confirmed.equals("")) {
			try { // creates output file to log data given users name
				File outputFile = new File(confirmed);
				FileWriter writer = new FileWriter(outputFile);
				PrintWriter output = new PrintWriter(writer);
				output.write(country + " COVID-19 Confirmed Cases Data:");
				output.println();

				// data file to be re-routed to user output file
				ArrayList<String> confirmDates = confirmedData.dates;
				for (Country co : confirmedData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < confirmDates.size(); ++i) {
							// creates string with date and corresponding case number
							outputData = "Date: " + confirmDates.get(i) + " Total confirmed cases: " + co.num.get(i);
							output.write(outputData);
							output.println();
						}
					}
				}
				output.close();
			} catch (IOException exception) {
				System.out.println("Error in file input format.");
			}
		}
		// recovered file input from user
		if (!recovered.equals("")) {
			try { // creates output file to log data given users name
				File outputFile = new File(recovered);
				FileWriter writer = new FileWriter(outputFile);
				PrintWriter output = new PrintWriter(writer);
				output.write(country + " COVID-19 Recovered Cases Data:");
				output.println();

				// data file to be re-routed to user output file
				ArrayList<String> recovDates = recoveredData.dates;
				for (Country co : recoveredData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < recovDates.size(); ++i) {
							// creates string with date and corresponding case number
							outputData = "Date: " + recovDates.get(i) + " Total recovered cases: " + co.num.get(i);
							output.write(outputData);
							output.println();
						}
					}
				}
				output.close();
			} catch (IOException exception) {
				System.out.println("Error in file input format.");
			}

		}
		// deaths file input from user
		if (!deaths.equals("")) {
			try { // creates output file to log data given users name
				File outputFile = new File(deaths);
				FileWriter writer = new FileWriter(outputFile);
				PrintWriter output = new PrintWriter(writer);
				output.write(country + " COVID-19 Total Deaths Data:");
				output.println();

				// data file to be re-routed to user output file
				ArrayList<String> deathDates = deathsData.dates;
				for (Country co : deathsData.countryList) {
					if (co.countryName.contentEquals(country)) {
						for (int i = 0; i < deathDates.size(); ++i) {
							// creates string with date and corresponding case number
							outputData = "Date: " + deathDates.get(i) + " Total deaths: " + co.num.get(i);
							output.write(outputData);
							output.println();
						}
					}
				}
				output.close();
			} catch (IOException exception) {
				System.out.println("Error in file input format.");
			}
		}

	}
}
