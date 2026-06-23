package database;

import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiForum.comment;
import guiForum.png;
import javafx.scene.image.Image;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MB)" on the l3ft side
 * of the page under the heading "Reference" for a PDF of 438 pages.)  This class leverages H2
 * and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by Pravalika 
 * 							Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01		2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE,"
				+ "onetimePassword VARCHAR(255) DEFAULT '')";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    
	    // Create the post table 
	    String postTable = "CREATE TABLE IF NOT EXISTS postDB ("
	    		+ "postId INT AUTO_INCREMENT PRIMARY KEY,"
	    		+ "authorUsername VARCHAR(255), "
	    		+ "title VARCHAR(255), "
	    		+ "body VARCHAR(2000), "
	    		+ "timestamp VARCHAR(255), "
	    		+ "isPinned BOOL DEFAULT FALSE )";
	    statement.execute(postTable);
	    
	    // Create the reply table 
	    String replyTable = "CREATE TABLE IF NOT EXISTS replyDB ("
	    		+ "replyId INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "parentPostId INT, "
	    		+ "authorUsername VARCHAR(255), "
	    		+ "body VARCHAR(2000), "
	    		+ "timestamp VARCHAR(255), "
	    		+ "isAccepted BOOL DEFAULT FALSE )";
	    statement.execute(replyTable);
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	/*******
	 * <p> Method: List&lt;String&gt; getInvitationList() </p>
	 *
	 * <p> Description: Generate a List of Strings, one for each outstanding invitation in the
	 * InvitationCodes table, formatted as "code  |  emailAddress  |  role". Returns an empty list
	 * when there are no invitations, or null if the query fails. </p>
	 *
	 * @return a list of the outstanding invitations found in the database.
	 */
	public List<String> getInvitationList() {
		List<String> invitationList = new ArrayList<String>();
		String query = "SELECT code, emailAddress, role FROM InvitationCodes";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				invitationList.add(rs.getString("code") + "  |  "
						+ rs.getString("emailAddress") + "  |  " + rs.getString("role"));
			}
		} catch (SQLException e) {
			return null;
		}
		return invitationList;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	/*******
	 * <p> Method: void deleteUserAccount(String username) </p>
	 * 
	 * <p> Description: Delete a user level account from the system </p>
	 * 
	 * @param 
	 * 
	 * 
	 */
	// Remove and delete a user account from the system database 
	public void deleteUserAccount(String username) {
		String query = "DELETE FROM userDB where userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1,  username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p> Method: void setOnetimePassword(String username, String otp) </p>
	 *
	 * <p> Description: Stores a one-time password for the specified user. The next time
	 * this user logs in with this password, they will be forced to set a new one. </p>
	 *
	 * @param username the username of the user
	 * @param otp the one-time password to store
	 */
	public void setOnetimePassword(String username, String otp) {
	    String query = "UPDATE userDB SET onetimePassword = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/*******
	 * <p> Method: boolean isOnetimePassword(String username, String password) </p>
	 *
	 * <p> Description: Returns true if the supplied password matches the stored one-time
	 * password for this user. </p>
	 *
	 * @param username the username of the user
	 * @param password the password to check
	 * @return true if it matches the one-time password, else false
	 */
	public boolean isOnetimePassword(String username, String password) {
	    String query = "SELECT onetimePassword FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String otp = rs.getString("onetimePassword");
	            return otp != null && !otp.isEmpty() && otp.equals(password);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	/*******
	 * <p> Method: void clearOnetimePassword(String username) </p>
	 *
	 * <p> Description: Clears the one-time password for the specified user after it has
	 * been used. </p>
	 *
	 * @param username the username of the user
	 */
	public void clearOnetimePassword(String username) {
	    String query = "UPDATE userDB SET onetimePassword = '' WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Method: void updatePassword(String username, String newPassword) </p>
	 * 
	 * <p> Description: Update the password for a user account  
	 * @param username
	 * @param newPassword
	 */
	
	public void updatePassword(String username, String newPassword) {
	    String query = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = newPassword;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	/**
	 * adds an image
	 * 
	 * @param user
	 * @param filename
	 * @param pic
	 * @param comments
	 * @param row
	 * @param col
	 */
	public void saveImageEntry(String user, String filename, Image pic, ArrayList<comment> comments, int row, int col) {
	    String insertImageSql = "INSERT INTO images (username, filename, image_data, row_position, col_position) VALUES (?, ?, ?, ?, ?)";
	    String insertCommentSql = "INSERT INTO comments (image_id, comment_text, comment_user) VALUES (?, ?, ?)";

	    try (
	        PreparedStatement imageStmt = connection.prepareStatement(insertImageSql, Statement.RETURN_GENERATED_KEYS);
	    ) {
	        imageStmt.setString(1, user);
	        imageStmt.setString(2, filename);

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        BufferedImage bImage = SwingFXUtils.fromFXImage(pic, null);
	        ImageIO.write(bImage, "png", baos);
	        byte[] imageBytes = baos.toByteArray();
	        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
	        imageStmt.setBinaryStream(3, bais, imageBytes.length);

	        imageStmt.setInt(4, row);
	        imageStmt.setInt(5, col);

	        imageStmt.executeUpdate();

	        // get image ID
	        int imageId = -1;
	        try (ResultSet generatedKeys = imageStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                imageId = generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating image failed, no ID obtained.");
	            }
	        }

	        // insert comments linked to image ID
	        if (comments != null && !comments.isEmpty()) {
	            try (PreparedStatement commentStmt = connection.prepareStatement(insertCommentSql)) {
	                for (comment c : comments) {
	                    commentStmt.setInt(1, imageId);
	                    commentStmt.setString(2, c.get_message());
	                    commentStmt.setString(3, c.get_user());
	                    commentStmt.addBatch();
	                }
	                commentStmt.executeBatch();
	            }
	        }

	    } catch (SQLException | IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * deletes an image
	 * 
	 * @param filename
	 */
	public void deleteImageEntry(String filename) {
	    String sql = "DELETE FROM images WHERE filename = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, filename);
	        int affectedRows = pstmt.executeUpdate();
	        System.out.println("Deleted rows: " + affectedRows);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * what i use to store hashmap
	 */
	public void createImageEntriesTable() {
	    String createImagesTable = "CREATE TABLE IF NOT EXISTS images (" +
	                               "id INT AUTO_INCREMENT PRIMARY KEY, " +
	                               "username VARCHAR(255) NOT NULL, " +
	                               "filename VARCHAR(255) NOT NULL, " +
	                               "image_data BLOB, " +
	                               "row_position INT, " +
	                               "col_position INT" +
	                               ")";
	    // stores the arraylist of comments
	    String createCommentsTable = "CREATE TABLE IF NOT EXISTS comments (" +
	                                 "id INT AUTO_INCREMENT PRIMARY KEY, " +
	                                 "image_id INT NOT NULL, " +
	                                 "comment_text VARCHAR(1000), " +
	                                 "comment_user VARCHAR(255), " +
	                                 "FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE" +
	                                 ")";
	    
	    try (Statement stmt = connection.createStatement()) {
	        stmt.execute(createImagesTable);
	        stmt.execute(createCommentsTable);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * grabs the hashmap
	 * 
	 * @return the hashmap
	 */
	public HashMap<png, ArrayList<comment>> loadImageEntries() {
	    HashMap<png, ArrayList<comment>> loadedEntries = new LinkedHashMap<>();

	    // load images
	    String loadImagesSql = "SELECT id, username, filename, image_data, row_position, col_position FROM images";
	    Map<Integer, png> imageMap = new HashMap<>();

	    try (PreparedStatement imgStmt = connection.prepareStatement(loadImagesSql);
	         ResultSet rsImages = imgStmt.executeQuery()) {

	        while (rsImages.next()) {
	            int imageId = rsImages.getInt("id");
	            String username = rsImages.getString("username");
	            String filename = rsImages.getString("filename");
	            Blob imageBlob = rsImages.getBlob("image_data");
	            int row = rsImages.getInt("row_position");
	            int col = rsImages.getInt("col_position");

	            InputStream imageStream = imageBlob.getBinaryStream();
	            Image img = new Image(imageStream);

	            png pngObj = new png(username, img, filename, row, col);
	            imageMap.put(imageId, pngObj);

	            loadedEntries.put(pngObj, new ArrayList<>());
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // load comments and add png
	    String loadCommentsSql = "SELECT image_id, comment_text, comment_user FROM comments";
	    try (PreparedStatement commentStmt = connection.prepareStatement(loadCommentsSql);
	         ResultSet rsComments = commentStmt.executeQuery()) {

	        while (rsComments.next()) {
	            int imageId = rsComments.getInt("image_id");
	            String commentText = rsComments.getString("comment_text");
	            String commentUser = rsComments.getString("comment_user");

	            png pngObj = imageMap.get(imageId);
	            if (pngObj != null) {
	                comment commentObj = new comment(commentText, commentUser);
	                loadedEntries.get(pngObj).add(commentObj);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return loadedEntries;
	}
	
	/**
	 * adds a comment
	 * 
	 * @param forum_data_view
	 * @param img
	 * @param newComment
	 */
	public void add_comment(HashMap<png, ArrayList<comment>> forum_data_view, png img, comment newComment) {
	    ArrayList<comment> list = forum_data_view.get(img);
	    if (list == null) {
	        list = new ArrayList<>();
	        forum_data_view.put(img, list);
	    }
	    list.add(newComment);

	    try {
	        // find image id
	        String findImageIdSql = "SELECT id FROM images WHERE filename = ? AND username = ?";
	        int imageId = -1;
	        try (PreparedStatement findStmt = connection.prepareStatement(findImageIdSql)) {
	            findStmt.setString(1, img.get_filename());
	            findStmt.setString(2, img.get_user());
	            try (ResultSet rs = findStmt.executeQuery()) {
	                if (rs.next()) {
	                    imageId = rs.getInt("id");
	                } 
	                else {
	                    // if no image exists then insert one
	                    String insertImageSql = "INSERT INTO images (username, filename, image_data, row_position, col_position) VALUES (?, ?, ?, ?, ?)";
	                    try (PreparedStatement insertStmt = connection.prepareStatement(insertImageSql, Statement.RETURN_GENERATED_KEYS)) {
	                        insertStmt.setString(1, img.get_user());
	                        insertStmt.setString(2, img.get_filename());
	                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                        BufferedImage bImage = SwingFXUtils.fromFXImage(img.get_pic(), null);
	                        ImageIO.write(bImage, "png", baos);
	                        byte[] imageBytes = baos.toByteArray();
	                        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
	                        insertStmt.setBinaryStream(3, bais, imageBytes.length);

	                        insertStmt.setInt(4, img.get_row());
	                        insertStmt.setInt(5, img.get_col());

	                        insertStmt.executeUpdate();

	                        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
	                            if (generatedKeys.next()) {
	                                imageId = generatedKeys.getInt(1);
	                            } else {
	                                throw new SQLException("Creating image failed, no ID obtained.");
	                            }
	                        }
	                    }
	                }
	            }
	        }

	        if (imageId != -1) {
	            // insert comment
	            String insertCommentSql = "INSERT INTO comments (image_id, comment_text, comment_user) VALUES (?, ?, ?)";
	            try (PreparedStatement commentStmt = connection.prepareStatement(insertCommentSql)) {
	                commentStmt.setInt(1, imageId);
	                commentStmt.setString(2, newComment.get_message());
	                commentStmt.setString(3, newComment.get_user());
	                commentStmt.executeUpdate();
	            }
	        } 
	        else {
	            System.err.println("Failed to find or insert image for comment.");
	        }
	    } catch (SQLException | IOException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * deletes a comment
	 * 
	 * @param forum_data_view
	 * @param img
	 * @param commentToDelete
	 */
	public void delete_comment(HashMap<png, ArrayList<comment>> forum_data_view, png img, comment commentToDelete) {
	    try {
	        // find image id
	        String findImageIdSql = "SELECT id FROM images WHERE filename = ? AND username = ?";
	        int imageId = -1;
	        try (PreparedStatement findStmt = connection.prepareStatement(findImageIdSql)) {
	            findStmt.setString(1, img.get_filename());
	            findStmt.setString(2, img.get_user());
	            try (ResultSet rs = findStmt.executeQuery()) {
	                if (rs.next()) {
	                    imageId = rs.getInt("id");
	                } 
	                else {
	                    System.err.println("Image not found for deletion.");
	                    return;
	                }
	            }
	        }

	        if (imageId != -1) {
	            // find comment id
	            String findCommentIdSql = "SELECT id FROM comments WHERE image_id = ? AND comment_text = ? AND comment_user = ?";
	            int commentId = -1;
	            try (PreparedStatement findCommentStmt = connection.prepareStatement(findCommentIdSql)) {
	                findCommentStmt.setInt(1, imageId);
	                findCommentStmt.setString(2, commentToDelete.get_message());
	                findCommentStmt.setString(3, commentToDelete.get_user());
	                try (ResultSet rsComment = findCommentStmt.executeQuery()) {
	                    if (rsComment.next()) {
	                        commentId = rsComment.getInt("id");
	                    } 
	                    else {
	                        System.err.println("Comment not found for deletion.");
	                        return;
	                    }
	                }
	            }

	            // delete comment
	            if (commentId != -1) {
	                String deleteCommentSql = "DELETE FROM comments WHERE id = ?";
	                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCommentSql)) {
	                    deleteStmt.setInt(1, commentId);
	                    int affectedRows = deleteStmt.executeUpdate();
	                    if (affectedRows > 0) {
	                        System.out.println("Comment deleted successfully.");
	                    } 
	                    else {
	                        System.err.println("Failed to delete comment.");
	                    }
	                }

	                ArrayList<comment> commentList = forum_data_view.get(img);
	                if (commentList != null) {
	                    commentList.removeIf(c -> 
	                        c.get_message().equals(commentToDelete.get_message()) &&
	                        c.get_user().equals(commentToDelete.get_user())
	                    );
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	// =========================================================
	// DISCUSSION BOARD — posts & replies
	// =========================================================

	/*******
	 * <p> Method: createDiscussionTables </p>
	 *
	 * <p> Description: Creates the posts and replies tables if they do not already exist.
	 * Called once at startup from FoundationsMain or from ViewDiscussion's constructor. </p>
	 */
	public void createDiscussionTables() {
	    String postsSql = "CREATE TABLE IF NOT EXISTS posts (" +
	            "id INT AUTO_INCREMENT PRIMARY KEY, " +
	            "author VARCHAR(255) NOT NULL, " +
	            "title VARCHAR(255) NOT NULL, " +
	            "body CLOB, " +
	            "post_type VARCHAR(10) DEFAULT 'text', " +
	            "image_filename VARCHAR(255), " +
	            "image_data BLOB, " +
	            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

	    String repliesSql = "CREATE TABLE IF NOT EXISTS replies (" +
	            "id INT AUTO_INCREMENT PRIMARY KEY, " +
	            "post_id INT NOT NULL, " +
	            "author VARCHAR(255) NOT NULL, " +
	            "body CLOB NOT NULL, " +
	            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
	            "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE)";

	    try (Statement stmt = connection.createStatement()) {
	        stmt.execute(postsSql);
	        stmt.execute(repliesSql);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/*******
	 * <p> Method: saveTextPost </p>
	 *
	 * <p> Description: Inserts a new text-based post into the posts table. </p>
	 *
	 * @param author   the username of the poster
	 * @param title    the post title
	 * @param body     the post body text
	 * @return the generated post id, or -1 on failure
	 */
	public int saveTextPost(String author, String title, String body) {
	    String sql = "INSERT INTO posts (author, title, body, post_type) VALUES (?, ?, ?, 'text')";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setString(1, author);
	        ps.setString(2, title);
	        ps.setString(3, body);
	        ps.executeUpdate();
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) return keys.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}

	/*******
	 * <p> Method: saveImagePost </p>
	 *
	 * <p> Description: Inserts a new image-based post into the posts table, storing the
	 * image as a BLOB alongside an optional caption in body. </p>
	 *
	 * @param author    the username of the poster
	 * @param title     the post title / caption
	 * @param filename  the original filename of the image
	 * @param img       the JavaFX Image object to persist
	 * @return the generated post id, or -1 on failure
	 */
	public int saveImagePost(String author, String title, String filename, javafx.scene.image.Image img) {
	    String sql = "INSERT INTO posts (author, title, post_type, image_filename, image_data) VALUES (?, ?, 'image', ?, ?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setString(1, author);
	        ps.setString(2, title);
	        ps.setString(3, filename);

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        java.awt.image.BufferedImage bImg = SwingFXUtils.fromFXImage(img, null);
	        ImageIO.write(bImg, "png", baos);
	        byte[] bytes = baos.toByteArray();
	        ps.setBinaryStream(4, new ByteArrayInputStream(bytes), bytes.length);

	        ps.executeUpdate();
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) return keys.getInt(1);
	        }
	    } catch (SQLException | IOException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}

	/*******
	 * <p> Method: getAllPosts </p>
	 *
	 * <p> Description: Loads all posts ordered newest-first. Each post's image_data BLOB
	 * is decoded into a JavaFX Image where applicable. </p>
	 *
	 * @return an ordered List of DiscussionPost objects
	 */
	public java.util.List<entityClasses.DiscussionPost> getAllPosts() {
	    java.util.List<entityClasses.DiscussionPost> list = new java.util.ArrayList<>();
	    String sql = "SELECT id, author, title, body, post_type, image_filename, image_data, created_at FROM posts ORDER BY created_at DESC";
	    try (PreparedStatement ps = connection.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            int id          = rs.getInt("id");
	            String author   = rs.getString("author");
	            String title    = rs.getString("title");
	            String body     = rs.getString("body");
	            String type     = rs.getString("post_type");
	            String imgFile  = rs.getString("image_filename");
	            Blob blob       = rs.getBlob("image_data");
	            String created  = rs.getString("created_at");

	            javafx.scene.image.Image img = null;
	            if (blob != null) {
	                img = new javafx.scene.image.Image(blob.getBinaryStream());
	            }
	            list.add(new entityClasses.DiscussionPost(id, author, title, body, type, imgFile, img, created));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	/*******
	 * <p> Method: getRepliesForPost </p>
	 *
	 * <p> Description: Loads all replies for a given post, ordered oldest-first so the
	 * conversation reads top-to-bottom. </p>
	 *
	 * @param postId  the id of the parent post
	 * @return a List of DiscussionReply objects
	 */
	public java.util.List<entityClasses.DiscussionReply> getRepliesForPost(int postId) {
	    java.util.List<entityClasses.DiscussionReply> list = new java.util.ArrayList<>();
	    String sql = "SELECT id, post_id, author, body, created_at FROM replies WHERE post_id = ? ORDER BY created_at ASC";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, postId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new entityClasses.DiscussionReply(
	                        rs.getInt("id"),
	                        rs.getInt("post_id"),
	                        rs.getString("author"),
	                        rs.getString("body"),
	                        rs.getString("created_at")));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	/*******
	 * <p> Method: addReply </p>
	 *
	 * <p> Description: Inserts a new reply for the given post. </p>
	 *
	 * @param postId  the parent post's id
	 * @param author  the replying user's username
	 * @param body    the reply text
	 * @return the generated reply id, or -1 on failure
	 */
	public int addReply(int postId, String author, String body) {
	    String sql = "INSERT INTO replies (post_id, author, body) VALUES (?, ?, ?)";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setInt(1, postId);
	        ps.setString(2, author);
	        ps.setString(3, body);
	        ps.executeUpdate();
	        try (ResultSet keys = ps.getGeneratedKeys()) {
	            if (keys.next()) return keys.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}

	/*******
	 * <p> Method: deleteReply </p>
	 *
	 * <p> Description: Deletes a single reply by its id. </p>
	 *
	 * @param replyId  the id of the reply to delete
	 */
	public void deleteReply(int replyId) {
	    String sql = "DELETE FROM replies WHERE id = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, replyId);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/*******
	 * <p> Method: deletePost </p>
	 *
	 * <p> Description: Deletes a post and all its replies (cascade). </p>
	 *
	 * @param postId  the id of the post to delete
	 */
	public void deletePost(int postId) {
	    String sql = "DELETE FROM posts WHERE id = ?";
	    try (PreparedStatement ps = connection.prepareStatement(sql)) {
	        ps.setInt(1, postId);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Method: void createPost(Post post) </p>
	 * 
	 * <p> Description: Inserts a new post record into the postDB table using the provided Post
	 * object. The postId is auto-generated by the database and does not need to be provided. </p>
	 * 
	 * @param post specifies the Post object to be added to the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public void createPost(Post post) throws SQLException {
		String query = "INSERT INTO postDB (authorUsername, title, body, timestamp, isPinned) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
				
			pstmt.setString(1,  post.getAuthorUsername());
			
			pstmt.setString(2, post.getTitle());
			
			pstmt.setString(3,  post.getBody());
			
			pstmt.setString(4,  post.getTimestamp());
			
			pstmt.setBoolean(5, post.getIsPinned());
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: Post getPostById(int postId) </p>
	 * 
	 * <p> Description: Retrieves a single Post record from the postDB table whose postId matches
	 * the specified value. Returns null if no matching post is found. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the post to retrieve.
	 * 
	 * @return the Post object with the matching postId, or null if not found.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */

	public Post getPostById (int postId) throws SQLException {
		String query = "SELECT * FROM postDB where postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			pstmt.setInt(1, postId); 
			
			ResultSet res = pstmt.executeQuery();
			
			if (res.next()) {
				return new Post(
						res.getInt("postId"), 
						res.getString("authorUsername"), 
						res.getString("title"), 
						res.getString("body"), 
						res.getString("timestamp"), 
						res.getBoolean("isPinned")
				
				);
					
			}
			
			return null;
			
		}
	}
	
	/*******
	 * <p> Method: List&lt;Post&gt; getAllPosts() </p>
	 * 
	 * <p> Description: Retrieves all Post records from the postDB table and returns them as a
	 * List of Post objects. Returns an empty list if no posts exist. This supports the
	 * requirement to store and retrieve all current posts. </p>
	 * 
	 * @return a List of all Post objects currently stored in the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public List<Post> getAllPosts() throws SQLException {
		String query = "SELECT * FROM postDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			ResultSet res = pstmt.executeQuery();  
			
			List<Post> posts = new ArrayList<>();
			
			while (res.next()) {
				posts.add(new Post(
						res.getInt("postId"), 
						res.getString("authorUsername"), 
						res.getString("title"), 
						res.getString("body"), 
						res.getString("timestamp"), 
						res.getBoolean("isPinned")
				));
			}
			
			return posts; 
		}
	}
	
	/*******
	 * <p> Method: void updatePost(Post post) </p>
	 * 
	 * <p> Description: Updates an existing post record in the postDB table using the provided
	 * Post object. The postId field is used to identify which record to update. All other
	 * fields are overwritten with the values from the provided Post object. </p>
	 * 
	 * @param post specifies the Post object containing the updated values to write to the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public void updatePost(Post post) throws SQLException {
		String query = "UPDATE postDB SET authorUsername = ?, "
				+ "title = ?, "
				+ "body = ?, "
				+ "timestamp = ?, "
				+ "isPinned = ? "
				+ "WHERE postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, post.getAuthorUsername());
			pstmt.setString(2, post.getTitle());
			pstmt.setString(3,  post.getBody());
			pstmt.setString(4, post.getTimestamp());
			pstmt.setBoolean(5, post.getIsPinned());
			pstmt.setInt(6, post.getPostId());
			
			pstmt.execute();
		}
	}
	
	/*******
	 * <p> Method: void deletePost(int postId) </p>
	 * 
	 * <p> Description: Deletes the post record from the postDB table whose postId matches the
	 * specified value. If no matching record exists, the operation completes silently with no
	 * effect. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the post to delete.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */

	public void deletePost(int postId) throws SQLException {
		String query = "DELETE FROM postDB WHERE postId = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			
			pstmt.execute(); 
		}
	}
	
	/*******
	 * <p> Method: void createReply(Reply reply) </p>
	 * 
	 * <p> Description: Inserts a new reply record into the replyDB table using the provided Reply
	 * object. The replyId is auto-generated by the database and does not need to be provided.
	 * The parentPostId field links this reply to its associated post. </p>
	 * 
	 * @param reply specifies the Reply object to be added to the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public void createReply(Reply reply) throws SQLException {
		String query = "INSERT INTO replyDB (parentPostId, authorUsername, body, timestamp, isAccepted) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
				
			pstmt.setInt(1,  reply.getParentPostId());
			
			pstmt.setString(2, reply.getAuthorUsername());
			
			pstmt.setString(3,  reply.getBody());
			
			pstmt.setString(4,  reply.getTimestamp());
			
			pstmt.setBoolean(5, reply.getIsAccepted());
			
			pstmt.executeUpdate();
		}
	}
	
	/*******
	 * <p> Method: Reply getReplyById(int replyId) </p>
	 * 
	 * <p> Description: Retrieves a single Reply record from the replyDB table whose replyId
	 * matches the specified value. Returns null if no matching reply is found. </p>
	 * 
	 * @param replyId is an int that specifies the unique identifier of the reply to retrieve.
	 * 
	 * @return the Reply object with the matching replyId, or null if not found.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public Reply getReplyById (int replyId) throws SQLException {
		String query = "SELECT * FROM replyDB where replyId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			pstmt.setInt(1, replyId); 
			
			ResultSet res = pstmt.executeQuery();
			
			if (res.next()) {
				return new Reply(
						res.getInt("replyId"),
						res.getInt("parentPostId"),
						res.getString("authorUsername"),
						res.getString("body"),
						res.getString("timestamp"),
						res.getBoolean("isAccepted")
				);
					
			}
			
			return null;
			
		}
	}
	
	/*******
	 * <p> Method: List&lt;Reply&gt; getReplyByPostId (int postId) </p>
	 * 
	 * <p> Description: Retrieves a list of Reply records from the replyDB table whose parentPostId
	 * matches the specified value. Returns an empty list is no matching replies are found. </p>
	 * 
	 * @param postId is an int that specifies the unique identifier of the parentPostId.
	 * 
	 * @return A list consisting of Reply object with matching parentPostId, or an empty list if no answer found.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public List<Reply> getReplyByPostId (int postId) throws SQLException {
		String query = "SELECT * FROM replyDB where parentPostId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			pstmt.setInt(1, postId);
			
			ResultSet res = pstmt.executeQuery();
			
			List<Reply> replies = new ArrayList<>(); 
			
			while (res.next()) {
				replies.add(new Reply(
						res.getInt("replyId"),
						res.getInt("parentPostId"),
						res.getString("authorUsername"),
						res.getString("body"),
						res.getString("timestamp"),
						res.getBoolean("isAccepted")
				));
			}
			
			return replies; 
		}
	}
	
	/*******
	 * <p> Method: List&lt;Reply&gt; getAllReplies() </p>
	 * 
	 * <p> Description: Retrieves all Reply records from the replyDB table and returns them as a
	 * List of Reply objects. Returns an empty list if no replies exist. This supports the
	 * requirement to store and retrieve all replies to all stored posts. </p>
	 * 
	 * @return a List of all Reply objects currently stored in the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public List<Reply> getAllReplies() throws SQLException {
		String query = "SELECT * FROM replyDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			
			ResultSet res = pstmt.executeQuery();  
			
			List<Reply> replies = new ArrayList<>();
			
			while (res.next()) {
				replies.add(new Reply(
						res.getInt("replyId"),
						res.getInt("parentPostId"),
						res.getString("authorUsername"),
						res.getString("body"),
						res.getString("timestamp"),
						res.getBoolean("isAccepted")
				));
										
			}
			
			return replies; 
		}
	}
	
	/*******
	 * <p> Method: void updateReply(Reply reply) </p>
	 * 
	 * <p> Description: Updates an existing reply record in the replyDB table using the provided
	 * Reply object. The replyId field is used to identify which record to update. All other
	 * fields are overwritten with the values from the provided Reply object. </p>
	 * 
	 * @param reply specifies the Reply object containing the updated values to write to the database.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public void updateReply(Reply reply) throws SQLException {
		String query = "UPDATE replyDB SET parentPostId = ?, "
				+ "authorUsername = ?, "
				+ "body = ?, "
				+ "timestamp = ?, "
				+ "isAccepted = ? "
				+ "WHERE replyId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reply.getParentPostId());
			pstmt.setString(2, reply.getAuthorUsername());
			pstmt.setString(3,  reply.getBody());
			pstmt.setString(4, reply.getTimestamp());
			pstmt.setBoolean(5, reply.getIsAccepted());
			pstmt.setInt(6, reply.getReplyId());
			
			pstmt.execute();
		}
	}
	
	/*******
	 * <p> Method: void deleteReply(int replyId) </p>
	 * 
	 * <p> Description: Deletes the reply record from the replyDB table whose replyId matches the
	 * specified value. If no matching record exists, the operation completes silently with no
	 * effect. </p>
	 * 
	 * @param replyId is an int that specifies the unique identifier of the reply to delete.
	 * 
	 * @throws SQLException when there is an issue creating the SQL command or executing it.
	 * 
	 */
	public void deleteReply(int replyId) throws SQLException {
		String query = "DELETE FROM replyDB WHERE replyId = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyId);
			
			pstmt.execute(); 
		}
	}

}
