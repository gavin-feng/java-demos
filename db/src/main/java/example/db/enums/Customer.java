package example.db.enums;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "status")
    private CustomerStatus status;

    @Column(name = "credit_level")
    @Enumerated(EnumType.STRING)
    private CustomerCreditLevel creditLevel;
}
