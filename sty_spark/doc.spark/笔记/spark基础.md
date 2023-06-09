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

   

### 行动算子





