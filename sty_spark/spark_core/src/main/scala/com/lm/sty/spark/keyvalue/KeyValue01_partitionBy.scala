package com.lm.sty.spark.keyvalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * @since 2023/6/14 17:16
 * @author liming
 * @version 1.0
 */
object KeyValue01_partitionBy {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "aaa"), (2, "bbb"), (3, "ccc")), 3)

    //3.2 对RDD重新分区
    val rdd2: RDD[(Int, String)] = rdd.partitionBy(new HashPartitioner(2))

    //3.3 打印查看对应分区数据  (0,(2,bbb))  (1,(1,aaa))  (1,(3,ccc))
    val indexRdd: RDD[(Int, (Int, String))] = rdd2.mapPartitionsWithIndex(
      (index: Int, datas: Iterator[(Int, String)]) => datas.map((index, (_: (Int, String))))
    )
    indexRdd.collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }
}
