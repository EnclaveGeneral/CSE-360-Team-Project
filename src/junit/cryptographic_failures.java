/**
 * the junit for the Cryptographic Failures
 * 
 * @author josh sprague
 */
package junit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import applicationMain.FoundationsMain;
import database.Database;
import database.encrypt;
import entityClasses.User;

class cryptographic_failures {
	private Database data_base = FoundationsMain.database;
	private encrypt en;
	private ArrayList<User> list;
	private User user;
	
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
		list = new ArrayList<>();
		list.add(user);
		en = new encrypt("miku1", 5);
	}

	/*******
	 * <p> Method: test_encryption() </p>
	 *
	 * <p> Description: checks if the encryption works </p>
	 *
	 */
	@Test
	void test_encryption() {
		user.setPassword(en.encrypt_data(list.get(0).getPassword()));
		user.setEmailAddress(en.encrypt_data(list.get(0).getEmailAddress()));
		assertEquals(list.get(0).getPassword(), "Rr66666!");
		assertEquals(list.get(0).getEmailAddress(), "jcfruqj@lrfnq.htr");
	}
	
	/*******
	 * <p> Method: decryption() </p>
	 *
	 * <p> Description: checks if the decryption works </p>
	 *
	 */
	@Test
	void test_decryption() {
		user.setPassword(en.encrypt_data(list.get(0).getPassword()));
		user.setEmailAddress(en.encrypt_data(list.get(0).getEmailAddress()));
		assertEquals(list.get(0).getPassword(), "Rr66666!");
		assertEquals(list.get(0).getEmailAddress(), "jcfruqj@lrfnq.htr");
		
		user.setPassword(en.decrypt_data(list.get(0).getPassword(), "miku1"));
		user.setEmailAddress(en.decrypt_data(list.get(0).getEmailAddress(), "miku1"));
		assertEquals(list.get(0).getPassword(), "Mm11111!");
		assertEquals(list.get(0).getEmailAddress(), "example@gmail.com");
	}

}
