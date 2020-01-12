package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private String login;
    private String password;
    private Set<Role> roles;            // zbior rol
    private LocalDateTime registrationDateTime;
    private boolean status;
    private int probes;
}
