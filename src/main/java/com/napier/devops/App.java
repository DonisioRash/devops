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
        app.displaySalariesByRole(employees);

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
     * Get Salaries by Role
     * @param title The job title to filter by (e.g., "Engineer")
     * @return A list of employees with their salaries who have the specified title
     */
    public ArrayList<Employee> getSalariesByRole(String title)
    {
        ArrayList<Employee> employees = new ArrayList<>();
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement with the title parameterized
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries, titles " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = titles.emp_no " +
                            "AND salaries.to_date = '9999-01-01' " +  // Get current salary
                            "AND titles.to_date = '9999-01-01' " +    // Get current title
                            "AND titles.title = '" + title + "' " +    // Filter by title
                            "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Loop through the result set
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);  // Add employee to the list
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries by role");
        }
        return employees;
    }

    /**
     * Display Salaries by Role
     */
    public void displaySalariesByRole(ArrayList<Employee> employees)
    {
        if (!employees.isEmpty())
        {
            for (Employee emp : employees)
            {
                System.out.println(
                        emp.emp_no + " " + emp.first_name + " " + emp.last_name + " - Salary: " + emp.salary
                );
            }
        }
        else
        {
            System.out.println("No employees found for the given role.");
        }
    }
}
