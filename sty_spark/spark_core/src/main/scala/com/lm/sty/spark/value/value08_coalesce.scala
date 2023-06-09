package com.lm.sty.spark.value

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/6/5 15:56
 * @author liming
 * @version 1.0
 */
object value08_coalesce {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3.创建一个RDD
    //val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4), 4)

    //3.1 缩减分区
    //val coalesceRdd: RDD[Int] = rdd.coalesce(2)

    //4. 创建一个RDD
    val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4, 5, 6), 3)
    //4.1 缩减分区
    //val coalesceRDD: RDD[Int] = rdd.coalesce(2)
    //4.2 执行shuffle
    val coalesceRdd: RDD[Int] = rdd.coalesce(2, shuffle = true)

    //5 查看对应分区数据
    val indexRDD: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex(
      (index: Int, datas: Iterator[Int]) => {
        datas.map((index, _: Int))
      }
    )

    //6 打印数据
    indexRDD.collect().foreach(println)

    //8 延迟一段时间，观察http://localhost:4040页面，查看Shuffle读写数据
    Thread.sleep(100000)

    //7.关闭连接
    sc.stop()
  }
}
