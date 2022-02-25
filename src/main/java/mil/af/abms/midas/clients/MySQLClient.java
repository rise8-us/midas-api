package mil.af.abms.midas.clients;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.exception.AbstractRuntimeException;

@Component
@Slf4j
public class MySQLClient {

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${custom.dbName}")
    private String dbName;

    public Set<String> getTableNames() {
        var query = String.format("SELECT table_name FROM information_schema.tables WHERE table_schema = \"%s\";", dbName);
        var tableNames = new HashSet<String>();

        try (var connection = DBUtils.connect(dbUrl, dbUser, dbPassword, dbDriver);
             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             var results =  statement.executeQuery(query);
        ) {
            while (results.next()) {
                tableNames.add(results.getString("table_name"));
            }
            return tableNames;
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return Set.of();
    }

    public String getLatestFlywayVersion() {
        var query = "SELECT version FROM flyway_schema_history ORDER BY version DESC LIMIT 1;";

        try (var connection = DBUtils.connect(dbUrl, dbUser, dbPassword, dbDriver);
             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             var results =  statement.executeQuery(query);
        ) {
            results.next();
            return results.getString("version");
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return "";
    }

    public void restore(String mysqlDump) {
        try (var connection = DBUtils.connect(dbUrl, dbUser, dbPassword, dbDriver);) {
            String sqlDropAllTablesUpdate = dropAllTables(mysqlDump);
            var sqlAsResource = new ByteArrayResource(sqlDropAllTablesUpdate.getBytes(StandardCharsets.UTF_8));
            ScriptUtils.executeSqlScript(connection, sqlAsResource);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new AbstractRuntimeException("Unable to restore DB");
        }
    }

    public String exportToSql() {

        StringBuilder sql = new StringBuilder();
        sql.append("--");
        sql.append("\n-- Flyway Version: ").append(getLatestFlywayVersion());
        sql.append("\n-- Date: ").append(new SimpleDateFormat("d-M-y H:m:s").format(new Date()));
        sql.append("\n-- Generated midas-api-mysqlclient");
        sql.append("\n--\n");

        sql.append("SET NAMES utf8;\n")
                .append("SET FOREIGN_KEY_CHECKS=0;\n")
                .append("SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO';\n")
                .append("USE `").append(dbName).append("`;\n")
                .append("SET NAMES utf8mb4;\n");

        Set<String> tableNames = getTableNames();
        for (String name: tableNames) {
            sql.append(getTableInsertStatement(name));
            sql.append(getDataInsertStatement(name));
        }

        return sql.toString();
    }

    private String getTableInsertStatement(String table) {
        table = Optional.ofNullable(table).orElse("");

        StringBuilder sql = new StringBuilder();
        var query = String.format("SHOW CREATE TABLE `%s`;", table);

        try (var connection = DBUtils.connect(dbUrl, dbUser, dbPassword, dbDriver);
             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             var results =  statement.executeQuery(query);
        ) {
            if (!table.isEmpty()) {
                while (results.next()) {
                    String tableName = results.getString(1);
                    String createTable = results.getString(2);
                    sql.append("\n\n").append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");
                    sql.append(createTable).append(";\n");
                }
            }
            return sql.toString();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new AbstractRuntimeException(String.format("Unable to get Table insert statement for %s", table));
        }
    }

    private String getDataInsertStatement(String table) {
        var sql = new StringBuilder();
        var query = String.format("SELECT * FROM `%s`;", table);
        try (var connection = DBUtils.connect(dbUrl, dbUser, dbPassword, dbDriver);
             var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             var results =  statement.executeQuery(query);
        ) {
            if (!results.last()) {
                return sql.toString();
            }

            var metaData = results.getMetaData();
            sql.append(buildInsertIntoColumns(metaData));
            sql.append(buildValues(results, metaData));
            return sql.toString();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new AbstractRuntimeException("unable to get table data");
        }
    }

    private String buildInsertIntoColumns(ResultSetMetaData metaData) throws SQLException {
        var sql = new StringBuilder();
        sql.append("\n\nINSERT INTO `").append(metaData.getTableName(1)).append("`(");
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            sql.append("`")
                    .append(metaData.getColumnName(i))
                    .append("`, ");
        }

        sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1).append(")\n");
        return sql.toString();
    }

    private String buildValues(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException {
        resultSet.beforeFirst();
        var sql = new StringBuilder();
        sql.append("VALUES\n");
        var columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            sql.append("(");
            for (int i = 1; i <= columnCount; i++) {

                int columnType = metaData.getColumnType(i);

                if (Objects.isNull(resultSet.getObject(i))) {
                    sql.append("").append(resultSet.getObject(i)).append(", ");
                } else if (columnType == Types.INTEGER || columnType == Types.TINYINT || columnType == Types.BIT) {
                    sql.append(resultSet.getInt(i)).append(", ");
                } else if (columnType == Types.BIGINT) {
                    sql.append(resultSet.getLong(i)).append(", ");
                } else {
                    String val = resultSet.getString(i);
                    val = val.replace("'", "\\'");
                    sql.append("'").append(val).append("', ");
                }
            }
            sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1);
            sql.append("),\n");
        }

        sql.deleteCharAt(sql.length() - 1).setLength(sql.length() - 1);
        sql.append(";");

        return sql.toString();
    }

    private String dropAllTables(String backupString) {
        Set<String> tableNames = getTableNames();
        StringBuilder sqlDropAllContent = new StringBuilder();

        sqlDropAllContent.append("SET FOREIGN_KEY_CHECKS=0;\n");

        for (String name: tableNames) {
            sqlDropAllContent.append("DROP TABLE IF EXISTS `").append(name).append("`;\n");
        }

        return sqlDropAllContent.append(backupString).toString();
    }
}
