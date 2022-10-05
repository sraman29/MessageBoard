import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Client.java
 *
 * This class organizes information about teacher or student objects based
 * on a single thread of user input via GUIs.
 *
 * @author Jun Cao, Emma Maki, Henry Merchant, sec 13180-L19
 *
 * @version May 2, 2022
 *
 */

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Client {
    private static JFrame f;
    private static JFrame main;
    private static JFrame dis;
    private static JFrame edit;
    private static JFrame reply;
    private static JFrame view;
    private static JFrame grade;
    private static JTable table;

    private static JButton button;
    private static JButton butt;
    private static JButton delete;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) {

        JButton login;
        JButton signup;

        try {
            Socket socket = new Socket("localhost", 6969);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            f = new JFrame("Project 5");
            f.setLayout(new BorderLayout());

            JPanel top = new JPanel();
            JLabel wel = new JLabel("Welcome to Project 5");
            top.add(wel);
            f.add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            login = new JButton("Log in");
            login.addActionListener(e -> {
                f.dispose();
                login();
            });
            signup = new JButton("Sign up");
            signup.addActionListener(e -> {
                f.dispose();
                signUp();
            });
            center.add(login);
            center.add(signup);
            f.add(center, BorderLayout.CENTER);

            f.setSize(500, 500);
            f.setDefaultCloseOperation(EXIT_ON_CLOSE);
            f.setVisible(true);
            f.setLocationRelativeTo(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void login() {

        JFrame LogIn = new JFrame("Log in");
        LogIn.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        JLabel id = new JLabel("ID:");
        JTextField idText = new JTextField(10);
        JLabel password = new JLabel("Password:");
        JPasswordField pass = new JPasswordField(10);

        center.add(id);
        center.add(idText);
        center.add(password);
        center.add(pass);
        LogIn.add(center, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        JButton in = new JButton("Login");
        in.addActionListener(e -> {
            try {
                //"login" to server
                String t;
                out.println(t = "login");
                System.out.println("sent to server: " + t);
                out.println(t = idText.getText());
                System.out.println("sent to server: " + t);
                out.println(t = String.valueOf(pass.getPassword()));
                System.out.println("sent to server: " + t);
                String valid = Client.in.readLine().trim(); //if the login was successful
                System.out.println("received from server: " + valid);

                if (valid.equals("loginSuccess")) {
                    String type = Client.in.readLine();
                    System.out.println("received: " + type);
                    String currUser = idText.getText();
                    //String pw = String.valueOf(pass.getPassword());
                    LogIn.setVisible(false);

                    mainMenu(currUser, type);
                } else if (valid.equals("alreadyLoggedIn")) {
                    JOptionPane.showMessageDialog(null, "Account is already logged in on a " +
                            "different window. Either logout, or login with a different account.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    idText.setText("");
                    pass.setText("");

                } else {
                    JOptionPane.showMessageDialog(null, "ID or password is wrong. " +
                                    "Please try again!", "Error", JOptionPane.ERROR_MESSAGE);
                    idText.setText("");
                    pass.setText("");
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
        });
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            LogIn.setVisible(false);
            f.setVisible(true);
        });
        bot.add(in);
        bot.add(back);
        LogIn.add(bot, BorderLayout.SOUTH);

        LogIn.setSize(500, 500);
        LogIn.setVisible(true);
        LogIn.setDefaultCloseOperation(EXIT_ON_CLOSE);
        LogIn.setLocationRelativeTo(null);
    }

    public static void signUp() {

        JFrame signup = new JFrame("Sign Up");
        signup.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        JLabel createID = new JLabel("ID:");
        JTextField idText = new JTextField(10);
        JLabel createPw = new JLabel("Password:");
        JPasswordField pwText = new JPasswordField(10);
        JLabel confirm = new JLabel("Confirm Password:");
        JPasswordField confi = new JPasswordField(10);
        JComboBox<String> type = new JComboBox<>();
        type.addItem("teacher");
        type.addItem("student");
        center.add(createID);
        center.add(idText);
        center.add(createPw);
        center.add(pwText);
        center.add(confirm);
        center.add(confi);
        center.add(type);
        signup.add(center, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        JButton createAccount = new JButton("Sign Up");
        createAccount.addActionListener(e -> {
            if (idText.getText().equals("") || String.valueOf(pwText.getPassword()).equals("") || 
                    String.valueOf(confi.getPassword()).equals("")) {
                JOptionPane.showMessageDialog(null, "Username and/or password cannot be empty.",
                        " Error", JOptionPane.ERROR_MESSAGE);
            } else if (!RevisedAccount.checkValidity(idText.getText(), String.valueOf(pwText.getPassword()))) {
                JOptionPane.showMessageDialog(null, "User name and/or password contains " +
                        "illegal characters! Try again using ONLY letters, numbers, and/ or underscores.", 
                        " Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String p1 = String.valueOf(pwText.getPassword());
                String p2 = String.valueOf(confi.getPassword());
                if (!(p1.equals(p2))) {
                    JOptionPane.showMessageDialog(null, "Passwords are different!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    idText.setText("");
                    pwText.setText("");
                    confi.setText("");
                } else {
                    try {
                        //"signUp" server interactions:
                        out.println("signUp");
                        System.out.println("Sent to Server: " + "signUp");
                        out.println(idText.getText()); //sends user
                        System.out.println("username sent to server in signUp: " + idText.getText());
                        String currUser = idText.getText();
                        out.println(pwText.getPassword()); //sends pass
                        System.out.println("pass sent to Server: " + Arrays.toString(pwText.getPassword()));
                        out.flush();
                        System.out.println("selected type:" + type.getSelectedItem());
                        out.println(type.getSelectedItem());
                        String valid = in.readLine(); //reads is valid
                        if (valid.equals("loginSuccess")) {
                            //id = IDText.getText();
                            signup.setVisible(false);
                            //goes to main menu
                            mainMenu(currUser, String.valueOf(type.getSelectedItem()));
                        } else {
                            JOptionPane.showMessageDialog(null, "This username is already in" +
                                            " use! Please login.", "Error", JOptionPane.ERROR_MESSAGE);
                            idText.setText("");
                            pwText.setText("");
                            confi.setText("");
                        }

                    } catch (Exception b) {
                        b.printStackTrace();
                    }
                }
            }
        });
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            signup.setVisible(false);
            f.setVisible(true);
        });
        bot.add(createAccount);
        bot.add(back);
        signup.add(bot, BorderLayout.SOUTH);


        signup.setSize(500, 500);
        signup.setDefaultCloseOperation(EXIT_ON_CLOSE);
        signup.setVisible(true);
        signup.setLocationRelativeTo(null);
    }

    public static void mainMenu(String currUser, String type) {

        out.println("courseUpdate");
        ArrayList<String> coursesReceived = new ArrayList<>();
        try {
            int size = Integer.parseInt(in.readLine());
            for (int i = 0; i < size; i++) {
                coursesReceived.add(in.readLine());
            }
        } catch (Exception a) {
            a.printStackTrace();
        }

        main = new JFrame("Main");
        main.setLayout(new BorderLayout());

        if (type.equals("teacher")) {

            JPanel top = new JPanel();
            JLabel hi = new JLabel(String.format("Hi, Prof. %s!", currUser));
            JButton logOut = new JButton("Log Out");
            logOut.addActionListener(e -> {
                main.dispose();
                f.setVisible(true);
                //out.println("logout");
            });
            top.add(hi);
            top.add(logOut);
            main.add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            JLabel course = new JLabel("Select Course:");
            JComboBox<String> comboCourses = new JComboBox<>();

            for (String c : coursesReceived) {
                comboCourses.addItem(c);
                System.out.println("adding item to courses: " + c);
            }

            main.add(comboCourses);
            JButton go = new JButton("Go");
            go.addActionListener(e -> {
                main.setVisible(false);
                dis(String.valueOf(comboCourses.getSelectedItem()), "teacher", currUser);
            });
            center.add(course);
            center.add(comboCourses);
            center.add(go);
            main.add(center, BorderLayout.CENTER);

            JPanel bot = new JPanel();
            JButton createCourse = new JButton("Create Course");
            createCourse.addActionListener(e -> {
                String course1 = JOptionPane.showInputDialog("Please enter a course name:");
                out.println("createCourse");
                out.println(course1);
                String courseCreated = "";
                try {
                    courseCreated = in.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (courseCreated.equals("courseCreated")) {
                    JOptionPane.showMessageDialog(null, "Course created successfully!",
                            "Create New Course", JOptionPane.INFORMATION_MESSAGE);
                    comboCourses.addItem(course1);
                    main.dispose();
                    mainMenu(currUser, type);
                } else {
                    JOptionPane.showMessageDialog(null, "Course already exists!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            JButton edit = new JButton("Account Info");
            edit.addActionListener(e -> {
                main.setVisible(false);
                edit();
            });
            JButton grade = new JButton("View Grades");
            grade.addActionListener(e -> viewGrade(currUser, type));

            bot.add(grade);
            bot.add(edit);
            bot.add(createCourse);
            main.add(bot, BorderLayout.SOUTH);

        } else {

            JPanel top = new JPanel();
            JLabel hi = new JLabel(String.format("Hi, %s!", currUser));
            JButton logOut = new JButton("Log Out");
            logOut.addActionListener(e -> {
                main.dispose();
                f.setVisible(true);
                //out.println("logout");
            });
            top.add(hi);
            top.add(logOut);
            main.add(top, BorderLayout.NORTH);

            JPanel center = new JPanel();
            JLabel course = new JLabel("Select a course:");
            JComboBox<String> courses = new JComboBox<>();
            for (String s : coursesReceived) {
                courses.addItem(s);
                System.out.println("added to JCombo: " + s);
            }
            JButton go = new JButton("Go");
            go.addActionListener(e -> {
                if (courses.getItemCount() != 0) {
                    main.setVisible(false);
                    dis(String.valueOf(courses.getSelectedItem()), "student", currUser);
                } else {
                    JOptionPane.showMessageDialog(null, "To access a course, one must be " +
                                    "created first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            center.add(course);
            center.add(courses);
            center.add(go);
            main.add(center, BorderLayout.CENTER);

            JPanel bot = new JPanel();
            JButton grade = new JButton("View Grades");
            grade.addActionListener(e -> {
                viewGrade(currUser, type);
                main.setVisible(false);
            });
            JButton edit = new JButton("Account Info");
            edit.addActionListener(e -> {
                main.setVisible(false);
                edit();
            });
            bot.add(grade);
            bot.add(edit);
            main.add(bot, BorderLayout.SOUTH);
        }

        main.setSize(500, 500);
        main.setDefaultCloseOperation(EXIT_ON_CLOSE);
        main.setVisible(true);
        main.setLocationRelativeTo(null);
    }

    public static void edit() {

        edit = new JFrame("Edit Account");
        edit.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        JButton newPass = new JButton("Change Password");
        newPass.addActionListener(e -> {
            try {
                String pass = JOptionPane.showInputDialog("Please type new password");
                String pass2 = JOptionPane.showInputDialog("Please type again");

                if (!pass.equals(pass2)) {
                    JOptionPane.showMessageDialog(null, "Passwords are different!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    out.println("changePass");
                    out.println(pass); //sends the new password
                    String passwordChanged = in.readLine();
                    System.out.println("Received from Server: " + passwordChanged);
                    if (passwordChanged.equals("passwordChanged")) {
                        JOptionPane.showMessageDialog(null, "Password change successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "New password cannot be same as " +
                                        "old password!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        });
        JButton delete = new JButton("Delete Account");
        delete.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete" +
                            " your account?", "Warning", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                out.println("deleteAcct");
                try {
                    if (in.readLine().equals("accountDeleted")) {
                        JOptionPane.showMessageDialog(null, "Account Deleted!", "Deleted",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error! Account could not be " +
                                        "deleted.","Error", JOptionPane.ERROR_MESSAGE);
                    }
                    edit.dispose();
                    f.setVisible(true);
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(null, "Error! Account could not be deleted.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        center.add(newPass);
        center.add(delete);
        edit.add(center, BorderLayout.CENTER);


        JPanel bot = new JPanel();
        JButton back = new JButton("Back");
        //back.setBounds(180, 200, 80, 30);
        back.addActionListener(e -> {
            edit.setVisible(false);
            main.setVisible(true);
        });
        bot.add(back);
        edit.add(bot, BorderLayout.SOUTH);

        edit.setSize(500, 500);
        edit.setVisible(true);
        edit.setDefaultCloseOperation(EXIT_ON_CLOSE);
        edit.setLocationRelativeTo(null);
    }

    public static void viewGrade(String currUser, String type) {
        main.dispose();
        grade = new JFrame("View Grades");
        grade.setSize(1000, 1000);
        grade.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JButton go = new JButton("Go");
        JTable teacherTable = new JTable();

        if (type.equals("teacher")) {
            out.println("viewStudents");
            JComboBox<String> usernames = new JComboBox<>();
            try {
                int size = Integer.parseInt(in.readLine());
                for (int i = 0; i < size; i++) {
                    usernames.addItem(in.readLine());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JLabel name = new JLabel("Select a Student");

            go.addActionListener(e -> {
                button = new JButton();
                Object[][] gradeData;
                String[] col = {"Content", "Grade"};
                out.println("viewGrades"); //calls view grades
                out.println(usernames.getSelectedItem()); //sends the selected username
                int size = 0;
                try {
                    size = Integer.parseInt(in.readLine()); //reads size of reply list
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ArrayList<String> reps = new ArrayList<>();
                ArrayList<String> grades = new ArrayList<>();
                try {
                    for (int i = 0; i < size; i++) {
                        reps.add(in.readLine()); //reads the reply content
                    }
                    for (int i = 0; i < size; i++) {
                        grades.add(in.readLine()); //reads the reply grade
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                if (size != 0) {
                    gradeData = new Object[size][2];
                    for (int i = 0; i < size; i++) {
                        // reply content
                        gradeData[i][0] = reps.get(i);
                        // grade
                        gradeData[i][1] = grades.get(i);
                    }
                } else {
                    gradeData = new Object[0][0];
                }

                DefaultTableModel model = new DefaultTableModel(gradeData, col);
                teacherTable.setModel(model);

                teacherTable.getColumn("Grade").setCellRenderer(new ButtonRendererGrades());
                teacherTable.getColumn("Grade").setCellEditor(new ButtonEditorGrades(new JCheckBox()));
                //resets the page
                grade.setVisible(false);
                grade.setVisible(true);

                button.addActionListener(event -> {
                    String grade1 = JOptionPane.showInputDialog("Type the grade");
                    int row = teacherTable.getSelectedRow();
                    String replyGraded = String.valueOf(teacherTable.getValueAt(row, 0));
                    teacherTable.setValueAt(grade1, row, 1);
                    //grade.add(scrollPane, BorderLayout.CENTER);
                    //case to add grade in server
                    out.println("addGrade");
                    //send the reply to be graded
                    out.println(replyGraded);
                    System.out.println("sent to server: " + replyGraded);
                    out.println(grade1);
                    System.out.println("sent to server: " + grade1);
                });
            });
            usernames.setPreferredSize(new Dimension(300, 20));

            JScrollPane scrollPane = new JScrollPane(teacherTable);
            grade.add(scrollPane, BorderLayout.CENTER);

            top.add(name);
            top.add(usernames);
            top.add(go);
        } else { //is a student

            //calls viewGrades in server
            out.println("viewGrades");

            out.println(currUser); //sends the username
            System.out.println("sent to server; currUser: " + currUser);
            String[] col = {"Content", "Grade"};
            //receiving replies and grades
            int size = 0;
            try {
                size = Integer.parseInt(in.readLine());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ArrayList<String> replies = new ArrayList<>();
            ArrayList<String> grades = new ArrayList<>();
            try {
                for (int i = 0; i < size; i++) {
                    replies.add(in.readLine()); //gets reply content for student
                }
                for (int i = 0; i < size; i++) {
                    grades.add(in.readLine()); //gets the grade data for student
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            //finds matched username and puts data in table
            Object[][] gradeData;
            if (size != 0) {
                gradeData = new Object[size][2];
                for (int i = 0; i < size; i++) {
                    // puts reply content into gradeData for student
                    gradeData[i][0] = replies.get(i);
                    // puts grade into gradeData for student
                    gradeData[i][1] = grades.get(i);
                }
            } else {
                gradeData = new Object[0][0];
            }

            JTable studentTable = new JTable(gradeData, col);
            //studentTable.setPreferredScrollableViewportSize(new Dimension(800,800));
            JScrollPane jsp = new JScrollPane(studentTable);
            //center.add(jsp);
            grade.add(jsp, BorderLayout.CENTER);
            grade.setVisible(false);
            grade.setVisible(true);
        }

        grade.add(top, BorderLayout.NORTH);

        JPanel bot = new JPanel();
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            grade.dispose();
            mainMenu(currUser, type);
        });
        bot.add(back);
        grade.add(bot, BorderLayout.SOUTH);

        grade.setVisible(true);
        grade.setDefaultCloseOperation(EXIT_ON_CLOSE);
        grade.setLocationRelativeTo(null);

    }

    public static void dis(String course, String type, String currUser) {

        button = new JButton();
        butt = new JButton();
        delete = new JButton();
        dis = new JFrame("Discussion");
        dis.setSize(1000, 1000);
        dis.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JPanel bot = new JPanel();
        JLabel ques = new JLabel("Select Question");
        JComboBox<String> questions = new JComboBox<>();
        questions.setPreferredSize(new Dimension(250, 20));
        JTable table = new JTable();

        JLabel sort = new JLabel("Sort by:");
        JComboBox<String> sortMethod = new JComboBox<>();
        sortMethod.addItem("newest to oldest");
        sortMethod.addItem("vote highest to lowest");
        JButton deleteDF = new JButton("Delete Question");
        JButton createDF = new JButton("Create Question");
        JButton editDF = new JButton("Edit Question");


        out.println("course"); //initiates course in server
        out.println(course); //sends the active course to server
        try {
            int size = Integer.parseInt(in.readLine());
            for (int i = 0; i < size; i++) {
                questions.addItem(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JButton go = new JButton("Go");
        //when go is pressed -> view the new comments for df post
        go.addActionListener(e -> {
            if (questions.getItemCount() == 0) {
                JOptionPane.showMessageDialog(null, "No replies have been posted " +
                        "to this forum yet.");
            } else { //there are more than 0 questions

                String q = String.valueOf(questions.getSelectedItem());
                int size = 0;
                String[] columns = new String[]{"Name", "Time", "Content", "Upvotes"};
                Object[][] curReplies;

                if (String.valueOf(sortMethod.getSelectedItem()).equals("newest to oldest")) { //sorts by time

                    //initiates the discussion forum case -> get parent replies
                    out.println("discussionForum");
                    //sends the question to server
                    out.println(q);
                    System.out.println("Sent to server: " + q);

                    try { //reads the size of the reply array<>
                        size = Integer.parseInt(in.readLine());
                        System.out.println("size of reply array received from server: " + size);
                    } catch (IOException a) {
                        a.printStackTrace();
                    }

                    curReplies = new Object[size][4];
                    if (size != 0) {
                        for (int i = 0; i < size; i++) {
                            try {
                                // name
                                curReplies[i][0] = Client.in.readLine();
                                System.out.println("currUser in dis received from server: " + curReplies[i][0]);
                                // time
                                curReplies[i][1] = Client.in.readLine();
                                System.out.println("current time in dis received from server: " + curReplies[i][1]);
                                // content
                                curReplies[i][2] = Client.in.readLine();
                                System.out.println("current replies in dis received from server: " + curReplies[i][2]);
                                // upvotes
                                curReplies[i][3] = Client.in.readLine();
                                System.out.println("current votes in dis received from server: " + curReplies[i][3]);
                            } catch (IOException a) {
                                System.out.println("server");
                            }
                        }
                        DefaultTableModel model = new DefaultTableModel(curReplies, columns);
                        if (type.equals("teacher")) {
                            model.addColumn("Delete");
                        }
                        table.setModel(model);

                        table.getColumn("Upvotes").setCellRenderer(new ButtonRendererVoting());
                        table.getColumn("Upvotes").setCellEditor(new ButtonEditorVoting(new JCheckBox()));

                        table.getColumn("Content").setCellEditor(new ButtonEditorView(new JCheckBox()));
                        table.getColumn("Content").setCellRenderer(new ButtonRendererView());

                        if (type.equals("teacher")) {
                            table.getColumn("Delete").setCellRenderer(new ButtonRendererDelete());
                            table.getColumn("Delete").setCellEditor(new ButtonEditorDelete(new JCheckBox()));
                        }
                    }

                } else { //sorting by upvotes
                    out.println("sortVotesDiscussion");

                    //sends the question to server
                    out.println(q);
                    System.out.println("Sent to server: " + q);

                    try { //reads the size of the reply array<>
                        size = Integer.parseInt(in.readLine());
                        System.out.println("size of reply array received from server: " + size);
                    } catch (IOException a) {
                        a.printStackTrace();
                    }

                    curReplies = new Object[size][4];

                    if (size != 0) {
                        for (int i = 0; i < size; i++) {
                            try {
                                // name
                                curReplies[i][0] = Client.in.readLine();
                                System.out.println("currUser in dis received from server: " + curReplies[i][0]);
                                // time
                                curReplies[i][1] = Client.in.readLine();
                                System.out.println("current time in dis received from server: " + curReplies[i][1]);
                                // content
                                curReplies[i][2] = Client.in.readLine();
                                System.out.println("current replies in dis received from server: " + curReplies[i][2]);
                                // upvotes
                                curReplies[i][3] = Client.in.readLine();
                                System.out.println("current votes in dis received from server: " + curReplies[i][3]);
                            } catch (IOException a) {
                                System.out.println("server");
                            }
                        }
                        DefaultTableModel model = new DefaultTableModel(curReplies, columns);
                        if (type.equals("teacher")) {
                            model.addColumn("Delete");
                        }
                        table.setModel(model);

                        table.getColumn("Upvotes").setCellRenderer(new ButtonRendererVoting());
                        table.getColumn("Upvotes").setCellEditor(new ButtonEditorVoting(new JCheckBox()));

                        table.getColumn("Content").setCellEditor(new ButtonEditorView(new JCheckBox()));
                        table.getColumn("Content").setCellRenderer(new ButtonRendererView());

                        if (type.equals("teacher")) {
                            table.getColumn("Delete").setCellRenderer(new ButtonRendererDelete());
                            table.getColumn("Delete").setCellEditor(new ButtonEditorDelete(new JCheckBox()));
                        }
                    }
                }

                if (type.equals("teacher")) {
                    //for deleting current discussion forum
                    deleteDF.addActionListener(actionEvent -> {
                        int confirmDeleteDF = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to delete this discussion forum: '" + q + "'?");
                        if (confirmDeleteDF == JOptionPane.YES_OPTION) {
                            //calls deleteQ case in server
                            out.println("deleteQ");
                            System.out.println("Client: deleteQ");
                            out.println(q);
                            System.out.println("Client--sending question to be deleted to server: " + q);
                            String deleted = "";
                            try {
                                deleted = in.readLine();
                            } catch (IOException exc) {
                                exc.printStackTrace();
                            }
                            if (deleted.equals("dfDeleted")) {
                                JOptionPane.showMessageDialog(null, "Deleted successfully!",
                                        "Discussion Forum Deleted", JOptionPane.INFORMATION_MESSAGE);
                                System.out.println("DF deleted successfully.");
                                questions.removeItem(questions.getSelectedItem());
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "You cannot delete this forum. Deleting the general discussion forum "
                                                + "discourages kids from asking questions and learning from " +
                                                "each other.", "Error", JOptionPane.ERROR_MESSAGE);
                                System.out.println("ERROR! DF could not be deleted!");
                            }
                        } //else we do nothing and exit without deleting
                    });

                    editDF.addActionListener(ae -> {
                        String currDFQ = String.valueOf(questions.getSelectedItem());
                        String newQ = JOptionPane.showInputDialog(null, "Edit " +
                                "Discussion Question", currDFQ);
                        if (newQ != null) {
                            questions.setSelectedItem(newQ);
                            int o = JOptionPane.showConfirmDialog(null, "Confirm Edit?\n"
                                            + newQ, "Confirm Edit", JOptionPane.YES_NO_OPTION);
                            if (o == JOptionPane.NO_OPTION) {
                                JOptionPane.showMessageDialog(null,
                                        "Edit has been cancelled.", "Edit Cancelled",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else if (o == JOptionPane.YES_OPTION) {
                                out.println("editQ");
                                out.println(currDFQ);
                                System.out.println("Sent to server: " + currDFQ);
                                out.println(newQ);
                                System.out.println("Sent to server: " + newQ);
                                String dfEdited = "";
                                try {
                                    dfEdited = in.readLine();
                                    System.out.println("Received from server: " + dfEdited);
                                } catch (IOException exc) {
                                    exc.printStackTrace();
                                }
                                if (dfEdited.equals("dfEdited")) {
                                    JOptionPane.showMessageDialog(null,
                                            "Edited successfully!", "Discussion Forum Edited",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    System.out.println("DF edited successfully.");
                                    questions.setSelectedItem(newQ);
                                    dis.setVisible(false);
                                    dis.setVisible(true);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Error! The general discussion form is required and " +
                                                    "cannot be edited.", "Error", JOptionPane.ERROR_MESSAGE);
                                    System.out.println("ERROR! DF could not be edited!");
                                }
                            }
                        }
                    });
                    delete.addActionListener(event -> {
                        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to delete this comment?",
                                "Confirm Delete", JOptionPane.YES_NO_OPTION)) {
                            out.println("deleteReply");
                            System.out.println("sent to server: deleteReply");
                            int row = table.getSelectedRow();
                            String selectedContent = table.getValueAt(row, 2).toString();
                            out.println(selectedContent);
                            System.out.println("sent to server: " + selectedContent);
                            try {
                                String tf = in.readLine();
                                if (tf.equals("replyDeleted")) {
                                    JOptionPane.showMessageDialog(null, "Reply has been deleted."
                                            , "Reply Deleted", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "There was an error" +
                                                    " deleting this reply.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception Ea) {
                                JOptionPane.showMessageDialog(null, "There was an error " +
                                                "deleting this reply.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    });
                }
                //for upvoting
                button.addActionListener(event -> {
                    out.println("upvote");
                    int row = table.getSelectedRow();
                    String selectedContent = table.getValueAt(row, 2).toString();
                    //send the current user
                    out.println(currUser);
                    System.out.println("currUser sent to server: " + currUser);
                    //send the reply to be upvoted
                    out.println(selectedContent);
                    System.out.println("reply to be upvoted sent  to server: " + selectedContent);
                    String voteAdded = "";
                    try {
                        voteAdded = in.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (voteAdded.equals("voteAdded")) {
                        JOptionPane.showMessageDialog(null, "Reply upvoted successfully!");
                        dis.setVisible(false);
                        dis.setVisible(true);
                    } else { //user has already liked the post
                        JOptionPane.showMessageDialog(null, "You cannot upvote a post " +
                                        "more than once!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            dis.setVisible(false);
            dis.setVisible(true);
        });

        JScrollPane scrollPane = new JScrollPane(table);
        dis.add(scrollPane, BorderLayout.CENTER);

        JButton viewAll = new JButton("View Full Question");
        String s = (String) questions.getSelectedItem();
        viewAll.addActionListener(e -> JOptionPane.showMessageDialog(null, "[Forum Question]: "
                + s));

        top.add(ques);
        top.add(questions);
        top.add(go);
        top.add(viewAll);
        top.add(sort);
        top.add(sortMethod);

        if (type.equals("teacher")) {
            bot.add(editDF);
            bot.add(deleteDF);
            bot.add(createDF);
            createDF.addActionListener(aE -> {
                int upload = JOptionPane.showConfirmDialog(null, "Do you want to upload" +
                        " a file?");
                String newDF = "";
                int tryAgain;
                int confirm = -1;
                boolean read;
                Object[] option = {"Yes", "Edit", "Cancel"};

                if (upload == JOptionPane.NO_OPTION) { //wants to edit
                    newDF = JOptionPane.showInputDialog("Enter a discussion forum topic:");

                    if (newDF != null) {
                        newDF = newDF.trim();
                        confirm = JOptionPane.showOptionDialog(null, "Confirm: is this what " +
                                        "your post should say? \n" + newDF, "Confirm Name",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option,
                                null);
                    }
                    //sending course name to server
                } else { //wants to import a file for dfq
                    String line = "";
                    String fileName;
                    do {
                        fileName = JOptionPane.showInputDialog(null, "What is the name of the " +
                                        "file? (include .txt)","File Import Menu", JOptionPane.QUESTION_MESSAGE);
                        if (fileName == null) {
                            fileName = "";
                        }
                        fileName = fileName.trim();
                        try {
                            // bookmark creating question
                            if (!fileName.equals("newaccounts.txt") && !fileName.equals("discussionforum.txt")) {
                                BufferedReader br = new BufferedReader(new FileReader(fileName));
                                newDF = br.readLine().trim();
                                System.out.println(newDF);
                                confirm = JOptionPane.showOptionDialog(null, "Confirm: is this" +
                                                " what your post should say? \n" + newDF, "Confirm Forum Question",
                                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                        option,null);
                                while (confirm == JOptionPane.NO_OPTION) {
                                    newDF = JOptionPane.showInputDialog(null, "Enter your edited"
                                                    + " post: \nCurrent Post:\n" + line, "Edit Forum Question",
                                            JOptionPane.QUESTION_MESSAGE);
                                    confirm = JOptionPane.showOptionDialog(null, "Confirm: is" +
                                                    " this what your post should say?\n" + line,
                                            "Confirm Forum Question", JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE, null, option, null);
                                }
                                tryAgain = JOptionPane.NO_OPTION;
                            } else {
                                Object[] option2 = {"Try Again", "Cancel"};
                                tryAgain = JOptionPane.showOptionDialog(null, "Error! Illegal" +
                                                " File Name!", "Error", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, option2, option2[0]);
                            }
                        } catch (IOException exc) {
                            Object[] option2 = {"Try Again", "Cancel"};
                            tryAgain = JOptionPane.showOptionDialog(null, "Error! File not " +
                                            "found!", "Error", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, option2, option2[0]);
                        }
                        read = tryAgain != JOptionPane.YES_OPTION;
                    } while (!read);
                }
                if (confirm == JOptionPane.YES_OPTION) { //we want to create the question
                    out.println("createTopic");
                    out.println(course);
                    System.out.println("Course sent to server: " + course);
                    out.println(newDF);
                    System.out.println("New df name sent to server: " + newDF);
                    String dfCreated = "";
                    try {
                        dfCreated = in.readLine();
                        System.out.println("Received from server: " + dfCreated);
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                    if (dfCreated.equals("dfCreated")) {
                        JOptionPane.showMessageDialog(null,
                                "Created successfully!", "Discussion Forum Created",
                                JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("DF created successfully.");
                        questions.addItem(newDF);
                        dis.setVisible(false);
                        dis.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Error! Discussion Forum could not be created.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println("ERROR! DF could not be created!");
                    }
                }
            });
        }
        dis.add(top, BorderLayout.NORTH);

        butt.addActionListener(e -> {
            int cellViewed = table.getSelectedRow();
            dis.setVisible(false);
            String parentReply = String.valueOf(table.getValueAt(cellViewed, 2));
            viewReply(course, String.valueOf(questions.getSelectedItem()), parentReply, currUser, type);
        });

        JButton back = new JButton("Main Menu");
        back.addActionListener(e -> {
            dis.setVisible(false);
            mainMenu(currUser, type);
        });
        JButton reply = new JButton("Reply");
        reply.addActionListener(e -> {
            dis.setVisible(false);
            reply(course, type, String.valueOf(questions.getSelectedItem()), currUser);
        });

        bot.add(reply);
        bot.add(back);
        dis.add(bot, BorderLayout.SOUTH);

        dis.setVisible(true);
        dis.setDefaultCloseOperation(EXIT_ON_CLOSE);
        dis.setLocationRelativeTo(null);
    }

    public static void reply(String course, String type, String currDFQ, String currUser) {
        reply = new JFrame("Reply");
        reply.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JLabel parent = new JLabel(currDFQ);
        top.add(parent);
        reply.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();

        JLabel repl = new JLabel("Compose your reply:");
        JTextArea rep = new JTextArea();
        rep.setLineWrap(true);
        rep.setWrapStyleWord(true);
        JScrollPane js = new JScrollPane(rep);
        js.setPreferredSize(new Dimension(450, 350));
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        center.add(repl);
        center.add(js);
        reply.add(center, BorderLayout.CENTER);

        JButton upload = new JButton("Upload File");
        upload.addActionListener(a -> {
            boolean read = false;
            int o;
            String line;
            String fileName;
            do {
                fileName = JOptionPane.showInputDialog(null, "What is the name of the file?" +
                                " (include .txt)","File Import Menu", JOptionPane.QUESTION_MESSAGE);
                if (fileName == null) {
                    fileName = "";
                }
                fileName = fileName.trim();
                Object[] option = {"Yes", "Edit", "Cancel"};
                try {
                    //reading reply
                    if (!fileName.equals("newaccounts.txt") && !fileName.equals("discussionforum.txt")) {
                        BufferedReader br = new BufferedReader(new FileReader(fileName));
                        line = br.readLine().trim();
                        System.out.println(line);
                        int confirm = JOptionPane.showOptionDialog(null, "Confirm: is this what"
                                        + " your comment should say? \n" + line, "Confirm Reply",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option,
                                null);
                        while (confirm == JOptionPane.NO_OPTION) {
                            line = JOptionPane.showInputDialog(null, "Enter your edited post:" +
                                    " \nCurrent Post:\n" + line, "Edit Reply", JOptionPane.QUESTION_MESSAGE);
                            confirm = JOptionPane.showOptionDialog(null, "Confirm: is this what"
                                            + " your comment should say? \n" + line, "Confirm Reply",
                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                    option, null);
                        }
                        read = true;
                        if (confirm == JOptionPane.YES_OPTION) {
                            out.println("replyParent");
                            System.out.println("Sent to server: replyParent");
                            //sending out the current discussion forum
                            out.println(currDFQ.trim());
                            System.out.println("Sent to server: " + currDFQ);
                            //sending out the contents of the new reply
                            out.println(line);
                            System.out.println("Sent to server: " + line);
                            //creating and sending a timestamp
                            String timestamp = Reply.createTimestamp();
                            out.println(timestamp.trim());
                            System.out.println("Sent to server: " + timestamp);
                            //sending out current username
                            out.println(currUser);
                            System.out.println("Sent to server: " + currUser);
                            //checking if addingReply was successful or not
                        }
                        o = JOptionPane.NO_OPTION;
                    } else {
                        Object[] option2 = {"Try Again", "Cancel"};
                        o = JOptionPane.showOptionDialog(null, "Error! File not found!",
                                "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                option2, option2[0]);
                    }
                } catch (IOException e) {
                    Object[] option2 = {"Try Again", "Cancel"};
                    o = JOptionPane.showOptionDialog(null, "Error! File not found!",
                            "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            option2, option2[0]);
                }
            } while (!read && JOptionPane.YES_OPTION == o);
        });

        JPanel bot = new JPanel();
        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            out.println("replyParent");
            System.out.println("Sent to server: replyParent");
            //sending out the current discussion forum
            out.println(currDFQ.trim());
            System.out.println("Sent to server: " + currDFQ);
            //sending out the contents of the new reply
            String newParent = rep.getText();
            out.println(newParent);
            System.out.println("Sent to server: " + newParent);
            //creating and sending a timestamp
            String timestamp = Reply.createTimestamp();
            out.println(timestamp.trim());
            System.out.println("Sent to server: " + timestamp);
            //sending out current username
            out.println(currUser);
            System.out.println("Sent to server: " + currUser);
            //checking if addingReply was successful or not
            String replyAdded = "";
            try {
                replyAdded = in.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (replyAdded.equals("replyAdded")) {
                System.out.println("Received from server: replyAdded. Adding reply was successful!");
                JOptionPane.showMessageDialog(null, "Reply created!");
                reply.dispose();
                dis(course, type, currUser);

            } else {
                System.out.println("Received from server: " + replyAdded + " . Adding reply failed!");
                JOptionPane.showMessageDialog(null, "Reply could not be created. Please" +
                                " try again!","Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back to Discussion Forum");
        back.addActionListener(e -> {
            reply.setVisible(false);
            //dis.setVisible(true);
            dis(course, type, currUser);
        });

        bot.add(submit);
        bot.add(upload);
        bot.add(back);
        reply.add(bot, BorderLayout.SOUTH);

        reply.setSize(500, 500);
        reply.setVisible(true);
        reply.setDefaultCloseOperation(EXIT_ON_CLOSE);
        reply.setLocationRelativeTo(null);

    }

    public static void nestedReply(String course, String currDFQ, String currParent, String currUser, String type) {
        JFrame nestedReply = new JFrame("Nested Reply");
        nestedReply.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JLabel parent = new JLabel(currDFQ);
        top.add(parent);
        nestedReply.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();

        JLabel repl = new JLabel("Compose your reply:");
        JTextArea rep = new JTextArea();
        rep.setLineWrap(true);
        rep.setWrapStyleWord(true);
        JScrollPane js = new JScrollPane(rep);
        js.setPreferredSize(new Dimension(450, 350));
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        center.add(repl);
        center.add(js);
        nestedReply.add(center, BorderLayout.CENTER);

        JPanel bot = new JPanel();
        JButton submit = new JButton("Submit");
        submit.addActionListener(e -> {
            out.println("replyNested");
            System.out.println("Sent to server: replyNested");
            //sending out the current discussion forum
            out.println(currDFQ.trim());
            System.out.println("currDFQ sent to server from replyNested: " + currDFQ);
            //sending out the parentReply
            out.println(currParent);
            System.out.println("currParent sent to server from replyNested: " + currParent);
            //sending out the contents of the new reply
            String newNested = rep.getText();
            out.println(newNested);
            System.out.println("newNested sent to server from replyNested: " + newNested);
            //creating and sending a timestamp
            String timestamp = Reply.createTimestamp();
            out.println(timestamp.trim());
            System.out.println("timestamp sent to server from replyNested: " + timestamp);
            //sending out current username
            out.println(currUser);
            System.out.println("currUser sent to server from replyNested: " + currUser);
            //checking if addingReply was successful or not
            String replyAdded = "";
            try {
                replyAdded = in.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (replyAdded.equals("replyAdded")) {
                System.out.println("Received from server: replyAdded. Adding reply was successful!");
                JOptionPane.showMessageDialog(null, "Reply created!");
                nestedReply.dispose();
                viewReply(course, currDFQ, currParent, currUser, type);
            } else {
                System.out.println("Received from server: " + replyAdded + " . Adding reply failed!");
                JOptionPane.showMessageDialog(null, "Reply could not be created. Please try " +
                                "again!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton upload = new JButton("Upload File");
        upload.addActionListener(a -> {
            boolean read = false;
            int tryAgain;
            String newNested = "";
            String fileName;
            do {
                fileName = JOptionPane.showInputDialog(null, "What is the name of the file? " +
                                "(include .txt)","File Import Menu", JOptionPane.QUESTION_MESSAGE);
                if (fileName == null) {
                    fileName = "";
                }
                fileName = fileName.trim();
                Object[] option = {"Yes", "Edit", "Cancel"};
                try {
                    if (!fileName.equals("newaccounts.txt") && !fileName.equals("discussionforum.txt")) {
                        BufferedReader br = new BufferedReader(new FileReader(fileName));
                        newNested += br.readLine().trim();
                        System.out.println(newNested);

                        int confirm = JOptionPane.showOptionDialog(null,
                                "Confirm: is this what your comment should say?"
                                        + " \n" + newNested, "Confirm Reply", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, option, null);
                        while (confirm == JOptionPane.NO_OPTION) {
                            newNested = JOptionPane.showInputDialog(null, "Enter your edited " +
                                    "post: \n Current Post: \n" + newNested, "Edit Reply",
                                    JOptionPane.QUESTION_MESSAGE);
                            confirm = JOptionPane.showOptionDialog(null,
                                    "Confirm: is this what your comment should say?"
                                            + "\n" + newNested, "Confirm Reply", JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, option, null);
                        }
                        read = true;
                        if (confirm == JOptionPane.YES_OPTION) {
                            out.println("replyNested");
                            System.out.println("Sent to server: replyNested");
                            //sending out the current discussion forum
                            out.println(currDFQ.trim());
                            System.out.println("currDFQ sent to server from replyNested: " + currDFQ);
                            //sending out the parentReply
                            out.println(currParent);
                            System.out.println("currParent sent to server from replyNested: " + currParent);
                            //sending out the contents of the new reply
                            out.println(newNested);
                            System.out.println("newNested sent to server from replyNested: " + newNested);
                            //creating and sending a timestamp
                            String timestamp = Reply.createTimestamp();
                            out.println(timestamp.trim());
                            System.out.println("timestamp sent to server from replyNested: " + timestamp);
                            //sending out current username
                            out.println(currUser);
                            System.out.println("currUser sent to server from replyNested: " + currUser);
                            //checking if addingReply was successful or not
                            String replyAdded = "";
                            try {
                                replyAdded = in.readLine();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (replyAdded.equals("replyAdded")) {
                                System.out.println("Received from server: replyAdded. Adding reply was successful!");
                                JOptionPane.showMessageDialog(null, "Reply created!");

                            } else {
                                System.out.println("Received from server: " + replyAdded + " . Adding reply failed!");
                                JOptionPane.showMessageDialog(null, "Reply could not be created."
                                        + " Please try again!","Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        Object[] option2 = {"Try Again", "Cancel"};
                        tryAgain = JOptionPane.showOptionDialog(null, "Error! File not found!",
                                "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                option2, option2[0]);
                    }
                    tryAgain = JOptionPane.NO_OPTION;
                } catch (IOException e) {
                    Object[] option2 = {"Try Again", "Cancel"};
                    tryAgain = JOptionPane.showOptionDialog(null, "Error! File not found!",
                            "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            option2, option2[0]);
                }
            } while (!read && JOptionPane.YES_OPTION == tryAgain);
        });

        //sends back to dis() method
        JButton back = new JButton("Back");
        back.addActionListener(e -> {
            nestedReply.dispose();
            dis(course, type, currUser);
        });
        //sends back to main

        JButton main = new JButton("Main Menu");
        main.addActionListener(e -> {
            nestedReply.dispose();
            mainMenu(currUser, type);
        });

        bot.add(submit);
        bot.add(upload);
        bot.add(back);
        bot.add(main);
        nestedReply.add(bot, BorderLayout.SOUTH);

        nestedReply.setSize(500, 500);
        nestedReply.setVisible(true);
        nestedReply.setDefaultCloseOperation(EXIT_ON_CLOSE);
        nestedReply.setLocationRelativeTo(null);

    }

    public static void viewReply(String course, String currDFQ, String parentReply, String currUser, String type) {

        table = new JTable();
        button = new JButton();
        delete = new JButton();
        butt = new JButton();
        view = new JFrame("Reply");
        view.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        JLabel parent = new JLabel("[user]: " + currUser + " [comment]: " + parentReply);
        top.add(parent);
        view.add(top, BorderLayout.NORTH);

        //calling case viewReply in server
        out.println("viewReply");
        System.out.println("sent to server: " + "viewReply");
        out.println(parentReply); //sends the parent reply
        System.out.println("sent to server: " + parentReply);

        int size;
        try { //reads the size of the reply array<>
            size = Integer.parseInt(in.readLine());
            System.out.println("size of reply array received in viewReply in Client: " + size);
        } catch (IOException a) {
            size = 0;
            a.printStackTrace();
        }

        String[] columns = {"Name", "Time", "Content", "Upvotes"};
        Object[][] curReplies = new Object[size][4];
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                try {
                    // name
                    curReplies[i][0] = Client.in.readLine();
                    System.out.println("username in viewReply received by Server: " + curReplies[i][0]);
                    // time
                    curReplies[i][1] = Client.in.readLine();
                    System.out.println("time in viewReply received by Server: " + curReplies[i][1]);
                    // content
                    curReplies[i][2] = Client.in.readLine();
                    System.out.println("reply content in viewReply received by Server: " + curReplies[i][2]);
                    // upvotes
                    curReplies[i][3] = Client.in.readLine();
                    System.out.println("votes in viewReply received by Server: " + curReplies[i][3]);
                } catch (IOException a) {
                    System.out.println("server error in viewReply");
                }
            }
        }
        DefaultTableModel model = new DefaultTableModel(curReplies, columns);
        if (type.equals("teacher")) {
            model.addColumn("Delete");
        }
        table.setModel(model);

        table.getColumn("Upvotes").setCellRenderer(new ButtonRendererVoting());
        table.getColumn("Upvotes").setCellEditor(new ButtonEditorVoting(new JCheckBox()));

        table.getColumn("Content").setCellEditor(new ButtonEditorView(new JCheckBox()));
        table.getColumn("Content").setCellRenderer(new ButtonRendererView());

        if (type.equals("teacher")) {
            table.getColumn("Delete").setCellRenderer(new ButtonRendererDelete());
            table.getColumn("Delete").setCellEditor(new ButtonEditorDelete(new JCheckBox()));

            delete.addActionListener(event -> {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this comment?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION)) {
                    out.println("deleteReply");
                    System.out.println("sent to server: deleteReply");
                    int row = table.getSelectedRow();
                    String selectedContent = table.getValueAt(row, 2).toString();
                    out.println(selectedContent);
                    System.out.println("sent to server: " + selectedContent);
                    try {
                        String tf = in.readLine();
                        if (tf.equals("replyDeleted")) {
                            JOptionPane.showMessageDialog(null, "Reply has been Deleted",
                                    "Reply Deleted", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "There was an error deleting" +
                                    " this reply", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception Ea) {
                        JOptionPane.showMessageDialog(null, "There was an error deleting this " +
                                        "reply", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Delete Cancelled",
                                        "Cancel Menu",JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        view.add(scrollPane, BorderLayout.CENTER);

        butt.addActionListener(event -> {
            //to view the full reply
            int row = table.getSelectedRow();
            String selectedContent = table.getValueAt(row, 2).toString();
            JOptionPane.showMessageDialog(null, "Comment: " + selectedContent, "View",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        button.addActionListener(event -> {
            out.println("upvote");
            int row = table.getSelectedRow();
            String selectedContent = table.getValueAt(row, 2).toString();
            //send the current user
            out.println(currUser);
            System.out.println("currUser sent to server: " + currUser);
            //send the reply to be upvoted
            out.println(selectedContent);
            System.out.println("reply to be upvoted sent  to server: " + selectedContent);
            String voteAdded = "";
            try {
                voteAdded = in.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (voteAdded.equals("voteAdded")) {
                JOptionPane.showMessageDialog(null, "Reply upvoted successfully!");
            } else { //user has already liked the post
                JOptionPane.showMessageDialog(null, "You cannot upvote a post more than once!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bot = new JPanel();
        JButton back = new JButton("Back to Discussion Forum");
        back.addActionListener(e -> {
            view.dispose();
            dis(course, type, currUser);
        });
        JButton reply = new JButton("Reply");
        reply.addActionListener(e -> {
            view.dispose();
            nestedReply(course, currDFQ, parentReply, currUser, type);
        });

        bot.add(reply);
        bot.add(back);
        view.add(bot, BorderLayout.SOUTH);

        view.setSize(500, 500);
        view.setVisible(true);
        view.setDefaultCloseOperation(EXIT_ON_CLOSE);
        view.setLocationRelativeTo(null);
    }

    //for upvoting button
    static class ButtonRendererVoting extends JButton implements TableCellRenderer {
        public ButtonRendererVoting() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Vote" : value.toString());
            return this;
        }
    }

    static class ButtonEditorVoting extends DefaultCellEditor {
        private String label;

        public ButtonEditorVoting(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Vote" : value.toString();
            button.setText(label);
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

    //for grade button
    static class ButtonRendererGrades extends JButton implements TableCellRenderer {
        public ButtonRendererGrades() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Vote" : value.toString());
            return this;
        }
    }

    static class ButtonEditorGrades extends DefaultCellEditor {
        private String label;

        public ButtonEditorGrades(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Grade" : value.toString();
            button.setText(label);
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

    static class ButtonEditorView extends DefaultCellEditor {
        private String label;

        public ButtonEditorView(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Content" : value.toString();
            butt.setText(label);
            return butt;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

    static class ButtonRendererView extends JButton implements TableCellRenderer {
        public ButtonRendererView() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Content" : value.toString());
            return this;
        }
    }

    static class ButtonRendererDelete extends JButton implements TableCellRenderer {
        public ButtonRendererDelete() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText((value == null) ? "Delete" : value.toString());
            return this;
        }
    }

    static class ButtonEditorDelete extends DefaultCellEditor {
        private String label;

        public ButtonEditorDelete(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Delete" : value.toString();
            delete.setText(label);
            return delete;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }

}
