package example.db.lock.optimistic;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "OptimisticUser")
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
