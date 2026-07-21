package guiClassRoster;



import java.util.List;

import database.Database;
import entityClasses.DiscussionPost;
import entityClasses.DiscussionReply;
import applicationMain.FoundationsMain;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.sql.SQLException;
import java.util.*;
import database.Database;


/*******
 * <p> Title: ControllerMyView Class </p>
 *
 * <p> Description: This class implements the Controller component of the MVC design pattern for
 * the unified MyView Board page. It handles all user interactions from MyView,
 * validates input via ModelMyView, and delegates all database operations to Database.
 * The purpose of this gui is to allow users to interact strictly with a filtered view of their 
 * own posts and replies to those posts. Users will be able to filter responses from specific users, specific keywords,
 * and between all messages vs only unread messages. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-07-19	For the express purpose of Team project 3.

 */
public class ControllerClassRoster {

	/*-*******************************************************************************************

	Attributes

	**********************************************************************************************/


	private static Database theDatabase = FoundationsMain.database;
	private static Database database;
    private static Map<String, List<String>> classList = new TreeMap<>();
	
	


	/*-*******************************************************************************************

	Constructor

	**********************************************************************************************/

	/*******
	 * <p> Method: ControllerMyView() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other controller classes. </p>
	 *
	 */
	public ControllerClassRoster() {
	}
	
	
	
	/*******
     * <p> Title: ClassRoster Constructor </p>
     *
     * <p> Description: This constructor initiates the class object and connects to the 
     * H2 database.
     *
     * @param database The database that will be connected to.
     * 
     * @throws SQLException throws the exception.
     */

    public ControllerClassRoster(Database database2) throws SQLException {
        database = database2;
       database.connectToDatabase();
//        classList = new TreeMap<>();
    }
	    
	    
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
	     * <p> Title: getClassList </p>
	     *
	     * <p> Description: Getter method to retrieve the list.</p>
	     * 
	     * @return a TreeMap of the class
	     */
	    
	    public Map<String, List<String>> getClassList() {
	        return new TreeMap<>(classList);
	    }
	    
	    /*******
	     * <p> Title: getReplyCount </p>
	     *
	     * <p> Description: Getter method to retrieve the value pair within the keys..</p>
	     * 
	     * @param studentName String value for the name.
	     * 
	     * @return Students Reply Count (Int)
	     */

	    public Integer getReplyCount(String studentName) {
	        return classList.get(studentName).size();
	    }
	    
	    
	    /*******
	     * <p> Title: containsStudent </p>
	     *
	     * <p> Description: Checks for a valid student in the class roster. </p>
	     * 
	     * @param studentName The name of the student that will be checked.
	     * 
	     * @return Boolean flag
	     */

	    public boolean containsStudent(String studentName) {
	        return classList.containsKey(studentName);
	    }
	    
	    /*******
	     * <p> Title: getRosterSize </p>
	     *
	     * <p> Description: Gets the roster size and returns as an int. </p>
	     * 
	     * @return returns an int for roster size.
	     */

	    public int getRosterSize() {
	        return classList.size();
	    }
	    
	    
	    /*******
	     * <p> Title: getFlag </p>
	     *
	     * <p> Description: Access student key values and generates a flag if the Integer
	     * is equal to 0. </p>
	     * 
	     * @param studentName String value for the name.
	     * 
	     * @return boolean for a flag value.
	     * 
	     */
	    
	    public static boolean getFlag (String studentName, int countNeeded) {
	    	int studentReplies = classList.get(studentName).size();
	    	
	    	return studentReplies < countNeeded;
	    }
		
		
		
	
	
	/*-*******************************************************************************************

	List refresh methods

	**********************************************************************************************/

	/*******
	 * <p> Method: refreshPostList() </p>
	 *
	 * <p> Description: Retrieves all posts from the database and repopulates listView_Posts.
	 * Text posts are prefixed with a document icon; image posts with an image icon so the
	 * user can distinguish post types at a glance. Called after any post CRUD operation. </p>
	 *
	 */
	protected static void refreshPostList() {
		
		System.out.println("Inside refreshPostList");
		System.out.println(classList);
		classList.clear();
		
		classList = theDatabase.getClassRoster();
		System.out.println(classList);

		
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

	        Label replies = new Label(classList.get(student).toString());
	        replies.setPrefWidth(250);
	        
	        String temp = getFlag(student, 3)  ? "Fail" : "Pass";
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
	    
	    System.out.println(classList);
	}


	/*-*******************************************************************************************

	Selection methods

	**********************************************************************************************/

	/*******
	 * <p> Method: selectPost() </p>
	 *
	 * <p> Description: Handles the user clicking a post in listView_Posts. Stores its id in
	 * selectedPostId, populates the input fields with the post's data, and loads its replies.
	 * For image posts, the body field is left blank since there is no editable text body. </p>
	 *
	 */
	protected static void selectPost() {
		int index = ViewClassRoster.listView_Posts.getSelectionModel().getSelectedIndex();
		if (index == -1) return;

		List<DiscussionPost> posts = theDatabase.getAllPosts();
		if (index >= posts.size()) return;

		DiscussionPost p = posts.get(index);


	}


	/*-*******************************************************************************************

	Post action methods

	**********************************************************************************************/
	
	/*******
	 * <p> Method: launchMyView() </p>
	 *
	 * <p> Description: method that is used to launch the gui of myView in MVC style. </p>
	 *
	 */
	public static void launchMyView() {
		guiMyView.ViewMyView.displayMyView(ViewClassRoster.theStage, ViewClassRoster.theUser);
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



