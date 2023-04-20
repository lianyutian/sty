package com.lm.sty.spark.partition

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 通过读取文件创建RDD分区数规则：sparkContext.textFile(path,[minPartitions: Int = defaultMinPartitions])
 *  1. 读取文件创建RDD分区数 >= minPartitions
 *  2. 读取文件创建的RDD分区数最终由文件切片决定
 *
 * 通过其他RDD衍生出的RDD的分区数：依赖的RDD列表里的第一个RDD的分区数
 * @since 2023/4/20 16:16
 * @author liming
 * @version 1.0
 */
object CreateRDD_FromFile_Partition {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("sparkCore").setMaster("local[*]")

    val sparkContext = new SparkContext(sparkConf)

    // 从文件中创建RDD
    val lineRDD = sparkContext.textFile("sty_spark/input/1.txt")
    println(lineRDD.getNumPartitions)

    val rdd2 = lineRDD.flatMap(_.split(" "))
    println(rdd2.getNumPartitions)
  }

}
