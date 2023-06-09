package com.lm.sty.spark.create

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/5/22 13:58
 * @author liming
 * @version 1.0
 */
object Test01_FromList {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("RDD")
    val sc = new SparkContext(conf)

    val rdd = sc.makeRDD(List(1, 2, 3, 4))
    rdd.collect().foreach(println)
    
    sc.stop()
  }
}
