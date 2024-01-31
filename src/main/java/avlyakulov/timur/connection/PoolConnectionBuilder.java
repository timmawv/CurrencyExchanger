package avlyakulov.timur.connection;

import lombok.extern.slf4j.Slf4j;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class PoolConnectionBuilder implements ConnectionBuilder {

    private DataSource dataSource;


    public PoolConnectionBuilder() {
        try {
            Context ctx = new InitialContext();//работает с зарегистрированными ресурсами
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/currencyExchanger");
        } catch (NamingException e) {
            log.error("Error with PoolConnectionBuilder");
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
