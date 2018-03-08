package example.db.concurrency;

import lombok.Data;

import javax.persistence.*;

@Data
//@OptimisticLocking
@Entity(name = "user")
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String name;

    @Version
    private Long version;
}
