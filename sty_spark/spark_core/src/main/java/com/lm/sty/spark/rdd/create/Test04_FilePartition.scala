package com.lm.sty.spark.rdd.create

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/5/22 16:03
 * @author liming
 * @version 1.0
 */
object Test04_FilePartition {
  def main(args: Array[String]): Unit = {
    // 1.创建sc的配置对象
    val conf: SparkConf = new SparkConf()
      .setAppName("sparkCore").setMaster("local[*]")

    // 2. 创建sc对象
    val sc = new SparkContext(conf)

    // 3. 编写任务代码
    // 默认填写的最小分区数   2和环境的核数取小的值  一般为2
    // math.min(defaultParallelism, 2)
    val lineRDD: RDD[String] = sc.textFile("sty_spark/datas/wc/1.txt", 3)

    // 具体的分区个数需要经过公式计算
    // 首先获取文件的总长度  totalSize
    // 计算平均长度  goalSize = totalSize / numSplits
    // 获取块大小 128M
    // 计算切分大小  splitSize = Math.max(minSize, Math.min(goalSize, blockSize));
    // 最后使用splitSize  按照1.1倍原则切分整个文件   得到几个分区就是几个分区

    // 实际开发中   只需要看文件总大小 / 填写的分区数  和块大小比较  谁小拿谁进行切分

    lineRDD.saveAsTextFile("output")

    // 数据会分配到哪个分区
    // 如果切分的位置位于一行的中间  会在当前分区读完一整行数据

    // 0 -> 1,2  1 -> 3  2 -> 4  3 -> 空

    // 4.关闭sc
    sc.stop()
  }
}
