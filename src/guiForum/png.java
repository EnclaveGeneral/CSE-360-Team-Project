/**
 * @author Joshua Sprague
 */
package guiForum;

import java.util.Objects;

import javafx.scene.image.Image;

/**
 * info needed for the png
 */
public class png {
	public String user;
	public Image pic;
	public String filename;
	public int row;
	public int col;
	
	/**
	 * constructor for png
	 * 
	 * @param user
	 * @param pic
	 * @param filename
	 * @param row
	 * @param col
	 */
	public png(String user, Image pic, String filename, int row, int col) {
		this.pic = pic;
		this.row = row;
		this.col = col;
		this.user = user;
		this.filename = filename;
	}
	
	/**
	 * set the user
	 * 
	 * @param user
	 */
	public void set_user(String user) {
		this.user = user;
	}
	
	/**
	 * set the filename
	 * 
	 * @param filename
	 */
	public void set_filename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * set the pic
	 * 
	 * @param pic
	 */
	public void set_pic(Image pic) {
		this.pic = pic;
	}
	
	/**
	 * get the pic
	 * 
	 * @return pic
	 */
	public Image get_pic() {
		return pic;
	}
	
	/**
	 * get the user
	 * 
	 * @return user
	 */
	public String get_user() {
		return user;
	}
	
	/**
	 * get the filename
	 * 
	 * @return filename
	 */
	public String get_filename() {
		return filename;
	}
	
	/**
	 * get the row
	 * 
	 * @return row
	 */
	public int get_row() {
		return row;
	}
	
	/**
	 * get the col
	 * 
	 * @return col
	 */
	public int get_col() {
		return col;
	}
	
	/**
	 * equal method
	 * 
	 * @param obj
	 * 
	 * @return true or false
	 */
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    png other = (png) obj;
	    return Objects.equals(this.get_filename(), other.get_filename()) &&
	           Objects.equals(this.get_user(), other.get_user());
	}

	/**
	 * gets the hash code
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
	    return Objects.hash(get_filename(), get_user());
	}
}
