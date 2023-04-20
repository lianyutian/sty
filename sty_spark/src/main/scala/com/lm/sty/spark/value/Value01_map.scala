package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/20 16:47
 * @author liming
 * @version 1.0
 */
object Value01_map {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(1 to 4, 2)

    val mapRdd = rdd.map(_ * 2)

    mapRdd.collect().foreach(println)

    sparkContext.stop()
  }

}
