package guiDeleteUser;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;

/*******
 * <p> Title: ViewDeleteUser Class. </p>
 *
 * <p> Description: The View for the Delete User page. Displays a list of all users and allows
 * the admin to select and delete one after confirming the action. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 *
 * @version 1.00	2026-06-06 Initial version
 */
public class ViewDeleteUser {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_Title = new Label("Delete a User");
	private static Label label_Instructions =
			new Label("Select a user from the list below and click Delete User.");
	protected static ListView<String> listView_Users = new ListView<>();
	private static Button button_DeleteUser = new Button("Delete User");
	private static Button button_Return = new Button("Return");
	private static Button button_Logout = new Button("Logout");
	private static Button button_Quit = new Button("Quit");

	private static ViewDeleteUser theView;
	protected static Stage theStage;
	private static Pane theRootPane;
	private static Scene theDeleteUserScene;
	protected static User theUser;

	/**********
	 * <p> Method: displayDeleteUser(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Delete User page. </p>
	 */
	public static void displayDeleteUser(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewDeleteUser();

		// Refresh the user list every time the page is shown
		ControllerDeleteUser.refreshUserList();

		theStage.setTitle("CSE 360 Foundation Code: Delete a User");
		theStage.setScene(theDeleteUserScene);
		theStage.show();
	}

	private ViewDeleteUser() {
		theRootPane = new Pane();
		theDeleteUserScene = new Scene(theRootPane, width, height);
		
		// Enable dark mode
		theDeleteUserScene.getStylesheets().add(ViewDeleteUser.class.getResource("/dark-theme.css").toExternalForm());

		setupLabelUI(label_Title, "Arial", 28, width, Pos.CENTER, 0, 10);

		setupLabelUI(label_Instructions, "Arial", 16, width, Pos.CENTER, 0, 55);

		// ListView to show all users
		listView_Users.setLayoutX(50);
		listView_Users.setLayoutY(90);
		listView_Users.setMinWidth(width - 100);
		listView_Users.setPrefHeight(300);

		setupButtonUI(button_DeleteUser, "Dialog", 16, 200, Pos.CENTER, 50, 410);
		button_DeleteUser.setOnAction((_) -> {ControllerDeleteUser.performDeleteUser(); });

		setupButtonUI(button_Return, "Dialog", 16, 150, Pos.CENTER, 50, 460);
		button_Return.setOnAction((_) -> {ControllerDeleteUser.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 16, 150, Pos.CENTER, 220, 460);
		button_Logout.setOnAction((_) -> {ControllerDeleteUser.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 16, 150, Pos.CENTER, 390, 460);
		button_Quit.setOnAction((_) -> {ControllerDeleteUser.performQuit(); });

		theRootPane.getChildren().addAll(label_Title, label_Instructions,
				listView_Users, button_DeleteUser, button_Return, button_Logout, button_Quit);
	}

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}