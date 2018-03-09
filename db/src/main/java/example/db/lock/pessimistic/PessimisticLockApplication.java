package example.db.lock.pessimistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 先拿到写锁的（事务1），其它的，肯定要等待事务1完成才行； 不再演示了
 * 先拿到读锁的
 *
 *  select ... for update 显式地加“写锁”
 *      具有排他性，不允许加任何其他锁
 *
 *  select ... lock in share mode 显式地加“读锁”
 *      具有共享性，其它事务也能申请同一记录上的“读锁”；
 *      很多网上的资料写明，不允许被申请“写锁”，实际上是错误的，可以被“申请”到，
 *      只不过此时事务2的状态是 LOCK WAIT，即在等待事务1将S锁释放；如果此时事务1进行update，则触发死锁。
 *      反证：如果事务2没有拿到写锁，那事务1的update操作是可以执行的，不会触发死锁。
 *  select ... lock in share mode 的使用场景：后续不会接update语句
 *
 */
@SpringBootApplication
@Slf4j
public class PessimisticLockApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public static void main(String[] args) {
        SpringApplication.run(PessimisticLockApplication.class, args);
        log.info("试验开始：");

        BusinessLogic business = PessimisticLockApplication.getBean(BusinessLogic.class);
        business.setRecordId(3L);

        // 此处更改示例相关方法
        Runnable run1 = () -> {business.readAndWrite_See_Seq1();};
        Runnable run2 = () -> {business.readAndWrite_See_Seq2();};

        Thread thread1 = new Thread(run1);
        Thread thread2 = new Thread(run2);
        thread1.start();
        thread2.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(PessimisticLockApplication.applicationContext == null){
            PessimisticLockApplication.applicationContext  = applicationContext;
        }
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return PessimisticLockApplication.applicationContext.getBean(clazz);
    }
}
