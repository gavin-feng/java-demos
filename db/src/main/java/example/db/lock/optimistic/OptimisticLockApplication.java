package example.db.lock.optimistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 乐观锁演示
 *
 */
@SpringBootApplication
@Slf4j
public class OptimisticLockApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public static void main(String[] args) {
        SpringApplication.run(OptimisticLockApplication.class, args);
        log.info("试验开始：");

        BusinessLogic business = OptimisticLockApplication.getBean(BusinessLogic.class);
        business.setRecordId(3L);

        Runnable run1 = () -> {business.readAndUpdateSeq1();};
        Runnable run2 = () -> {business.readAndUpdateSeq2();};

        Thread thread1 = new Thread(run1);
        Thread thread2 = new Thread(run2);
        thread1.start();
        thread2.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(OptimisticLockApplication.applicationContext == null){
            OptimisticLockApplication.applicationContext  = applicationContext;
        }
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return OptimisticLockApplication.applicationContext.getBean(clazz);
    }
}
