import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reply.java
 *
 * This class intuitively organizes information from the client server regarding user input for comments and replies
 * with file reading/writing.
 *
 * @author Emma Maki, lab sec 13180-L19
 *
 * @version April 30, 2022
 */

public class Reply extends DiscussionForum {

    private String discussionForum;
    private String reply;
    private Reply parentReply;
    private int upvotes;
    private final RevisedAccount user;
    private final String timestamp;
    private String grade;
    private ArrayList<RevisedAccount> usersLiked;
    private static final List<String> times = new ArrayList<>();

    //when reading from file & is parentReply
    public Reply(String course, String discussionForum, String reply, RevisedAccount user, int upvotes,
                 String timestamp,
                 String grade) {
        super(course, discussionForum);
        this.reply = reply; //initial comment
        this.user = user; //user associated with initial comment
        this.upvotes = upvotes; //total upvotes associated with initial comment
        this.timestamp = timestamp; //sets timestamp
        times.add(timestamp);
        this.grade = grade;
        if (upvotes < 0) {
            throw new IllegalArgumentException("Upvotes cannot be negative!");
        }
        if (user.isStudent()) {
            RevisedStudentAccount student = (RevisedStudentAccount) user;
            student.addStudentReply(this);
        }
        usersLiked = new ArrayList<>();
    }

    //when reading from file & is nestedReply
    public Reply(String course, String discussionForum, Reply parentReply, String reply, RevisedAccount user,
                 int upvotes, String timestamp, String grade) {
        super(course, discussionForum);
        this.parentReply = parentReply;
        this.reply = reply; //initial comment
        this.user = user; //user associated with initial comment
        this.upvotes = upvotes; //total upvotes associated with initial comment
        this.timestamp = timestamp; //sets timestamp
        times.add(timestamp);
        this.grade = grade;
        if (upvotes < 0) {
            throw new IllegalArgumentException("Upvotes cannot be negative!");
        }
        if (user.isStudent()) {
            RevisedStudentAccount student = (RevisedStudentAccount) user;
            student.addStudentReply(this);
        }
        usersLiked = new ArrayList<>();
    }

    //when creating new parentReply from server
    public Reply(String course, String discussionForum, String reply, RevisedAccount user, String timestamp) {
        super(course, discussionForum);
        this.reply = reply; //initial comment
        this.user = user; //user associated with initial comment
        this.upvotes = 0; //total upvotes associated with initial comment
        this.timestamp = timestamp; //sets timestamp
        times.add(timestamp);
        grade = "No Grade Available";
        if (user.isStudent()) {
            RevisedStudentAccount student = (RevisedStudentAccount) user;
            student.addStudentReply(this);
        }
        usersLiked = new ArrayList<>();
    }

    //when creating new nestedReply from server
    public Reply(String course, String discussionForum, String parentReply, String reply, RevisedAccount user,
                 String timestamp) {
        super(course, discussionForum);
        this.parentReply = getReply(parentReply);
        this.reply = reply; //initial comment
        this.user = user; //user associated with initial comment
        this.upvotes = 0; //total upvotes associated with initial comment
        this.timestamp = timestamp; //sets timestamp
        times.add(timestamp);
        grade = "No Grade Available";
        if (user.isStudent()) {
            RevisedStudentAccount student = (RevisedStudentAccount) user;
            student.addStudentReply(this);
        }
        usersLiked = new ArrayList<>();
    }

    //GETTERS
    public boolean isParent() {
        if (this.parentReply == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNested() {
        return !this.isParent();
    }

    public String getReplyString() {
        return reply;
    }

    public static Reply getReply(String reply) {
        for (DiscussionForum df : getDFs()) {
            for (int j = 0; j < df.getAllReplies().size(); j++) {
                if (df.getAllReplies().get(j).getReplyString().equals(reply)) {
                    return df.getAllReplies().get(j);
                }
            }
        }
        return null;
    }

    public static ArrayList<Reply> getNestedReplies(String parentReplyString) {
        Reply parentReply = getParentReply(parentReplyString);
        ArrayList<Reply> nestedReplies = new ArrayList<>();
        for (int i = 0; i < getDFs().size(); i++) {
            for (int j = 0; j < getDFs().get(i).getNestedReplies().size(); j++) {
                if (getDFs().get(i).getNestedReplies().get(j).getParentReply().equals(parentReply)) {
                    nestedReplies.add(getDFs().get(i).getNestedReplies().get(j));
                }
            }
        }
        return nestedReplies;
    }

    //returns parent Reply object
    public Reply getParentReply() {
        return parentReply;
    }

    //returns parent reply string
    public String getParentReplyString() {
        return this.getParentReply().getReplyString();
    }

    //returns parent Reply object given a parent Reply string
    public static Reply getParentReply(String parentReply) {
        for (int i = 0; i < getDFs().size(); i++) {
            for (int j = 0; j < getDFs().get(i).getParentReplies().size(); j++) {
                if (getDFs().get(i).getParentReplies().get(j).getReplyString().equals(parentReply)) {
                    return getDFs().get(i).getParentReplies().get(j);
                }
            }
        }
        return null;
    }

    public int getVotes() {
        return upvotes;
    }

    public RevisedAccount getUser() {
        return user;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public int getUpvotes() {
        return upvotes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    //will write "N/A" if teacher reply, "Grade Unavailable" if student reply && no grade from teacher has been added
    public String getGrade() {
        if (checkUserExists()) {
            if (user.isStudent()) {
                return grade;
            } else return "N/A";
        } else {
            return "UNAVAILABLE";
        }
    }

    public static ArrayList<String> getStudentGrades(String username) {
        ArrayList<Reply> replies = returnUserReplies(username);
        ArrayList<String> grades = new ArrayList<>();
        for (Reply r : replies) {

            grades.add(r.getGrade());
        }
        return grades;
    }

    public static ArrayList<String> getUserReplies(String username) {
        ArrayList<Reply> replies = returnUserReplies(username);
        ArrayList<String> replyStrings = new ArrayList<>();
        for (Reply r : replies) {
            replyStrings.add(r.getReplyString());
        }
        return replyStrings;
    }

    public ArrayList<RevisedAccount> getUsersLiked() {
        return this.usersLiked;
    }

    public ArrayList<String> getUsersLikedStrings() {
        ArrayList<String> users = new ArrayList<>();
        for (RevisedAccount user : usersLiked) {
            users.add(user.getUsername());
        }
        return users;
    }


    //SETTERS

    public boolean checkAddVote(String username) {
        //System.out.println("reply string of every reply checked: " + writeReply());
        for (RevisedAccount user : getUsersLiked()) {
            //System.out.println("username of every user checked: " + user.getUsername());
            if (user.getUsername().equals(username)) {
                //System.out.println(username + " already voted!!");
                return false;
            }
        }
        this.usersLiked.add(RevisedAccount.getUser(username));
        //System.out.println("upvoted!");
        upvotes++;
        defaultWriteReplies();
        //System.out.println("default writing replies in checkAddVote of Reply.java");
        return true;
    }

    public void setDFName(String dfName) {
        this.discussionForum = dfName;
        defaultWriteReplies();
        //System.out.println("default writing replies in setDFName of Reply.java");
    }

    public void setGrade(String grade) {
        this.grade = grade;
        //System.out.println("default writing replies in setGrade of Reply.java");
        defaultWriteReplies();
    }

    public void setReply(String reply) {
        this.reply = reply;
        //System.out.println("default writing replies in setReply of Reply.java");
        defaultWriteReplies();
    }

    public void setVotes(int votes) {
        this.upvotes = votes;
        defaultWriteReplies();
    }

    //for parsing through file and appending usersLiked to ArrayList
    public void setUsersLiked(String usersLiked) {
        if (!usersLiked.isEmpty() || !usersLiked.isBlank()) {
            String[] arrayUsersLiked = usersLiked.split(",");
            ArrayList<String> listUsersLiked = new ArrayList<>(Arrays.asList(arrayUsersLiked));
            for (String s : listUsersLiked) {
                this.usersLiked.add(getUser(s));
            }
        }
    }

    public static String createTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    //sorting replies by time (newest at the top)
    public static void sortByTime() {
        for (DiscussionForum df : getDFs()) {
            df.getNestedReplies().sort(Comparator.comparing(Reply::getTimestamp).reversed());
            df.getParentReplies().sort(Comparator.comparing(Reply::getTimestamp).reversed());
        }
    }

    //sorting replies by votes (most votes at the top)
    public static void sortByVotes() {
        for (DiscussionForum df : getDFs()) {
            df.getNestedReplies().sort(Comparator.comparing(Reply::getUpvotes).reversed());
            df.getParentReplies().sort(Comparator.comparing(Reply::getUpvotes).reversed());
        }
    }

    //the default write method to sort by time
    public static void defaultWriteReplies() {
        sortByTime();
        writeReplies();
    }

    //having brackets in reply could skew comments stored in file
    public String checkReply(String reply) {
        StringBuilder noBrackets = null;
        String[] illegalWords = new String[]{"[username]:", "[upvotes]:", "[Course]:", "[DiscussionForum]:",
                "[parent_reply]:",
                "[nested_reply]:", "[timestamp]:", "[grade]:"};
        for (String illegalWord : illegalWords) {
            if (illegalWord.equals(reply)) {
                for (int i = 0; i < reply.length(); i++) {
                    noBrackets = new StringBuilder(reply);
                    if (reply.charAt(i) == '[') {
                        noBrackets.setCharAt(i, '{');
                    } else if (reply.charAt(i) == ']') {
                        noBrackets.setCharAt(i, '}');
                    }
                }

            }
        }
        return noBrackets != null ? noBrackets.toString() : null;
    }

    public static void readDF(String fileName) {
        ArrayList<String> temp = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println(file + " created!");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                temp.add(br.readLine());
            }
            br.close();
            temp.removeIf(r -> r.isEmpty() || r.isBlank());
            //for assigning nested and parent replies
            for (int i = 0; i < temp.size(); i++) {
                int indexCourse = temp.get(i).indexOf("[Course]:");
                if (indexCourse != -1) {
                    String courseName = temp.get(i).substring(indexCourse + 9).trim();
                    //System.out.println("courseName in readDF in Reply.java: " + courseName);
                    //loop for discussion forums
                    for (int j = i + 1; j < temp.size(); j++) {
                        int indexDF = temp.get(j).indexOf("[DiscussionForum]:");
                        if (temp.get(j).contains("[Course]:")) {
                            break;
                        }
                        if (indexDF != -1) {
                            String dfString = temp.get(j).substring(indexDF + 18).trim();
                            DiscussionForum df = new DiscussionForum(courseName, dfString);
                            addDF(df);
                            //loop for parent replies
                            for (int n = j + 1; n < temp.size(); n++) {
                                int indexParentReply = temp.get(n).indexOf("[parent_reply]:");
                                if (temp.get(n).contains("[Course]:") || temp.get(n).contains("[DiscussionForum]:")) {
                                    break;
                                }
                                int indexUser = temp.get(n).indexOf("[username]:");
                                int indexVotes = temp.get(n).indexOf("[upvotes]:");
                                int indexTimestamp = temp.get(n).indexOf("[timestamp]:");
                                int indexGrade = temp.get(n).indexOf("[grade]:");
                                int indexUsersLiked = temp.get(n).indexOf("[users_liked]:");

                                if (indexParentReply != -1) {
                                    String initialReply = temp.get(n).substring(indexParentReply + 15,
                                            indexUser).trim();
                                    String username = temp.get(n).substring(indexUser + 11, indexVotes).trim();
                                    int totalUpvotes = -1;
                                    try {
                                        totalUpvotes = Integer.parseInt(temp.get(n).substring(indexVotes + 10,
                                                indexTimestamp).trim());
                                    } catch (NumberFormatException ex) {
                                        System.out.println("Upvotes must be an integer!");
                                    }
                                    String timestamp = temp.get(n).substring(indexTimestamp + 12, indexGrade).trim();
                                    String grade = temp.get(n).substring(indexGrade + 8, indexUsersLiked).trim();
                                    String usersLiked = temp.get(n).substring(indexUsersLiked + 14).trim();
                                    Reply parent = new Reply(courseName, dfString, initialReply,
                                            RevisedAccount.getUser(username),
                                            totalUpvotes, timestamp, grade);
                                    parent.setUsersLiked(usersLiked);
                                    parent.checkUserExists();
                                    df.addParentReply(parent);
                                    //loop for nested replies
                                    for (int k = n + 1; k < temp.size(); k++) {
                                        if (temp.get(k).contains("[Course]:") || temp.get(k).contains
                                                ("[DiscussionForum]:") ||
                                                temp.get(k).contains("[parent_reply]:")) {
                                            break;
                                        }
                                        int indexNestedReply = temp.get(k).indexOf("[nested_reply]:");
                                        int indexNestedUser = temp.get(k).indexOf("[username]:");
                                        int indexNestedVotes = temp.get(k).indexOf("[upvotes]:");
                                        int indexNestedTimestamp = temp.get(k).indexOf("[timestamp]:");
                                        int indexNestedGrade = temp.get(k).indexOf("[grade]:");
                                        int indexNestedUsersLiked = temp.get(k).indexOf("[users_liked]:");
                                        if (indexNestedReply != -1) {
                                            String nestedReply = temp.get(k).substring(indexNestedReply + 15,
                                                    indexNestedUser).trim();
                                            String usernameNested = temp.get(k).substring(indexNestedUser + 11,
                                                    indexNestedVotes).trim();
                                            int totalUpvotesNested = -1;
                                            try {
                                                totalUpvotesNested = Integer.parseInt(temp.get(k).substring
                                                        (indexNestedVotes + 10,
                                                                indexNestedTimestamp).trim());
                                            } catch (NumberFormatException ex) {
                                                System.out.println("Upvotes must be an integer!");
                                            }
                                            String nestedTimestamp = temp.get(k).substring(indexNestedTimestamp + 12,
                                                    indexNestedGrade).trim();
                                            String nestedGrade = temp.get(k).substring(indexNestedGrade + 8,
                                                    indexNestedUsersLiked).trim();
                                            String nestedUsersLiked = temp.get(k).substring
                                                    (indexNestedUsersLiked + 14).trim();
                                            Reply nested = new Reply(courseName, dfString, parent, nestedReply,
                                                    RevisedAccount.getUser(usernameNested),
                                                    totalUpvotesNested, nestedTimestamp, nestedGrade);
                                            nested.setUsersLiked(nestedUsersLiked);
                                            nested.checkUserExists();
                                            df.addNestedReply(nested);
                                        } //close for "if" nested reply
                                    } //close loop for nested replies
                                } //close for "if parent reply
                            } //close loop for parent replies
                        }
                    } //close loop for discussion forums
                }
                //close for "if" course
            } //close entire loop
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeReplies() {
        try {
            clearFile("discussionforum.txt");
            FileWriter fw = new FileWriter(("discussionforum.txt"), true);
            PrintWriter pw = new PrintWriter(fw, true);

            System.out.println("WRITING REPLIES...");
            for (int i = 0; i < getDFs().size(); i++) {
                if (i == 0) {
                    pw.println(getDFs().get(0).courseToString());
                } else if (!getDFs().get(i).getCourseName().equals(getDFs().get(i - 1).getCourseName())) {
                    pw.println(getDFs().get(i).courseToString());
                }
                pw.println(getDFs().get(i).dfToString());
                for (int j = 0; j < getDFs().get(i).getParentReplies().size(); j++) {
                    pw.println(getDFs().get(i).getParentReplies().get(j).writeReply());
                    for (int k = 0; k < getDFs().get(i).getNestedReplies().size(); k++) {
                        if (getDFs().get(i).getNestedReplies().get(k).getParentReply().equals
                                (getDFs().get(i).getParentReplies().get(j))) {
                            pw.println(getDFs().get(i).getNestedReplies().get(k).writeReply());
                        }
                    }
                }
            }

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //editing reply info if user deleted account
    public boolean checkUserExists() {
        if (!user.doesExist()) {
            setGrade("UNAVAILABLE");
            return false;
        }
        return true;
    }

    public static boolean addReply(String courseName, String df, String parentReply, String nestedReply,
                                   String username, String timestamp) {
        RevisedAccount user = RevisedAccount.getUser(username);
        if (user == null) {
            return false;
        }
        boolean courseAndDFExist = false;
        Reply reply;
        if (nestedReply.isEmpty() || nestedReply.isBlank()) {
            reply = new Reply(courseName, df, parentReply, user, timestamp);
        } else {
            reply = new Reply(courseName, df, parentReply, nestedReply, user, timestamp);
        }
        for (int i = 0; i < getDFs().size(); i++) {
            if (getDFs().get(i).getCourseName().equals(reply.getCourseName()) &&
                    getDFs().get(i).getDFName().equals(reply.getDFName())) {
                getDFs().get(i).addToAllReplies(reply);
                courseAndDFExist = true;
                if (reply.isNested()) {
                    for (int j = 0; j < getDFs().get(i).getParentReplies().size(); j++) {
                        if (getDFs().get(i).getParentReplies().get(j).equals(reply.getParentReply())) {
                            getDFs().get(i).getNestedReplies().add(reply);
                            break;
                        }
                    }
                } else if (reply.isParent()) {
                    getDFs().get(i).getParentReplies().add(reply);
                    break;
                }
            }
        }
        if (courseAndDFExist) {
            defaultWriteReplies();
            System.out.println("DEFAULT WRITING REPLIES in Reply.java...");
        }
        return courseAndDFExist;
    }

    public static boolean deleteReply(String reply) {
        Reply replyRemoved = getReply(reply);
        if (replyRemoved != null) {
            removeReply(replyRemoved);
            defaultWriteReplies();
            System.out.println("DEFAULT WRITING REPLIES in deleteReply of Reply.java...");
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<Reply> returnUserReplies(String username) {
        ArrayList<Reply> userReplies = new ArrayList<>();
        for (int i = 0; i < getDFs().size(); i++) {
            for (int j = 0; j < getDFs().get(i).getAllReplies().size(); j++) {
                if (getDFs().get(i).getAllReplies().get(j).getUser().equals(getUser(username))
                        && (Objects.requireNonNull(getUser(username))).isStudent()) {
                    userReplies.add(getDFs().get(i).getAllReplies().get(j));
                }
            }
        }
        return userReplies;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Reply)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        Reply temp = (Reply) o;
        if (temp.isParent()) {
            return super.equals(temp) && this.getReplyString().equals(temp.getReplyString()) &&
                    this.upvotes == temp.getUpvotes() &&
                    this.timestamp.equals(temp.getTimestamp());
        } else if (temp.isNested()) {
            return super.equals(temp) && this.getReplyString().equals(temp.getReplyString()) &&
                    this.upvotes == temp.getUpvotes() && this.getParentReplyString().equals(temp.
                    getParentReplyString())
                    && this.timestamp.equals(temp.getTimestamp());
        } else {
            return false;
        }
    }

    public String writeUsersLiked() {
        ArrayList<String> users = this.getUsersLikedStrings();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < users.size(); i++) {
            s.append(users.get(i));
            if (i != users.size() - 1) {
                s.append(",");
            }
        }
        return s.toString();
    }

    public String writeReply() {
        String reply;
        if (this.isNested()) {
            reply = "    [nested_reply]";
        } else {
            reply = "[parent_reply]";
        }
        return String.format("    %s:%s [username]:%s [upvotes]:%d [timestamp]:%s [grade]:%s [users_liked]:%s", reply,
                this.getReplyString(), this.getUsername(), this.getUpvotes(), this.getTimestamp(), this.getGrade(),
                this.writeUsersLiked());
    }

    public static void clearFile(String fileName) {
        try (FileWriter fw = new FileWriter(fileName, false)) {
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
