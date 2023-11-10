package com.dolthub;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "seed")
public class DaoSeed {

    @Id
    @Column(name = "seed")
    private long seed;

    public DaoSeed() { }

    public DaoSeed(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

}
