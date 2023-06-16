package com.lm.sty.spark.doublevalue

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/6/14 17:00
 * @author liming
 * @version 1.0
 */
object DoubleValue03_subtract {
  def main(args: Array[String]): Unit = {

    //1.创建SparkConf并设置App名称
    val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")

    //2.创建SparkContext，该对象是提交Spark App的入口
    val sc: SparkContext = new SparkContext(conf)

    //3具体业务逻辑
    //3.1 创建第一个RDD
    val rdd: RDD[Int] = sc.makeRDD(1 to 4)

    //3.2 创建第二个RDD
    val rdd1: RDD[Int] = sc.makeRDD(4 to 8)

    //3.3 计算第一个RDD与第二个RDD的差集并打印
    // 同样使用shuffle的原理  将两个RDD的数据写入到相同的位置 进行求差集
    // 需要走shuffle  效率低  不推荐使用
    rdd.subtract(rdd1).collect().foreach(println)

    //4.关闭连接
    sc.stop()
  }

}
