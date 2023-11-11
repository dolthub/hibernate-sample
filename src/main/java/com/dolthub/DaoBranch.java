package com.dolthub;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * A fabricated object which doesn't have a direct modeling in the DB. This represented the branch name
 * and it's current HEAD value. It's created from the `dolt_branches` table which has additional information
 * we ignore.
 */
@Entity
public class DaoBranch {
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
