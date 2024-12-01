package org.cyberchronicle.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "userdata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator="user_id_seq", strategy=GenerationType.SEQUENCE)
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Instant registrationDate;
}
