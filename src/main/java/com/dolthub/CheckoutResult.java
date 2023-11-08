package com.dolthub;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CheckoutResult {
    @Id
    @Column(name = "status")
    private int status;

    @Column(name = "message")
    private String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean success() {
        return status == 0;
    }

}
