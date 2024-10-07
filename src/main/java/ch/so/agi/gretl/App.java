package ch.so.agi.gretl;

import java.sql.SQLException;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

public class App {

    public static void main(String[] args) throws SQLException, SystemException {
//        var singleTask = new SingleTask();
//        singleTask.run();

        MyTransactionManager txManager = null;
        try {
            txManager = MyTransactionManager.getInstance();
            txManager.begin();
             
            var taskA = new SqlExecutorSampleTask();
            taskA.run();
            
            var taskB = new Db2DbSampleTask();
            taskB.run();
            
            txManager.commit();
            
        } catch (Exception e) {
            try {
                txManager.rollback();
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // Step 9: Close connections
//            if (editConn != null) editConn.close();
//            if (pubConn != null) pubConn.close();
            
//            txManager.closeConnections(); 
            
            // Shutdown the transaction manager
//            txManager.close();
            txManager.close();
        }
        
        
        
        
        System.out.println("Hallo Welt.");
    }

}
