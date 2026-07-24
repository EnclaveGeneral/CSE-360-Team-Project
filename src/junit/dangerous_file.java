/**
 * the junit for the Unrestricted Upload of File with Dangerous Type
 * 
 * @author josh sprague
 */
package junit;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import database.Database;
import entityClasses.ImageComment;
import entityClasses.ImagePost;
import entityClasses.User;

@TestMethodOrder(OrderAnnotation.class)
class dangerous_file {
    private Database data_base;
    private User user;

    /*******
	 * <p> Method: createJpgImage() </p>
	 *
	 * <p> Description: create a 1 by 1 pixel jpg </p>
	 *
	 */
    public Image createJpgImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, 0xFF0000); // red pixel

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        return new Image(new ByteArrayInputStream(imageBytes));
    }
    
    /*******
	 * <p> Method: createPngImage() </p>
	 *
	 * <p> Description: create a 1 by 1 pixel png </p>
	 *
	 */
    public Image createPngImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, 0xFF0000); // red pixel

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        return new Image(new ByteArrayInputStream(imageBytes));
    }
    
    /*******
	 * <p> Method: createJpegImage() </p>
	 *
	 * <p> Description: create a 1 by 1 pixel jpeg </p>
	 *
	 */
    public Image createJpegImage() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, 0xFF0000); // red pixel

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        return new Image(new ByteArrayInputStream(imageBytes));
    }
    
    /*******
	 * <p> Method: setUp() </p>
	 *
	 * <p> Description: initialize stuff </p>
	 *
	 */
    @BeforeEach
    void setUp() throws Exception {
        data_base = new Database();
        data_base.connectToDatabase();

        user = new User("miku", "Mm11111!", "josh", "allen", "sprague", "Joshua", "example@gmail.com", true, false, false);
        data_base.register(user);
    }

    /*******
	 * <p> Method: test_png() </p>
	 *
	 * <p> Description: shows that we can insert a png into the database </p>
	 *
	 */
    @Test
    @Order(1)
    void test_png() {
	    Image dummyPngImage = null;
	    
		try {
			dummyPngImage = createPngImage();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Save image entry with empty comments and valid row/col
	    data_base.saveImageEntry("miku", "test.png", dummyPngImage, new ArrayList<>(), 2, 2);

	    // make sure list is 1
	    HashMap<ImagePost, ArrayList<ImageComment>> map = data_base.loadImageEntries();
  		System.out.println(map);
  		assertEquals(map.size(), 1);
  		
  		// check image extension
  		for (ImagePost key : map.keySet()) {
  			assertTrue(key.get_filename().contains(".png"));
  		}
	}

    /*******
	 * <p> Method: test_jpg() </p>
	 *
	 * <p> Description: shows that we can insert a jpg into the database </p>
	 *
	 */
	@Test
	@Order(2)
	void test_jpg() {
	    Image dummyJpgImage = null;
		try {
			dummyJpgImage = createJpgImage();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Save image entry with empty comments and valid row/col
	    data_base.saveImageEntry("miku", "test.jpg", dummyJpgImage, new ArrayList<>(), 2, 2);

	    // make sure list is 1
	    HashMap<ImagePost, ArrayList<ImageComment>> map = data_base.loadImageEntries();
  		System.out.println(map);
  		assertEquals(map.size(), 1);
	  		
  		// check image extension
  		for (ImagePost key : map.keySet()) {
  			assertTrue(key.get_filename().contains(".jpg"));
  		}
  	}

	/*******
	 * <p> Method: test_jpeg() </p>
	 *
	 * <p> Description: shows that we can insert a jpeg into the database </p>
	 *
	 */
	@Test
	@Order(3)
	void test_jpeg() {
	    Image dummyJpegImage = null;
		try {
			dummyJpegImage = createJpegImage();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Save image entry with empty comments and valid row/col
	    data_base.saveImageEntry("miku", "test.jpeg", dummyJpegImage, new ArrayList<>(), 2, 2);

	    // make sure list is 1
	    HashMap<ImagePost, ArrayList<ImageComment>> map = data_base.loadImageEntries();
  		System.out.println(map);
  		assertEquals(map.size(), 1);
	  		
  		// check image extension
  		for (ImagePost key : map.keySet()) {
  			assertTrue(key.get_filename().contains(".jpeg"));
  		}
	}
}
