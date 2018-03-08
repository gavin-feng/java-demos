package example.db.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
@Slf4j
public class ConcurrencyApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApplication.class, args);
        log.info("试验开始：");

        BusinesssLogic business = ConcurrencyApplication.getBean(BusinesssLogic.class);
        business.setRecordId(3L);

        Runnable run1 = () -> {business.phantomReadUpdateSeq1();};
        Runnable run2 = () -> {business.phantomReadSeq2();};

        Thread thread1 = new Thread(run1);
        Thread thread2 = new Thread(run2);
        thread1.start();
        thread2.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(ConcurrencyApplication.applicationContext == null){
            ConcurrencyApplication.applicationContext  = applicationContext;
        }
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return ConcurrencyApplication.applicationContext.getBean(clazz);
    }
}
