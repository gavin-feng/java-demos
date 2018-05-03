package com.wnbt;

import com.aliyun.oss.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;

/**
 * 简单的PutObject的示例代码，
 * OSS产品官网，详见 https://www.aliyun.com/product/oss 的「文档&SDK」
 *   https://help.aliyun.com/document_detail/32009.html 「SDK参考」-> 「Java-SDK」->「安装」
 *   有示例说明：文件夹操作、断点续传、CRC校验、Bucket操作、进度条等
 */

@Slf4j
@SpringBootApplication
public class PutObjectSample {
    // 替换为正确的值
    private static String endpoint = "oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "<keyId>";
    private static String accessKeySecret = "<keySecret>";
    private static String bucketName = "wagonsclub";
    private static String key = "test/crc-sample.txt";


    public static void main(String[] args) {
        SpringApplication.run(PutObjectSample.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String content = "Hello OSS, Hi OSS, OSS OK.";
            System.out.println("执行OSS操作");

            OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

            try {
                ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
//            ossClient.deleteObject(bucketName, key);

            } catch (OSSException oe) {
                System.out.println("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                System.out.println("Error Message: " + oe.getErrorCode());
                System.out.println("Error Code:       " + oe.getErrorCode());
                System.out.println("Request ID:      " + oe.getRequestId());
                System.out.println("Host ID:           " + oe.getHostId());
            } catch (ClientException ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message: " + ce.getMessage());
            } catch (InconsistentException ie) {
                System.out.println("Caught an OSSException");
                System.out.println("Request ID:      " + ie.getRequestId());
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                ossClient.shutdown();
            }
        };
    }

}
