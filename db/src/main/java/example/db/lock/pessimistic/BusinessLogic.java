package example.db.lock.pessimistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class BusinessLogic {
    @Autowired
    private PessimisticUserRepositoryJDBC userRepository;

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
     *     | T3 |    悲观读 20    |                |
     *     ----------------------------------------
     *     | T4 |                |     悲观读 20   |
     *     ----------------------------------------
     *     | T5 |                | 更新30（等待1锁）|
     *     ----------------------------------------
     *     | T6 | 更新 35（死锁） |                 |     触发死锁，MySQL检测到，释放事务1锁；事务2完成更新
     *     ----------------------------------------
     *
     */
    @Transactional
    public void readAndReadSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<PessimisticUser> userRes = userRepository.findByIdPessimisticRead(recordId);
        PessimisticUser user = userRes.get();
        log.info("悲观读 " + user.getUsername());
        notify2AndWait1();

        user.setUsername("" + System.currentTimeMillis());
        log.info("开始更新 " + user.getUsername());
        userRepository.save(user);
        log.info("更新结束 " + user.getUsername());
        notify2AndWait1();

        log.info("结束");
    }
    @Transactional
    public synchronized void readAndReadSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        log.info("\t\t\t\t\t\t\t\t开始读 ");
        Optional<PessimisticUser> userRes = userRepository.findByIdPessimisticRead(recordId);
        PessimisticUser user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t悲观读 " + user.getUsername());
        user.setUsername("" + System.currentTimeMillis());
        log.info("\t\t\t\t\t\t\t\t开始更新 " + user.getUsername());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新结束 " + user.getUsername());
        notify1AndWait2();
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
     *     | T3 |    悲观读 20    |                |
     *     ----------------------------------------
     *     | T4 |                | 悲观写20（等待1）|
     *     ----------------------------------------
     *     | T5 | 更新 35（死锁） |                 |     触发死锁，MySQL检测到，释放事务2锁；事务1完成更新
     *     ----------------------------------------
     *
     */
    @Transactional
    public void readAndWriteSeq1() {
        readAndReadSeq1();
    }
    @Transactional
    public synchronized void readAndWriteSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        log.info("\t\t\t\t\t\t\t\t开始悲观写的查询 ");
        Optional<PessimisticUser> userRes = userRepository.findByIdPessimisticWrite(recordId);
        PessimisticUser user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t悲观写 " + user.getUsername());
        user.setUsername("" + System.currentTimeMillis());
        log.info("\t\t\t\t\t\t\t\t开始更新 " + user.getUsername());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新结束 " + user.getUsername());
        notify1AndWait2();
    }

    /**
     * 一直等待，查询锁的情况
     */
    @Transactional
    public void readAndWrite_See_Seq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<PessimisticUser> userRes = userRepository.findByIdPessimisticRead(recordId);
        PessimisticUser user = userRes.get();
        log.info("悲观读 " + user.getUsername());
        notify2AndWait1Forever();
    }
    @Transactional
    public synchronized void readAndWrite_See_Seq2() {
        readAndWriteSeq2();
    }

    private void notify2AndWait1Forever() {
        synchronized (obj2) {
            obj2.notify();
        }
        try {
            synchronized (obj1) {
                obj1.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
