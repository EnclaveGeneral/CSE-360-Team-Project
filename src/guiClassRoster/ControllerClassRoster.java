package guiClassRoster;

import database.Database;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.*;


/*******
 * <p> Title: ControllerClassRoster Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the unified ClassRoster Board page. It handles all user interactions from ClassRoster,
 * validates input via ModelClassRoster, and delegates all database operations to Database.
 * The purpose of this gui is to allow users to see a list of the students within the class. Additionally,
 * instructors and admins are able to see student pass/fail grades and which students have been completely inactive. </p>
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-07-19	For the express purpose of Team project 3.

 */
public class ControllerClassRoster {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/

	/****
	 * theDatabase is the main accesspoint of the database.
	 * 
	 */
	private static Database theDatabase = FoundationsMain.database;
	
	/**
	 * classList is the store place of the roster to be passed around.
	 * 
	 */
    private static Map<String, List<String>> classList = new TreeMap<>();
	
	


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ControllerClassRoster() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerClassRoster() {
	}

	    
	
    /*******
	 * <p> Title: build </p>
	 *
	 * <p> Description: loads the classList param with users as keys and empty value Lists </p>
	 * 
	 * @param classList This is the map that will be filled with a baseline class roster.
	 * 
	 */
    
	public static void build(Map<String, List<String>> classList) {
	    List<String> users = theDatabase.getUserList();
	
	    if (users == null) {
	        throw new IllegalStateException(
	                "Database returned a null user list.");
	        }
	        if(classList.size() > 0 ) {
	        	classList.clear();
	        }
	       
	        
	        
	
	        for (String username : users) {
	        	List<String> studentResponses = new ArrayList<>(); 
	        	if (!classList.containsKey(username)) {
		             classList.put(username, studentResponses);
		            }
	        	}
	        }
	   
	
	
	/*******
	 * <p> Title: getFlag </p>
	 *
	 * <p> Description: Access student key values and generates a flag if the Integer
	 * is equal to 0. </p>
	 * 
	 * @param studentName String value for the name.
	 * 
	 * @param countNeeded Integer threshold that needs to be exceeded to not get flagged.
	 * 
	 * @return boolean for a flag value.
	 * 
	 */
	
	public static boolean getFlag (String studentName, int countNeeded) {
		int studentReplies = classList.get(studentName).size();
		
		return studentReplies < countNeeded;
	}
	
	/*******
	 * <p> Title: refreshClassRoster </p>
	 *
	 * <p> Description: Access student key values and generates a flag if the Integer
	 * is equal to 0. </p>
	 * 
	 * @param user EntityClass Object storing user info.
	 * 
	 */
	
	
	protected static void refreshClassRoster(entityClasses.User user) {
		if (user.getNewRole1() || user.getAdminRole()) {
			instructorPlusClassRoster();
		} else {
			studentClassRoster();
		}
		
	}
	

	/*-*******************************************************************************************

	List refresh methods

	**********************************************************************************************/

	/*******
	 * <p> Method: instructorPlusClassRoster() </p>
	 *
	 * <p> Description: Retrieves all students from the database and fills the listview with the 
	 * students names, and flags for passing the class. </p>
	 *
	 */
	protected static void instructorPlusClassRoster() {
		
		classList.clear();
		
		classList = theDatabase.getClassRoster();
	
	    ViewClassRoster.listView_Posts.getItems().clear();
	    
	    // set up top line.
        HBox headerRow = new HBox(10);
        headerRow.setPadding(new Insets(5));
	    
	    Label headerName = new Label("Students");
	    headerName.setPrefWidth(120);

        Label headerReplies = new Label("Responded to Students");
        headerReplies.setPrefWidth(250);

        Label headerFlag1 = new Label("3 Responses");
        headerFlag1.setPrefWidth(100);

        Label headerFlag2 = new Label("No Responses");
        headerFlag2.setPrefWidth(100);


        headerRow.getChildren().addAll(headerName, headerReplies, headerFlag1, headerFlag2);

        ViewClassRoster.listView_Posts.getItems().add(headerRow);
	   
	    
	    
	    
	    // Set up following rows.
	    for (String student : classList.keySet()) {

	        Label name = new Label(student);
	        name.setPrefWidth(120);

	        Label replies = new Label("" + classList.get(student).size());
	        replies.setPrefWidth(250);
	        
	        String temp = getFlag(student, 3)  ? "Not Met" : "Met";
	        Label flag1 = new Label(temp);
	        flag1.setPrefWidth(100);
	        
	        
	        temp = getFlag(student, 1)  ? "CRITICAL" : "";
	        Label flag2 = new Label(temp);
	        flag2.setPrefWidth(100);

	        HBox row = new HBox(10);
	        row.setPadding(new Insets(5));

	        row.getChildren().addAll(name, replies, flag1, flag2);

	        ViewClassRoster.listView_Posts.getItems().add(row);
	    }
	    
//	    for (String student : classList.keySet()) {
//	    	System.out.println(student + ": " + classList.get(student));
//	    	System.out.println(student + " Responded to " + classList.get(student).size() + " Students.\n");
//	    }
	    
	}
	
	/*******
	 * <p> Method: studentClassRoster() </p>
	 *
	 * <p> Description: Retrieves all students from the database and fills the listview with the 
	 * students names. This view is meant for those with the student role. No permissions for grades. </p>
	 *
	 */
	protected static void studentClassRoster() {
		
		classList.clear();
		
		classList = theDatabase.getClassRoster();
	
	    ViewClassRoster.listView_Posts.getItems().clear();
	    
	    // set up top line.
        HBox headerRow = new HBox(10);
        headerRow.setPadding(new Insets(5));
	    
	    Label headerName = new Label("Students");
	    headerName.setPrefWidth(120);
	    

        headerRow.getChildren().addAll(headerName);

        ViewClassRoster.listView_Posts.getItems().add(headerRow);
	   
	    
	    
	    
	    // Set up following rows.
	    for (String student : classList.keySet()) {

	        Label name = new Label(student);
	        name.setPrefWidth(120);


	        HBox row = new HBox(10);
	        row.setPadding(new Insets(5));

	        row.getChildren().addAll(name);

	        ViewClassRoster.listView_Posts.getItems().add(row);
	    }
	    
	}



	/*-*******************************************************************************************

	Navigation

	**********************************************************************************************/

	/*******
	 * <p> Method: performBack() </p>
	 *
	 * <p> Description: Returns the user to their home page. Reads activeHomePage to decide
	 * whether to route to the Admin home (1) or Role1 home (any other value). </p>
	 *
	 */
	protected static void performBack() {
		if (ViewClassRoster.theUser.getAdminRole()) {
			guiAdminHome.ViewAdminHome.displayAdminHome(
				ViewClassRoster.theStage, ViewClassRoster.theUser);
		} else if (ViewClassRoster.theUser.getNewRole1()) {
			guiRole1.ViewRole1Home.displayRole1Home(
					ViewClassRoster.theStage, ViewClassRoster.theUser);
		} else {
			guiRole2.ViewRole2Home.displayRole2Home(
				ViewClassRoster.theStage, ViewClassRoster.theUser);
		}
	}
	
}



