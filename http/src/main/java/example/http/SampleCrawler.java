package example.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SampleCrawler {
    @Resource
    private Environment env;

    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";

    private String keywords="电脑 租赁";

    private static String BAIDU_URL = "https://www.baidu.com/s";

    @PostConstruct
    public String baiduSearch() throws Exception {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setConnectTimeout(30000)
                .setSocketTimeout(30000)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setRedirectStrategy(new LaxRedirectStrategy()).build();

        String proxyHost = env.getProperty("inverst.proxy.host");
        if (Strings.isNotBlank(proxyHost)) {
            int proxyPort = Integer.parseInt(env.getProperty("inverst.proxy.port", "0"));
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(globalConfig)
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setRoutePlanner(new DefaultProxyRoutePlanner(proxy))
                    .build();
        }

        try {
            log.info("keyword: "+ keywords);
            //封装请求参数
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("wd", keywords));
            //转换为键值对
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
            str = encodeURIComponent(str);
            log.info("encode: " + str);

            //创建Get请求
            HttpGet httpGet = new HttpGet(BAIDU_URL+"?"+str);
            log.info("user-agent: " + userAgent);
            httpGet.setHeader("User-Agent", userAgent);
            httpGet.setHeader("Connection", "keep-alive");
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode / 100 != 2) {
                throw new HttpResponseException(statusCode, "error status: " + statusCode);
            }
            try {
                HttpEntity httpEntity = httpResponse.getEntity();
                String res = EntityUtils.toString(httpEntity, Consts.UTF_8);
                log.info(res);
                return res;
            } finally {
                httpResponse.close();
            }
        } finally {
            httpClient.close();
        }
    }

    private String encodeURIComponent(String s) {
        String result = s
                .replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");

        return result;
    }
}
