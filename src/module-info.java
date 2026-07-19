/*******
 * <p> Title: FoundationsF26 </p>
 *
 * <p> Description: Module-info folder that ties the libraries together</p>
 *
*/

module FoundationsF26 {
    requires javafx.controls;
    requires java.sql;
	requires javafx.graphics;
	requires javafx.swing;
    
    opens applicationMain to javafx.graphics, javafx.fxml;
    opens guiAdminHome to javafx.graphics, javafx.fxml;
    opens guiFirstAdmin to javafx.graphics, javafx.fxml;
    opens guiUserLogin to javafx.graphics, javafx.fxml;
    opens guiNewAccount to javafx.graphics, javafx.fxml;
    opens guiUserUpdate to javafx.graphics, javafx.fxml;
    opens guiRole1 to javafx.graphics, javafx.fxml;
    opens guiRole2 to javafx.graphics, javafx.fxml;
    opens guiAddRemoveRoles to javafx.graphics, javafx.fxml;
    opens guiMultipleRoleDispatch to javafx.graphics, javafx.fxml;
    opens guiListUsers to javafx.graphics, javafx.fxml;
    opens guiManageInvitations to javafx.graphics, javafx.fxml;
    opens guiDeleteUser to javafx.graphics, javafx.fxml;
    opens guiSetOnetimePassword to javafx.graphics, javafx.fxml;
    opens guiForcePasswordReset to javafx.graphics, javafx.fxml;
    opens guiDiscussion to javafx.graphics, javafx.fxml;
    opens passwordPopUpWindow to javafx.graphics, javafx.fxml;
    opens passwordEvaluationTestbedMain to javafx.graphics, javafx.fxml;
    opens entityClasses to javafx.graphics, javafx.fxml;
    opens database to javafx.graphics, javafx.fxml;
}