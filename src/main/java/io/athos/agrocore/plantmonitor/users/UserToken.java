package io.athos.agrocore.plantmonitor.users;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;
//    @ManyToOne
//    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;




}
