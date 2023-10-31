import java.sql.*;
import java.util.Properties;

public class Main{

    public static void main(String[] args) {

        databaseConnection();

    }

    public static void databaseConnection(){

        // initializing JDBC information's
        final String USERNAME = "sa";
        final String PASSWORD = "1234";
        final String DATABASE_NAME = "dbTest";
        final String PORT = "1433";
        final String ENCRYPT = "false";
        final String URL = "jdbc:sqlserver://localhost:"+ PORT +";databaseName="+DATABASE_NAME;


        // creating the property's for JDBC connection.
        Properties properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("encrypt", ENCRYPT);


        // initializes connection.
        Connection connection = null;
        // JDBC tryes to connect to SQL database add URL.
        try {
            connection = DriverManager.getConnection(URL, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Hello JDBC");

        /* ---------------------------------------------------------------------------------------------
            eksampel på sql quarry!
        */
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareCall("SELECT * FROM tblUser");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // setting up result set
        //ResultSet resultSet = preparedStatement.executeQuery(); // Read type.
        //preparedStatement.executeUpdate();


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

        /* ---------------------------------------------------------------------------------------------
            End of example.
        */

        // closing connection to JDBC
        System.out.println("Closing connection to JDBC..");
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    // ANSI escape code colors.
    // from -> https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

}