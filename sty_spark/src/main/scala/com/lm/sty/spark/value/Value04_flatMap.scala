package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 14:56
 * @author liming
 * @version 1.0
 */
object Value04_flatMap {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val listRDD = sparkContext.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7)), 2)

    listRDD.flatMap(list => list).collect().foreach(println)

    sparkContext.stop()
  }
}
