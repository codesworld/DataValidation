import java.sql.Connection;

import Utils.DBConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.engine.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataValidationTest {
    private static Connection connection;

    @BeforeAll
    public static void setup() {
        try{
        connection = DBConnection.getConnection();
        Assertions.assertNotNull(connection, "Database connection is NULL. Check your config.");
        System.out.println("Database connection successful!");
    } catch (Exception e) {
            Assertions.fail("Database connection failed: " + e.getMessage());
        }
    }

    @AfterAll
    public static void teardown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEmployeeCount() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM chinook.Employee";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                System.out.println("Employee Count: " + count);
                Assertions.assertTrue(count > 0, "Employee count should be greater than 0");
            }
        }
    }

    @Test
    public void testCustomerEmails() throws SQLException {
        String query = "SELECT Email FROM chinook.Customer LIMIT 5";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String email = resultSet.getString("Email");
                System.out.println("Customer Email: " + email);
                Assertions.assertTrue(email.contains("@"), "Email should contain '@'");
            }
        }

    }
    /**
     * 1. Customers who bought the most songs
     */
    @Test
    public void testTopCustomerByTrackPurchases() throws SQLException {
        String query = """
            SELECT c.CustomerId, c.FirstName, c.LastName, COUNT(il.TrackId) AS TotalTracks
            FROM chinook.Customer c
            JOIN Invoice i ON c.CustomerId = i.CustomerId
            JOIN InvoiceLine il ON i.InvoiceId = il.InvoiceId
            GROUP BY c.CustomerId, c.FirstName, c.LastName
            ORDER BY TotalTracks DESC
            LIMIT 1;
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Assertions.assertTrue(resultSet.next(), "No customers found.");

            int totalTracks = resultSet.getInt("TotalTracks");
            System.out.println("Top Customer Purchased Tracks: " + totalTracks);
            Assertions.assertTrue(totalTracks > 0, "Top customer should have purchased at least 1 track.");
        }
    }

    /**
     * 2. top billing countries
     */
    @Test
    public void testTopCountriesByInvoiceCount() throws SQLException {
        String query = """
            SELECT BillingCountry, COUNT(*) AS InvoiceCount
            FROM Invoice
            GROUP BY BillingCountry
            ORDER BY InvoiceCount DESC
            LIMIT 5;
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Assertions.assertTrue(resultSet.next(), "No invoice data found.");

            int invoiceCount = resultSet.getInt("InvoiceCount");
            System.out.println("Country with highest invoices: " + resultSet.getString("BillingCountry") +
                    " - " + invoiceCount + " invoices");

            Assertions.assertTrue(invoiceCount > 0, "Invoice count should be greater than 0.");
        }
    }

    /**
     * 3. most popular artist
     */
    @Test
    public void testMostPopularArtistBySales() throws SQLException {
        String query = """
            SELECT ar.ArtistId, ar.Name, COUNT(il.InvoiceLineId) AS TotalSales
            FROM InvoiceLine il
            JOIN Track t ON il.TrackId = t.TrackId
            JOIN Album a ON t.AlbumId = a.AlbumId
            JOIN Artist ar ON a.ArtistId = ar.ArtistId
            GROUP BY ar.ArtistId, ar.Name
            ORDER BY TotalSales DESC
            LIMIT 1;
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Assertions.assertTrue(resultSet.next(), "No artist sales data found.");

            int totalSales = resultSet.getInt("TotalSales");
            System.out.println("Most Popular Artist: " + resultSet.getString("Name") + " - " + totalSales + " sales");

            Assertions.assertTrue(totalSales > 0, "Most popular artist must have at least 1 sale.");
        }
    }

    /**
     * 4. most popular genre
     */
    @Test
    public void testMostPopularGenre() throws SQLException {
        String query = """
            SELECT g.Name, COUNT(t.TrackId) AS TrackCount
            FROM Genre g
            JOIN Track t ON g.GenreId = t.GenreId
            GROUP BY g.Name
            ORDER BY TrackCount DESC
            LIMIT 1;
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Assertions.assertTrue(resultSet.next(), "No genre data found.");

            int trackCount = resultSet.getInt("TrackCount");
            System.out.println("Most Popular Genre: " + resultSet.getString("Name") + " - " + trackCount + " tracks");

            Assertions.assertTrue(trackCount > 0, "Most popular genre must have at least 1 track.");
        }
    }

    /**
     * 5. most expensive invoice
     */
    @Test
    public void testMostExpensiveInvoice() throws SQLException {
        String query = """
            SELECT InvoiceId, CustomerId, Total
            FROM Invoice
            ORDER BY Total DESC
            LIMIT 1;
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            Assertions.assertTrue(resultSet.next(), "No invoice data found.");

            double total = resultSet.getDouble("Total");
            System.out.println("Most Expensive Invoice: $" + total);

            Assertions.assertTrue(total > 0, "Invoice total must be greater than 0.");
        }
    }
}