# MapReduce简介

MapReduce 是 Google 提出的一种分布式计算模型，是一种用于大规模数据处理的编程框架。MapReduce 模型的基本思想是将大规模的数据集分成小的数据块进行并行处理，以实现高效的数据处理和计算。

MapReduce 模型主要由两个函数组成：Map 函数和 Reduce 函数。其中，Map 函数用于将输入数据转换成键值对形式的中间结果，Reduce 函数则对中间结果进行汇总和处理。在 MapReduce 模型中，用户只需要编写 Map 函数和 Reduce 函数，框架会自动完成数据分发、调度和并行处理等操作，从而实现高效的数据处理和计算。



# MapReduce优缺点

**MapReduce 的优点包括：**

1.  高可靠性：MapReduce 模型中的任务可以自动重试和容错，因此具有高可靠性。
2.  易于扩展：MapReduce 模型可以很容易地扩展到多台计算机上进行并行计算，从而可以处理大规模的数据集。
3.  易于编程：MapReduce 模型的编程接口简单易用，用户只需要编写 Map 函数和 Reduce 函数，而不需要编写复杂的并行代码。
4.  易于优化：MapReduce 模型可以通过调整分块大小、调整并行度等方式进行优化，从而提高计算性能。

**MapReduce 的缺点包括：**

1. 任务调度开销较大：MapReduce 模型中的任务调度需要一定的开销，因此对于小规模的数据集，MapReduce 模型的性能可能不如传统的计算模型。

2. 数据读写开销较大：MapReduce 模型需要将数据写入磁盘进行中间结果的存储和传输，因此对于数据读写密集的应用，MapReduce 模型的性能可能受到影响。

3. 处理流程单一：MapReduce 模型中的数据处理过程比较单一，不能支持复杂的数据处理流程。

   

# MapReduce核心思想

MapReduce 的核心思想是将大规模的数据处理任务分解成多个独立的子任务，通过并行执行这些子任务来高效地处理数据。它的核心思想包括以下几个关键要点：

1.  分而治之：将大规模的数据处理任务分解成多个独立的子任务，每个子任务只处理部分数据，从而实现任务的并行执行。
    
2.  映射（Map）：通过映射将输入数据集中的每个元素转化为中间键值对（key-value pairs），即 <key, value> 对。映射阶段的任务是独立的，可以并行处理。
    
3.  合并和排序：将映射阶段的输出结果按照键（key）进行合并和排序，将具有相同键的中间结果聚合在一起。
    
4.  归约（Reduce）：将合并和排序后的中间结果按照键分组，然后对每个组进行归约操作，生成最终的输出结果。
    
5.  自动任务调度和容错：MapReduce 框架自动处理任务的调度、容错和数据复制等细节，用户只需关注业务逻辑的实现。
    

通过将任务分解、并行处理和结果合并，MapReduce 实现了高效的大规模数据处理。它可以在分布式计算集群中运行，利用集群中的多台计算机资源并行执行任务，大大加快了数据处理的速度。同时，MapReduce 框架提供了任务调度、容错和数据复制等机制，确保了任务的可靠性和高可用性。这使得开发人员能够专注于业务逻辑的实现，而无需关注分布式计算的底层细节。

**以计算单词个数为例**

![](img\MapReduce核心思想.png)



# MapReduce进程

在 MapReduce 中，涉及到以下几个核心进程：

1.  JobTracker：在整个 MapReduce 作业的生命周期中，JobTracker 负责作业的调度、监控和容错等任务。它运行在集群的主节点上，管理着多个任务的执行。JobTracker 负责分配任务给空闲的 TaskTracker，并监控任务的执行情况。
    
2.  TaskTracker：TaskTracker 运行在集群的各个工作节点上，它负责执行具体的任务。TaskTracker 从 JobTracker 获取待执行的任务，执行 Map 任务或 Reduce 任务，并将任务的状态和进度等信息反馈给 JobTracker。TaskTracker 还负责数据本地化，尽量在存储有数据块的节点上执行任务，以减少数据传输开销。
    
3.  Map Task：Map Task 是在 TaskTracker 上执行的任务，负责对输入数据进行分片处理，并生成中间结果。它从输入数据源中读取数据，应用 Map 函数将数据转换成 <key, value> 对，然后将中间结果输出。
    
4.  Reduce Task：Reduce Task 是在 TaskTracker 上执行的任务，负责对 Map 任务的中间结果进行合并和归约操作。它接收来自多个 Map 任务的中间结果，按照键进行合并和排序，并将相同键的值传递给 Reduce 函数进行归约，最终生成最终的输出结果。
    

这些进程之间通过网络通信来协同工作，完成整个 MapReduce 作业的执行过程。JobTracker 负责调度和监控作业，将任务分配给空闲的 TaskTracker；TaskTracker 负责执行具体的任务，Map Task 负责数据处理，Reduce Task 负责结果归约；它们通过交换消息和数据来完成任务的执行和结果的传递。整个 MapReduce 进程的协作和分布式计算能力使得大规模数据处理成为可能。



# Hadoop序列化

### 什么是序列化

​	**序列化**就是把内存中的对象，转换成字节序列（或其他数据传输协议）以便于存储到磁盘（持久化）和网络传输。 

​	**反序列化**就是将收到字节序列（或其他数据传输协议）或者是磁盘的持久化数据，转换成内存中的对象。

### 为什么序列化

​	一般来说，“活的”对象只生存在内存里，关机断电就没有了。而且“活的”对象只能由本地的进程使用，不能被发送到网络上的另外一台计算机。 然而序列化可以存储“活的”对象，可以将“活的”对象发送到远程计算机。

### 为什么不用Java的序列化

​	Java的序列化是一个重量级序列化框架（Serializable），一个对象被序列化后，会附带很多额外的信息（各种校验信息，Header，继承体系等），不便于在网络中高效传输。所以，Hadoop自己开发了一套序列化机制（Writable）。

### 自定义bean对象实现序列化接口

​	在企业开发中往往常用的基本序列化类型不能满足所有需求，比如在Hadoop框架内部传递一个bean对象，那么该对象就需要实现序列化接口。

具体实现bean对象序列化步骤如下7步。

1. 必须实现Writable接口

2. 反序列化时，需要反射调用空参构造函数，所以必须有空参构造

   ```java
   public FlowBean() {
   
   	super();
   
   }
   ```

3. 重写序列化方法

   ```java
   @Override
   
   public void write(DataOutput out) throws IOException {
   
   	out.writeLong(upFlow);
   
   	out.writeLong(downFlow);
   
   	out.writeLong(sumFlow);
   
   }
   ```

4. 重写反序列化方法

   ```java
   @Override
   
   public void readFields(DataInput in) throws IOException {
   
   	upFlow = in.readLong();
   
   	downFlow = in.readLong();
   
   	sumFlow = in.readLong();
   
   }
   ```

5. 注意反序列化的顺序和序列化的顺序完全一致。

6. 要想把结果显示在文件中，需要重写toString()，可用"\t"分开，方便后续用。

7. 如果需要将自定义的bean放在key中传输，则还需要实现Comparable接口，因为MapReduce框中的Shuffle过程要求对key必须能排序。**详见后面排序案例**。

   ```java
   @Override
   
   public int compareTo(FlowBean o) {
   
   	// 倒序排列，从大到小
   
   	return this.sumFlow > o.getSumFlow() ? -1 : 1;
   
   }
   ```

   **示例见代码**

# InputFormat数据输入

### 切片与MapTask并行度决定机制

1. 问题思考

   MapTask的并行度决定Map阶段的任务处理并发度，进而影响到整个Job的处理速度。

   思考：1G的数据，启动8个MapTask，可以提高集群的并发处理能力。那么1K的数据，也启动8个MapTask，会提高集群性能吗？MapTask并行任务是否越多越好呢？哪些因素影响了MapTask并行度？

2. MapTask并行度决定机制

   **数据块：**Block是HDFS物理上把数据分成一块一块。数据块是HDFS存储数据单位。

   **数据切片：**数据切片只是在逻辑上对输入进行分片，并不会在磁盘上将其切分成片进行存储。数据切片是MapReduce程序计算输入数据的单位，一个切片会对应启动一个MapTask。



# MapReduce工作机制

###  MapTask工作机制

![](img\MapReduce工作机制.png)

1. Read阶段：MapTask通过InputFormat获得的RecordReader（底层是lineRecordReader），从输入InputSplit中解析出一个个key/value。

2. Map阶段：该节点主要是将解析出的key/value交给用户编写map()函数处理，并产生一系列新的key/value。

3. Collect收集阶段：在用户编写map()函数中，当数据处理完成后，一般会调用OutputCollector.collect()输出结果。在该函数内部，它会将生成的key/value分区（调用Partitioner），并写入一个环形内存缓冲区中。

4. Spill阶段：即“溢写”，当环形缓冲区满后，MapReduce会将数据写到本地磁盘上，生成一个临时文件。需要注意的是，将数据写入本地磁盘之前，先要对数据进行一次本地排序，并在必要时对数据进行合并、压缩等操作。

   **溢写阶段详情：**

   步骤1：利用快速排序算法对缓存区内的数据进行排序，排序方式是，先按照分区编号Partition进行排序，然后按照key进行排序。这		样，经过排序后，数据以分区为单位聚集在一起，且同一分区内所有数据按照key有序。	

   步骤2：按照分区编号由小到大依次将每个分区中的数据写入任务工作目录下的临时文件output/spillN.out（N表示当前溢写次数）		中。如果用户设置了Combiner，则写入文件之前，对每个分区中的数据进行一次聚集操作。	

   步骤3：将分区数据的元信息写到内存索引数据结构SpillRecord中，其中每个分区的元信息包括在临时文件中的偏移量、压缩前数大	小和压缩后数据大小。如果当前内存索引大小超过1MB，则将内存索引写到文件output/spillN.out.index中。

5. Merge阶段：当所有数据处理完成后，MapTask对所有临时文件进行一次合并，以确保最终只会生成一个数据文件。

当所有数据处理完后，MapTask会将所有临时文件合并成一个大文件，并保存到文件output/file.out中，同时生成相应的索引文件output/file.out.index。

​	在进行文件合并过程中，MapTask以分区为单位进行合并。对于某个分区，它将采用多轮递归合并的方式。每轮合并mapreduce.task.io.sort.factor（默认10）个文件，并将产生的文件重新加入待合并列表中，对文件排序后，重复以上过程，直到最终得到一个大文件。

​	让每个MapTask最终只生成一个数据文件，可避免同时打开大量文件和同时读取大量小文件产生的随机读取带来的开销。



### ReduceTask工作机制

![](img\ReduceTask工作机制.png)

1. Copy阶段：ReduceTask从各个MapTask上远程拷贝一片数据，并针对某一片数据，如果其大小超过一定阈值，则写到磁盘上，否则直接放到内存中。
2. Sort阶段：在远程拷贝数据的同时，ReduceTask启动了两个后台线程对内存和磁盘上的文件进行合并，以防止内存使用过多或磁盘上文件过多。按照MapReduce语义，用户编写reduce()函数输入数据是按key进行聚集的一组数据。为了将key相同的数据聚在一起，Hadoop采用了基于排序的策略。由于各个MapTask已经实现对自己的处理结果进行了局部排序，因此，ReduceTask只需对所有数据进行一次归并排序即可。
3. Reduce阶段：reduce()函数将计算结果写到HDFS上。



### ReduceTask并行度决定机制

**回顾：**MapTask并行度由切片个数决定，切片个数由输入文件和切片规则决定。

**思考：**ReduceTask并行度由谁决定？

1. 设置ReduceTask并行度

   ReduceTask的并行度同样影响整个Job的执行并发度和执行效率，但与MapTask的并发数由切片数决定不同，ReduceTask数量的决定是可以直接手动设置：

   ```java
   // 默认值是1，手动设置为4
   job.setNumReduceTasks(4);
   ```



# Shuffle机制

Map方法之后，Reduce方法之前的数据处理过程称之为Shuffle。

![](img\shuffle.png)



