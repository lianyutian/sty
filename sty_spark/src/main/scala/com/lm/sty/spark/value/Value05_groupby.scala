package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 15:08
 * @author liming
 * @version 1.0
 */
object Value05_groupby {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(1 to 4, 2)

    rdd.groupBy(_%2).collect().foreach(println)

    val rdd1 = sparkContext.makeRDD(List("hello", "hive", "hadoop", "spark", "scala"))

    rdd1.groupBy(str => str.substring(0, 1)).collect().foreach(println)

    sparkContext.stop()
  }
}
