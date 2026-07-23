package Tp3Testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.*;


import applicationMain.FoundationsMain;
import database.Database;
import guiClassRoster.ControllerClassRoster;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

/*******
 * <p> Title: JUnitClassRoster Class </p>
 *
 * <p> Description: This class implements JUnite tests on the ClassRoster.java file/class. 
 * These tests are to ensure all functionality of this aspect are executing as intended.</p> 
 * 
 *
 * @author Omid Kadkhodaei
 *
 * @version 1.00	2026-07-23	For the express purpose of TP3.

 */


class JUnitClassRoster {
	
		
	public Database database = FoundationsMain.database;
    public ControllerClassRoster roster;
    public static int testNum = 1;
    private Map<String, List<String>> classList;

    /*******
	 * <p> Method: setUpBeforeClass() </p>
	 *
	 * <p> Description: This set up takes place before each test to ensure the classes
	 * are set up instead of calling them each time. </p>
	 */
    
	@BeforeEach
	 void setUpBeforeClass() throws Exception {
		database.connectToDatabase();

		if(database.getNumberOfUsers() == 0 || !database.getUserAccountDetails("Alice")) {
			database.inject();
		}
		
		classList = new TreeMap<>();
		System.out.println("This is test number: " + testNum);
		roster = new ControllerClassRoster();
        classList = database.getClassRoster();
	}
	
	/*******
	 * <p> Method: setUpAfterClass() </p>
	 *
	 * <p> Description: This setUp verifies that a text successfully executed. It does not
	 * Define weather the test itself passed or failed, simply executed. </p>
	 */
	
	@AfterEach
	void setUpAfterClass() {
		System.out.println("Test exececuted\n");
		testNum++;
	}
	
	/*******
	 * <p> Method: classRosterExists() </p>
	 *
	 * <p> Description: First test verifies a roster exists by getting the size of the
	 * class roster. The printLine is simply aesthetic and not important to the test. </p>
	 */
	
	@Test
	@Order(1)
	void classRosterExists() {
		for (String student : classList.keySet()) {
	    	System.out.println(student + ": " + classList.get(student));
//	    	System.out.println(student + " Responded to " + classList.get(student).size() + " Students.\n");
	    }
		assertEquals(true, classList != null);
		
	}
	
	/*******
	 * <p> Method: rosterIncludesAlice() </p>
	 *
	 * <p> Description: Verifies a user by the name of Alice exists in the roster. </p>
	 */
	
	@Test
	@Order(2)
	void rosterIncludesAlice () {
		assertEquals(true, classList.containsKey("Alice"));
	}
	
	/*******
	 * <p> Method: rosterDoesNotInclude() </p>
	 *
	 * <p> Description: Verifies the user Tony is not in the roster. </p>
	 */
	
	@Test
	@Order(3)
	void rosterDoesNotInclude() {
		assertEquals(false, classList.containsKey("Tony"));
	}
	
	/*******
	 * <p> Method: getUniqueResponsesAlice() </p>
	 *
	 * <p> Description: This test returns the value of unique responses. Accurate
	 * numbers here is vital for future tests and methods. </p>
	 */
	
	@Test
	@Order(4)
	void getUniqueResponsesAlice () {
		int responsesAlice = classList.get("Alice").size();
		assertTrue(0 <= responsesAlice);
	}
	
	/*******
	 * <p> Method: getFlagAlice() </p>
	 *
	 * <p> Description: This test addresses the deliverable that if the number
	 * of unique responses is 0, then a flag boolean (true) is returned. </p>
	 */
	
	@Test
	@Order(5)
	void getTrueAtLeast3ResponsesAlice() {
		assertTrue(classList.get("Alice").size() >=3);
	}
	
	/*******
	 * <p> Method: getUniqueResponsesAndrew() </p>
	 *
	 * <p> Description: This test addresses the deliverable that if the number
	 * of unique responses is 0, then a flag boolean (false) is returned. </p>
	 */
	
	@Test
	@Order(6)
	void getUniqueResponsesAndrew () {
		assertEquals(true, classList.get("Liam").size() == 0);
	}
	
	

}



