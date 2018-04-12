package example.value.inject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SampleService {
    @Value("#{${example.keywords-map}}")
    Map<String, String> keywordsMap;
    @Value("#{'${example.stores}'.split(',')}")
    List<String> stores;

    @PostConstruct
    public void showInjectedValues() {
        for (Map.Entry<String, String> entry : keywordsMap.entrySet()) {
            log.info(entry.getKey() + ": " + entry.getValue());
        }

        log.info("store有：");
        for (String store : stores) {
            log.info(store);
        }
    }
}
