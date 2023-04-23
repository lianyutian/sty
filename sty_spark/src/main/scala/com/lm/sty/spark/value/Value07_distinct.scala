package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 15:18
 * @author liming
 * @version 1.0
 */
object Value07_distinct {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(List(1, 2, 1, 5, 2, 9, 6, 7))

    rdd.distinct().collect().foreach(println)

    // 对RDD采用多个Task去重，提高并发度
    rdd.distinct(2).collect().foreach(println)

    sparkContext.stop()
  }
}
