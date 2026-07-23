package guiInstructions;

import java.util.ArrayList;

import entityClasses.User;
import guiAdminHome.ViewAdminHome;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class viewInstructions {
	private static double width = 600;
	private static double height = 730;
	
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;	
	
	protected static Label label_PageTitle = new Label();
	protected static Label label = new Label();
	
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_Next = new Button();
	protected static Button button_Back = new Button();
	protected static Button button_Finish = new Button("Finish");
// The current logged in User

	private static viewInstructions theView;
	private static Scene theInstructionsScene;	
	
	private ArrayList<Image> list = new ArrayList<>();
	private int index = 0;
	private int num_images;
	
	public static void displayviewInstructions(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new viewInstructions();	
		
		theStage.setTitle("CSE 360 instructions for instructors");
		theStage.setScene(theInstructionsScene);						// Set this page onto the stage
		theStage.show();	
	}
	
	private viewInstructions() {
		list = controllerInstructions.get_images().get(0);
		num_images = list.size();
		Image left = controllerInstructions.get_images().get(1).get(0);
		Image right = controllerInstructions.get_images().get(1).get(1);
		theRootPane = new Pane();
		
		theInstructionsScene = new Scene(theRootPane, width, height);
		theInstructionsScene.getStylesheets().add(ViewAdminHome.class.getResource("/dark-theme.css").toExternalForm());

	
		ImageView imageView = new ImageView(list.get(index));
		imageView.setX(10);
		imageView.setY(10);
		imageView.setFitWidth(577);
		imageView.setFitHeight(711); 
		imageView.setPreserveRatio(false);
		
		ImageView leftView = new ImageView(left);
		leftView.setX(110);
		leftView.setY(635);
		leftView.setFitWidth(72);
		leftView.setFitHeight(40); 
		leftView.setPreserveRatio(false);
		leftView.setVisible(false);
		//leftView.setOpacity(0.5);
		
		button_Back.setLayoutX(110);
		button_Back.setLayoutY(635);
		button_Back.setOpacity(0);
		button_Back.setPrefHeight(40);
		button_Back.setPrefWidth(72);
		
		ImageView rightView = new ImageView(right);
		rightView.setX(400);
		rightView.setY(635);
		rightView.setFitWidth(72);
		rightView.setFitHeight(40); 
		rightView.setPreserveRatio(false);
		rightView.setVisible(false);
		//rightView.setOpacity(0);
		
		button_Next.setLayoutX(400);
		button_Next.setLayoutY(635);
		button_Next.setOpacity(0);
		button_Next.setPrefHeight(40);
		button_Next.setPrefWidth(72);
	
		button_Finish.setLayoutX(255);
		button_Finish.setLayoutY(635);
		button_Finish.setDisable(false);
		button_Finish.setOpacity(0);
		
	
		button_Next.setOnMouseEntered(event -> {
		    rightView.setVisible(true);
		});

		// Hide rightView when mouse exits
		button_Next.setOnMouseExited(event -> {
		    rightView.setVisible(false);
		});
		
		button_Back.setOnMouseEntered(event -> {
			leftView.setVisible(true);
		});

		button_Back.setOnMouseExited(event -> {
			leftView.setVisible(false);
		});

		button_Next.setOnAction(event -> {
		    index++;
		    
		    if (index < list.size()) {
		        imageView.setImage(list.get(index));
		    }

		    if (index > 0) {
		        button_Back.setDisable(false);
		    }
		    
		    if (index == num_images - 1) {
		        button_Next.setDisable(true);
		        button_Finish.setOpacity(100);
		        button_Finish.setDisable(false);
		    }
		    
		 
		    System.out.println("Next clicked, index = " + index);
		});

		
		button_Back.setOnAction(event -> {
		    index--;
		    
		    if (index >= 0) {
		        imageView.setImage(list.get(index));
		    }

		    if (index < num_images) {
		        button_Next.setDisable(false);
		    }

		    if (index <= 0) {
		        button_Back.setDisable(true);
		        index = 0; 
		    }

		    System.out.println("Back clicked, index = " + index);
		});
		
		button_Finish.setOnAction(event -> {
			guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
		});
		
		label_PageTitle.setText("Admin Home Page");
		
		theRootPane.getChildren().addAll(
				imageView, leftView, rightView, button_Next, button_Back, button_Finish
				
		);
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		
	}
	
	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	
	/**********
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param b		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		Is this TextField user editable?
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	

	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
