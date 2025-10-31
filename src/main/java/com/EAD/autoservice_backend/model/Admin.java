package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User{
    @Column(unique = true)
    private String nic;

}
