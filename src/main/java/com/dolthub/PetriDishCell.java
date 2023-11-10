package com.dolthub;

import jakarta.persistence.*;

import java.awt.*;

@Entity
@Table(name = "petridish")
public class PetriDishCell {

    @EmbeddedId
    private PetriDishPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(name = "strength")
    private double strength;

    public PetriDishCell() { }

    public PetriDishCell(PetriDishCell cell) {
        this.id = cell.getId();
        this.species = cell.getSpecies();
        this.strength = cell.getStrength();
    }


    public PetriDishCell(PetriDishPrimaryKey id, Species species, double strength) {
        this.id = id;
        this.species = species;
        this.strength = strength;
    }

    public PetriDishPrimaryKey getId() {
        return id;
    }

    public Species getSpecies() {
        return species;
    }

    public double getStrength() {
        return strength;
    }

    public void setId(PetriDishPrimaryKey id) {
        this.id = id;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public void lowerStrength(double hits) {
        this.strength -= Math.min(0.1, hits);
    }

}
