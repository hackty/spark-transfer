# spark-transfer
transfer anydata anywhere

## support
Spark 1.3+<br>
Teradata<br>
DB2<br>
Mysql<br>
...

## usage
an example of transferring data from teradata to hive

### step1:
configure the datasource(datasource.properties):<br>
>
TDDATASOURCE.DB_ALPHA.jdbc.driver=com.teradata.jdbc.TeraDriver<br>
TDDATASOURCE.DB_ALPHA.jdbc.url=jdbc:teradata://td-datasource/CLIENT_CHARSET=GBK,TMODE=TERA,CHARSET=ASCII,database=db_alpha,LOB_Support=ON<br>
TDDATASOURCE.DB_ALPHA.jdbc.username=dbc<br>
TDDATASOURCE.DB_ALPHA.jdbc.password=dbc<br>

### step2:
run the script:
>
./tohive.sh TDDATASOURCE.DB_ALPHA.TB_USER TB_USER
