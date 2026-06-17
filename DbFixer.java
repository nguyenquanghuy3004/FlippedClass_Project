import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbFixer {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=FlippedClass;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            // 1. Alter table
            System.out.println("Altering tables...");
            try { stmt.execute("ALTER TABLE users ALTER COLUMN full_name NVARCHAR(255)"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE student_profiles ALTER COLUMN bio NVARCHAR(1000)"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE student_profiles ALTER COLUMN phone_number NVARCHAR(20)"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE student_profiles ALTER COLUMN class_name NVARCHAR(255)"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE student_profiles ALTER COLUMN major NVARCHAR(255)"); } catch (Exception e) {}

            // 2. Select data
            System.out.println("Fetching data...");
            ResultSet rs = stmt.executeQuery("SELECT id, full_name FROM users");
            while (rs.next()) {
                System.out.println("User: ID=" + rs.getInt("id") + ", Name=" + rs.getString("full_name"));
            }
            rs.close();

            ResultSet rs2 = stmt.executeQuery("SELECT id, user_id, phone_number, bio FROM student_profiles");
            while (rs2.next()) {
                System.out.println("Profile: ID=" + rs2.getInt("id") + ", UserID=" + rs2.getInt("user_id") + ", Phone=" + rs2.getString("phone_number") + ", Bio=" + rs2.getString("bio"));
            }
            rs2.close();
            System.out.println("DONE!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
