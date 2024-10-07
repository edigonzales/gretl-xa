package ch.so.agi.gretl;

import java.sql.SQLException;

import javax.transaction.SystemException;

public class App {

    public static void main(String[] args) throws SystemException, SQLException {
        var singleTask = new SingleTask();
        singleTask.run();
        
        
        
        
        System.out.println("Hallo Welt.");
    }

}
