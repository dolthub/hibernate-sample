package com.dolthub;

import jakarta.persistence.*;

import java.awt.*;
import java.util.Map;

/**
 * Dao Object for Species Entities. Species entries consist of a one-to-many table which states the damage
 * that this species does on other species.
 */
@Entity
@Table(name = "species")
public class DaoSpecies {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "color")
    private String hexColor;

    @Column(name = "tick_health_impact")
    private double tickHealthImpact;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "attacker")
    @MapKey(name = "victim")
    private Map<String, DaoDamage> damageMap;

    /**
     * Only constructor for DaoSpecies. All instances are created and managed by Hibernate.
     */
    public DaoSpecies() {}
       
    public String getId() {
        return id;
    }
       
    public void setId( String id ) {
        this.id = id;
    }

    /**
     * Conver the `#ffffff` for color into a Color object for display.
     *
     * @return Translated color
     */
    public Color getColor() {
        return Color.decode(this.hexColor);
    }

    public String getColorText() {
        return this.hexColor;
    }

    public double getTickHealthImpact() {
        return this.tickHealthImpact;
    }

    public void setTickHealthImpact(double newVal) {
        this.tickHealthImpact = newVal;
    }

    @Override
    public String toString() {
        return "{id "+ this.id+ ", color " + this.hexColor + ", tickHealthImpact " + this.tickHealthImpact + "}";
    }

    /**
     * Read the loaded damage value, or return 0.0 if one doesn't exist.
     *
     * @param other
     * @return
     */
    public double getDamage(DaoSpecies other) {
        DaoDamage dmg = this.damageMap.get(other.getId());
        if (dmg == null) {
            return 0.0;
        }
        return dmg.getDamage();
    }

    /**
     * Set a new value for the damage this species causes to another. This will be persisted correctly, as an
     * INSERT or UPDATE, when the species class is persisted, even though the damage is in another table.
     *
     * @param other
     * @param damage
     */
    public void setDamage(DaoSpecies other, double damage) {
        DaoDamage dmg = this.damageMap.get(other.getId());
        if (dmg == null) {
            dmg = new DaoDamage();
            dmg.setAttacker(this.getId());
            dmg.setVictim(other.getId());

            this.damageMap.put(other.getId(), dmg);
        }
        dmg.setDamage(damage);
    }
}