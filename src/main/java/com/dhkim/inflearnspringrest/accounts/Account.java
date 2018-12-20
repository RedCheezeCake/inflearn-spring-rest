package com.dhkim.inflearnspringrest.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Account {

    @Id @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<AccountRole> roles;
}
