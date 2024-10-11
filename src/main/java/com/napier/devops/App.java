package com.napier.devops;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    public static void main(String[] args)
    {
        // Create new Application
        App app = new App();

        // Connect to database
        app.connect();

        // Get salaries by role
        String title = "Engineer";  // Example: specify the role here
        ArrayList<Employee> employees = app.getSalariesByRole(title);

        // Display results
        app.printSalaries(employees); // Using printSalaries to display the employee list with salaries

        // Disconnect from database
        app.disconnect();
    }

    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gets all the current employees and their salaries by role (title).
     * @param title The role to filter by.
     * @return A list of employees filtered by role, or null if there is an error.
     */
    public ArrayList<Employee> getSalariesByRole(String title)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement to filter by role (title)
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = titles.emp_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND titles.to_date = '9999-01-01' "
                            + "AND titles.title = '" + title + "' "
                            + "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details by role");
            return null;
        }
    }

    /**
     * Prints a list of employees and their salaries.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check if employees list is not empty
        if (!employees.isEmpty())
        {
            // Print header
            System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));

            // Loop over all employees in the list and print their details
            for (Employee emp : employees)
            {
                String emp_string = String.format("%-10s %-15s %-20s %-8s",
                        emp.emp_no, emp.first_name, emp.last_name, emp.salary);
                System.out.println(emp_string);
            }
        }
        else
        {
            System.out.println("No employees found for the given role.");
        }
    }
}
