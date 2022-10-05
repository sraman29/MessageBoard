import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;


/**
 * ClientHandler.java
 * 
 * This class receives and handles information from a given Client thread based on user input/interaction.
 *
 * @author Emma Maki, Henry Merchant, lab sec 13180-L19
 *
 * @version May 2, 2022
 */

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private String username;
    private String password;
    private static ArrayList<String> nUsers = new ArrayList<>();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // get the output stream of client
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // get the input stream of client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            boolean forceLogout = false;
            RevisedAccount user = null;
            DiscussionForum.clearDFs();
            RevisedAccount.clearAccounts();
            RevisedAccount.readAccounts();
            Reply.readDF("discussionforum.txt");

            while (!(line = in.readLine()).equals("logout") || forceLogout) { //quit message
                //printing the message from client
                switch (line) {
                    case ("login"): //read: username -> password ->
                        username = in.readLine().trim();
                        password = in.readLine().trim();
                        //synchronized test login
                        synchronized (this) {
                            System.out.println("Username: " + username);
                            System.out.println("Password: " + password);
                            user = RevisedAccount.login(username, password);
                        }
                        if (user != null) { //&& !nUsers.contains(username)) { //successful login
                            //if (nUsers.contains(username)) {
                            // out.println("alreadyLoggedIn");
                            //} else {
                            out.println("loginSuccess");
                            System.out.println("Server: true");
                            out.println(user.getAccountType());
                            System.out.println("Server: " + user.getAccountType());
                            synchronized (this) {
                                nUsers.add(username);
                            }
                            //}
                        } else {
                            System.out.println("login failed");
                            out.println("false");
                            System.out.println("Server: " + "false");
                        }
                        break;

                    case ("signUp"): //read username -> password -> acctType send: loginFail / loginSuccess
                        username = in.readLine();
                        System.out.println("received from client: " + username);
                        password = in.readLine();
                        System.out.println("received from client " + password);
                        String accountType = in.readLine();
                        System.out.println("received from client " + accountType);

                        boolean created;
                        synchronized (this) {
                            created = RevisedAccount.createAccount(username, password, accountType);
                            if (created) nUsers.add(username);
                        }
                        if (created) {
                            out.println("loginSuccess");
                            System.out.println("Server: " + "loginSuccess");
                        } else {
                            out.println("loginFail");
                            System.out.println("Server: " + "loginFail");
                        }
                        break;

                    case ("courseUpdate"): //send: size -> courses
                        ArrayList<String> courses;
                        synchronized (this) {
                            courses = DiscussionForum.getCourses();
                        }
                        out.println(courses.size()); //sends size
                        System.out.println("Server: " + courses.size());
                        for (String course : courses) {
                            out.println(course); //send courses
                            System.out.println("Server: " + course);
                        }
                        break;

                    case ("changePass"): //read old pass -> read new pass -> send passChanged or passNotChanged //
                        String newPassword = in.readLine();
                        System.out.println("received from client: " + newPassword);
                        boolean passChanged;
                        if (user != null) {
                            synchronized (this) {
                                System.out.println("change pass called");
                                passChanged = user.editPassword(password, newPassword);
                            }
                            if (passChanged) {
                                out.println("passwordChanged");
                                System.out.println("Server: " + "passwordChanged");
                            } else {
                                out.println("passwordNotChanged");
                                System.out.println("Server: " + "passwordNotChanged");
                            }
                        }
                        break;

                    case ("deleteAcct"): //sent true or false deletion
                        boolean deleted;
                        synchronized (this) {
                            assert user != null;
                            deleted = user.deleteAccount();
                        }
                        if (deleted) {
                            out.println("accountDeleted");
                            System.out.println("Server: " + "accountDeleted");
                        } else {
                            out.println("accountNotDeleted");
                            System.out.println("Server: " + "accountNotDeleted");
                        }
                        //making the user logout
                        forceLogout = true;
                        nUsers.remove(username);
                        break;

                    case ("course"): //read course -> sends: size of array list -> all elements of array list
                        String course = in.readLine();
                        System.out.println("received from client " + course);
                        ArrayList<String> dfs;
                        synchronized (this) {
                            dfs = DiscussionForum.getDFs(course);
                        }
                        out.println(dfs.size());
                        System.out.println("Server: " + dfs.size());
                        for (String df : dfs) {
                            out.println(df);
                            System.out.println("Server: " + df);
                        }
                        break;

                    case ("discussionForum"):
                        String dfName = in.readLine();
                        System.out.println("received from client " + dfName);
                        DiscussionForum df;
                        ArrayList<Reply> parentReplies;
                        synchronized (this) {
                            df = DiscussionForum.returnDF(dfName);
                            if (df != null) {
                                parentReplies = df.getParentReplies();
                            } else {
                                parentReplies = null;
                            }
                        }
                        if (parentReplies != null) {
                            out.println(parentReplies.size());
                            System.out.println("Server: " + parentReplies.size());
                            for (Reply parentReply : parentReplies) {
                                String currUsername = parentReply.getUser().getUsername();
                                out.println(currUsername); //sends username
                                System.out.println("Server: " + currUsername);
                                String time = parentReply.getTimestamp();
                                out.println(time); //sends timestamp
                                System.out.println("Server: " + time);
                                String reply = parentReply.getReplyString();
                                out.println(reply); //sends the reply contents
                                System.out.println("Server: " + reply);
                                int votes = parentReply.getUpvotes();
                                out.println(votes); //sends the upvotes
                                System.out.println("Server: " + votes);
                            }
                        } else {
                            out.println(0);
                            System.out.println("Server: " + 0);
                        }
                        break;

                    //given reply content and parent topic : add a reply to the txt file
                    case ("replyParent"): //content order: parentTopic
                        //receiving dfName
                        String dfNameReplying = in.readLine().trim();
                        System.out.println("dfName received from client in replyParent : " + dfNameReplying);
                        //receiving contents of new parent reply string
                        String newParentString = in.readLine().trim();
                        System.out.println("new parent string received from client in replyParent : " +
                                newParentString);
                        //receiving newly-formed timestamp
                        String timestamp = in.readLine().trim();
                        System.out.println("timestamp in replyParent received from client: " + timestamp);
                        String currUser = in.readLine().trim();
                        System.out.println("currUser in replyParent received from client: " + currUser);
                        //adding the parent reply
                        boolean replyAdded;
                        synchronized (this) {
                            String courseName = DiscussionForum.getCourseName(dfNameReplying);
                            replyAdded = Reply.addReply(courseName, dfNameReplying, newParentString,
                                    "", currUser, timestamp);
                        }
                        if (replyAdded) {
                            out.println("replyAdded");
                            System.out.println("replyAdded");
                        } else {
                            out.println("replyNotAdded");
                            System.out.println("replyNotAdded");
                        }
                        break;

                    //creates a nested reply
                    case ("replyNested"):
                        //receiving dfName
                        dfNameReplying = in.readLine().trim();
                        System.out.println("dfName in replyNested received from client: " + dfNameReplying);
                        //receiving parentName
                        String parentString = in.readLine().trim();
                        System.out.println("parent reply in replyNested received from client: " + parentString);
                        //receiving contents of new nested reply string
                        String newNestedString = in.readLine().trim();
                        System.out.println("new nested reply in replyNested received from client: " + newNestedString);
                        //receiving newly-formed timestamp
                        timestamp = in.readLine().trim();
                        System.out.println("timestamp in replyNested received from client: " + timestamp);
                        currUser = in.readLine().trim();
                        System.out.println("currUser in replyNested received from client: " + currUser);
                        //creating the nested reply
                        synchronized (this) {
                            String courseName = DiscussionForum.getCourseName(dfNameReplying);
                            replyAdded = Reply.addReply(courseName, dfNameReplying, parentString, newNestedString,
                                    currUser, timestamp);
                        }
                        if (replyAdded) {
                            out.println("replyAdded");
                        } else {
                            out.println("replyNotAdded");
                        }
                        break;

                    //given a parent reply, output the nested replies
                    case ("viewReply"):
                        String parentReply = in.readLine();
                        System.out.println("parentReply received from client in viewReply:" + parentReply);

                        ArrayList<Reply> nestedReplies;
                        synchronized (this) {
                            nestedReplies = Reply.getNestedReplies(parentReply);
                        }

                        out.println(nestedReplies.size());
                        for (Reply reply : nestedReplies) {
                            String currUsername = reply.getUser().getUsername();
                            out.println(currUsername);
                            System.out.println("username sent from server in viewReply: " + currUsername);
                            String time = reply.getTimestamp();
                            out.println(time);
                            System.out.println("time sent from server in viewReply: " + time);
                            String replyString = reply.getReplyString();
                            out.println(replyString);
                            System.out.println("reply string sent from server in viewReply: " +
                                    reply.getReplyString());
                            int votes = reply.getUpvotes();
                            out.println(votes);
                            System.out.println("votes in viewReply sent from server: " + votes);
                        }
                        break;

                    case ("createCourse"):
                        String newCourse = in.readLine();
                        boolean courseCreated;
                        synchronized (this) {
                            //creates a DF with a given course name and no DFs or DFName
                            courseCreated = DiscussionForum.createCourse(newCourse);
                        }
                        if (courseCreated) {
                            out.println("courseCreated");
                            System.out.println("Server: courseCreated");
                        } else {
                            out.println("courseNotCreated");
                            System.out.println("Server: courseNotCreated");
                        }
                        break;

                    // creating new
                    case ("createTopic"): //reads: course
                        String courseName = in.readLine();
                        System.out.println("Server: courseName: " + courseName);
                        String newTopic = in.readLine();
                        System.out.println("Server: newTopic name: " + newTopic);
                        boolean dfCreated;
                        synchronized (this) {
                            dfCreated = DiscussionForum.createDF(courseName, newTopic);
                        }
                        if (dfCreated) {
                            out.println("dfCreated");
                            System.out.println("Server: dfCreated");
                        } else {
                            out.println("dfNotCreated");
                            System.out.println("Server: dfNotCreated");
                        }
                        break;

                    case ("deleteQ"):
                        //name of the df to be deleted
                        String deleteDF = in.readLine();
                        boolean dfDeleted;
                        synchronized (this) {
                            dfDeleted = DiscussionForum.deleteDF(deleteDF);
                        }
                        if (dfDeleted) {
                            out.println("dfDeleted");
                            System.out.println("Server: dfDeleted");
                        } else {
                            out.println("dfNotDeleted");
                            System.out.println("Server: dfNotDeleted");
                        }
                        break;

                    case ("editQ"):
                        //name of df to be edited
                        String editDF = in.readLine();
                        //new name of df
                        String newNameDF = in.readLine();
                        boolean dfEdited;
                        synchronized (this) {
                            dfEdited = DiscussionForum.editDF(editDF, newNameDF);
                        }
                        if (dfEdited) {
                            out.println("dfEdited");
                            System.out.println("Server: dfEdited");
                        } else {
                            out.println("dfNotEdited");
                            System.out.println("Server: dfNotEdited");
                        }
                        break;

                    case ("viewStudents"):
                        ArrayList<String> students;
                        synchronized (this) {
                            students = RevisedAccount.getAllStudents();
                        }
                        out.println(students.size());
                        for (String student : students) {
                            out.println(student);
                        }
                        break;
                    case ("viewGrades"):
                        ArrayList<String> replies;
                        ArrayList<String> grades;
                        String currUsername = in.readLine().trim();
                        System.out.println("Server: username in viewGrades: " + currUsername);
                        synchronized (this) {
                            replies = Reply.getUserReplies(currUsername);
                            grades = Reply.getStudentGrades(currUsername);
                        }
                        out.println(replies.size());
                        System.out.println("size of both in viewGrade: " + replies.size());
                        for (String reply : replies) {
                            out.println(reply);
                            System.out.println("reply in viewGrade: " + reply);
                        }
                        for (String grade : grades) {
                            out.println(grade);
                            System.out.println("Grade in viewGrade: " + grade);
                        }
                        break;

                    case ("addGrade"):
                        String replyToGrade = in.readLine().trim(); //receives the reply
                        System.out.println("reply to grade received: " + replyToGrade);
                        String grade = in.readLine(); //receives the grade
                        System.out.println("grade received: " + grade);
                        synchronized (this) {
                            Reply reply = Reply.getReply(replyToGrade);
                            System.out.println("reply in addGrade: " + reply);
                            System.out.println("new grade for reply: " + Objects.requireNonNull(reply).getGrade());
                            Objects.requireNonNull(reply).setGrade(grade);
                        }
                        break;

                    case ("getUserList"):
                        ArrayList<String> studentList;
                        synchronized (this) {
                            studentList = Objects.requireNonNull(user).getStudents();
                        }
                        for (String s : studentList) {
                            out.println(s);
                            System.out.println("Server: " + s);
                        }
                        break;

                    case ("upvote"):
                        currUser = in.readLine();
                        System.out.println("server: currUser: " + currUser);
                        String currParentReply = in.readLine();
                        System.out.println("server currReply: '" + currParentReply + "'");
                        boolean voteAdded;
                        synchronized (this) {
                            voteAdded = Objects.requireNonNull(Reply.getReply(currParentReply.trim())).
                                    checkAddVote(currUser);
                        }
                        if (voteAdded) {
                            out.println("voteAdded");
                            System.out.println("voteAdded");
                        } else {
                            out.println("voteNotAdded");
                            System.out.println("voteNotAdded");
                        }

                        break;

                    case ("deleteReply"):
                        String replyToDelete = in.readLine();
                        System.out.println("reply to be deleted: " + replyToDelete);
                        boolean replyDeleted;
                        synchronized (this) {
                            replyDeleted = Reply.deleteReply(replyToDelete);
                        }
                        if (replyDeleted) {
                            out.println("replyDeleted");
                            System.out.println("Server: replyDeleted");
                        } else {
                            out.println("replyNotDeleted");
                            System.out.println("Server: replyNotDeleted");
                        }
                        break;

                    case ("sortVotesDiscussion"):
                        dfName = in.readLine();
                        System.out.println("received from client " + dfName);
                        synchronized (this) {
                            df = DiscussionForum.returnDF(dfName);
                            if (df != null) {
                                Reply.sortByVotes();
                                parentReplies = df.getParentReplies();
                            } else {
                                parentReplies = null;
                            }
                        }
                        if (parentReplies != null) {
                            out.println(parentReplies.size());
                            System.out.println("Server: " + parentReplies.size());
                            for (Reply parentRep : parentReplies) {
                                username = parentRep.getUser().getUsername();
                                out.println(username); //sends username
                                System.out.println("Server: " + username);
                                String time = parentRep.getTimestamp();
                                out.println(time); //sends timestamp
                                System.out.println("Server: " + time);
                                String replyParent = parentRep.getReplyString();
                                out.println(replyParent); //sends the reply contents
                                System.out.println("Server: " + replyParent);
                                int votes = parentRep.getUpvotes();
                                out.println(votes); //sends the upvotes
                                System.out.println("Server: " + votes);
                            }
                        } else {
                            out.println(0);
                            System.out.println("Server: " + 0);
                        }
                        //resetting to default -> sorts by time
                        synchronized (this) {
                            Reply.sortByTime();
                        }
                        break;

                    case ("sortVotesViewReply"):
                        parentReply = in.readLine();
                        System.out.println("parentReply received from client in viewReply:" + parentReply);
                        synchronized (this) {
                            Reply.sortByVotes(); //sorts by vote
                            nestedReplies = Reply.getNestedReplies(parentReply);
                        }

                        out.println(nestedReplies.size());
                        for (Reply reply : nestedReplies) {
                            username = reply.getUser().getUsername();
                            out.println(username);
                            System.out.println("username sent from server in viewReply: " + username);
                            String time = reply.getTimestamp();
                            out.println(time);
                            System.out.println("time sent from server in viewReply: " + time);
                            String replyString = reply.getReplyString();
                            out.println(replyString);
                            System.out.println("reply string sent from server in viewReply: " + reply.
                                    getReplyString());
                            int votes = reply.getUpvotes();
                            out.println(votes);
                            System.out.println("votes in viewReply sent from server: " + votes);
                        }
                        //goes back to default sorting
                        synchronized (this) {
                            Reply.sortByTime();
                        }
                        break;

                    default:
                        System.out.println("Went to default case in Server class");
                }
            }
        } catch (IOException e) {
            System.out.println("Force close...");
        } finally {
            out.close();
            try {
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Socket close...");
            }
        }
    }
}
