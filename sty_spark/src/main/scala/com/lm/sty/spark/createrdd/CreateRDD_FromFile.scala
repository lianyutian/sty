package com.lm.sty.spark.createrdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/20 14:38
 * @author liming
 * @version 1.0
 */
object CreateRDD_FromFile {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("sparkCore").setMaster("local[*]")

    val sparkContext = new SparkContext(sparkConf)

    // 从文件中创建RDD
    val lineRDD = sparkContext.textFile("sty_spark/input/1.txt")
    lineRDD.collect().foreach(println)

    sparkContext.stop()
  }
}
