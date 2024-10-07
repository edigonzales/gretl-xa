package ch.so.agi.gretl;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.transaction.SystemException;

public class SingleTask {
    
    public void run() throws SystemException, SQLException {
        // Step 1: Configure AtomikosDataSourceBean for "edit" and "pub" PostgreSQL databases
        AtomikosDataSourceBean editDataSource = configureAtomikosDataSource(
                "editDataSource", "jdbc:postgresql://localhost:54321/edit", "postgres", "secret");

        AtomikosDataSourceBean pubDataSource = configureAtomikosDataSource(
                "pubDataSource", "jdbc:postgresql://localhost:54322/pub", "postgres", "secret");
        
        // Step 2: Initialize Atomikos UserTransactionManager
        UserTransactionManager transactionManager = new UserTransactionManager();
        transactionManager.init(); // Initializes the Atomikos transaction manager

        Connection editConn = null;
        Connection pubConn = null;

        try {
            // Step 3: Begin the Atomikos transaction
            transactionManager.begin();

            // Step 4: Get connections from Atomikos DataSources
            editConn = editDataSource.getConnection();
            pubConn = pubDataSource.getConnection();

            // Disable Auto-commit for both connections
            editConn.setAutoCommit(false);
            pubConn.setAutoCommit(false);

            // Step 5: Perform read from "edit" database
//            String editQuery1 = "INSERT INTO foo (attr1) VALUES (?);";
//            PreparedStatement editStmt = editConn.prepareStatement(editQuery1);
//            editStmt.setString(1, "gaga");
//            editStmt.executeUpdate();

            // Records in Pub-DB löschen
            String deleteQuery = "DELETE FROM bar WHERE attr1='gaga'";
            PreparedStatement deleteStmt = pubConn.prepareStatement(deleteQuery);
            deleteStmt.execute();        
            
            String editQuery1 = "SELECT attr1 FROM foo";
            PreparedStatement editStmt = editConn.prepareStatement(editQuery1);
            ResultSet rs = editStmt.executeQuery();

            // Step 6: Perform write to "pub" database
            //String writeQuery = "INSERT INTO pub_table (column1, column2) VALUES (?, ?)";
            String writeQuery = "INSERT INTO bar (attr1) VALUES (?)";
            PreparedStatement pubStmt = pubConn.prepareStatement(writeQuery);
//            pubStmt.setString(1, "gagaXXXXXXXXXXXXXXXX");
//            pubStmt.executeUpdate();
            
            while (rs.next()) {
                String data1 = rs.getString("attr1");
                System.out.println("****"+data1);
//                String data2 = rs.getString("column2");
//
//                // Write data to "pub" database
                pubStmt.setString(1, data1+"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//                pubStmt.setString(2, data2);
                pubStmt.executeUpdate();
            }

            // Step 7: Commit the Atomikos transaction (both databases will commit)
            transactionManager.commit();

            System.out.println("Transaction committed successfully!");

        } catch (Exception e) {
            // Step 8: Rollback the Atomikos transaction in case of any error
            try {
                transactionManager.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // Step 9: Close connections
            if (editConn != null) editConn.close();
            if (pubConn != null) pubConn.close();
            
            // Shutdown the transaction manager
            transactionManager.close();
        }
    }
    
    
    
    // Helper method to configure AtomikosDataSourceBean for PostgreSQL
    private static AtomikosDataSourceBean configureAtomikosDataSource(String uniqueResourceName,
                                                                      String jdbcUrl, String user, String password) {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("url", jdbcUrl);

        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName(uniqueResourceName);
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource"); // PostgreSQL XA DataSource
        ds.setXaProperties(properties);
        ds.setMinPoolSize(1);
        ds.setMaxPoolSize(5);
        return ds;
    }
}
