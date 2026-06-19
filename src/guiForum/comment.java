/**
 * @author Joshua Sprague
 */
package guiForum;

/**
 * provides the info needed for each comment
 */
public class comment {
	public String user;
	public String message;
	
	/**
	 * constructor for comment
	 * 
	 * @param message
	 * @param user
	 */
	public comment(String message, String user) {
		this.user = user;
		this.message = message;
	}
	
	/**
	 * set user
	 * 
	 * @param user
	 */
	public void set_user(String user) {
		this.user = user;
	}
	
	/**
	 * set message
	 * 
	 * @param message
	 */
	public void set_message(String message) {
		this.message = message;
	}
	
	/**
	 * get message
	 * 
	 * @return message
	 */
	public String get_message() {
		return message;
	}
	
	/**
	 * get user
	 * 
	 * @return user
	 */
	public String get_user() {
		return user;
	}
}
