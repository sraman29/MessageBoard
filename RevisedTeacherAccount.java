/**
 * RevisedTeacherAccount.java
 *
 * Organizes information about teacher objects based on user input and server output, and allows
 * teacher-oriented actions to be performed by the teacher user.
 *
 * @author Emma Maki, lab sec 13180-L19
 *
 * @version April 22, 2022
 */

public class RevisedTeacherAccount extends RevisedAccount {
    private String exists;

    //for reading from file
    RevisedTeacherAccount(String username, String password, String exists) {
        super(username, password, exists);
    }

    //for when user creates new teacher account
    RevisedTeacherAccount(String username, String password) {
        super(username, password);
        this.exists = "true";
    }

    public String toString() {
        return super.toString() + " [account_type]:teacher";
    }

}
