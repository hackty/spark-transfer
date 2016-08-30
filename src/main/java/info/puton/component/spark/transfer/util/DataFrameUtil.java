package info.puton.component.spark.transfer.util;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructField;

public class DataFrameUtil {

	public static String generateHiveDDL(DataFrame df, String tableName){
		scala.collection.Iterator<StructField> st = df.schema().iterator();
		String createSql = "CREATE TABLE " + tableName + "(";
		while(st.hasNext()){
			StructField sf = st.next();
			String type = sf.dataType().toString();
			String name = sf.name().toString();
			if(type.startsWith("StringType")){
				type = "STRING";
			}else if(type.startsWith("IntegerType")){
				type = "INT";
			}else if(type.startsWith("DecimalType")){
				type = "DECIMAL";
			}else if(type.startsWith("FloatType")){
				type = "FLOAT";
			}else if(type.startsWith("DoubleType")){
				type = "DOUBLE";
			}else if(type.startsWith("DateType")){
				type = "DATE";
			}else if(type.startsWith("LongType")){
				type = "BIGINT";
			}else{
				type = "STRING";
			}
			createSql = createSql + name + " " + type + ",";
		}
		createSql = createSql.substring(0,createSql.length() - 1);
		createSql += ")";
		return createSql;
	}

	public static String generateTeradataDDL(DataFrame df,String tableName){
		scala.collection.Iterator<StructField> st = df.schema().iterator();
		String createSql = "CREATE TABLE " + tableName + "(";
		while(st.hasNext()){
			StructField sf = st.next();
			String type = sf.dataType().toString();
			String name = sf.name().toString();
			if(type.startsWith("StringType")){
				type = "VARCHAR(300)";
			}else if(type.startsWith("IntegerType")){
				type = "INTEGER";
			}else if(type.startsWith("DecimalType")){
				type = "DECIMAL";
			}else if(type.startsWith("FloatType")){
				type = "FLOAT";
			}else if(type.startsWith("DoubleType")){
				type = "DOUBLE PRECISION";
			}else if(type.startsWith("DateType")){
				type = "DATE";
			}else if(type.startsWith("LongType")){
				type = "BIGINT";
			}else{
				type = "VARCHAR(300)";
			}
			createSql = createSql + name + " " + type + ",";
		}
		createSql = createSql.substring(0,createSql.length() - 1);
		createSql += ")";
		return createSql;
	}

	public static void main(String[] args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setAppName("SparkSQL").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		HiveContext hiveContext = new HiveContext(sc.sc());
//        DataFrame df = sqlContext.read().json("archetype-bigdata/src/main/resources/people.json");
		DataFrame df = hiveContext.jsonFile("src/main/resources/people.json");
		df.show();
		df.printSchema();
//		System.out.println(generateTeradataDDL(df,"tb_td"));
		System.out.println(generateHiveDDL(df,"tb_hive"));

	}

}
