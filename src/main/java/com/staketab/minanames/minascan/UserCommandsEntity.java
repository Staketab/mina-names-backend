package com.staketab.minanames.minascan;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_commands")
public class UserCommandsEntity {

    @Id
    private int id;
    private String type;
    private long nonce;
    private Long validUntil;
    private String memo;
    private String hash;

}
