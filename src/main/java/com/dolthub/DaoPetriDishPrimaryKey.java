package com.dolthub;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DaoPetriDishPrimaryKey implements Serializable {
    private int x;
    private int y;

    public DaoPetriDishPrimaryKey() {
        this.x = -1;
        this.y = -1;
    }

    public DaoPetriDishPrimaryKey(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DaoPetriDishPrimaryKey that = (DaoPetriDishPrimaryKey) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
