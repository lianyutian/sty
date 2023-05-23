# RDD

## 什么是RDD

RDD（Resilient Distributed Dataset）叫做弹性分布式数据集，是Spark中最基本的数据抽象。

代码中是一个抽象类，它代表一个弹性的、不可变、可分区、里面的元素可并行计算的集合。

## RDD五大特性

- 一组分区，即是数据集的基本组成单位，标记数据是哪个分区的

  ```scala
  protected def getPartitions: Array[Partition]
  ```

- 一个计算每个分区的函数

  ```scala
  def compute(split: Partition, context: TaskContext): Iterator[T]
  ```

- RDD之间的依赖关系

  ```scala
  protected def getDependencies: Seq[Dependency[_]] = deps
  ```

- 一个Partitioner，即RDD的分片函数。控制分区的数据流向

  ```scala
  @transient val partitioner: Option[Partitioner] = None
  ```

- 一个列表，存储存取每个Partitioner的优先位置。如果节点和分区个数不对应，优先把分区在哪个节点上。移动数据不如移动计算，除非资源不够。

  ```scala
  protected def getPreferredLocations(split: Partition): Seq[String] = Nil
  ```



## RDD编程

### RDD的创建

在Spark中创建RDD的创建方式可以分为三种：

- 从集合中创建RDD

  ```scala
  import org.apache.spark.rdd.RDD
  import org.apache.spark.{SparkConf, SparkContext}
  
  
  object Test01_FromList {
    def main(args: Array[String]): Unit = {
      // 1.创建sc的配置对象
      val conf: SparkConf = new SparkConf()
        .setAppName("sparkCore").setMaster("local[*]")
  
      // 2. 创建sc对象
      val sc = new SparkContext(conf)
  
      // 3. 编写任务代码
      val list = List(1, 2, 3, 4)
  
      // 从集合创建rdd
      val intRDD: RDD[Int] = sc.parallelize(list)
      intRDD.collect().foreach(println)
  
      // 底层调用parallelize   推荐使用  比较好记
      val intRDD1: RDD[Int] = sc.makeRDD(list)
      intRDD1.collect().foreach(println)
  
      // 4.关闭sc
      sc.stop()
    }
  }
  ```

  

- 从外部存储创建RDD

  由外部存储系统的数据集创建RDD包括：本地的文件系统，还有所有Hadoop支持的数据集，比如HDFS、HBase等。

  ```scala
  import org.apache.spark.rdd.RDD
  import org.apache.spark.{SparkConf, SparkContext}
  
  object Test02_FromFile {
    def main(args: Array[String]): Unit = {
      // 1.创建sc的配置对象
      val conf: SparkConf = new SparkConf()
        .setAppName("sparkCore").setMaster("local[*]")
  
      // 2. 创建sc对象
      val sc = new SparkContext(conf)
  
      // 3. 编写任务代码
      // 不管文件中存的是什么数据  读取过来全部当做字符串处理
      val lineRDD: RDD[String] = sc.textFile("input/1.txt")
  
      lineRDD.collect().foreach(println)
  
      // 4.关闭sc
      sc.stop()
    }
  }
  ```

  

- 从其他RDD创建。

  主要是通过一个RDD运算完后，再产生新的RDD。

### 分区规则

- 从集合中创建分区规则

  ```scala
  import org.apache.spark.rdd.RDD
  import org.apache.spark.{SparkConf, SparkContext}
  
  object Test03_ListPartition {
    def main(args: Array[String]): Unit = {
      // 1.创建sc的配置对象
      val conf: SparkConf = new SparkConf()
        .setAppName("sparkCore").setMaster("local[*]")
  
      // 2. 创建sc对象
      val sc = new SparkContext(conf)
  
      // 3. 编写任务代码
      // 默认环境的核数
      // 可以手动填写参数控制分区的个数
      val intRDD: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4, 5),2)
  
      // 数据分区的情况
      // 0 => 1,2  1 => 3,4,5
      // RDD的五大特性   getPartitions
      // 利用整数除机制  左闭右开
      // 0 => start 0*5/2  end 1*5/2
      // 1 => start 1*5/2  end 2*5/2
  
  
      // 将rdd保存到文件  有几个文件生成  就有几个分区
      intRDD.saveAsTextFile("output")
  
      // 4.关闭sc
      sc.stop()
    }
  }
  ```

  分区源码

  ```scala
  // Sequences need to be sliced at the same set of index positions for operations
  // like RDD.zip() to behave as expected
  // length: 数据长度
  // numSlices: 分区数
  def positions(length: Long, numSlices: Int): Iterator[(Int, Int)] = {
      (0 until numSlices).iterator.map { i =>
          val start = ((i * length) / numSlices).toInt
          val end = (((i + 1) * length) / numSlices).toInt
          (start, end)
      }
  }
  ```

- 从文件中创建RDD分区规则

  ```scala
  import org.apache.spark.rdd.RDD
  import org.apache.spark.{SparkConf, SparkContext}
  
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
      val lineRDD: RDD[String] = sc.textFile("input/1.txt",3)
  
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
  ```

  分区源码

  ```scala
  org.apache.hadoop.mapred.FileInputFormat.getSplits(JobConf job, int numSplits)
  
  注意：
  getSplits文件返回的是切片规划，真正读取是在compute方法中创建LineRecordReader读取的，有两个关键变量： 
  start = split.getStart()	   
  end = start + split.getLength
  
  1.分区数量的计算方式:
  totalSize = 10
  goalSize = 10 / 3 = 3(byte) 表示每个分区存储3字节的数据
  分区数= totalSize/ goalSize = 10 /3 => 3,3,4
  4子节大于3子节的1.1倍,符合hadoop切片1.1倍的策略,因此会多创建一个分区,即一共有4个分区  3,3,3,1
  
  2.Spark读取文件，采用的是hadoop的方式读取，所以一行一行读取，跟字节数没有关系
  
  3.数据读取位置计算是以偏移量为单位来进行计算的。
  
  4.数据分区的偏移量范围的计算
  0 => [0,3]         1@@     012        0 => 1,2
  1 => [3,6]         2@@     345        1 => 3        
  2 => [6,9]         3@@     678        2 => 4
  3 => [9,9]         4         9        3 => 无
  ```




### 转换算子

#### value类型

##### map

1. 函数签名：

   `def map[U: ClassTag](f: T=> U): RDD[U]`

2. 功能说明：

   参数 f 是一个函数，它可以接收一个参数。当某个RDD执行map方法时，会遍历该RDD中的每一个数据项，并依次应用 f 函数，从而产生一个新的RDD。即，这个新RDD中的每一个元素都是原来RDD中每一个元素依次应用 f 函数而得到的。

3. 需求说明：

   创建一个1-4数组的RDD，两个分区，将所有元素 *2 形成新的RDD

   ![](E:\workspace\sty_project\sty\sty_spark\doc.spark\笔记\img\map.png)

   ```scala
   object value01_map {
   
       def main(args: Array[String]): Unit = {
   
           //1.创建SparkConf并设置App名称
           val conf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
           //2.创建SparkContext，该对象是提交Spark App的入口
           val sc = new SparkContext(conf)
   
           //3具体业务逻辑
           // 3.1 创建一个RDD
           val rdd: RDD[Int] = sc.makeRDD(1 to 4, 2)
   
           // 3.2 调用map方法，每个元素乘以2
           val mapRdd: RDD[Int] = rdd.map(_ * 2)
   
           // 3.3 打印修改后的RDD中数据
           mapRdd.collect().foreach(println)
   
           //4.关闭连接
           sc.stop()
       }
   }
   ```

#####  mapPartitions

1. 函数签名：

   `def map[U: ClassTag](f: T=> U): RDD[U]`

2. 功能说明：

   参数 f 是一个函数，它可以接收一个参数。当某个RDD执行map方法时，会遍历该RDD中的每一个数据项，并依次应用 f 函数，从而产生一个新的RDD。即，这个新RDD中的每一个元素都是原来RDD中每一个元素依次应用 f 函数而得到的。

### 行动算子





