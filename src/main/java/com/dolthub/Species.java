package com.dolthub;

import jakarta.persistence.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "Species")
public class Species {
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
       private Map<String,Damage> damageMap;

       public Species() {}
       
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

       public double getDamage(Species other) {
           Damage dmg = this.damageMap.get(other.getId());
           if (dmg == null) {
                return 0.0;
           }
           return dmg.getDamage();
       }
       public void setDamage(Species other, double damage) {
           Damage dmg = this.damageMap.get(other.getId());
           if (dmg == null) {
               dmg = new Damage();
               dmg.setAttacker(this.getId());
               dmg.setVictim(other.getId());

               this.damageMap.put(other.getId(), dmg);
           }
           dmg.setDamage(damage);
       }
}