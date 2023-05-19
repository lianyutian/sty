package com.lm.sty.spark.wordcount

import org.apache.spark.api.java.JavaSparkContext.fromSparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @since 2023/5/19 15:40
 * @author liming
 * @version 1.0
 */
object WordCount {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("WordCount")
    val context: SparkContext = new SparkContext(conf)

    val lines: RDD[String] = context.textFile("sty_spark/datas/wc")

    val words: RDD[String] = lines.flatMap(_.split(" "))

    val wordGroup: RDD[(String, Iterable[String])] = words.groupBy(word => word)

    val wordCount: RDD[(String, Int)] = wordGroup.map {
      x => {
        x match {
          case (word, list) =>
            (word, list.size)
        }
      }
    }

    val tuples = wordCount.collect()

    tuples.foreach(println)

    context.close()
  }

}
