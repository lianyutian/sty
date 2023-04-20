package com.lm.sty.spark.partition

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 通过本地集合创建RDD的分区数规则：sparkContext.parallelize(集合,[numSlices: Int = defaultParallelism])
 *  1. 如果有设置numSlices参数，此时分区数 = 设置的numSlices值
 *  2. 如果没有设置numSlices参数，此时分区数 = defaultParallelism
 *   1）如果在sparkConf中有设置 spark.default.parallelism ,此时 defaultParallelism = 设置的 spark.default.parallelism 参数值
 *   2) 如果在sparkConf中没有设置 spark.default.parallelism 参数
 *      master=local,此时 defaultParallelism = 1
 *      master=local[N],此时 defaultParallelism = N
 *      master=local[*],此时 defaultParallelism = 机器逻辑cpu核数
 *      master=spark://...,此时 defaultParallelism = math.max(所有executor总核数,2)
 *
 * @since 2023/4/20 15:58
 * @author liming
 * @version 1.0
 */
object CreateRDD_FromList_Partition {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setAppName("sparkCore")
      .set("spark.default.parallelism", "10")
      .setMaster("local[4]")

    val sparkContext = new SparkContext(sparkConf)

    val rdd = sparkContext.parallelize(List(1, 2, 3, 4), 3)
    // 查看rdd的分区数
    println(rdd.getNumPartitions)
  }

}
