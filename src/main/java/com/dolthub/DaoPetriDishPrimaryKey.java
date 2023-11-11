package com.dolthub;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DaoPetriDishPrimaryKey implements Serializable {
    private int x;
    private int y;

    public DaoPetriDishPrimaryKey() {
        // Note these values are illegal in the DB. Hibernate will update them before writing.
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

    /**
     * Due to this being an embedded id, we want to make sure that the equals method is right.
     * @param o
     * @return
     */
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
