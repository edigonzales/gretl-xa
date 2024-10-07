package ch.so.agi.gretl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.transaction.SystemException;

public class SqlExecutorSampleTask {
    
    public void run() throws SystemException, SQLException {
        MyTransactionManager txManager = MyTransactionManager.getInstance();
        Connection conn = txManager.addDataSource("jdbc:postgresql://localhost:54322/pub", "postgres", "secret");
        
//        String editQuery1 = "SELECT attr1 FROM foo";
//        PreparedStatement editStmt = conn.prepareStatement(editQuery1);
//        ResultSet rs = editStmt.executeQuery();
//
//        while (rs.next()) {
//            String data1 = rs.getString("attr1");
//            System.out.println("****: "+data1);
//
//        }
        
        String deleteQuery = "DELETE FROM bar WHERE attr1='gaga'";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
        deleteStmt.execute();        


        conn.close();
        
        System.out.println("**************** END OF SQLEXECUTOR");
    }
}
