package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 14:50
 * @author liming
 * @version 1.0
 */
object Value03_mapPartitionsWithIndex {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(1 to 4, 2)

    // 创建一个RDD，使每个元素跟所在分区号形成一个元组，组成一个新的RDD
    val indexRdd = rdd.mapPartitionsWithIndex((index, items) => {
      items.map((index, _))
    })

    indexRdd.collect().foreach(println)

    sparkContext.stop()
  }

}
