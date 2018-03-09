package example.db.lock.pessimistic;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "lockDemoUser")
@Table(name = "user")
public class PessimisticUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Version
    @Column
    private Long version;
}
