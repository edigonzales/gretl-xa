package ch.so.agi.gretl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.transaction.SystemException;

public class Db2DbSampleTask {
    public void run() throws SystemException, SQLException {
        MyTransactionManager txManager = MyTransactionManager.getInstance();
        Connection editConn = txManager.addDataSource("jdbc:postgresql://localhost:54321/edit", "postgres", "secret");
        Connection pubConn = txManager.addDataSource("jdbc:postgresql://localhost:54322/pub", "postgres", "secret");

        String editQuery1 = "SELECT attr1 FROM foo";
        PreparedStatement editStmt = editConn.prepareStatement(editQuery1);
        ResultSet rs = editStmt.executeQuery();

        String writeQuery = "INSERT INTO bar (attr1) VALUES (?)";
        PreparedStatement pubStmt = pubConn.prepareStatement(writeQuery);

        while (rs.next()) {
            String data1 = rs.getString("attr1");
            System.out.println("attr1 from edit: "+data1);
            pubStmt.setString(1, data1+"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            pubStmt.executeUpdate();
        }

        
        
        editConn.close();
        pubConn.close();
        
        System.out.println("***************** END OF DB2DB");
    }
    
}
