package example.db.lock.optimistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("optiBusinessLogic")
@Slf4j
public class BusinessLogic {
    @Autowired
    private OptimisticUserRepository userRepository;

    Object obj1 = new Object();
    Object obj2 = new Object();

    private Long recordId;

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    /**
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 |     查询 20    |                 |
     *     ----------------------------------------
     *     | T4 |                |     查询 20     |
     *     ----------------------------------------
     *     | T5 |                |     更新 30     |
     *     ----------------------------------------
     *     | T6 |     更新 35    |                 |     抛异常
     *     ----------------------------------------
     *
     */
    @Transactional
    public void readAndUpdateSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<OptimisticUser> userRes = userRepository.findById(recordId);
        OptimisticUser user = userRes.get();
        log.info("查询 " + user.getUsername());
        notify2AndWait1();

        user.setUsername("" + System.currentTimeMillis());
        log.info("开始更新 " + user.getUsername());
        userRepository.save(user);
        log.info("更新结束 " + user.getUsername());
        notify2AndWait1();

        log.info("结束");
    }
    @Transactional
    public synchronized void readAndUpdateSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        Optional<OptimisticUser> userRes = userRepository.findById(recordId);
        OptimisticUser user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t查询 " + user.getUsername());
        user.setUsername("" + System.currentTimeMillis());
        log.info("\t\t\t\t\t\t\t\t开始更新 " + user.getUsername());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新结束 " + user.getUsername());
        notify1AndWait2();
    }

    private void notify2AndWait1() {
        synchronized (obj2) {
            obj2.notify();
        }
        wait1ForOneSecondOrTimeout();
    }

    private void wait1ForOneSecondOrTimeout() {
        try {
            synchronized (obj1) {
                obj1.wait(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notify1AndWait2() {
        synchronized (obj1) {
            obj1.notify();
        }
        try {
            synchronized (obj2) {
                obj2.wait(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 一直等待，直到由线程1来唤醒该线程2
    private void wait2TillNotified() {
        try {
            synchronized (obj2) {
                obj2.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
