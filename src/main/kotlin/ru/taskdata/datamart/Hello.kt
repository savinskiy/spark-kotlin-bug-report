package ru.taskdata.datamart

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession
import org.elasticsearch.hadoop.cfg.ConfigurationOptions
import org.elasticsearch.spark.sql.api.java.JavaEsSparkSQL


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

