package com.lm.sty.spark.rdd.create

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/5/22 17:02
 * @author liming
 * @version 1.0
 */
object Test02_FromFile {
  def main(args: Array[String]): Unit = {
    // 1.创建sc的配置对象
    val conf: SparkConf = new SparkConf()
      .setAppName("sparkCore").setMaster("local[*]")

    // 2. 创建sc对象
    val sc = new SparkContext(conf)

    // 3. 编写任务代码
    // 不管文件中存的是什么数据  读取过来全部当做字符串处理
    val lineRDD: RDD[String] = sc.textFile("sty_spark/datas/wc/1.txt")

    lineRDD.collect().foreach(println)

    // 4.关闭sc
    sc.stop()
  }
}
