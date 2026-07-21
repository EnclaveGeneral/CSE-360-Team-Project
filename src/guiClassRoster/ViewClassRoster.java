package guiClassRoster;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;



/*******
 * <p> Title: ViewClassRoster Class </p>
 *
 * <p> Description: This class implements the View component of the MVC design pattern for
 * the unified ClassRoster Board page. It handles all user interactions from ClassRoster,
 * validates input via ModelClassRoster, and delegates all database operations to Database.
 * The purpose of this gui is to allow instructors and admins to quickly find information on student
 * participation in the discussion board. </p>
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-07-19	For the express purpose of Team project 3.

 */
public class ViewClassRoster {

	/*-*******************************************************************************************

	Singleton instance and scene management

	**********************************************************************************************/

	/****
	 * GUI for the view.
	 */
	
	private static ViewClassRoster theView = null;
	/****
	 * View of the GUI to be put on theRootPane
	 */
	private static Scene theRoster;
	/****
	 * GUI page.
	 */
	private static Pane  theRootPane;

	/****
	 * GUI Object.
	 */
	static Stage              theStage;
	
	/****
	 * User object.
	 */
	static entityClasses.User theUser;
	
	/****
	 * Width of the application window.
	 * 
	 */

	private static double width  	= FoundationsMain.WINDOW_WIDTH;
	
	/****
	 * Height of application window.
	 * 
	 */
	private static double height = FoundationsMain.WINDOW_HEIGHT;

	/*-*******************************************************************************************

	GUI Widgets — Header

	**********************************************************************************************/

	/****
	 * GUI page Title.
	 * 
	 */
	static Label  label_PageTitle     = new Label("Class Roster");
	
	/****
	 * GUI Aesthetics
	 * 
	 */
	static Line   line_Sep1 = new Line(20, 70, width - 20, 70);



	/*-*******************************************************************************************

	GUI Widgets — Post List

	**********************************************************************************************/
	
	/****
	 * GUI helper. Interactive board that can be filled with the roster.
	 */

	static ListView<HBox> listView_Posts = new ListView<>();
	
	/****
	 * GUI Aesthetic.
	 * 
	 */

	static Line line_Sep2 = new Line(20, 655, width - 20, 655);


	/*-*******************************************************************************************

	GUI Widgets — Status and Navigation

	**********************************************************************************************/
	
	
	/******
	 * 
	 * Navigation button to return to home screen.
	 */
	
	static Button button_Back       	= new Button("Back");


	/*-*******************************************************************************************

	Display method

	**********************************************************************************************/

	/*******
	 * <p> Method: displayClassRoster(Stage ps, User user) </p>
	 *
	 * <p> Description: Entry point called from Role1 and Admin home pages. Creates the singleton
	 * on the first call; subsequent calls simply swap the scene and refresh the class list. </p>
	 *
	 * @param ps   is the Stage object onto which this page's scene will be set and displayed.
	 *
	 * @param user is the currently logged-in User whose username is displayed in the header.
	 *
	 */
	public static void displayClassRoster(Stage ps, entityClasses.User user) {
		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewClassRoster();


		theStage.setTitle("CSE 360: Class Roster");
		theStage.setScene(theRoster);
		theStage.show();

		ControllerClassRoster.refreshClassRoster();
	}


	/*-*******************************************************************************************

	Private constructor — initializes all GUI elements (runs once, Singleton)

	**********************************************************************************************/

	/*******
	 * <p> Method: ViewClassRoster() </p>
	 *
	 * <p> Description: This private constructor initializes all elements of the graphical user
	 * interface. It determines the location, size, font, colour, and event handlers for each
	 * GUI widget. This method is only called once due to the Singleton design pattern. </p>
	 *
	 */
	private ViewClassRoster() {

		theRootPane        = new Pane();
		theRoster = new Scene(theRootPane, width, height);
		theRoster.getStylesheets().add(
			ViewClassRoster.class.getResource("/dark-theme.css").toExternalForm());

		// ── Header (y 8–70) ──────────────────────────────────────────────────────
		setupLabel(label_PageTitle,     "Arial", 22, width, Pos.CENTER,        0,  10);



		// ── Post list (y 215–358) ─────────────────────────────────────────────────
		listView_Posts.setLayoutX(20);
		listView_Posts.setLayoutY(80);
		listView_Posts.setPrefWidth(760);
		listView_Posts.setPrefHeight(560);
		listView_Posts.setOnMouseClicked((_) -> {});

		// ── Reply input (y 370–470) ───────────────────────────────────────────────
		setupButton(button_Back, "Dialog", 13, 110, Pos.CENTER, 660, 665);
		button_Back.setOnAction((_) -> {ControllerClassRoster.performBack(); });
		


		// Add all widgets to the pane
		theRootPane.getChildren().addAll(
			label_PageTitle, line_Sep1,
			listView_Posts,
			line_Sep2,
			button_Back
		);

		// Ensure discussion tables exist
		FoundationsMain.database.createDiscussionTables();
	}
	


	/*-*******************************************************************************************

	Helper methods

	**********************************************************************************************/
	

	/*******
	 * <p> Method: setupLabel </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Label. </p>
	 *
	 * @param l   The Label object to be initialized
	 * @param ff  The font family to be used
	 * @param f   The size of the font to be used
	 * @param w   The minimum width of the Label
	 * @param p   The alignment (e.g. left, centered, or right)
	 * @param x   The location from the left edge (x axis)
	 * @param y   The location from the top (y axis)
	 */
	private void setupLabel(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}


	/*******
	 * <p> Method: setupButton </p>
	 *
	 * <p> Description: Private local method to initialize the standard fields for a Button. </p>
	 *
	 * @param b   The Button object to be initialized
	 * @param ff  The font family to be used
	 * @param f   The size of the font to be used
	 * @param w   The minimum width of the Button
	 * @param p   The alignment (e.g. left, centered, or right)
	 * @param x   The location from the left edge (x axis)
	 * @param y   The location from the top (y axis)
	 */
	private void setupButton(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

}
