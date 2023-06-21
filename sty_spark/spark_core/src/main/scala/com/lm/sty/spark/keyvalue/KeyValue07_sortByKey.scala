package com.lm.sty.spark.keyvalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/6/21 10:29
 * @author liming
 * @version 1.0
 */
object KeyValue07_sortByKey {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((3, "aa"), (6, "cc"), (2, "bb"), (1, "dd")))

    //3.2 按照key的正序（默认顺序）
    rdd.sortByKey(true).collect().foreach(println)

    //3.3 按照key的倒序
    rdd.sortByKey(false).collect().foreach(println)
    // 只会按照key来排序  最终的结果是key有序  value不会排序
    // spark的排序是全局有序  不会进行hash shuffle处理
    // 使用range分区器
    // new RangePartitioner(numPartitions, self, ascending)

    //4.关闭连接
    sc.stop()
  }
}
