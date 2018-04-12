package example.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ExampleService {
    // 在生命周期内，要确保不被回收，通常用Map来保存着
    GaugeValue valueForAd1 = new GaugeValue();
    GaugeValue valueForAd2 = new GaugeValue();

    // 用于存储gauge数值的
    class GaugeValue {
        private double gaugeValue = 0;

        public double value() {
            return this.gaugeValue;
        }

        public void setValue(double gaugeValue) {
            this.gaugeValue = gaugeValue;
        }
    }

    // 设置为 PostConstruct，简化示例代码的执行
    @PostConstruct
    public void doSomething() {
        // 结果：ad_search_total{keyword="kk",} 1.0
        Counter counter = Metrics.counter("ad.search", "keyword", "kk");
        counter.increment();

        // 示例结果：
        // ad_rank{corp="Google",} 458.0
        // ad_rank{corp="Facebook",} 777.0
        String gaugeName = "ad.rank";
        String tagAttr = "corp";

        String corp1 = "Google";
        List<Tag> tagList1 = buildGaugeTags(tagAttr, corp1);
        Metrics.gauge(gaugeName, tagList1, valueForAd1, obj->obj.value());

        String corp2 = "Facebook";
        List<Tag> tagList2 = buildGaugeTags(tagAttr, corp2);
        Metrics.gauge(gaugeName, tagList2, valueForAd2, obj->obj.value());

        valueForAd1.setValue(458);
        valueForAd2.setValue(777);
    }

    private List<Tag> buildGaugeTags(String tagAttr, String tagValue) {
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag() {
            @Override
            public String getKey() {
                return tagAttr;
            }

            @Override
            public String getValue() {
                return tagValue;
            }
        });
        return tagList;
    }
}
