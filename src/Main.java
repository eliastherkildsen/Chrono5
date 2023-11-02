import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main{

    // JDBC PROPS
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "1234";
    public static final String DATABASE_NAME = "dbChrono5";
    public static final String PORT = "1433";
    public static final String ENCRYPT = "false";
    public static final String URL = "jdbc:sqlserver://localhost:"+ PORT +";databaseName="+DATABASE_NAME;

    public static Connection connection = null;


    public static void main(String[] args) {

        System.out.printf("%sProgram starting.%s%n", ANSI_YELLOW, ANSI_RESET);

        // getting props
        Properties properties = setProps();
        System.out.printf("%sSetting up props.%s%n", ANSI_YELLOW, ANSI_RESET);

        // creating JDBC connection
        connection = databaseConnection(properties, URL);
        System.out.printf("%sCreating connection.%s%n", ANSI_YELLOW, ANSI_RESET);

        // closing JDBC connection
        databaseClose(connection);


    }

    /***
     * Properties method used to create a properties containing all props used to
     * connect to JDBC
     * @return properties
     */
    public static Properties setProps(){
        // creating an intestines of properties.
        Properties properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("encrypt", ENCRYPT);

        databaseConnection(properties, URL);
        return properties;
    }

    /***
     * Method used to close a database connection.
     * @param connection
     */
    public static void databaseClose(Connection connection){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // closing connection to JDBC
        System.out.printf("%sClosing connection to JDBC..%s", ANSI_YELLOW, ANSI_RESET);



    }

    /***
     * method for creating a connection to a database
     * @param properties
     * @param URL
     * @return
     */
    public static Connection databaseConnection(Properties properties, String URL){

        // initializes connection.
        Connection connection = null;

        // JDBC tries to connect to SQL database add URL.
        try {
            connection = DriverManager.getConnection(URL, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;

    }

    public static void test(){

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareCall("SELECT * FROM tblUser");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            // Assuming you already have a PreparedStatement object named preparedStatement
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Assuming "tblUser" has columns like "column1", "column2", etc.
                int column1Value = resultSet.getInt("fldID");
                String column2Value = resultSet.getString("fldName");
                // Retrieve other columns as needed

                System.out.println("column1: " + column1Value + ", column2: " + column2Value);
                // Print other columns as needed
            }

            resultSet.close();
        } catch (SQLException e) {

        }


    }

    /***
     * method for getting a user input as a string. and closing scanner again.
     * @return user input.
     */
    public static String getUserInputStr(){
        // init scanner and string to hold input.
        Scanner scanner = new Scanner(System.in);
        String input = "";

        // reads input.
        input = scanner.nextLine();

        // returns input.
        return input;
    }

    /***
     * method for getting a user input. checking if the input is a numeric value
     * checking if the value is positive.
     * and closing scanner again.
     * @return user input if valid.
     */
    public static int getUserInputInt(String prompt){

        Scanner scanner = new Scanner(System.in);
        int input;

        do {
            // checks if user has entered an integer.
            System.out.printf(prompt);
            if (scanner.hasNextInt()){
                input = scanner.nextInt();
                // checks if entered integer is negative.
                if (!(input >= 0)){
                    System.out.printf("%sYou have entered a negative number witch is not allowed%s%n", ANSI_RED, ANSI_RESET);
                }

                break;


            }
            // if a none numeric value is entered.
            else {
                System.out.printf("%sYou are only allowed to enter numbers, please try again.%s%n", ANSI_RED, ANSI_RESET);
                scanner.nextLine();
            }

        }while (true);
        return input;

    }

    /**
     * prompts the user for a data, month, year
     * validates the inputs, and returns them in a string formatted DD.MM.YY
     * @return formatted date
     */
    public static String dateFormattingWithValidation() {

        int day;
        int month;
        int year;
        String prompt;

        do {
            prompt = "Enter day (DD): ";
            day = getUserInputInt(prompt);

            // checks if the month is in the valid range (1 - 12).
            if (day >= 1 && day <= 31){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value witch is not a valid. " +
                    "day! please enter a day 1 between 31 %s%n", ANSI_RED,ANSI_RESET);


        } while (true);

        do {
            prompt = "Enter month (MM): ";
            month = getUserInputInt(prompt);

            // checks if the month is in the valid range (1 - 12).
            if (month >= 1 && month <= 12){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value witch is not a valid. " +
                    "month! please enter a month 1 between 12 %s%n", ANSI_RED,ANSI_RESET);


        } while (true);

        do {
            prompt = "Enter year (YYYY): ";
            year = getUserInputInt(prompt);
            // checks if the year is in the valid range.
            if (year >= 2022 && year <= 2030){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value  witch is not a valid. " +
                    "day! please enter a year between 2022 and 2030%s%n", ANSI_RED,ANSI_RESET);

        } while (true);

        return String.format("%s.%s.%s",day, month, year);


    }

    /**
     * Method to create a project, prompts user for project information ex.
     * projectStartDate, projectEndDate and projectName.
     * writes all project attributes to the database.
     */
    public static void createProject(){

        // initializing
        String projectStartDate;
        String projectEndDate;
        String projectName;


        // prompts the user to enter a project name and saves the input.
        System.out.printf("Pleas give your project a name %n");
        projectName = getUserInputStr();

        // prompt user to create a project start date and saves the input.
        System.out.printf("Please enter information relating to the project start date%n");
        projectStartDate = dateFormattingWithValidation();

        // prompt user to create a project start data and saves the input.
        System.out.printf("Please enter information relating to the project end date%n");
        projectEndDate = dateFormattingWithValidation();

        // preparing SQL quarry.
        String quarryValues = String.format("('" + projectStartDate + "'," + "'" + projectEndDate + "'," + "'" + projectName + "')");
        String quarry = "INSERT INTO tblProject (fldProjectStartDate, fldProjectEndDate, fldProjectName) VALUES " + quarryValues;

        // preparing SQL statement.
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareCall(quarry);
            System.out.printf("%sQuarry sent! %s",ANSI_YELLOW,ANSI_RESET );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * method prompts for an input, then checks if input is yes or no.
     * @return true if input is yes, false if input is no.
     */
    public static boolean askYesNo(String prompt) {
        Scanner in = new Scanner(System.in);
        do {
            System.out.print(prompt);
            String input = in.nextLine().toLowerCase();
            if (input.equals("yes")) {
                return true;
            } else if (input.equals("no")) {
                return false;
            } else {
                System.out.printf("%sInvalid input, please try again!%s%n", ANSI_RED, ANSI_RESET);
            }
        } while(true);
    }


    public static void searchProject() {
        Scanner in = new Scanner(System.in);
        System.out.println("Search for project");
        boolean match = false;
        do {
            System.out.print("Project ID: ");
            int searchID = in.nextInt();
            PreparedStatement getProjectIDs = null;
            try {
                getProjectIDs = connection.prepareCall("SELECT fldProjectID FROM tblProject");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                ResultSet projectIDs = getProjectIDs.executeQuery();
                while (projectIDs.next()) {
                    int projectID = projectIDs.getInt("fldProjectID");
                    if (searchID == projectID) {
                        match = true;
                        displayProject(searchID);
                        break;
                    }
                }
                if (match != true) {
                    System.out.printf("%sThere is no project with that ID, please try again!%s%n", ANSI_RED, ANSI_RESET);
                }
            } catch (SQLException e) {

            }

        } while (!match);
    }

    public static void displayProject(int projectID) {
        PreparedStatement getProject = null;
        try {
            getProject = connection.prepareCall("SELECT * FROM tblProject where fldProjectID=" + projectID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            ResultSet projectDetails = getProject.executeQuery();
            while (projectDetails.next()) {
                String projectName = projectDetails.getString("fldProjectName");
                String projectStart = projectDetails.getString("fldProjectStartDate");
                String projectEnd = projectDetails.getString("fldProjectEndDate");

                System.out.println("Project name: " + projectName);
                System.out.println("Start date: " + projectStart);
                System.out.println("End date: " + projectEnd);
            }
            String promptUpdate = "Would you like to update project? input [yes] or [no]: ";
            boolean updateProject = askYesNo(promptUpdate);
            if (updateProject) {
                System.out.println("Update project!");
            }
        } catch (SQLException e) {

        }
    }


    // ANSI escape code colors.
    // from -> https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

}