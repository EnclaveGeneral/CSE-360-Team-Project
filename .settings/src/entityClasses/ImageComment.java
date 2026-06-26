package entityClasses;

/*******
 * <p> Title: ImageComment Class </p>
 *
 * <p> Description: This ImageComment class represents a comment left on an ImagePost in the
 * unified discussion board. Each comment stores the message text and the username of the
 * commenter. Comments are associated with their parent ImagePost through the database's
 * comments table and are not stored as a direct field on ImagePost. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Joshua Sprague, Weiye (Richard) Zhang
 *
 * @version 1.00	2026-06-22	Initial version in guiForum package
 * @version 2.00	2026-06-23	Moved to entityClasses; renamed from comment to ImageComment
 */
public class ImageComment {

	/*
	 * These are the public attributes for this entity object
	 */
	public String user;
	public String message;


	/*******
	 * <p> Method: ImageComment(String message, String user) </p>
	 *
	 * <p> Description: This constructor is used to establish ImageComment entity objects. </p>
	 *
	 * @param message specifies the text content of this comment
	 *
	 * @param user    specifies the username of the commenter
	 *
	 */
	public ImageComment(String message, String user) {
		this.user    = user;
		this.message = message;
	}


	/*******
	 * <p> Method: void set_user(String user) </p>
	 *
	 * <p> Description: This setter defines the user attribute. </p>
	 *
	 * @param user is a String that specifies the username of the commenter.
	 *
	 */
	public void set_user(String user) {
		this.user = user;
	}


	/*******
	 * <p> Method: void set_message(String message) </p>
	 *
	 * <p> Description: This setter defines the message attribute. </p>
	 *
	 * @param message is a String that specifies the text content of this comment.
	 *
	 */
	public void set_message(String message) {
		this.message = message;
	}


	/*******
	 * <p> Method: String get_message() </p>
	 *
	 * <p> Description: This getter returns the text content of this comment. </p>
	 *
	 * @return a String of the comment message
	 *
	 */
	public String get_message() {
		return message;
	}


	/*******
	 * <p> Method: String get_user() </p>
	 *
	 * <p> Description: This getter returns the username of the commenter. </p>
	 *
	 * @return a String of the commenter's username
	 *
	 */
	public String get_user() {
		return user;
	}

}
