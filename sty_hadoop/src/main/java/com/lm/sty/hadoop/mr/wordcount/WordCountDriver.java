package com.lm.sty.hadoop.mr.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * WordCountDriver
 *
 * @author liming
 * @version 1.0
 * @since 2023/2/13 下午2:09
 */
public class WordCountDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        // 1 获取配置信息以及封装任务
        Configuration conf = new Configuration();

        //设置在集群运行的相关参数-设置HDFS,NAMENODE的地址
//        conf.set("fs.defaultFS", "hdfs://hadoop01:9820");
//        //指定MR运行在Yarn上
//        conf.set("mapreduce.framework.name","yarn");
//        //指定MR可以在远程集群运行
//        conf.set("mapreduce.app-submission.cross-platform", "true");
//        //指定yarn resourcemanager的位置
//        conf.set("yarn.resourcemanager.hostname", "hadoop02");

        Job job = Job.getInstance(conf);

        // 2 关联本Driver程序的jar (本地跑）
        job.setJarByClass(WordCountDriver.class);
        // 设置jar加载路径 （远程跑）
        //job.setJar("E:\\workspace\\sty_project\\sty_hadoop\\target\\sty_hadoop-1.0-SNAPSHOT.jar");

        // 3 关联Mapper和Reducer的jar
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        // 4 设置Mapper输出的kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 5 设置最终输出kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(CombineTextInputFormat.class);
        CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);

        // 6 设置输入和输出路径
//        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.1.201:9820/input"));
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.1.201:9820/output1"));
        FileInputFormat.setInputPaths(job, new Path("E:\\workspace\\sty_project\\sty\\sty_hadoop\\src\\main\\resources\\input\\word.txt"));
        FileOutputFormat.setOutputPath(job, new Path("E:\\workspace\\sty_project\\sty\\sty_hadoop\\src\\main\\resources\\output"));

        // 7 提交job
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
