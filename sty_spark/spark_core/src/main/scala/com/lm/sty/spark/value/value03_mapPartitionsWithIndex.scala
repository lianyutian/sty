package com.lm.sty.spark.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/6/1 14:38
 * @author liming
 * @version 1.0
 */
object value03_mapPartitionsWithIndex {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc = new SparkContext(conf)

    //3具体业务逻辑
    // 3.1 创建一个RDD
    val rdd: RDD[Int] = sc.makeRDD(1 to 4, 2)

    // 3.2 创建一个RDD，使每个元素跟所在分区号形成一个元组，组成一个新的RDD
    val indexRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((index: Int, items: Iterator[Int]) => {
      items.map((index, (_: Int)))
    })

    // 3.3 打印修改后的RDD中数据
    indexRdd.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
