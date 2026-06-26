package entityClasses;

import java.util.Objects;
import javafx.scene.image.Image;

/*******
 * <p> Title: ImagePost Class </p>
 *
 * <p> Description: This ImagePost class represents an image uploaded to the unified discussion
 * board. It stores the uploader's username, the decoded JavaFX Image, the original filename,
 * and the grid position (row, col) used when displaying images in the forum. Equality and
 * hashing are based on filename and username so that the same image cannot be inserted twice
 * by the same user. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Joshua Sprague, Weiye (Richard) Zhang
 *
 * @version 1.00	2026-06-22	Initial version in guiForum package
 * @version 2.00	2026-06-23	Moved to entityClasses; renamed from png to ImagePost
 */
public class ImagePost {

	/*
	 * These are the private attributes for this entity object
	 */
	public String user;
	public Image  pic;
	public String filename;
	public int    row;
	public int    col;


	/*******
	 * <p> Method: ImagePost(String user, Image pic, String filename, int row, int col) </p>
	 *
	 * <p> Description: This constructor is used to establish ImagePost entity objects. </p>
	 *
	 * @param user     specifies the username of the user who uploaded this image
	 *
	 * @param pic      specifies the decoded JavaFX Image object
	 *
	 * @param filename specifies the original filename of the uploaded image
	 *
	 * @param row      specifies the row position of this image in the display grid
	 *
	 * @param col      specifies the column position of this image in the display grid
	 *
	 */
	public ImagePost(String user, Image pic, String filename, int row, int col) {
		this.pic      = pic;
		this.row      = row;
		this.col      = col;
		this.user     = user;
		this.filename = filename;
	}


	/*******
	 * <p> Method: void set_user(String user) </p>
	 *
	 * <p> Description: This setter defines the user attribute. </p>
	 *
	 * @param user is a String that specifies the username of the uploader.
	 *
	 */
	public void set_user(String user) {
		this.user = user;
	}


	/*******
	 * <p> Method: void set_filename(String filename) </p>
	 *
	 * <p> Description: This setter defines the filename attribute. </p>
	 *
	 * @param filename is a String that specifies the original filename of the image.
	 *
	 */
	public void set_filename(String filename) {
		this.filename = filename;
	}


	/*******
	 * <p> Method: void set_pic(Image pic) </p>
	 *
	 * <p> Description: This setter defines the pic attribute. </p>
	 *
	 * @param pic is a JavaFX Image that specifies the decoded image object.
	 *
	 */
	public void set_pic(Image pic) {
		this.pic = pic;
	}


	/*******
	 * <p> Method: Image get_pic() </p>
	 *
	 * <p> Description: This getter returns the decoded JavaFX Image. </p>
	 *
	 * @return the JavaFX Image object for this post
	 *
	 */
	public Image get_pic() {
		return pic;
	}


	/*******
	 * <p> Method: String get_user() </p>
	 *
	 * <p> Description: This getter returns the username of the uploader. </p>
	 *
	 * @return a String of the uploader's username
	 *
	 */
	public String get_user() {
		return user;
	}


	/*******
	 * <p> Method: String get_filename() </p>
	 *
	 * <p> Description: This getter returns the original filename of the image. </p>
	 *
	 * @return a String of the image filename
	 *
	 */
	public String get_filename() {
		return filename;
	}


	/*******
	 * <p> Method: int get_row() </p>
	 *
	 * <p> Description: This getter returns the row position in the display grid. </p>
	 *
	 * @return an int of the row position
	 *
	 */
	public int get_row() {
		return row;
	}


	/*******
	 * <p> Method: int get_col() </p>
	 *
	 * <p> Description: This getter returns the column position in the display grid. </p>
	 *
	 * @return an int of the column position
	 *
	 */
	public int get_col() {
		return col;
	}


	/*******
	 * <p> Method: boolean equals(Object obj) </p>
	 *
	 * <p> Description: Two ImagePost objects are considered equal if they share the same
	 * filename and username. This prevents the same user from uploading duplicate images. </p>
	 *
	 * @param obj is the Object to compare against this ImagePost.
	 *
	 * @return true if obj is an ImagePost with the same filename and user, else false
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		ImagePost other = (ImagePost) obj;
		return Objects.equals(this.get_filename(), other.get_filename()) &&
		       Objects.equals(this.get_user(),     other.get_user());
	}


	/*******
	 * <p> Method: int hashCode() </p>
	 *
	 * <p> Description: This getter returns the hash code derived from filename and username,
	 * consistent with the equals() contract. </p>
	 *
	 * @return an int hash code for this ImagePost
	 *
	 */
	@Override
	public int hashCode() {
		return Objects.hash(get_filename(), get_user());
	}

}
