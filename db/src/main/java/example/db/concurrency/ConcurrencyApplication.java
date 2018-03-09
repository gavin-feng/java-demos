package example.db.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 问题：
 *     1、脏读、不可重复读、幻读、第一类更新丢失、第二类更新丢失 等
 *     2、车辆列表页，两个人同时修改； 不是加 @Transactional 就能解决的
 *
 * 演示顺序：
 *     1、默认使用 UserRepositoryJDBC，对每一种并发问题进行展示；
 *         1.1 对于幻读，再用update方法来演示出问题；
 *     2、更改事务的隔离级别，看是否都解决了；
 *     3、更改为 UserRepositoryJPA，即 使用Hibernate的缓存机制；
 *     4、将User申明为 @OptimisticLocking 进行演示
 *
 * 参考资料：
 *     1、官网对锁的描述；
 *     2、MySQL的MVCC；
 *     3、好文章： https://segmentfault.com/a/1190000012650596
 *     4、淘宝的数据库内核月报：  http://mysql.taobao.org/monthly/
 *     等等
 *
 * 结论：
 *     采用乐观锁的机制来防范第二类更新丢失，MySQL默认采用 REPEATABLE_READ，只有部分幻读的问题存在
 */
@SpringBootApplication
@Slf4j
public class ConcurrencyApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApplication.class, args);
        log.info("试验开始：");

        BusinesssLogic business = ConcurrencyApplication.getBean(BusinesssLogic.class);
        business.setRecordId(3L);

        // 此处更改示例相关方法
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
