package com.lm.sty.spark.cases

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 需求：统计出每一个省份广告被点击次数的Top3
 * 时间戳          省份  城市  用户  广告
 * 1516609239711  辽宁  沈阳  张三  AAA
 *
 * @since 2023/6/21 14:59
 * @author liming
 * @version 1.0
 */
object Test01_DemoTop3 {
  def main(args: Array[String]): Unit = {
    // 1. 创建配置对象
    val conf: SparkConf = new SparkConf().setAppName("coreTest").setMaster("local[*]")

    // 2. 创建sc
    val sc = new SparkContext(conf)

    // 3. 编写代码  执行操作
    val lineRDD: RDD[String] = sc.textFile("sty_spark/datas/agent/agent.log")

    // 步骤一: 过滤出需要的数据
    val tupleRDD: RDD[(String, String)] = lineRDD.map((line: String) => {
      val data: Array[String] = line.split(" ")
      (data(1), data(4))
    })
    // 将一行的数据转换为(省份,广告)
    //    tupleRDD.collect().foreach(println)

    // 步骤二: 对省份加广告进行wordCount 统计
    val provinceCountRDD: RDD[((String, String), Int)] = tupleRDD.map(((_: (String, String)), 1)).reduceByKey((_: Int) + (_: Int))


    // 一步进行过滤数据加wordCount
    val tupleRDD1: RDD[((String, String), Int)] = lineRDD.map((line: String) => {
      val data: Array[String] = line.split(" ")
      ((data(1), data(4)), 1)
    })

    val provinceCountRDD1: RDD[((String, String), Int)] = tupleRDD1.reduceByKey((_: Int) + (_: Int))

    // 统计单个省份单条广告点击的次数  ((省份,广告id),count次数)
    //    provinceCountRDD.collect().foreach(println)

    // 步骤三:分省份进行聚合
    // ((省份,广告id),count次数)
    // 使用groupBY的方法 数据在后面会有省份的冗余
    //    val provinceRDD: RDD[(String, Iterable[((String, String), Int)])] = provinceCountRDD1.groupBy(tuple => tuple._1._1)
    //    provinceRDD.collect().foreach(println)

    // 推荐使用groupByKey   => 前面已经聚合过了
    // ((省份,广告id),count次数) => (省份,(广告id,count次数))

    // 使用匿名函数的写法
    val value: RDD[(String, (String, Int))] = provinceCountRDD1.map((tuple: ((String, String), Int)) =>
      (tuple._1._1, (tuple._1._2, tuple._2)))

    // 偏函数的写法
    provinceCountRDD1.map({
      case ((province, id), count) => (province, (id, count))
    })

    val provinceRDD1: RDD[(String, Iterable[(String, Int)])] = value.groupByKey()

    // (省份,(广告id,count次数)) => (省份,List((广告1,次数),(广告2,次数),(广告3,次数)))
    //    provinceRDD1.collect().foreach(println)

    //步骤四: 对单个二元组中的value值排序取top3
    // 相当于只需要对value进行处理
    val result: RDD[(String, List[(String, Int)])] = provinceRDD1.mapValues((it: Iterable[(String, Int)]) => {
      // 将list中的广告加次数排序取top3即可
      val list1: List[(String, Int)] = it.toList

      // 此处调用的sort是集合常用函数
      // 对rdd调用的是算子  对list调用的是集合常用函数
      list1.sortWith((_: (String, Int))._2 > (_: (String, Int))._2).take(3)
    })

    result.collect().foreach(println)

    Thread.sleep(60000)

    // 4. 关闭sc
    sc.stop()
  }
}
