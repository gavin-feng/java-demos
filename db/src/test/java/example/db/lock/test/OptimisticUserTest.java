package example.db.lock.test;

import example.db.lock.OptimisticUser;
import example.db.lock.OptimisticUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
//@Transactional
@SpringBootTest
@Slf4j
public class OptimisticUserTest {
    @Autowired
    OptimisticUserRepository optimisticUserRepository;

    /**
     * 不能使用 @Transactional 来进行回滚（在同一事务中，乐观锁似乎失效了）
     */
    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void testOptimisticLockingFailure() {
        OptimisticUser user = new OptimisticUser();
        user.setUsername("abc");

        optimisticUserRepository.save(user);

        Optional<OptimisticUser> user1 = optimisticUserRepository.findById(user.getId());
        log.info("user1: " + user1);
        Optional<OptimisticUser> user2 = optimisticUserRepository.findById(user.getId());
        log.info("user2: " + user2);
        user1.get().setUsername("name1");
        user2.get().setUsername("name2");
        optimisticUserRepository.save(user1.get());
        user1 = optimisticUserRepository.findById(user.getId());
        log.info("user1: " + user1);

        optimisticUserRepository.save(user2.get());
        user2 = optimisticUserRepository.findById(user.getId());
        log.info("user2: " + user2);
    }
}
