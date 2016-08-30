package info.puton.component.spark.transfer;

import info.puton.component.spark.transfer.model.SQLJob;
import info.puton.component.spark.transfer.util.DataFrameUtil;
import info.puton.component.spark.transfer.util.JdbcUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by taoyang on 2016/8/25.
 */
public class Porter {

    public void toHive(String initialFullTableName, String targetFullTableName) {

        String initialDatabaseType = initialFullTableName.trim().toUpperCase().split("\\.")[0];
        String initialDatabaseName = initialFullTableName.trim().toUpperCase().split("\\.")[1];
        String initialTableName = initialFullTableName.trim().toUpperCase().split("\\.")[2];
        String initialullDatabaseName = initialDatabaseType + "." + initialDatabaseName;

        SQLJob sqlJob = null;
        try {
            sqlJob = new SQLJob(initialullDatabaseName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String sparkMode = null;
        SparkConf sparkConf = null;

        InputStream in = this.getClass().getResourceAsStream("/spark-transfer/spark-transfer.properties");
        Properties p = new Properties();
        try {
            p.load(in);
            sparkMode = p.getProperty("spark.mode");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if(sparkMode.equals("local")){
            sparkConf = new SparkConf().setAppName("spark-transfer-"+initialTableName).setMaster("local");
        }else if(sparkMode.equals("vmcluster")){
            //VM NEED
            sparkConf = new SparkConf().setAppName("spark-transfer-"+initialTableName);
        }else if(sparkMode.equals("testcluster")){
            //TEST NEED
            //租户认证及授权
            String principal ="xytd_T@HADOOP.COM";
            String keytab = "/opt/keytab/user.keytab";
            String krbfilepath = "/opt/keytab/krb5.conf";
            Configuration conf_sec = new Configuration();
            conf_sec.set(principal, "spark/hadoop.hadoop.com@HADOOP.COM");
            //keytab file
            //conf_sec.set(keytab, "/opt/huawei/Bigdata/etc/1_11_SparkResource/spark.keytab");
            conf_sec.set(keytab, "/opt/huawei/Bigdata/etc/5_70_SparkResource/ spark.keytab");
            System.setProperty("java.security.krb5.conf", krbfilepath);
            UserGroupInformation.setConfiguration(conf_sec);
            try {
                UserGroupInformation.loginUserFromKeytab(principal, keytab);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            sparkConf = new SparkConf().setAppName("spark-transfer-"+initialTableName);
        }else if(sparkMode.equals("prodcluster")){
            //PROD NEED
            //租户认证及授权
            String principal ="xytd_T@HADOOP_B.COM";
            String keytab = "/home/xytd/keytab/xytd.keytab";
            String krbfilepath = "/home/xytd/keytab/krb5.conf";
            Configuration conf_sec = new Configuration();
            conf_sec.set(principal, "spark/hadoop.hadoop.com@HADOOP_B.COM");
            //keytab file
            //conf_sec.set(keytab, "/opt/huawei/Bigdata/etc/1_11_SparkResource/spark.keytab");
            conf_sec.set(keytab, "/opt/huawei/Bigdata/etc/5_74_SparkResource/spark.keytab");
            System.setProperty("java.security.krb5.conf", krbfilepath);
            UserGroupInformation.setConfiguration(conf_sec);
            try {
                UserGroupInformation.loginUserFromKeytab(principal, keytab);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            sparkConf = new SparkConf().setAppName("spark-transfer-"+initialTableName);
        }

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        //SQLContext sqlContext = new SQLContext(sc);
        HiveContext hiveContext = new HiveContext(sc.sc());

        String sql = "( "+JdbcUtil.getFullSelect(initialFullTableName)+" ) AS "+ initialTableName;

        Map<String,String> options = new HashMap<String,String>();
        options.put("driver",sqlJob.getJdbcDriver());
        options.put("url",sqlJob.getJdbcUrl());
        options.put("dbtable",sql);
//        options.put("dbtable","(select USER_ID from SHDW.DWA_BEH_CALL_20160426 FETCH FIRST 200 ROWS ONLY ) as DWA_BEH_CALL_1");
//        options.put("dbtable", TestInjection.getValueByDefault());
        options.put("user",sqlJob.getJdbcUsername());
        options.put("password", sqlJob.getJdbcPassword());

        long startTime1=System.currentTimeMillis();
        //spark1.3
        DataFrame df = hiveContext.load("jdbc",options);
        df.printSchema();
        long endTime1 = System.currentTimeMillis();
        System.out.println("Fetch Took "+(endTime1-startTime1)+" ms");
        df.show();

        String ddl = DataFrameUtil.generateHiveDDL(df, targetFullTableName);

        long startTime2=System.currentTimeMillis();
//        hiveContext.sql(ddl);
        df.saveAsTable(targetFullTableName, SaveMode.Append);
        long endTime2 = System.currentTimeMillis();
        System.out.println("Save Took "+(endTime2-startTime2)+" ms");
        sc.close();

    }

    public static void main(String[] args) {
        Porter porter = new Porter();
        String initialFullTableName = null;
        String targetFullTableName = null;
        if(args.length==1) {
            initialFullTableName = String.valueOf(args[0]);
            targetFullTableName = initialFullTableName.trim().toUpperCase().split("\\.")[2];
        } else if (args.length==2) {
            initialFullTableName = String.valueOf(args[0]);
            targetFullTableName = String.valueOf(args[1]);
        } else {
            return;
        }
        porter.toHive(initialFullTableName, targetFullTableName);
    }

}
