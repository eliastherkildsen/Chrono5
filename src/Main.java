import javax.sound.midi.SoundbankResource;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;


public class Main{
    // JDBC PROPS
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "1234";
    public static final String DATABASE_NAME = "dbTest";
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

        System.out.println(DateFormattingWithValidation());


        // closing JDBC connection
        databaseClose(connection);


    }

    /***
     * Properties method used to create a propertie containing all props used to
     * connect to JDBC
     * @return properties
     */
    public static Properties setProps(){
        // creating a intestines of properties.
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

        // JDBC tryes to connect to SQL database add URL.
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
     * method for getting a userinput as a string. and closing scanner again.
     * @return userinput.
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

    /**
     * method for getting a user input. checking if the input is a numeric value
     * checking if the value is positive.
     * and closing scanner again.
     * @return
     */
    public static int getUserInputInt(){

        Scanner scanner = new Scanner(System.in);
        int input;

        do {
            // checks if user has entered a integer.
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
                String x =scanner.nextLine();
            }

        }while (true);
        return input;

    }

    /**
     * prompts the user for a data, month, year
     * validates the inputs, and returns them in a string formated DD.MM.YY
     * @return formateddate
     */
    public static String DateFormattingWithValidation() {

        int day;
        int month;
        int year;

        do {
            System.out.print("Enter day (DD): ");
            day = getUserInputInt();

            // checks if the month is in the vaild range (1 - 12).
            if (day >= 1 && day <= 31){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value witch is not a vaild. " +
                    "day! please enter a day 1 between 31 2023%s%n", ANSI_RED,ANSI_RESET);


        } while (true);

        do {
            System.out.print("Enter month (MM): ");
            month = getUserInputInt();

            // checks if the month is in the vaild range (1 - 12).
            if (month >= 1 && month <= 12){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value witch is not a vaild. " +
                    "month! please enter a month 1 between 12 2023%s%n", ANSI_RED,ANSI_RESET);


        } while (true);

        do {
            System.out.print("Enter year (YYYY): ");
            year = getUserInputInt();
            // checks if the year is in the vaild range.
            if (year >= 2022 && year >= 2030){
                break;
            }

            // sends error message to user.
            System.out.printf("%sYou have entered a value  witch is not a vaild. " +
                    "day! please enter a year between 2022 and 2023%s%n", ANSI_RED,ANSI_RESET);

        } while (true);

        return String.format("%s.%s.%s",day, month, year);


    }
    /**
     * Sending a SQL UPDATE to the DB to change fields. Includes basic SQL error handling.
     * @param connection To function with our DB
     * @param tableName Name of the table you want to edit.
     * @param column Name of the column you want to change.
     * @param value The value you want to change.
     * @param Condition The condition of the change.
     */
    public static void editProject(Connection connection, String tableName, String column, String value, String Condition) {
        try {
            //Prepare SQL
            PreparedStatement preparedStatement = connection.prepareCall("UPDATE " + tableName + " SET " + column + " = '" + value + "' WHERE " + Condition);

            //Execute it.
            int row = preparedStatement.executeUpdate();
            System.out.printf("%s%S%d%n",ANSI_YELLOW,"rows affected: ",row);

            //Catch Error with some what usable text if relevant.
        } catch (SQLException e) {
            System.out.println("Error: ");
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