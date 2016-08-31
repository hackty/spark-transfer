package info.puton.component.spark.transfer.util;

import info.puton.component.spark.transfer.model.SQLJob;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taoyang on 2016/8/29.
 */
public class JdbcUtil {

    public static String getFullSelect(String fullTableName){
        String databaseName = fullTableName.trim().toUpperCase().split("\\.")[1];
        String tableName = fullTableName.trim().toUpperCase().split("\\.")[2];

        String sql = "SELECT "
                + getColumnStringAs(fullTableName)
                + " FROM "
                + databaseName+"."+tableName;
        System.out.println(sql);
        return sql;
    }

    public static String getColumnString(String fullTableName){
        List<Map> metaList = getMetaList(fullTableName);
        String columnString = "";
        for (int i = 0; i<metaList.size()-1; i++) {
            Map metaMap = metaList.get(i);
            columnString+=metaMap.get("columnName")+",";
        }
        Map metaMap = metaList.get(metaList.size()-1);
        columnString+=metaMap.get("columnName")+"";
        return columnString;
    }

    public static String getColumnStringAs(String fullTableName){
        List<Map> metaList = getMetaList(fullTableName);
        String columnString = "";
        for (int i = 0; i<metaList.size()-1; i++) {
            Map metaMap = metaList.get(i);
            columnString+=metaMap.get("columnName") + " AS " + metaMap.get("columnName") +",";
        }
        Map metaMap = metaList.get(metaList.size()-1);
        columnString+=metaMap.get("columnName") + " AS " + metaMap.get("columnName");
        return columnString;
    }

    public static List<Map> getMetaList(String fullTableName){
        String databaseType = fullTableName.trim().toUpperCase().split("\\.")[0];
        String databaseName = fullTableName.trim().toUpperCase().split("\\.")[1];
        String tableName = fullTableName.trim().toUpperCase().split("\\.")[2];
        String fullDatabaseName = databaseType + "." + databaseName;
        try {
            SQLJob sqlJob = new SQLJob(fullDatabaseName);
            Class.forName(sqlJob.getJdbcDriver()).newInstance();
            Connection conn = DriverManager.getConnection(
                    sqlJob.getJdbcUrl()
                    ,sqlJob.getJdbcUsername()
                    ,sqlJob.getJdbcPassword());
            DatabaseMetaData dbMata  = conn.getMetaData();
            String columnName;
            String columnType;
            ResultSet colRet = dbMata.getColumns(null,databaseName, tableName,"%");
            List<Map> metaList = new ArrayList<>();
            while(colRet.next()) {
                Map metaMap = new HashMap();
                columnName = colRet.getString("COLUMN_NAME");
                columnType = colRet.getString("TYPE_NAME");
                int datasize = colRet.getInt("COLUMN_SIZE");
                int digits = colRet.getInt("DECIMAL_DIGITS");
                int nullable = colRet.getInt("NULLABLE");
                System.out.println(columnName+" "+columnType+" "+datasize+" "+digits+" "+ nullable);
                metaMap.put("columnName",columnName);
                metaMap.put("columnType",columnType);
                metaMap.put("datasize",datasize);
                metaMap.put("digits",digits);
                metaMap.put("nullable",nullable);
                metaList.add(metaMap);
            }
            return metaList;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

//        getMetaList("TDDATASOURCE.DB_ALPHA.TB_USER");
//        System.out.println(getColumnString("TDDATASOURCE.DB_ALPHA.TB_USER_DETAIL"));
//        System.out.println(getFullSelect("TDDATASOURCE.DB_ALPHA.TB_USER_DETAIL"));
        System.out.println(getFullSelect("TDDATASOURCE.DB_ALPHA.TB_WITHTITLE"));
//        System.out.println(getFullSelect("DB2DATASOURCE.DB_BETA.TB_ORDER"));

    }

}
