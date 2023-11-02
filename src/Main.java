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

        //Main US
        //handleProject();

        mainMenu();

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
        projectName = getProjectName();

        // prompt user to create a project start date and saves the input.
        projectStartDate = getProjectDate("start date");

        // prompt user to create a project end date and saves the input.
        projectEndDate = getProjectDate("end date");


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

    /***
     * Method prompts the user to enter start date for project
     * @param date
     * @return
     */
    private static String getProjectDate(String date) {
        String formattedDate;
        do {
            System.out.printf("Please enter information relating to the %s %n", date);
            formattedDate = dateFormattingWithValidation();

            // prompts the user to check if the entered name is correct!
            String prompt = String.format("Is %s%s%s the correct %s for your project? %n enter " +
                    "[yes] to continue or enter [no] to try again!",ANSI_GREEN, formattedDate, ANSI_RESET, date);
            if (askYesNo(prompt)){
                break;
            }

        }while (true);

        return formattedDate;

    }

    /***
     * Method prompts the user for a project name, and
     * gives the user the ability to re-enter the project name.
     * @return the project name.
     */
    private static String getProjectName() {
        // initialise variables.
        String projectName;
        do {
            // prompts the user to enter a project name.
            System.out.printf("Pleas give your project a name %n");
            projectName = getUserInputStr();

            // prompts the user to check if the entered name is correct!
            String prompt = String.format("Is %s%s%s the correct name for your project? %n " +
                    "enter [yes] to continue or enter [no] to try again!",ANSI_GREEN, projectName, ANSI_RESET);

            // checks if the user wants to retry entering the project name.
            if (askYesNo(prompt)){
                break;
            }
        }while (true);

        return projectName;
    }

    /**
     * method prompts for an input, then checks if input is yes or no.
     * @return true if input is yes, false if input is no.
     */
    public static boolean askYesNo(String prompt) {
        do {
            //Prompt user to input yes or no
            System.out.print(prompt);
            String input = getUserInputStr();
            //If input is yes return true
            if (input.equals("yes")) {
                return true;
            //If input is no return false
            } else if (input.equals("no")) {
                return false;
            //If invalid input print error-message and repeat prompt
            } else {
                System.out.printf("%sInvalid input, please try again!%s%n", ANSI_RED, ANSI_RESET);
            }
        } while(true);
    }

    /**
     * method for choosing function. Lets user choose what function to process in program.
     */
    public static void mainMenu() {
        do {
            //Prompt user to input 1, 2 or 3 i.e. choose function
            System.out.println("Please choose a function");
            System.out.println("[1] to handle project");
            System.out.println("[2] to administer customer");
            System.out.println("[3] to administer consultant");
            System.out.println("[exit] to exit program");
            System.out.print("Input: ");
            String input = getUserInputStr();
            //Run function corresponding with input, repeat until user input exit
            switch (input) {
                case "1": handleProject(); break;
                case "2": /*administerCustomer*/ break;
                case "3": /*administerConsultant*/ break;
                case "exit": System.exit(0);
                default: System.out.printf("%sInvalid input, please try again!%s%n", ANSI_RED, ANSI_RESET);
            }
        } while (true);
    }

    /**
     * method for handling project. Lets user handle a project.
     */
    public static void handleProject() {
        do {
            //Prompt user to input 1 or 2 i.e. choose function
            String prompt = "Input [1] to search for project. Input [2] to create a new project: ";
            int input = getUserInputInt(prompt);
            //If input is 1 then search project
            if (input == 1) {
                searchProject();
                break;
            //If input is 2 then create project
            } else if (input == 2) {
                createProject();
                break;
            //If invalid input print error-message and repeat prompt
            } else {
                System.out.printf("%sInvalid input, please try again!%s%n", ANSI_RED, ANSI_RESET);
            }
        } while (true);
    }

    /**
     * method for searching project. Lets user search a project.
     */
    public static void searchProject() {
        //Search for project
        System.out.println("Search for project");
        boolean match = false;
        do {
            //Prompt user for ID, use input to search in database
            String promptID = "Project ID: ";
            int searchID = getUserInputInt(promptID);
            //Prepare SQL-statement
            PreparedStatement getProjectIDs = null;
            //Get all IDs from database with SQL-statement
            try {
                getProjectIDs = connection.prepareCall("SELECT fldProjectID FROM tblProject");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //Check if input match with any ID from database
            try {
                ResultSet projectIDs = getProjectIDs.executeQuery();
                while (projectIDs.next()) {
                    int projectID = projectIDs.getInt("fldProjectID");
                    //If matched, end searchProject and then call displayProject
                    if (searchID == projectID) {
                        match = true;
                        displayProject(searchID);
                        break;
                    }
                }
                //If not matched, print error-message and repeat prompt
                if (match != true) {
                    System.out.printf("%sThere is no project with that ID, please try again!%s%n", ANSI_RED, ANSI_RESET);
                }
            } catch (SQLException e) {}
        } while (!match);
    }

    /**
     * method for displaying project.
     * @param projectID to display.
     */
    public static void displayProject(int projectID) {
        //Prepare SQl-statement
        PreparedStatement getProject = null;
        //Get project from projectID in database with SQl-statement
        try {
            getProject = connection.prepareCall("SELECT * FROM tblProject where fldProjectID=" + projectID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //Display project details
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
            //Prompt user for choice, let user chose to update
            String promptUpdate = "Would you like to update project? input [yes] or [no]: ";
            boolean updateProject = askYesNo(promptUpdate);
            if (updateProject) {
                // editProjectUpdateSql();
            }
        } catch (SQLException e) {}
    }
    /**
     * Sending a SQL UPDATE to the DB to change fields. Includes basic SQL error handling.
     * @param connection To function with our DB
     * @param tableName Name of the table you want to edit.
     * @param column Name of the column you want to change.
     * @param value The value you want to change.
     * @param Condition The condition of the change.
     */
    public static void editProjectUpdateSql(Connection connection, String tableName, String column, String value, String Condition) {
        try {
            //Prepare SQL
            PreparedStatement preparedStatement = connection.prepareCall("UPDATE " + tableName + " SET " + column + " = '" + value + "' WHERE " + Condition);

            //Execute it.
            int row = preparedStatement.executeUpdate();
            System.out.printf("%s%S%d%s%n",ANSI_YELLOW,"rows affected: ",row,ANSI_RESET);

            //Catch Error with some what usable text if relevant.
        } catch (SQLException e) {
            System.out.printf("%s%S%s%n",ANSI_RED,"error:",ANSI_RESET);
            e.printStackTrace();
        }
    }
    public static void deleteProjectSql(Connection connection, int condition) {
        try {
            //Prepare SQL
            PreparedStatement preparedStatement = connection.prepareCall("DELETE FROM tblProject WHERE fldProjectID = " + condition);
            boolean areYouSure = false;

            do {
                System.out.printf("%s%S%n%s%S%S%n", ANSI_RED,"you are about to delete a project, are you sure?", ANSI_RESET, "TYPE: DELETE " + condition, ". or no");
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

    // ANSI escape code colors.
    // from -> https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

}