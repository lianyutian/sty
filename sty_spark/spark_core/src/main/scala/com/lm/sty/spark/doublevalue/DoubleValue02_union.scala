package com.lm.sty.spark.doublevalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/6/14 16:48
 * @author liming
 * @version 1.0
 */
object DoubleValue02_union {
  def main(args: Array[String]): Unit = {
    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd1: RDD[Int] = sc.makeRDD(1 to 4)

    //3.2 创建第二个RDD
    val rdd2: RDD[Int] = sc.makeRDD(4 to 8)

    //3.3 计算两个RDD的并集
    // 将原先的RDD的分区和数据都保持不变  简单的将多个分区合并在一起 放到一个RDD中
    // 由于不走shuffle  效率高  所有会使用到
    rdd1.union(rdd2).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
