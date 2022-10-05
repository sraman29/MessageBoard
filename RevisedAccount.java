import java.io.*;
import java.util.ArrayList;

/**
 * RevisedAccount.java
 *
 * This server class organizes information about teacher and student objects based
 * on outside user interaction.
 *
 * @author Emma Maki, lab sec 13180-L19
 *
 * @version April 18, 2022
 *
 */

public class RevisedAccount {
    private static final String accountDeleted = "**ACCOUNT DELETED**";
    private static ArrayList<RevisedAccount> accounts = new ArrayList<>();

    private String username; //CAN ONLY CONTAIN LETTERS, NUMBERS, AND UNDERSCORES
    private String password; //CAN ONLY CONTAIN LETTERS, NUMBERS, AND UNDERSCORES
    private String exists;

    public RevisedAccount(String username, String password, String exists) {
        this.username = username;
        this.password = password;
        this.exists = exists.trim();
        accounts.add(this);
    }

    public RevisedAccount(String username, String password) {
        this.username = username;
        this.password = password;
        exists = "true";
        accounts.add(this);
    }

    public RevisedAccount() {
        this.username = "";
        this.password = "";
        exists = "true";
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<String> getStudents() {
        ArrayList<String> students = new ArrayList<>();
        if (this.isTeacher()) {
            for (RevisedAccount a : accounts) {
                if (a.isStudent()) {
                    students.add(a.getUsername());
                }
            }
            return students;
        } else { return null; }
    }

    public boolean isTeacher() { return this instanceof RevisedTeacherAccount; }

    public boolean isStudent() { return this instanceof RevisedStudentAccount; }

    public String getAccountType() {
        if (isStudent()) {
            return "student";
        } else if (isTeacher()) {
            return "teacher";
        }
        return null;
    }

    public static ArrayList<String> getAllStudents() {
        ArrayList<String> users = new ArrayList<>();
        for (RevisedAccount a : accounts) {
            if (a.isStudent() && a.doesExist()) {
                users.add(a.getUsername());
            }
        }
        return users;
    }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public static RevisedAccount getUser(String username) {
        for (RevisedAccount a : accounts) {
            if (a.getUsername().equals(username)) {
                return a;
            }
        }
        System.out.println("User does not exist!");
        return null;
    }

    public boolean doesExist() { return exists.equals("true"); }

    public static void clearAccounts() { accounts.clear(); }

    public static boolean checkValidity(String username, String password) {
        boolean addUser = false;
        boolean addPass = false;
        for (int i = 0; i < username.length(); i++) {
            char character = username.charAt(i);
            if ((character >= 48 && character <= 57) || (character >= 65 && character <= 90) ||
                    (character >= 97 && character <= 122) || (character == 95)) {
                addUser = true;
            } else {
                addUser = false;
                break;
            }
        }
        for (int i = 0; i < password.length(); i++) {
            char character = password.charAt(i);
            if ((character >= 48 && character <= 57) || (character >= 65 && character <= 90) ||
                    (character >= 97 && character <= 122) || (character == 95)) {
                addPass = true;
            } else {
                addPass = false;
                break;
            }
        }
        if (username.equals(accountDeleted) || password.equals(accountDeleted)) { addUser = true; }
        if (!addUser || !addPass) {
            System.out.println("Username and/or password contained unsupported characters.");
        }
        return addUser && addPass;
    }

    public static void readAccounts() {
        ArrayList<String> fileAccounts = new ArrayList<>();
        File file = null;
        try {
            file = new File("newaccounts.txt");
            if (file.createNewFile()) {
                System.out.println(file + " created!");
            }
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            while (bfr.ready()) { fileAccounts.add(bfr.readLine()); }
            bfr.close();
            //removing item from array if empty or blank
            fileAccounts.removeIf(a -> a.isEmpty() || a.isBlank());
            //adding password, username, and account type fields -> create arraylist of accounts
            for (String fileAccount : fileAccounts) {
                int indexUser = fileAccount.indexOf("[username]:");
                int indexPass = fileAccount.indexOf("[password]:");
                int indexAccType = fileAccount.indexOf("[account_type]:");
                int indexExists = fileAccount.indexOf("[account_exists]:");
                String usernameChecked = fileAccount.substring(indexUser + 11, indexPass).trim();
                String passwordChecked = fileAccount.substring(indexPass + 11, indexExists).trim();
                String accountExists = fileAccount.substring(indexExists + 17, indexAccType);
                String accountTypeChecked = fileAccount.substring(indexAccType + 15).trim();
                if (accountTypeChecked.equals("student")) {
                    RevisedStudentAccount student = new RevisedStudentAccount(usernameChecked, passwordChecked, accountExists);
                } else if (accountTypeChecked.equals("teacher")) {
                    RevisedTeacherAccount teacher = new RevisedTeacherAccount(usernameChecked, passwordChecked, accountExists);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error reading '" + file + "'.");
        }
    }

    public static void writeAccounts() {
        String fileName = "newaccounts.txt";
        try (FileWriter fw = new FileWriter(fileName);
             PrintWriter pw = new PrintWriter(fw)) {
            for (RevisedAccount a : accounts) {
                pw.println(a);
            }
        } catch (FileNotFoundException e) {
            System.out.println(fileName + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RevisedAccount login(String username, String password) {
        for (RevisedAccount a : accounts) {
            if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                if (a.doesExist()) {
                    return a;
                } else {
                    System.out.println("Account is no longer active.");
                    return null;
                }
            }
        }
        System.out.println("Either the username or password was incorrect, or you have not yet created an account.");
        return null;
    }

    public boolean deleteAccount() {
        boolean accountExists = false;
        RevisedAccount user = login(username, password);
        if (user != null) {
            accountExists = true;
            user.exists = "false";
            user.setUsername(accountDeleted);
            user.setPassword(accountDeleted);
            writeAccounts();
            System.out.println("Account deleted successfully.");

        }
        return accountExists;
    }


    public boolean editPassword(String oldPassword, String newPassword) {
        boolean accountExists = true;

        for (RevisedAccount a : accounts) {

            if (a.getPassword().equals(oldPassword)) {
                if (newPassword.equals(a. getPassword())) {
                    System.out.println("New password cannot be the same as old password!");
                    return false;
                } else if (newPassword.equals("")) {
                    System.out.println("New password cannot be blank!");
                } else {
                    this.password = newPassword;
                    System.out.println("Password changed!");
                    System.out.println("Account info updated: " + a);
                    writeAccounts();
                    accountExists = true;
                    break;
                }
            } else {
                accountExists = false;
            }
        }
        if (!accountExists) { System.out.println("Account could not be deleted either because your account doesn't exist, " +
                "or you are not yet logged in."); }
        return accountExists;
    }

    public boolean editProfileName(String newProfileName) {
        boolean accountExists = true;
        for (RevisedAccount a : accounts) {
            if (login(username, password) != null) {
                if (newProfileName.equals(username)) {
                    System.out.println("New username cannot be the same as old username!");
                    return false;
                } else if (newProfileName.equals("")) {
                    System.out.println("New password cannot be blank!");
                } else {
                    this.username = newProfileName;
                    System.out.println("Username changed!");
                    System.out.println("Account info updated: " + a);
                    writeAccounts();
                    accountExists = true;
                    break;
                }
            } else {
                accountExists = false;
            }
        }
        if (!accountExists) { System.out.println("Account could not be deleted either because your account doesn't exist, " +
                "or you are not yet logged in."); }
        return accountExists;
    }


    public static boolean createAccount(String username, String password, String accountType) {
        //checking if user already exists
        boolean addUser = true;
        for (RevisedAccount a : accounts) {
            if (a.getUsername().equals(username)) {
                System.out.println("Account could not be created because username is already in use.");
                return false;
            } //else, continue
        }
        boolean checked = false;
        if (checkValidity(username, password)) {
            checked = true;
            //creating a new account
            if (accountType.equals("student")) {
                RevisedStudentAccount student = new RevisedStudentAccount(username, password);
            } else if (accountType.equals("teacher")) {
                RevisedTeacherAccount teacher = new RevisedTeacherAccount(username, password);
            }
            //writing to file
            System.out.println("WRITING TO accounts.txt in RevisedAccount.java");
            writeAccounts();
        }
        if (checked) { System.out.println("Account created successfully!"); }
        return checked;
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

    public String toString() {
        return String.format("[username]:%s [password]:%s [account_exists]:%s", username, password, exists);
    }
}
