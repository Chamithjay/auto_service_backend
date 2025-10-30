package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User{
    @Column(unique = true)
    private String nic;

}
