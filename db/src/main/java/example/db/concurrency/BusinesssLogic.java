package example.db.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class BusinesssLogic {
    @Autowired
    private UserRepositoryJDBC userRepository;

    Object obj1 = new Object();
    Object obj2 = new Object();

    private Long recordId;

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    /**
     * 脏读：
     * 数据已修改事务没提交，另一事务读取到未提交的数据！（事务没提交另一事务就读到未提交的数据）
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 |   取出数据 20   |                |
     *     ----------------------------------------
     *     | T4 |     更新 30    |                 |
     *     ----------------------------------------
     *     | T5 |               |    读取数据 30   |
     *     ----------------------------------------
     *
     *   READ_UNCOMMITTED 级别下出现
     */

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void dirtyReadSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("取出数据 " + user.getUsername());
        user.setUsername("" + System.currentTimeMillis());
        log.info("更新数据 " + user.getUsername());
        userRepository.save(user);
        notify2AndWait1();

        log.info("再次有控制权");
        throw new RuntimeException("事务1应该回滚了！");
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public synchronized void dirtyReadSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t取出数据 " + user.getUsername());
        notify1AndWait2();
    }

    /**
     * 不可重复读
     * 同一事务两次读取数据不一样；第一个事务读去数据，第二个事务修改数据提交，
     * 第一个事务再一次读取数据，这样第一个事务两次读取的数据将不一致。
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 |   查询数据 20   |                |
     *     ----------------------------------------
     *     | T4 |               |     取出数据 20  |
     *     ----------------------------------------
     *     | T5 |               |     更新数据 30  |
     *     ----------------------------------------
     *     | T6 |               |      提交事务    |
     *     ----------------------------------------
     *     | T7 |   查询数据 30  |                 |
     *     ----------------------------------------
     *
     *   READ_UNCOMMITTED、READ_COMMITTED 级别下会出现
     */

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unrepeatableReadSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("查询数据 " + user.getUsername());
        notify2AndWait1();

        userRes = userRepository.findById(recordId);
        user = userRes.get();
        log.info("查询数据 " + user.getUsername());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unrepeatableReadSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t取出数据 " + user.getUsername());
        user.setUsername("" + System.currentTimeMillis());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新数据 " + user.getUsername());
        log.info("\t\t\t\t\t\t\t\t提交事务 ");
    }

    /**
     * 幻读：
     * 两个事务，第一个事务将所有行的数据都修改了，第二个事务将插入一条数据提交，第1个事务提交发现有一条数据并没有修改。
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 | 查询数据 1条记录 |                |
     *     ----------------------------------------
     *     | T4 |                |    插入1条记录  |
     *     ----------------------------------------
     *     | T5 |                |     提交事务    |
     *     ----------------------------------------
     *     | T6 | 查询数据 2条记录 |                |
     *     ----------------------------------------
     *
     *   READ_UNCOMMITTED、READ_COMMITTED 级别下会出现，
     *   但 因为采用MVCC机制，innodb在快照读的情况下可以部分避免幻读, 而在当前读的情况下可以避免不可重复读和幻读!
     *
     *   效果: 如果事务B在事务A执行中, insert了一条数据并提交, 事务A再次查询, 虽然读取的是undo中的旧版本数据(防止了部分幻读),
     *   但是事务A中执行update或者delete都是可以成功的!!
     *     ----------------------------------------
     *     | T6'| 更新数据 2条记录 |                |
     *     ----------------------------------------
     */

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void phantomReadSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        long total = userRepository.count();
        log.info("查询数据 {}条数据", total);
        notify2AndWait1();

        total = userRepository.count();
        log.info("查询数据 {}条数据", total);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void phantomReadSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        User newUser = new User();
        newUser.setUsername("幻读");
        log.info("\t\t\t\t\t\t\t\t插入一条数据 ");
        userRepository.save(newUser);
        log.info("\t\t\t\t\t\t\t\t提交事务 ");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void phantomReadUpdateSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        String username = "幻读";
        long total = userRepository.countByUsername(username);
        log.info("查询数据 {}条数据", total);
        notify2AndWait1();

        total = userRepository.updateNameByUsername(""+System.currentTimeMillis(), username);
        log.info("更新数据 {}条数据", total);
    }

    /**
     * 第一类更新丢失（回滚丢失）：
     * 当2个事务更新相同的数据源，如果一个事务被提交，而另外一个事务却被撤销，
     * 那么会连同第一个事务所做的更新也被撤销。也就是说第一个事务做的跟新丢失了
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 |   取出数据 20   |                |
     *     ----------------------------------------
     *     | T4 |                |    取出数据 20  |
     *     ----------------------------------------
     *     | T5 |                |    更新数据 35  |
     *     ----------------------------------------
     *     | T6 |                |     提交事务    |
     *     ----------------------------------------
     *     | T7 |   更新数据 30   |                |
     *     ----------------------------------------
     *     | T8 |     回滚事务    |                |
     *     ----------------------------------------
     *
     *   MySQL的 InnoDB使用MVCC中结合了排他锁 不会出现第一类更新丢失的问题
     */

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void rollbackLostSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("取出数据 " + user.getUsername());
        notify2AndWait1();

        user.setUsername("" + System.currentTimeMillis());
        userRepository.save(user);
        log.info("更新数据 " + user.getUsername());
        throw new RuntimeException("事务1回滚" + System.currentTimeMillis());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rollbackLostSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t取出数据 " + user.getUsername());

        user.setUsername("" + System.currentTimeMillis());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新数据 {} 于{}", user.getUsername(), System.currentTimeMillis());
        log.info("\t\t\t\t\t\t\t\t提交事务 ");
    }

    /**
     * 第二类更新丢失（覆盖丢失）：
     * 不可重复读的特例。有两个并发事务同时读取同一行数据，第一个对它进行修改提交，第二个也进行了修改提交。
     * 这就会造成第一次写操作失效。
     *
     *     ----------------------------------------
     *     |    |      事务1     |      事务2      |
     *     ----------------------------------------
     *     | T1 |     开启事务    |                |
     *     ----------------------------------------
     *     | T2 |                |     开启事务    |
     *     ----------------------------------------
     *     | T3 |   取出数据 20   |                |
     *     ----------------------------------------
     *     | T4 |                |    取出数据 20  |
     *     ----------------------------------------
     *     | T5 |   更新数据 30   |                |
     *     ----------------------------------------
     *     | T6 |    提交事务     |                |
     *     ----------------------------------------
     *     | T7 |                |    更新数据 35  |
     *     ----------------------------------------
     *     | T8 |                |     提交事务    |
     *     ----------------------------------------
     *
     *  注意：无法模拟出 事务1更新、事务2更新、事务1提交、事务2提交 的序列，因为事务1未提交之前，
     *       事务2的更新是无法得到写锁的，必须等事务1提交才行。
     *
     *   READ_UNCOMMITTED、READ_COMMITTED、REPEATABLE_READ都会出现该问题
     */

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void coverLostSeq1() {
        log.info("事务开始");
        wait1ForOneSecondOrTimeout();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("取出数据 " + user.getUsername());
        notify2AndWait1();

        user.setUsername("" + System.currentTimeMillis());
        userRepository.save(user);
        log.info("更新数据 " + user.getUsername());
        log.info("提交事务于 " + System.currentTimeMillis());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void coverLostSeq2() {
        log.info("\t\t\t\t\t\t\t\t事务开始");
        wait2TillNotified();

        Optional<User> userRes = userRepository.findById(recordId);
        User user = userRes.get();
        log.info("\t\t\t\t\t\t\t\t取出数据 " + user.getUsername());
        notify1AndWait2();

        user.setUsername("" + System.currentTimeMillis());
        userRepository.save(user);
        log.info("\t\t\t\t\t\t\t\t更新数据 {} 于{}", user.getUsername(), System.currentTimeMillis());
        
        log.info("\t\t\t\t\t\t\t\t提交事务于 " + new Date());
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
