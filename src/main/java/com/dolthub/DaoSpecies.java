package com.dolthub;

import jakarta.persistence.*;

import java.awt.*;
import java.util.Map;

@Entity
@Table(name = "Species")
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

       public DaoSpecies() {}
       
       public String getId() {
          return id;
       }
       
       public void setId( String id ) {
          this.id = id;
       }

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

       public double getDamage(DaoSpecies other) {
           DaoDamage dmg = this.damageMap.get(other.getId());
           if (dmg == null) {
                return 0.0;
           }
           return dmg.getDamage();
       }
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