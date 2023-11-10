package com.dolthub;

import jakarta.persistence.*;

/* Data Access Object for the damage table. The Damage table is for tracking one number, which is how much
 * damage is dealt from one species to another. Application doesn't use Damage class directly, as it's obtained
 * as a OneToMany lookup in the Species class.
 */
@Entity
@Table(name = "damage")
public class DaoDamage {
       @Id
       @Column(name = "id")
       @GeneratedValue(strategy = GenerationType.UUID)
       private String id;

       @Column(name = "attacker")
       private String attacker;

       @Column(name = "victim")
       private String victim;

       @Column(name = "damage")
       private double damage;

       public DaoDamage() {}
       
       public String getId() {
          return id;
       }

       public String getAttacker() {
              return attacker;
       }

       public String getVictim() {
              return victim;
       }

       public double getDamage() {
              return damage;
       }

       public void setAttacker(String attacker) {
              this.attacker = attacker;
       }

       public void setVictim(String victim) {
              this.victim = victim;
       }

       public void setDamage(double damage) {
              this.damage = damage;
       }

       @Override
       public String toString() {
           return "{id: "+ this.id+ ", attacker: " +this.attacker + ", victim: " + this.victim + ", damage: " + this.damage + "}";
       }
}