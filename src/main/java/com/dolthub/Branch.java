package com.dolthub;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Branch {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "hash")
    private String hash;

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }
}
