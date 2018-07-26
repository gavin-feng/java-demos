package example.db.enums;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findAllByCreditLevel(CustomerCreditLevel creditLevel);
    List<Customer> findAllByCreditLevelIn(List<CustomerCreditLevel> levels);
}
