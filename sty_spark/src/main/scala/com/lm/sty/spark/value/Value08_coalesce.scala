package com.lm.sty.spark.value

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/4/23 15:22
 * @author liming
 * @version 1.0
 */
object Value08_coalesce {
  def main(args: Array[String]): Unit = {
    val sparkContext = new SparkContext(new SparkConf().setAppName("sparkCore").setMaster("local[*]"))

    val rdd = sparkContext.makeRDD(Array(1, 2, 3, 4), 4)

    val coalesceRDD = rdd.coalesce(2)

    val rdd2 = sparkContext.makeRDD(Array(1, 2, 3, 4, 5, 6), 3)

    val coalesceRDD2 = rdd.coalesce(2)

    val indexRDD = coalesceRDD2.mapPartitionsWithIndex((index, datas) => {
      datas.map((index, _))
    })

    indexRDD.collect().foreach(println)

    // 延迟一段时间，观察http://localhost:4040页面，查看Shuffle读写数据
    Thread.sleep(100000)

    sparkContext.stop()
  }
}
