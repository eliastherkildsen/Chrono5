import java.sql.*;
import java.util.Locale;
import java.util.Objects;
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

        //deleteProjectSql(connection, 8);
        updateProjectInterface();

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
        input = input.toLowerCase(Locale.ROOT);
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

    /**
     * method for handling project. Let user handle a project.
     */
    public static void handleProject() {
        Scanner in = new Scanner(System.in);
        do {
            System.out.print("Input [1] to search for project. Input [2] to create a new project: ");
            int input = in.nextInt();
            if (input == 1) {
                searchProject();
                break;
            } else if (input == 2) {
                //createProject();
                break;
            } else {
                System.out.printf("%sInvalid input, please try again!%s%n", ANSI_RED, ANSI_RESET);
            }
        } while (true);
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
                String projectId = projectDetails.getString("fldProjectID");
                String projectName = projectDetails.getString("fldProjectName");
                String projectStart = projectDetails.getString("fldProjectStartDate");
                String projectEnd = projectDetails.getString("fldProjectEndDate");

                System.out.printf("%s%s%n",ANSI_GREEN,"Project ID: " + projectId);
                System.out.printf("%s%n","Project name: " + projectName);
                System.out.printf("%s%n","Start date: " + projectStart);
                System.out.printf("%s%s%n","End date: " + projectEnd,ANSI_RESET);
            }
            /*String promptUpdate = "Would you like to update project? input [yes] or [no]: ";
            boolean updateProject = askYesNo(promptUpdate);
            if (updateProject) {
                System.out.println("Update project!");
            }*/
        } catch (SQLException e) {

        }
    }
    /**
     * Sending a SQL UPDATE to the DB to change fields. Includes basic SQL error handling.
     * @param connection To function with our DB
     *
     * @param column Name of the column you want to change.
     * @param value The value you want to change.
     * @param Condition The condition of the change.
     */
    public static void editProjectUpdateSql(Connection connection, String column, String value, String Condition) {
        try {
            //Prepare SQL
            PreparedStatement preparedStatement = connection.prepareCall("UPDATE tblProject SET " + column + " = '" + value + "' WHERE " + Condition);

            //Execute it.
            int row = preparedStatement.executeUpdate();
            System.out.printf("%s%S%d%s%n",ANSI_YELLOW,"rows affected: ",row,ANSI_RESET);

            //Catch Error with some what usable text if relevant.
        } catch (SQLException e) {
            System.out.printf("%s%S%s%n",ANSI_RED,"error:",ANSI_RESET);
            e.printStackTrace();
        }
    }

    /**
     * Method to delete row in SQL
     * @param connection connection
     * @param condition fldProjectID
     */
    public static void deleteProjectSql(Connection connection, int condition) {
        try {
            //Prepare SQL
            PreparedStatement preparedStatement = connection.prepareCall("DELETE FROM tblProject WHERE fldProjectID = " + condition);
            boolean areYouSure = false;
            displayProject(condition);
            do {
                System.out.printf("%s%S%s%n%S%n%S%n", ANSI_RED,"you are about to delete project " + condition + ", are you sure?", ANSI_RESET, "TYPE: DELETE " + condition, "or no to cancel.");
                String input = getUserInputStr();
                if (Objects.equals(input, "delete " + condition)) {
                    int row = preparedStatement.executeUpdate();
                    System.out.printf("%s%S%s%d%n", ANSI_YELLOW, "Deletion complete - rows affected: ", ANSI_RESET, row);
                    areYouSure = true;
                } else if (Objects.equals(input, "no")) {
                    System.out.printf("%s%S%s%n", ANSI_YELLOW, "Cancelling..", ANSI_RESET);
                    areYouSure = true;
                } else {
                    System.out.printf("%s%S%s%n", ANSI_YELLOW,"Invalid Input.",ANSI_RESET);
                    areYouSure = false;
                }
            } while(!areYouSure);

            //Catch Error with somewhat usable text if relevant.
        } catch (SQLException e) {
            System.out.printf("%s%S%s%n",ANSI_RED,"error:",ANSI_RESET);
            e.printStackTrace();
        }
    }

    /**
     * updateProjectInterface facilitates the use of update and delete.
     */
    public static void updateProjectInterface () {
        int projectId = 1;
        boolean doWhileAction1;
        boolean doWhileAction2;
        String column = "";
        String value = "";
        String condition = "fldProjectID = " + projectId;

        System.out.printf("%n%s%S%s%n",ANSI_YELLOW,"Update your project.",ANSI_RESET);
        displayProject(projectId);

        do {
            System.out.printf("%n%s%n","Which action do you want?");
            int actionChosen = getUserInputInt("To EDIT type 1 or 2 to DELETE:%n");
            doWhileAction1 = false;
            if (actionChosen == 1) {
                displayProject(projectId);
                System.out.printf("%S%n%S%n%S%n", "[1] - Project Name", "[2] - Start Date", "[3] - End Date");
                actionChosen = getUserInputInt("Type 1 - 2 - 3 depending on field you wish to change:%n");
                do {
                    doWhileAction2 = false;
                    switch (actionChosen) {
                        case 1:
                            System.out.printf("%s%n", "[1] - Project Name");
                            column = "fldProjectName";
                            System.out.printf("%s","Please input new Project Name: ");
                            value = getUserInputStr();
                            doWhileAction2 = true;
                            break;
                        case 2:
                            System.out.printf("%s%n", "[2] - Start Date");
                            column = "fldProjectStartDate";
                            System.out.printf("%s","Please input new Start Date: ");
                            value = dateFormattingWithValidation();
                            doWhileAction2 = true;
                            break;
                        case 3:
                            System.out.printf("%s%n", "[3] - End Date");
                            column = "fldProjectEndDate";
                            System.out.printf("%s","Please input new End Date: ");
                            value = dateFormattingWithValidation();
                            doWhileAction2 = true;
                            break;
                        default:
                            System.out.printf("%s", "What?");
                    }
                } while (!doWhileAction2);
                editProjectUpdateSql(connection, column, value, condition);
                doWhileAction1 = true;
            } else if (actionChosen == 2) {
                deleteProjectSql(connection, projectId);
                doWhileAction1 = true;
            } else {
                System.out.printf("%s%S%s",ANSI_RED,"Please try again.",ANSI_RESET);
            }
        } while (!doWhileAction1);


    }

    // ANSI escape code colors.
    // from -> https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

}