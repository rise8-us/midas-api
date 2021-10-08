package mil.af.abms.midas.clients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.exception.AbstractRuntimeException;

@Slf4j
public class DBUtils {

    private DBUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection connect(String dbUrl, String dbUser, String dbPassword, String dbDriver) {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        throw new AbstractRuntimeException("unable to connect to the database");
    }

}
