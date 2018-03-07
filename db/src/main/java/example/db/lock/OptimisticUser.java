package example.db.lock;

import lombok.Data;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;

@Data
@OptimisticLocking
@Entity
@Table(name = "user")
public class OptimisticUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Version
    @Column
    private Long version;
}
