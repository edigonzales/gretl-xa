package ch.so.agi.gretl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

public class MyTransactionManager {
    private static MyTransactionManager INSTANCE;

    public UserTransactionManager transactionManager;
    
    private Map<String, AtomikosDataSourceBean> dataSources = new HashMap<>();
    private Map<String, Connection> connections = new HashMap<>();

    private MyTransactionManager() throws SystemException {
        transactionManager = new UserTransactionManager();
        transactionManager.init();
    }
    
    public static MyTransactionManager getInstance() throws SystemException {
        if(INSTANCE == null) {
            INSTANCE = new MyTransactionManager();
        }
        return INSTANCE;
    }

    public void begin() throws NotSupportedException, SystemException {
        this.transactionManager.begin();
    }
    
    public void commit() throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        this.transactionManager.commit();
    }
    
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        this.transactionManager.rollback();
    }
    
    public void close() {
        System.out.println("**** shutdown");
        transactionManager.close();
    }
    
    public void closeConnections() throws SQLException {
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            System.out.println("connection: " + entry.getKey());
            Connection conn = entry.getValue();
            if (conn != null) {
                conn.close();
            }
            
        }
    }
    
    public Connection addDataSource(String jdbcUrl, String user, String password) throws SQLException {
        //String resourceName = String.valueOf(Objects.hash(jdbcUrl, user, password));  
        String resourceName = jdbcUrl;  
        if (dataSources.containsKey(resourceName)) {
            AtomikosDataSourceBean ds = dataSources.get(resourceName);
            Connection conn = ds.getConnection(); // connections.get(resourceName);
            conn.setAutoCommit(false);
            return conn;
        }
        
        AtomikosDataSourceBean ds = configureAtomikosDataSource(resourceName, jdbcUrl, user, password);
        dataSources.put(resourceName, ds);
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        connections.put(resourceName, conn);
        return conn;
    }
    
    private static AtomikosDataSourceBean configureAtomikosDataSource(String uniqueResourceName, String jdbcUrl,
            String user, String password) {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("url", jdbcUrl);

        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName(uniqueResourceName);
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource"); 
        ds.setXaProperties(properties);
        ds.setMinPoolSize(1);
        ds.setMaxPoolSize(1);
        return ds;
    }

}
