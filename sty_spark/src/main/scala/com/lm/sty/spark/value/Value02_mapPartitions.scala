package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/20 16:51
 * @author liming
 * @version 1.0
 */
object Value02_mapPartitions {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd1 = sparkContext.makeRDD(1 to 4, 2)
    val rdd2 = rdd1.mapPartitions(x => x.map(_ * 2))

    rdd2.collect().foreach(println)
  }

}
