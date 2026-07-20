/**
 * encrypt and decrypt data
 * 
 * @author josh sprague
 */
package database;

public class encrypt {
	private String password;
	private int shift;
	
	/*******
	 * <p> Method: encrypt() </p>
	 *
	 * <p> Description: initialize stuff </p>
	 * 
	 * @param password the password needed to decrypt data
	 * @param shift the amount to shift by
	 *
	 */
	public encrypt(String password, int shift) {
		this.password = password;
		this.shift = shift;
	}
	
	/*******
	 * <p> Method: encrypt_data() </p>
	 *
	 * <p> Description: encrypt the data given </p>
	 * 
	 * @param str the string that is going to be encrypted
	 * 
	 * @return the encrypted string
	 *
	 */
	public String encrypt_data(String str) {
        StringBuilder encrypted = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Shift lowercase letters
            if (c >= 'a' && c <= 'z') {
                c = (char) ((c - 'a' + shift) % 26 + 'a');
            }
            // Shift uppercase letters
            else if (c >= 'A' && c <= 'Z') {
                c = (char) ((c - 'A' + shift) % 26 + 'A');
            }
            // Shift digits 0-9
            else if (c >= '0' && c <= '9') {
                c = (char) ((c - '0' + shift) % 10 + '0');
            }

            encrypted.append(c);
        }

        return encrypted.toString();
    }

	/*******
	 * <p> Method: decrypt_data() </p>
	 *
	 * <p> Description: decrypt the data given </p>
	 * 
	 * @param str the string that is going to be decrypted
	 * @param pass the password needed
	 * 
	 * @return the decrypted string
	 *
	 */
    public String decrypt_data(String str, String pass) {
    	if(!pass.equals(password)) {
    		System.out.println("wrong password");
    		return null;
    	}
        StringBuilder decrypted = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Reverse shift lowercase letters
            if (c >= 'a' && c <= 'z') {
                c = (char) ((c - 'a' - shift + 26) % 26 + 'a');
            }
            // Reverse shift uppercase letters
            else if (c >= 'A' && c <= 'Z') {
                c = (char) ((c - 'A' - shift + 26) % 26 + 'A');
            }
            // Reverse shift digits 0-9
            else if (c >= '0' && c <= '9') {
                c = (char) ((c - '0' - shift + 10) % 10 + '0');
            }

            decrypted.append(c);
        }

        return decrypted.toString();
    }
    
    public boolean check_password(String password) {
    	return this.password.equals(password);
    }
}
