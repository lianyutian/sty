package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 15:14
 * @author liming
 * @version 1.0
 */
object Value06_filter {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(Array(1, 2, 3, 4), 2)

    val filterRDD = rdd.filter(_ % 2 == 0)

    filterRDD.collect().foreach(println)

    sparkContext.stop()
  }
}
