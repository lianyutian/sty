package com.lm.sty.spark.createrdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/20 14:30
 * @author liming
 * @version 1.0
 */
object CreateRDD_FromList {
  def main(args: Array[String]): Unit = {
    // 1. 创建 SparkConf 配置对象
    val conf = new SparkConf().setAppName("sparkCore").setMaster("local[*]")

    // 2. 创建 SparkContext 对象
    val sparkContext = new SparkContext(conf)

    // 3. 从集合创建RDD
    val list = List(1, 2, 3, 4)
    val intRDD = sparkContext.parallelize(list)
    intRDD.collect().foreach(println)

    // makeRDD() 底层调用 parallelize()
    val intRDD1 = sparkContext.makeRDD(list)
    intRDD1.collect().foreach(println)

    // 4. 关闭 sparkContext
    sparkContext.stop()
  }
}
