package com.lm.sty.hadoop.mr.serialization;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

    private long upFlow;
    private long sumFlow;
    private long downFlow;

    private FlowBean v = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Reducer<Text, FlowBean, Text, FlowBean>.Context context)
            throws IOException, InterruptedException {

        upFlow = 0;
        downFlow = 0;
        sumFlow = 0;

        for (FlowBean flowBean : values) {
            upFlow += flowBean.getUpFlow();
            downFlow += flowBean.getDownFlow();
            sumFlow += flowBean.getUpFlow() + flowBean.getDownFlow();
        }

        v.setUpFlow(upFlow);
        v.setDownFlow(downFlow);
        v.setSumFlow(sumFlow);

        context.write(key, v);
    }
}
