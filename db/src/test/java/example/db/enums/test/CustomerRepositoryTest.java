package example.db.enums.test;

import com.alibaba.fastjson.JSON;
import example.db.enums.Customer;
import example.db.enums.CustomerCreditLevel;
import example.db.enums.CustomerRepository;
import example.db.enums.CustomerStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void enumTest2() {
        // 对变量名称可以进行 valueOf
        CustomerCreditLevel level = CustomerCreditLevel.valueOf("PASS_B");
        log.info(level.getCode() + " " + level.getDesc() + " " + level.name() + " " + level.ordinal());
        // 会报错
        level = CustomerCreditLevel.valueOf("B+");
        log.info(level.getCode() + level.getDesc());
    }

    @Test
    public void testEnumJson() {
        Customer customer = new Customer();
        customer.setName("cc");
        customer.setStatus(CustomerStatus.UN_APPLY);
        customer.setCreditLevel(CustomerCreditLevel.PASS_B);
        String customerStr = JSON.toJSONString(customer);
        log.info(customerStr);
        Customer parsedCustomer = JSON.parseObject(customerStr, Customer.class);
        log.info(parsedCustomer.getId() + " " + parsedCustomer.getStatus().getDesc() + " " + parsedCustomer.getCreditLevel().getDesc());

    }

    /**
     * 实际使用时，采用 @Enumerated(EnumType.STRING)，int类型（EnumType.ORDINAL），顺序可能会变，更容易出问题
     */
    @Test
    public void enumTest() {
        Customer customer = new Customer();
        customer.setName("cc");
        customer.setStatus(CustomerStatus.UN_APPLY);
        customer.setCreditLevel(CustomerCreditLevel.PASS_B);
        customer = customerRepository.save(customer);
        log.info(customer.getId() + " " + customer.getStatus().getDesc() + " ");
        customer = customerRepository.findById(customer.getId()).orElse(null);
        log.info(customer.getId() + " " + customer.getStatus().getDesc() + " ");
    }

    @Test
    public void testSearch() {
        List<CustomerCreditLevel> levels = new ArrayList<>();
        levels.add(CustomerCreditLevel.PASS_A);
        levels.add(CustomerCreditLevel.PASS_BP);
        List<Customer> customers = customerRepository.findAllByCreditLevelIn(levels);
        for (Customer customer : customers) {
            log.info(customer.getId() + " " + customer.getStatus().getDesc() + " " + customer.getCreditLevel().getDesc());
        }
    }

    @Test
    public void testSaveNull() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("kk");
        customerRepository.save(customer);
    }
}
