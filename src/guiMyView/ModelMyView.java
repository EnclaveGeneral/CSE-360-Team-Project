package guiMyView;

/*******
 * <p> Title: ModelMyView Class </p>
 *
 * <p> Description: This class implements the Model component of the MVC design pattern for
 * the unified MyView Board page. It provides input validation methods for post and reply
 * fields. All methods are static since this class is never instantiated directly. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-06-15	Initial version for HW2 text-only MyView board
 */
public class ModelMyView {


	/*******
	 * <p> Method: ModelMyView() </p>
	 *
	 * <p> Description: The default constructor. Not used directly since all methods are static,
	 * but required by the MVC pattern for consistency with other model classes. </p>
	 *
	 */
	public ModelMyView() {

	}


	/*******
	 * <p> Method: String validateTitle(String title) </p>
	 *
	 * <p> Description: Validates that a post title is not null or blank. Returns an empty
	 * String if valid so that the caller can check isEmpty() to determine success. </p>
	 *
	 * @param title is a String that specifies the post title to validate.
	 *
	 * @return an empty String if the title is valid, or an error message String if not
	 *
	 */
	public static String validateTitle(String title) {
		if (title == null || title.isBlank())
			return "Error: Post title cannot be empty.";
		return "";
	}


	/*******
	 * <p> Method: String validateBody(String body) </p>
	 *
	 * <p> Description: Validates that a body field (post body or reply body) is not null or
	 * blank. Returns an empty String if valid so that the caller can check isEmpty() to
	 * determine success. </p>
	 *
	 * @param body is a String that specifies the body text to validate.
	 *
	 * @return an empty String if the body is valid, or an error message String if not
	 *
	 */
	public static String validateBody(String body) {
		if (body == null || body.isBlank())
			return "Error: Body cannot be empty.";
		return "";
	}


	/*******
	 * <p> Method: String validateAuthor(String author) </p>
	 *
	 * <p> Description: Validates that an author field is not null or blank. Returns an empty
	 * String if valid so that the caller can check isEmpty() to determine success. </p>
	 *
	 * @param author is a String that specifies the author username to validate.
	 *
	 * @return an empty String if the author is valid, or an error message String if not
	 *
	 */
	public static String validateAuthor(String author) {
		if (author == null || author.isBlank())
			return "Error: Author cannot be empty.";
		return "";
	}


	/*******
	 * <p> Method: String validateImageFile(java.io.File file) </p>
	 *
	 * <p> Description: Validates that the user selected an image file in the FileChooser
	 * dialog. A null value indicates that no file was chosen. Returns an empty String if
	 * valid so that the caller can check isEmpty() to determine success. </p>
	 *
	 * @param file is the java.io.File returned by the FileChooser dialog, which may be null
	 *             if the user dismissed the dialog without selecting a file.
	 *
	 * @return an empty String if a file was selected, or an error message String if not
	 *
	 */
	public static String validateImageFile(java.io.File file) {
		if (file == null)
			return "Error: No image file selected.";
		return "";
	}

}
