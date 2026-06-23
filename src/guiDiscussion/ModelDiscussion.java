package guiDiscussion;

/*******
 * <p> Title: ModelDiscussion Class </p>
 *
 * <p> Description: This class implements the Model component of the MVC design pattern for
 * the Discussion Board page. It provides input validation methods for post and reply fields,
 * returning error message strings when validation fails and empty strings when validation
 * passes. An empty string return value indicates valid input. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Weiye (Richard) Zhang
 *
 * @version 1.00	2026-06-15
 */
public class ModelDiscussion {

    /*******
     * <p> Method: String validatePostTitle(String title) </p>
     *
     * <p> Description: Validates the title field for a post. The title must not be null
     * or empty, and must not exceed 255 characters to match the database column size. </p>
     *
     * @param title is the String title value to validate.
     *
     * @return an empty String if valid, or an error message String if invalid.
     *
     */
    public static String validatePostTitle(String title) {
        if (title == null || title.isEmpty())
            return "Error: Post title cannot be empty.";
        if (title.length() > 255)
            return "Error: Post title cannot exceed 255 characters.";
        return "";
    }

    /*******
     * <p> Method: String validateBody(String body) </p>
     *
     * <p> Description: Validates the body field for a post or reply. The body must not be
     * null or empty, and must not exceed 2000 characters to match the database column size. </p>
     *
     * @param body is the String body value to validate.
     *
     * @return an empty String if valid, or an error message String if invalid.
     *
     */
    public static String validateBody(String body) {
        if (body == null || body.isEmpty())
            return "Error: Body cannot be empty.";
        if (body.length() > 2000)
            return "Error: Body cannot exceed 2000 characters.";
        return "";
    }

    /*******
     * <p> Method: String validateAuthor(String author) </p>
     *
     * <p> Description: Validates the author field for a post or reply. The author must not
     * be null or empty, and must not exceed 255 characters to match the database column size. </p>
     *
     * @param author is the String author value to validate.
     *
     * @return an empty String if valid, or an error message String if invalid.
     *
     */
    public static String validateAuthor(String author) {
        if (author == null || author.isEmpty())
            return "Error: Author cannot be empty.";
        if (author.length() > 255)
            return "Error: Author cannot exceed 255 characters.";
        return "";
    }

}