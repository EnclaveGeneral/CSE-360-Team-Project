/**
 * @author Joshua Sprague
 */
package guiComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import database.Database;
import entityClasses.User;
import guiForum.ViewForum;
import guiForum.comment;
import guiForum.png;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * the view for the comment page
 */
public class ViewComment {
	protected static Label label_Instructions =
			new Label("All comments currently in the system for the image:");
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Stage theStage;		
	protected static Pane theRootPane;	
	protected static User theUser;			
	public static Scene theCommentScene = null;
	private static ViewComment theView;	
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static HashMap<png, ArrayList<comment>> forum_data_view = new HashMap<png, ArrayList<comment>>();

	protected static TextField text = new TextField();
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_add_comment = new Button("Add Comment");
	private static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	private ImageView imageView;
	private static png currentPng;
	
	/**
	 * display the comments
	 * 
	 * @param ps
	 * @param user
	 * @param png
	 */
	public static void displayComment(Stage ps, User user, png png) {
	    theStage = ps;
	    theUser = user;

	    if (theView == null) {
	        theView = new ViewComment(ps, png);
	    } 
	    else {
	        theView.updateView(png);
	    }

	    label_UserDetails.setText("User: " + theUser.getUserName());
	    ControllerComment.refreshPage();
	    print_list();

	    theStage.setTitle("CSE 360 Foundation Code: Image Comments");
	    theStage.setScene(theCommentScene);
	    theStage.show();
	}
	
	/**
	 * updates the image view with the image clicked on
	 * 
	 * @param newImage
	 */
	public void updateView(png newImage) {
	    theRootPane.getChildren().removeIf(node -> node instanceof HBox);
	    imageView.setImage(newImage.get_pic());
	    refreshGrid(newImage);
	}
	
	/**
	 * constuctor for comments
	 * 
	 * @param ps
	 * @param png
	 */
	public ViewComment(Stage ps, png png){
		theRootPane = new Pane();
		theCommentScene = new Scene(theRootPane, width, height);
		
	    refreshGrid(png); 
	    imageView = new ImageView(png.get_pic());
	    imageView.setFitWidth(100); 
	    imageView.setFitHeight(100);  
	    imageView.setPreserveRatio(true);
	    imageView.setLayoutX(50);
	    imageView.setLayoutY(130);
	    
	    setupButtonUI(button_add_comment, "Dialog", 18, 210, Pos.CENTER, 20, 600);
		button_add_comment.setOnAction((_) -> { addComment(ps, currentPng);	});
	
	    theCommentScene.getStylesheets().add(ViewForum.class.getResource("/dark-theme.css").toExternalForm());
		setupTextUI(text, "Arial", 18, 300, Pos.BASELINE_LEFT, 50, 280, true);
		
		label_PageTitle.setText("Comments Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: ");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{ guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		setupLabelUI(label_Instructions, "Arial", 20, 400, Pos.BASELINE_LEFT, 20, 250);
	
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> { ControllerComment.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> { ControllerComment.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> { ControllerComment.performQuit(); });

		theRootPane.getChildren().clear();
		
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_Instructions, line_Separator4,
				button_Return, button_Logout, text, imageView, button_add_comment, button_Quit);
				
		forum_data_view = theDatabase.loadImageEntries();
		refreshGrid(png);
	}
	
	/**
	 * adds a comment to the image list/value
	 * 
	 * @param stage
	 * @param displayedPng
	 */
	private static void addComment(Stage stage, png displayedPng) {
	    String ctext = text.getText().trim();
	    if(ctext.length() > 10) {
	    	Alert alert = new Alert(Alert.AlertType.ERROR);
	    	alert.setTitle("Input Error");
	        alert.setHeaderText(null);
	        alert.setContentText("The comment cannot be longer than 10 characters.");
	        alert.showAndWait();
	        return;
	        
	    }
	    comment comment = new comment(ctext, theUser.getUserName());
	    png matchedKey = displayedPng;

	    if (matchedKey != null) {
	        ArrayList<comment> list = forum_data_view.get(matchedKey);
	        if (list == null) {
	            list = new ArrayList<>();
	        }
	        list.add(comment);
	        forum_data_view.put(matchedKey, list);
	        theDatabase.add_comment(forum_data_view, matchedKey, comment);
	        refreshGrid(displayedPng);
	    } 
	    else {
	        System.out.println("No matching image key found for adding comment.");
	    }
	}

	/**
	 * rebuilds the comments
	 * 
	 * @param img
	 */
	private static void refreshGrid(png img) {
		currentPng = img;
	    forum_data_view = theDatabase.loadImageEntries();
	    ArrayList<comment> list = forum_data_view.get(img);

	    if (list != null) {
	        list.removeIf(Objects::isNull);
	    }

	    theRootPane.getChildren().removeIf(node -> node instanceof HBox);
	    int yPosition = 350; 
	    
	    if (list != null) {
	        for (int i = 0; i < list.size(); i++) {
	            comment c = list.get(i);
	            Label commentLabel = new Label("from: " + c.get_user() + "   message: " + c.get_message());
	            commentLabel.setWrapText(true);
	            commentLabel.setLayoutX(50);
	            commentLabel.setLayoutY(yPosition);
	            commentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
	            
	            int buttonWidth = 60;
	            Button deleteButton = new Button("Delete");
	            deleteButton.setPrefWidth(buttonWidth);

	            print_list();

	            final int commentIndex = i;
	            deleteButton.setOnAction(e -> {
	                comment commentToDelete = list.get(commentIndex);
	                list.remove(commentIndex);
	                forum_data_view.put(img, list);
	                theDatabase.delete_comment(forum_data_view, img, commentToDelete);
	                forum_data_view = theDatabase.loadImageEntries();
	                refreshGrid(img);
	            });

	            HBox hbox = new HBox(10);
	            hbox.setLayoutX(50);
	            hbox.setLayoutY(yPosition);
	            hbox.setAlignment(Pos.CENTER_LEFT);
	            hbox.getChildren().addAll(deleteButton, commentLabel);

	            theRootPane.getChildren().add(hbox);

	            yPosition += 30;
	        }
	    }
	}
	
	/**
	 * hashmap printer for the programmers
	 */
	public static void print_list() {
	    System.out.println("Current forum_data_view contents:");
	    if (forum_data_view == null || forum_data_view.isEmpty()) {
	        System.out.println("forum_data_view is empty or null.");
	        return;
	    }

	    for (png key : forum_data_view.keySet()) {
	        if (key == null) {
	            System.out.println("Key: null");
	            continue;
	        }
	        System.out.println("Key (png): " + key + " (filename: " + key.get_filename() + ")");
	        ArrayList<comment> comments = forum_data_view.get(key);
	        if (comments == null || comments.isEmpty()) {
	            System.out.println("  No comments.");
	            continue;
	        }
	        for (comment c : comments) {
	        		System.out.println("  Comment from: " + c.get_user() + " message: " + c.get_message());
	        	
	        }
	    }
	}

	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Private local method to initialize the standard fields for a label.
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a button.
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, 
			boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
}
