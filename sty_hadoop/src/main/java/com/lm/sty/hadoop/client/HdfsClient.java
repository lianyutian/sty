package com.lm.sty.hadoop.client;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author liming
 * @version 1.0
 * @since 2023/4/23 17:27
 */
public class HdfsClient {

    @Test
    public void testMkdirs() throws URISyntaxException, IOException, InterruptedException {
        Configuration configuration = new Configuration();
        // 注意该地址为namenode配置的地址，不是web端地址
        //configuration.set("fs.defaultFS", "hdfs://192.168.1.201:9820");
        FileSystem fs = FileSystem.get(new URI("hdfs://192.168.1.201:9820"), configuration, "hadoop");
        //FileSystem fs = FileSystem.get(configuration);
        fs.mkdirs(new Path("/sanguozhanji2"));
    }

    @Test
    public void testCopyFromLocalFile() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://192.168.1.201:9820");
        FileSystem fileSystem = FileSystem.get(configuration);

        fileSystem.copyFromLocalFile(
                new Path("/home/liming/develop_tools/maven/apache-maven-3.8.6/conf/settings.xml"),
                new Path("/settings.xml"));

        fileSystem.close();
    }
}
