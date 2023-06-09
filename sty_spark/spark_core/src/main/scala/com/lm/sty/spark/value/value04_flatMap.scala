package com.lm.sty.spark.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import java.util
import scala.collection.immutable

/**
 * @since 2023/6/1 17:21
 * @author liming
 * @version 1.0
 */
object value04_flatMap {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc = new SparkContext(conf)

    //3具体业务逻辑
    // 3.1 创建一个RDD
    val listRDD: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7)), 2)

    // 3.2 把所有子集合中数据取出放入到一个大的集合中
    listRDD.flatMap((item: immutable.Seq[Int]) => item).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
