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

   ```scala
   def map[U: ClassTag](f: T=> U): RDD[U]
   ```

2. 功能说明：

   参数 f 是一个函数，它可以接收一个参数。当某个RDD执行map方法时，会遍历该RDD中的每一个数据项，并依次应用 f 函数，从而产生一个新的RDD。即，这个新RDD中的每一个元素都是原来RDD中每一个元素依次应用 f 函数而得到的。

3. 需求说明：

   创建一个1-4数组的RDD，两个分区，将所有元素 *2 形成新的RDD

   ![](img\map.png)

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

   ```scala
   def mapPartitions[U: ClassTag](
       f: Iterator[T] => Iterator[U],
       preservesPartitioning: Boolean = false): RDD[U]
   ```

2. 功能说明：

   参数 f 是一个函数，它可以接收一个参数。当某个RDD执行map方法时，会遍历该RDD中的每一个数据项，并依次应用 f 函数，从而产生一个新的RDD。即，这个新RDD中的每一个元素都是原来RDD中每一个元素依次应用 f 函数而得到的。

3. 需求说明

   创建一个RDD，4个元素，2个分区，使每个元素*2组成新的RDD

   ![](img\mapPartitions.png)

   ```scala
   object value02_mapPartitions {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(1 to 4, 2)
   
       // 3.2 调用mapPartitions方法，每个元素乘以2
       val rdd1: RDD[Int] = rdd.mapPartitions((x: Iterator[Int]) => x.map((_: Int) * 2))
   
       // 3.3 打印修改后的RDD中数据
       rdd1.collect().foreach(println)
       
       //4.关闭连接
       sc.stop()
     }
   }
   ```

**两者区别**

1. map一次处理一个分区中的一个元素
2. mapPartitions每次处理一个分区的数，这个分区的数据处理完成后，原RDD中分区的数据才能释放，可能导致OOM。

开发经验：当内存较大的时候建议使用mapPartition，以提高处理效率。



##### mapPartitionsWithIndex

1. 函数签名

   ```scala
     def mapPartitionsWithIndex[U: ClassTag](
         f: (Int, Iterator[T]) => Iterator[U],
         preservesPartitioning: Boolean = false): RDD[U]
   ```

2. 功能说明

   类似于mapPartitions，比mapPartitions多一个整数参数表示分区号

3. 需求说明

   创建一个RDD，使每个元素跟所在的分区号形成一个元组，组成一个新的RDD

   ![](img\mapPartitionsWithIndex.png)

   ```scala
   object value03_mapPartitionsWithIndex {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(1 to 4, 2)
   
       // 3.2 创建一个RDD，使每个元素跟所在分区号形成一个元组，组成一个新的RDD
       val indexRdd: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex((index: Int, items: Iterator[Int]) => {
         items.map((index, (_: Int)))
       })
   
       // 3.3 打印修改后的RDD中数据
       indexRdd.collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```

   

**flatMap**

1. 函数签名

   ```scala
   def flatMap[U: ClassTag](f: T => TraversableOnce[U]): RDD[U]
   ```

2. 功能说明

   与map操作类似，将RDD中的每一个元素通过应用f函数依次转换为新的元素，并封装到RDD中。

   区别：在flatMap操作中，f函数的返回值是一个集合，并且会将每一个该集合中的元素拆分出来放到新的RDD中。

3. 需求说明

   创建一个集合，集合里面存储的还是子集合，把所有子集合中数据取出放入到一个大的集合中。

   ![](img\flatMap.png)

   ```scala
   object value04_flatMap {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val listRDD: RDD[List[Int]] = sc.makeRDD(List(List(1, 2), List(3, 4), List(5, 6), List(7)), 2)
   
       // 3.2 把所有子集合中数据取出放入到一个大的集合中
       listRDD.flatMap((item: immutable.Seq[Int]) => item).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



##### groupBy

1. 函数签名

   ```scala
   def groupBy[K](f: T => K)(implicit kt: ClassTag[K]): RDD[(K, Iterable[T])]
   ```

   - groupBy会存在shuffle过程
   - shuffle：将不同的分区数据进行打乱重组的过程
   - shuffle一定会落盘。可以在local模式下执行程序，通过4040看效果。

2. 功能说明

   分组，按照传入函数的返回值进行分组。将相同的key对应的值放入一个迭代器。

3. 需求说明

   创建一个RDD，按照元素模以2的值进行分组。

   ![](img\groupBy.png)

   ```scala
   object value05_groupby {
     def main(args: Array[String]): Unit = {
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(1 to 4, 2)
   
       // 3.2 将每个分区的数据放到一个数组并收集到Driver端打印
       rdd.groupBy((_: Int) % 2).collect().foreach(println)
   
       // 3.3 创建一个RDD
       val rdd1: RDD[String] = sc.makeRDD(List("hello", "hive", "hadoop", "spark", "scala"))
   
       // 3.4 按照首字母第一个单词相同分组
       rdd1.groupBy((_: String).substring(0, 1)).collect().foreach(println)
   
       sc.stop()
     }
   }
   ```



##### **filter**

1. 函数签名

   ```scala
   def filter(f: T => Boolean): RDD[T]
   ```

2. 功能说明

   接收一个返回值为布尔类型的函数作为参数。当某个RDD调用filter方法时，会对该RDD中每一个元素应用f函数，如果返回值类型为true，则该元素会被添加到新的RDD中。

3. 需求说明

   创建一个RDD，过滤出对2取余等于0的数据。

   ![](img\filter.png)

   ```scala
   object value06_filter {
     def main(args: Array[String]): Unit = {
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3.创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4), 2)
   
       //3.1 过滤出符合条件的数据
       val filterRdd: RDD[Int] = rdd.filter((_: Int) % 2 == 0)
   
       //3.2 收集并打印数据
       filterRdd.collect().foreach(println)
   
       //4 关闭连接
       sc.stop()
     }
   
   }
   ```



##### **distinct**

1. 函数签名

   ```scala
   def distinct(): RDD[T]
   
   // 对RDD采用多个Task去重，提高并发度
   def distinct(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]
   ```

2. 功能说明

   对内部元素去重，并将去重后的元素放入新的RDD中。

3. 需求说明

   创建一个RDD，对RDD中数据进行去重。

   ```scala
   object value07_distinct {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val distinctRdd: RDD[Int] = sc.makeRDD(List(1, 2, 1, 5, 2, 9, 6, 1))
   
       // 3.2 打印去重后生成的新RDD
       distinctRdd.distinct().collect().foreach(println)
   
       // 3.3 对RDD采用多个Task去重，提高并发度
       distinctRdd.distinct(2).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```

>
>
>注意：distinct过程存在shuffle



##### **coalesce**

Coalesce算子包括：配置执行Shuffle和配置不执行Shuffle两种方式。

1. 函数签名

   ```scala
   // 默认shuffle = false 不执行shuffle
   def coalesce(numPartitions: Int, shuffle: Boolean = false,
                  partitionCoalescer: Option[PartitionCoalescer] = Option.empty)
                 (implicit ord: Ordering[T] = null)
         : RDD[T]
   ```

2. 功能说明

   缩减分区数，用于大数据集过滤后，提高小数据集的执行效率。

3. 需求说明

   将4个分区合并为2个分区

   ![](img\coalesce.png)

   ```scala
   object value08_coalesce {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3.创建一个RDD
       //val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4), 4)
   
       //3.1 缩减分区
       //val coalesceRdd: RDD[Int] = rdd.coalesce(2)
   
       //4. 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4, 5, 6), 3)
       //4.1 缩减分区-不执行shuffle
       //val coalesceRDD: RDD[Int] = rdd.coalesce(2)
       //4.2 执行shuffle
       val coalesceRdd: RDD[Int] = rdd.coalesce(2, shuffle = true)
   
       //5 查看对应分区数据
       val indexRDD: RDD[(Int, Int)] = rdd.mapPartitionsWithIndex(
         (index: Int, datas: Iterator[Int]) => {
           datas.map((index, _: Int))
         }
       )
   
       //6 打印数据
       indexRDD.collect().foreach(println)
   
       //8 延迟一段时间，观察http://localhost:4040页面，查看Shuffle读写数据
       Thread.sleep(100000)
   
       //7.关闭连接
       sc.stop()
     }
   }
   ```



##### **repartition**

1. 函数签名

   ```scala
   def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T]
   ```

2. 功能说明

   该操作内部其实执行的是coalesce操作，参数shuffle的默认值为true。无论是将分区数多的RDD转换为分区数少的RDD，还是将分区数少的RDD转换为分区数多的RDD，repartition操作都可以完成，因为无论如何都会经shuffle过程。分区规则不是hash，因为平时使用的分区都是按照hash来实现的，repartition一般是对hash的结果不满意，想要打散重新分区。

3. 与 coalesce 区别

   - coalesce重新分区，可以选择是否进行shuffle过程。由参数shuffle: Boolean = false/true决定。

   - repartition实际上是调用的coalesce，进行shuffle。

     ```scala
     def repartition(numPartitions: Int)(implicit ord: Ordering[T] = null): RDD[T] = withScope {
         coalesce(numPartitions, shuffle = true)
     }
     ```

   - **coalesce一般为缩减分区**，如果扩大分区，不使用shuffle是没有意义的，repartition扩大分区执行shuffle。

4. 需求说明

   创建一个4个分区的RDD，对其重新分区。

   ![](img\repartition.png)

   ```scala
   object value09_repartition {
     def main(args: Array[String]): Unit = {
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3. 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4, 5, 6), 3)
   
       //3.1 缩减分区
       //val coalesceRdd: RDD[Int] = rdd.coalesce(2, true)
   
       //3.2 重新分区
       val repartitionRdd: RDD[Int] = rdd.repartition(2)
   
       //4 打印查看对应分区数据
       val indexRdd: RDD[(Int, Int)] = repartitionRdd.mapPartitionsWithIndex(
         (index: Int, datas: Iterator[Int]) => {
           datas.map((index, _: Int))
         }
       )
   
       //5 打印
       indexRdd.collect().foreach(println)
   
       //6. 关闭连接
       sc.stop()
     }
   }
   ```



##### **sortBy**

1. 函数签名

   ```scala
     def sortBy[K](
         f: (T) => K,
         ascending: Boolean = true,
         numPartitions: Int = this.partitions.length)
         (implicit ord: Ordering[K], ctag: ClassTag[K]): RDD[T]
   ```

2. 功能说明

   该操作用于排序数据。在排序之前，可以将数据通过f函数进行处理，之后按照f函数处理的结果进行排序，默认为正序排列。排序后新产生的RDD的分区数与原RDD的分区数一致。

3. 需求说明

   创建一个RDD，按照数字大小分别实现正序和倒序排序。

   ![](img\sortBy.png)

   ```scala
   object value10_sortBy {
     def main(args: Array[String]): Unit = {
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       // 3.1 创建一个RDD
       val rdd: RDD[Int] = sc.makeRDD(List(2, 1, 3, 4, 6, 5))
   
       // 3.2 默认是升序排
       val sortRdd: RDD[Int] = rdd.sortBy((num: Int) => num)
       sortRdd.collect().foreach(println)
   
       // 3.3 配置为倒序排
       val sortRdd2: RDD[Int] = rdd.sortBy((num: Int) => num, ascending = false)
       sortRdd2.collect().foreach(println)
   
       // 3.4 创建一个RDD
       val strRdd: RDD[String] = sc.makeRDD(List("1", "22", "12", "2", "3"))
   
       // 3.5 按照字符的int值排序
       strRdd.sortBy((num: String) => num.toInt).collect().foreach(println)
   
       // 3.5 创建一个RDD
       val rdd3: RDD[(Int, Int)] = sc.makeRDD(List((2, 1), (1, 2), (1, 1), (2, 2)))
   
       // 3.6 先按照tuple的第一个值排序，相等再按照第2个值排
       rdd3.sortBy((t: (Int, Int)) => t).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```




#### **双Value类型**

##### intersection

1. 函数签名

   ```scala
   def intersection(other: RDD[T]): RDD[T]
   ```

2. 功能说明

   对源RDD和参数RDD求交集后返回一个新的RDD

3. 需求说明

   创建两个RDD，求两个RDD的交集。

   ![](img\intersection.png)

   ```scala
   object DoubleValue01_intersection {
     def main(args: Array[String]): Unit = {
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd1: RDD[Int] = sc.makeRDD(1 to 4)
   
       //3.2 创建第二个RDD
       val rdd2: RDD[Int] = sc.makeRDD(4 to 8)
   
       //3.3 计算第一个RDD与第二个RDD的交集并打印
       // 利用shuffle的原理进行求交集  需要将所有的数据落盘shuffle 效率很低  不推荐使用
       rdd1.intersection(rdd2).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



##### union

1. 函数签名

   ```scala
   def union(other: RDD[T]): RDD[T]
   ```

2. 功能说明

   对源RDD和参数RDD求并集后返回一个新的RDD

3. 需求说明

   创建两个RDD，求并集

   ![](img\union.png)

   ```scala
   object DoubleValue02_union {
     def main(args: Array[String]): Unit = {
       val conf : SPark
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd1: RDD[Int] = sc.makeRDD(1 to 4)
   
       //3.2 创建第二个RDD
       val rdd2: RDD[Int] = sc.makeRDD(4 to 8)
   
       //3.3 计算两个RDD的并集
       // 将原先的RDD的分区和数据都保持不变  简单的将多个分区合并在一起 放到一个RDD中
       // 由于不走shuffle  效率高  所以会使用到
       rdd1.union(rdd2).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   
   ```



##### subtract

1. 函数签名

   ```scala
   def subtract(other: RDD[T]): RDD[T]
   ```

2. 功能说明

   计算差的一种函数，去除两个RDD中相同元素，不同的RDD将保留下来

3. 需求说明

   创建两个RDD，求第一个RDD与第二个RDD的差集

   ```scala
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
   ```



##### zip拉链

1. 函数签名

2. 功能说明

3. 需求说明

   创建两个RDD，并将两个RDD组合到一起形成一个(k,v)RDD

   ![](img\zip.png)

   ```scala
   object DoubleValue04_zip {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd1: RDD[Int] = sc.makeRDD(Array(1, 2, 3), 3)
   
       //3.2 创建第二个RDD
       val rdd2: RDD[String] = sc.makeRDD(Array("a", "b", "c"), 3)
   
       //3.3 第一个RDD组合第二个RDD并打印
       rdd1.zip(rdd2).collect().foreach(println)
   
       //3.4 第二个RDD组合第一个RDD并打印
       rdd2.zip(rdd1).collect().foreach(println)
   
       //3.5 创建第三个RDD（与1，2分区数不同）
       val rdd3: RDD[String] = sc.makeRDD(Array("a", "b"), 3)
   
       //3.6 元素个数不同，不能拉链
       // Can only zip RDDs with same number of elements in each partition
       rdd1.zip(rdd3).collect().foreach(println)
   
       //3.7 创建第四个RDD（与1，2分区数不同）
       val rdd4: RDD[String] = sc.makeRDD(Array("a", "b", "c"), 2)
   
       //3.8 分区数不同，不能拉链
       // Can't zip RDDs with unequal numbers of partitions: List(3, 2)
       rdd1.zip(rdd4).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



#### Key-Value类型

##### partitionBy**按照K重新分区**

1. 函数签名

   ```scala
   def partitionBy(partitioner: Partitioner): RDD[(K, V)]
   ```

2. 功能说明

   将RDD[K,V]中的K按照指定Partitioner重新进行分区；

   如果原有的RDD和新的RDD是一致的话就不进行分区，否则会产生Shuffle过程。

3. 需求说明

   创建一个3个分区的RDD，对其重新分区

   ![](img\partitionBy.png)

   ```scala
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
   ```



#####  groupByKey

1. 函数签名

   ```scala
   def groupByKey(): RDD[(K, Iterable[V])]
   ```

2. 功能说明

   groupByKey对每个key进行操作，但只生成一个seq，并不进行聚合。

   该操作可以指定分区器或者分区数（默认使用HashPartitioner）

3. 需求说明

   统计单词出现次数

   ![](img\groupByKey.png)

   ```scala
   object KeyValue03_groupByKey {
   
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd = sc.makeRDD(List(("a", 1), ("b", 5), ("a", 5), ("b", 2)))
   
       //3.2 将相同key对应值聚合到一个Seq中
       val group: RDD[(String, Iterable[Int])] = rdd.groupByKey()
   
       //3.3 打印结果
       group.collect().foreach(println)
   
       //3.4 计算相同key对应值的相加结果
       group.map(t => (t._1, t._2.sum)).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```

   

##### reduceByKey

1. 函数签名

   ```scala
   def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)]
   ```

2. 功能说明

   该操作可以将RDD[K,V]中的元素按照相同的K对V进行聚合。其存在多种重载形式，还可以设置新RDD的分区数。

3. 需求说明

   统计单词出现次数

   ![](img\reduceByKey.png)

   ```scala
   object KeyValue02_reduceByKey {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd = sc.makeRDD(List(("a", 1), ("b", 5), ("a", 5), ("b", 2)))
   
       //3.2 计算相同key对应值的相加结果
       val reduce: RDD[(String, Int)] = rdd.reduceByKey((v1, v2) => v1 + v2)
   
       //3.3 打印结果
       reduce.collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```




**reduceByKey和groupByKey的区别**

1. reduceBykey：按照 **key** 进行聚合，在 **shuffle** 之前有 **combine**（预聚合）操作，返回结果是 **RDD[K,V]**
2. groupByKey：按照 **key** 进行分组，直接进行 **shuffle**

在不影响业务逻辑的前提下，优先使用 reduceByKey。求和操作不影响业务逻辑，求平均值影响业务逻辑，后续会学习功能更加强大的归约算子，能够在预聚合的情况下实现求平均值。



##### aggregateByKey

1. 函数签名

   ```scala
   def aggregateByKey[U: ClassTag](zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) => U,
         combOp: (U, U) => U): RDD[(K, U)]
   ```

   1. zeroValue(初始值)：给每一个分区中的每一种key一个初始值。
   2. seqOp(分区内)：函数用于在每一个分区中用初始值逐步迭代value。
   3. combOp(分区间)：函数用于合并每个分区中的结果。

2. 功能说明

   分区内和分区间逻辑不同的归约。

3. 需求说明

   取出每个分区相同key对应值的最大值，然后相加。

   ![](img\aggregateByKey.png)

   ```scala
   object KeyValue04_aggregateByKey {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd: RDD[(String, Int)] = sc.makeRDD(List(("a",1),("a",3),("a",5),("b",7),("b",2),("b",4),("b",6),("a",7)), 2)
   
       //3.2 取出每个分区相同key对应值的最大值，然后相加
       rdd.aggregateByKey(0)(math.max, (_: Int) + (_: Int)).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



##### sortByKey

1. 函数签名

   ```scala
   def sortByKey(ascending: Boolean = true, numPartitions: Int = self.partitions.length) : RDD[(K, V)]
   
   ascending: Boolean = true 默认升序
   ```

2. 功能说明

   在一个(K,V)的RDD上调用，K必须实现Ordered接口，返回一个按照key进行排序的(K,V)的RDD。

3. 需求说明

   创建一个pairRDD，按照key的正序和倒序进行排序。

   ![](img\sortBy.png)

   ```scala
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
   ```



#####  mapValues

1. 函数签名

   ```scala
   def mapValues[U](f: V => U): RDD[(K, U)]
   ```

2. 功能说明

   针对于(K,V)形式的类型只对V进行操作。

3. 需求说明

   创建一个pairRDD，并将value添加字符串"|||"。

   ![](img\mapValues.png)

   ```scala
   object KeyValue08_mapValues {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "a"), (1, "d"), (2, "b"), (3, "c")))
   
       //3.2 对value添加字符串"|||"
       rdd.mapValues((_: String) + "|||").collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



##### join

1. 函数签名

   ```scala
   def join[W](other: RDD[(K, W)], partitioner: Partitioner): RDD[(K, (V, W))]
   ```

2. 功能说明

   在类型为(K,V)和(K,W)的RDD上调用，返回一个相同key对应的所有元素对在一起的(K,(V,W))的RDD。

   **等同于sql里的内连接,关联上的要,关联不上的舍弃**

3. 需求说明

   创建两个pairRDD，并将key相同的数据聚合到一个元组。
   ![](img\join.png)

   ```scala
   object KeyValue09_join {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "a"), (2, "b"), (3, "c")))
   
       //3.2 创建第二个pairRDD
       val rdd1: RDD[(Int, Int)] = sc.makeRDD(Array((1, 4), (2, 5), (4, 6)))
   
       //3.3 join操作并打印结果
       rdd.join(rdd1).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   
   ```



##### cogroup

1. 函数签名

   ```scala
     def cogroup[W1, W2, W3](other1: RDD[(K, W1)],
         other2: RDD[(K, W2)],
         other3: RDD[(K, W3)],
         partitioner: Partitioner)
         : RDD[(K, (Iterable[V], Iterable[W1], Iterable[W2], Iterable[W3]))]
   ```

2. 功能说明

   在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的RDD。

   操作两个RDD中的KV元素，每个RDD中相同key中的元素分别聚合成一个集合。

   **类似于sql的全连接，但是在同一个RDD中对key聚合**

3. 需求说明

   创建两个pairRDD，并将key相同的数据聚合到一个迭代器。

   ![](img\cogroup.png)

   ```scala
   object KeyValue10_cogroup {
     def main(args: Array[String]): Unit = {
   
       //1.创建SparkConf并设置App名称
       val conf: SparkConf = new SparkConf().setAppName("SparkCoreTest").setMaster("local[*]")
   
       //2.创建SparkContext，该对象是提交Spark App的入口
       val sc: SparkContext = new SparkContext(conf)
   
       //3具体业务逻辑
       //3.1 创建第一个RDD
       val rdd: RDD[(Int, String)] = sc.makeRDD(Array((1, "a"), (2, "b"), (3, "c")))
   
       //3.2 创建第二个RDD
       val rdd1: RDD[(Int, Int)] = sc.makeRDD(Array((1, 4), (2, 5), (4, 6)))
   
       //3.3 cogroup两个RDD并打印结果
       // (1,(CompactBuffer(a),CompactBuffer(4)))
       // (2,(CompactBuffer(b),CompactBuffer(5)))
       // (3,(CompactBuffer(c),CompactBuffer()))
       // (4,(CompactBuffer(),CompactBuffer(6)))
       rdd.cogroup(rdd1).collect().foreach(println)
   
       //4.关闭连接
       sc.stop()
     }
   }
   ```



#### 实例

需求：统计出每一个省份广告被点击次数的Top3

 [agent.log](..\..\datas\agent\agent.log) 

![](img\top3.png)

```scala
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
```



### 行动算子

行动算子是触发了整个作业的执行。因为转换算子都是懒加载，并不会立即执行。

#### collect

1. 函数签名
2. 功能说明
3. 需求说明





