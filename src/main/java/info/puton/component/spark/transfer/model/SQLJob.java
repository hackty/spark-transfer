package info.puton.component.spark.transfer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by taoyang on 3/30/16.
 */
public class SQLJob {

    private String databaseType;

    private String database;

    private String table;

    private String jdbcDriver;

    private String jdbcUrl;

    private String jdbcUsername;

    private String jdbcPassword;

    private String sql;

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType.toUpperCase();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql.toUpperCase();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database.toUpperCase();
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table.toUpperCase();
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver.toUpperCase();
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl.toUpperCase();
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername.toUpperCase();
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword.toUpperCase();
    }

    public SQLJob() {
    }

    public SQLJob(String database) throws IOException {
        this.database = database.toUpperCase();
        this.databaseType = database.trim().toUpperCase().split("\\.")[0];


        String datasourceConfigPath = null;

        InputStream in0 = this.getClass().getResourceAsStream("/spark-transfer/spark-transfer.properties");
        Properties p0 = new Properties();
        p0.load(in0);
        datasourceConfigPath = p0.getProperty("datasource.config.path");
//        System.out.println(datasourceConfigPath);
        in0.close();

        InputStream in = new FileInputStream(new File(datasourceConfigPath));
        Properties p = new Properties();
        p.load(in);

        this.jdbcDriver=p.getProperty(database.toUpperCase()+".jdbc.driver");
        this.jdbcUrl=p.getProperty(database.toUpperCase()+".jdbc.url");
        this.jdbcUsername=p.getProperty(database.toUpperCase()+".jdbc.username");
        this.jdbcPassword=p.getProperty(database.toUpperCase()+".jdbc.password");

        in.close();

    }

    @Override
    public String toString() {
        return "SQLJob{" +
                "databaseType='" + databaseType + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", jdbcDriver='" + jdbcDriver + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", jdbcUsername='" + jdbcUsername + '\'' +
                ", jdbcPassword='" + jdbcPassword + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }
}
