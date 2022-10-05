import java.util.ArrayList;

/**
 * RevisedStudentAccount.java
 *
 * Organizes information about student objects based on user input and server output, and allows
 * student-oriented actions to be performed by the student user.
 *
 * @author Emma Maki, lab sec 13180-L19
 *
 * @version April 25, 2022
 *
 */

public class RevisedStudentAccount extends RevisedAccount {
    private ArrayList<Reply> studentReplies;
    private int totalUpvotes;

    RevisedStudentAccount(String username, String password, String exists, int totalUpvotes, ArrayList<Reply>
            studentReplies) {
        super(username, password, exists);
        this.totalUpvotes = totalUpvotes;
        this.studentReplies = studentReplies;
    }

    //for reading from file
    RevisedStudentAccount(String username, String password, String exists) {
        super(username, password, exists);
        this.studentReplies = new ArrayList<>();
        this.totalUpvotes = 0;
    }

    //for when user creates new student account
    RevisedStudentAccount(String username, String password) {
        super(username, password);
        this.studentReplies = new ArrayList<>();
        this.totalUpvotes = 0;
    }

    //specifies that the Reply object created was by this RevisedStudentAccount object
    public void addStudentReply(Reply reply) {
        studentReplies.add(reply);
        this.totalUpvotes += reply.getUpvotes();
    }

    public String toString() {
        return super.toString() + " [account_type]:student";
    }

}
