package com.lm.sty.hadoop.mr.serialization;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 需求
 * 统计每一个手机号耗费的总上行流量、总下行流量、总流量
 * （1）输入数据
 *      phone_data.txt
 * （2）输入数据格式：
 *      7 	13560436666	120.196.100.99		1116		 954			200
 *      id	手机号码		网络ip			上行流量  下行流量     网络状态码
 * （3）期望输出数据格式
 *      13560436666 		1116		      954 			2070
 *      手机号码		    上行流量        下行流量		总流量
 */
public class FlowMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    private final Text k = new Text();
    private final FlowBean v = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        String[] data = line.split(" ");

        String phone = data[1];
        String upFlow = data[4];
        String downFlow = data[5];

        k.set(phone);
        v.setUpFlow(Long.parseLong(upFlow));
        v.setDownFlow(Long.parseLong(downFlow));

        context.write(k, v);
    }
}
