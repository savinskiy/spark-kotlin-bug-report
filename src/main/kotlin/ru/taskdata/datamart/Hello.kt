package ru.taskdata.datamart

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.api.java.UDF1
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL
import java.security.Timestamp

private val latestCost = UDF1<Array<Row>, Long> { costStructure ->
    if (costStructure != null) {
        costStructure
            .maxBy {
                val applicationDate = it.getAs<Timestamp>("application_date")
                val timestamp =
                    if (applicationDate != null) applicationDate else it.getAs<Timestamp>("approvement_date")
                timestamp.timestamp
            }?.getAs<Long>("value")
    } else {
        0
    }
}

fun main() {

    val conf = SparkConf()
    conf.setMaster("local")
    conf.setAppName("Effectiveness report")
    conf.set("es.nodes", "http://remote-server.ru")
    conf.set(ConfigurationOptions.ES_NODES_WAN_ONLY, "true")
    conf.set("es.nodes.resolve.hostname", "false")
    conf.set("es.port", "9200")
    conf.set("es.http.retries", "0")

    val sc = JavaSparkContext(conf)

    val session = SparkSession(sc.sc())
    //  session.udf().register("latestCost", latestCost, DataTypes.LongType)
    val records = JavaEsSparkSQL.esDF(session, "index")
    records.createOrReplaceTempView("records")
    records.sqlContext().sql("select count(*) from records")
        .show()

    println("asd")
}

