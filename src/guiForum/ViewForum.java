/**
 * @author Joshua Sprague
 */
package guiForum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * view for form
 */
public class ViewForum {
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	protected static Label label_Instructions =
			new Label("All images currently in the system:");
	
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	private static Line line_Separator1 = new Line(20, 95, width - 20, 95);
	
	private static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_add_image = new Button("Add Image");
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	private static int row = 0;
	private static int col = 0;
	private static ViewForum theView;	
	protected static HashMap<png, ArrayList<comment>> forum_data_view = new HashMap<png, ArrayList<comment>>();
	protected static GridPane grid = new GridPane();
	protected static Stage theStage;		
	protected static Pane theRootPane;		
	protected static User theUser;			
	public static Scene theForumScene = null;
	private static ScrollPane scrollPane = new ScrollPane();
	private static double num = scrollPane.getLayoutY() + 200;
	
	/**
	 * display the form
	 * 
	 * @param ps
	 * @param user
	 */
	public static void displayForum(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		if (theView == null) theView = new ViewForum(ps);
		
		scrollPane.setLayoutY(num);
		scrollPane.setPrefViewportWidth(1800);
		grid.setHgap(50);  
		grid.setVgap(30);

		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerForum.refreshPage();

		theStage.setTitle("CSE 360 Foundation Code: Forum");
		theStage.setScene(theForumScene);
		theStage.show();
	}
	
	/**
	 * view the form
	 * 
	 * @param ps
	 */
	public ViewForum(Stage ps) {
		theRootPane = new Pane();
		theForumScene = new Scene(theRootPane, width, height);
		
		theDatabase.createImageEntriesTable();
		forum_data_view = theDatabase.loadImageEntries();
		System.out.println("list: " + forum_data_view);

	    refreshGrid();
	
		theForumScene.getStylesheets().add(ViewForum.class.getResource("/dark-theme.css").toExternalForm());

		label_PageTitle.setText("Forum Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: ");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{ guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		setupLabelUI(label_Instructions, "Arial", 20, 400, Pos.BASELINE_LEFT, 20, 130);
		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> { ControllerForum.performReturn(); });

		setupButtonUI(button_add_image, "Dialog", 18, 210, Pos.CENTER, 20, 600);
		button_add_image.setOnAction((_) -> { addImage(ps); });
		
		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> { ControllerForum.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> { ControllerForum.performQuit(); });

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_Instructions, line_Separator4,
				button_Return, button_Logout, button_add_image, button_Quit);
				}
	
	/**
	 * add image to the forum 
	 * 
	 * @param stage
	 */
	private static void addImage(Stage stage) {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Select Image");
	    fileChooser.getExtensionFilters().addAll(
	        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
	    );

	    String userHome = System.getProperty("user.home");
	    File downloadsDir = new File(userHome, "Downloads");
	    if (downloadsDir.exists() && downloadsDir.isDirectory()) {
	        fileChooser.setInitialDirectory(downloadsDir);
	    }

	    File selectedFile = fileChooser.showOpenDialog(stage);
	    if (selectedFile != null) {
	    	String filename = selectedFile.getName();
	        try (FileInputStream inputStream = new FileInputStream(selectedFile)) {
	            Image img = new Image(inputStream);
	            System.out.println(img);
	            if(col == 3) {
	            	col = 0;
	            	row++;
	            }
	            png png = new png(theUser.getUserName(), img, filename, row, col);
	            comment comment = new comment("", "");
	            ArrayList<comment> list = new ArrayList<>();
	            list.add(comment);	
	            theDatabase.saveImageEntry(
	            		theUser.getUserName(),
	            		filename,
		                img,   
		                list,          
		                row,
		                col
		            );
	            forum_data_view.put(png, list);
	            refreshGrid();
	        } 
	        catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}	
	
	/**
	 * rebuilds the grid
	 */
	private static void refreshGrid() {
	    grid.getChildren().clear();
	    
	    row = 0;
	    col = 0;
	    
	    forum_data_view = theDatabase.loadImageEntries();
	    forum_data_view.remove(null);

	    for (Map.Entry<png, ArrayList<comment>> entry : forum_data_view.entrySet()) {
	    	final png currentKey = entry.getKey();
	    	if (col == 3) {  
	            col = 0;
	            row++;
	        }
	        ImageView imageView = new ImageView(entry.getKey().get_pic());
	        Label infoLabel = new Label(entry.getKey().get_filename() + "\n" + entry.getKey().get_user());
	        infoLabel.setWrapText(true);
	        infoLabel.setTextAlignment(TextAlignment.CENTER);

	        VBox vbox = new VBox(5); 
	        vbox.setAlignment(Pos.CENTER);
	        
	        Button deleteButton = new Button("Delete Image");
	        deleteButton.setOnAction(e -> {	            
	            theDatabase.deleteImageEntry(
		                entry.getKey().get_filename()
		            );
	            
	            forum_data_view.remove(entry.getKey());
	            refreshGrid();
	        });

	        	        
	        imageView.setFitWidth(100);
	        imageView.setFitHeight(100);
	        imageView.setPreserveRatio(true);

	        Button image_button = new Button();
	        image_button.setGraphic(imageView);
	        vbox.getChildren().clear();
	        vbox.getChildren().addAll(image_button, infoLabel, deleteButton);
	        grid.add(vbox, col, row);
	        col++;
	        
	        image_button.setOnAction(e -> {
	        	ControllerForum.displayComment(currentKey);
	        });
	    }

	    if (!theRootPane.getChildren().contains(scrollPane)) {
	        scrollPane.setContent(grid);
	        scrollPane.setPrefViewportWidth(450);
	        scrollPane.setPrefViewportHeight(300);
	        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

	        theRootPane.getChildren().add(scrollPane);
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
}