import java.util.*;

/**
 * DiscussionForum.java
 *
 * This class intuitively organizes information about a reply given a user and the number of upvotes.
 *
 * @author Emma Maki, lab sec 13180-L19
 *
 * @version April 25, 2022
 *
 */

public class DiscussionForum extends RevisedAccount {

    private static ArrayList<DiscussionForum> dfs = new ArrayList<>();

    private ArrayList<Reply> parentReplies;
    private ArrayList<Reply> nestedReplies;
    private ArrayList<Reply> allReplies;
    private String discussionForum;
    private String course;

    public DiscussionForum(String course, String discussionForum) {
        this.course = course;
        this.discussionForum = discussionForum;
        this.nestedReplies = new ArrayList<>();
        this.parentReplies = new ArrayList<>();
        this.allReplies = new ArrayList<>();
    }

    public DiscussionForum(String course) {
        this.course = course;
        this.discussionForum = "Post general questions here!";
        this.nestedReplies = new ArrayList<>();
        this.parentReplies = new ArrayList<>();
        this.allReplies = new ArrayList<>();
    }

    //getters
    public String getCourseName() { return course; }

    public String getDFName() { return discussionForum; }

    //returns a DF object given the name
    public static DiscussionForum returnDF(String dfName) {
        for (DiscussionForum df : dfs) {
            if (df.getDFName().equals(dfName)) {
                return df;
            }
        }
        return null;
    }

    public static String getCourseName(String dfName) {
        for (DiscussionForum df : dfs) {
            if (df.getDFName().equals(dfName)) {
                return df.getCourseName();
            }
        }
        return null;
    }

    public static ArrayList<String> getDFs(String courseName) {
        ArrayList<String> dfStrings = new ArrayList<>();
        for (DiscussionForum df : dfs) {
            if (df.getCourseName().equals(courseName)) {
                dfStrings.add(df.getDFName());
                //System.out.println("finding df in getDFS(courseName): " + df.getDFName());
            }
        }
        return dfStrings;
    }

    public static ArrayList<String> getCourses() {
        ArrayList<String> courses = new ArrayList<>();
        for (int i = 0; i < dfs.size(); i++) {
            if (i == 0) {
                courses.add(dfs.get(i).getCourseName());
                //System.out.println("getting courses in getCourses: " + dfs.get(i).getCourseName());
            } else if (!dfs.get(i).getCourseName().equals(dfs.get(i - 1).getCourseName())) {
                courses.add(dfs.get(i).getCourseName());
                //System.out.println("getting courses in getCourses: " + dfs.get(i).getCourseName());
            }
        }
        return courses;
    }

    public static ArrayList<DiscussionForum> getDFs() { return dfs; }

    public ArrayList<Reply> getAllReplies() { return allReplies; }

    public ArrayList<Reply> getParentReplies() { return parentReplies; }

    public ArrayList<Reply> getNestedReplies() { return nestedReplies; }

    //setters

    public static void clearDFs() { dfs.clear(); }

    public static void removeReply(Reply r) {
        dfs.remove(r);
        for (DiscussionForum df : dfs) {
            df.getAllReplies().removeIf(reply -> reply.equals(r));
            df.getParentReplies().removeIf(reply -> reply.equals(r));
            df.getNestedReplies().removeIf(reply -> reply.equals(r));
        }
    }

    public static void addDF(DiscussionForum df) { dfs.add(df); }

    public void setCourseName(String courseName) {
        this.course = courseName;
        Reply.defaultWriteReplies();
    }

    public void setDF(String newDFName) {
        this.discussionForum = newDFName;
    }

    public void setDFName(String newDFName) {
        this.discussionForum = newDFName;
        Reply.defaultWriteReplies();
    }

    public static boolean editDF(String oldName, String newName) {
        if (oldName.equals("Post general questions here!")) { return false; }
        DiscussionForum oldDF = returnDF(oldName);
        if (oldDF == null) { return false; }
        for (DiscussionForum df : dfs) {
            if (oldDF.equals(df)) {
                df.setDFName(newName);
                Reply.defaultWriteReplies();
                return true;
            }
        }
        return false;
    }

    public static boolean createCourse(String courseName) {
        for (String course : getCourses()) {
            if (course.equals(courseName)) {
                return false;
            }
        }
        DiscussionForum newCourse = new DiscussionForum(courseName);
        dfs.add(newCourse);
        Reply.defaultWriteReplies();
        return true;
    }


    public static boolean createDF(String courseName, String dfName) {
        boolean courseExists = false;
        for (DiscussionForum df : dfs) {
            if (df.getCourseName().equals(courseName)) {
                courseExists = true;
                break;
            }
        }
        if (courseExists) {
            DiscussionForum newDF = new DiscussionForum(courseName, dfName);
            dfs.add(newDF);
            Reply.defaultWriteReplies();
        }
        return courseExists;
    }

    public static boolean addDF(String courseName, String dfName) {
        boolean courseExists = false;
        boolean alreadyExists = false;
        for (DiscussionForum df : dfs) {
            if (df.getCourseName().equals(courseName)) {
                System.out.println("at addDF in 1st for loop + dfname: " + df.getDFName());
                courseExists = true;
                break;
            }
        }
        for (DiscussionForum df : dfs) {
            if (df.getDFName().equals(dfName)) {
                System.out.println("at addDF in 2nd for loop + dfname: " + df.getDFName());
                alreadyExists = true;
                return false;
            }
        }
        if (courseExists) {
            DiscussionForum newDF = new DiscussionForum(courseName, dfName);
            dfs.add(newDF);
        }
        return courseExists;
    }


    public void addToAllReplies(Reply reply) {
        allReplies.add(reply);
    }

    public static boolean deleteDF(String dfName) {
        DiscussionForum df = returnDF(dfName);
        if (df != null && !df.getDFName().equals("Post general questions here!")) {
            dfs.remove(df);
            Reply.defaultWriteReplies();
            return true;
        } else { return false; }
    }

    public static void deleteCourse(String courseName) {
        dfs.removeIf(df -> df.getDFName().equals(courseName));
        Reply.defaultWriteReplies();
    }

    public void addNestedReply(Reply reply) {
        allReplies.add(reply);
        nestedReplies.add(reply);
    }

    public void addParentReply(Reply reply) {
        allReplies.add(reply);
        parentReplies.add(reply);
    }

    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof DiscussionForum)) { return false; }
        DiscussionForum temp = (DiscussionForum) o;
        return temp.getCourseName().equals(this.course) && temp.getDFName().equals(this.discussionForum);
    }

    public String courseToString() {
        return String.format("[Course]:%s", this.getCourseName());
    }
    public String dfToString() {
        return String.format("[DiscussionForum]:%s", this.getDFName());
    }

}
