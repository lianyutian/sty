package com.lm.sty.hadoop.mr.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * map阶段
 *
 * @author liming
 * @version 1.0
 * @since 2023/2/13 下午1:48
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    Text k = new Text();
    IntWritable v = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        // 获取一行
        String line = value.toString();
        // 分割单词
        String[] words = line.split(" ");

        for (String word : words) {
            k.set(word);
            context.write(k, v);
        }
    }
}
