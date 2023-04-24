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

    /**
     * 文件上传
     *
     * @throws IOException
     */
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

    /**
     * 文件下载
     *
     * @throws IOException
     */
    @Test
    public void testCopyToLocalFile() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://192.168.1.201:9820");
        FileSystem fileSystem = FileSystem.get(configuration);

        // boolean delSrc 指是否将原文件删除
        // Path src 指要下载的文件路径
        // Path dst 指将文件下载到的路径
        // boolean useRawLocalFileSystem 是否开启文件校验
        fileSystem.copyToLocalFile(false,
                new Path("/settings.xml"),
                new Path("/home/liming/develop_tools/maven/apache-maven-3.8.6/conf/settings.xml"),
                true
        );

        fileSystem.close();
    }
}
