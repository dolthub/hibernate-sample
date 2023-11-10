package com.dolthub;

import jakarta.persistence.*;

@Entity
@Table(name = "petridish")
public class DaoPetriDishCell {

    @EmbeddedId
    private DaoPetriDishPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private DaoSpecies species;

    @Column(name = "strength")
    private double strength;

    public DaoPetriDishCell() { }

    public DaoPetriDishCell(DaoPetriDishCell cell) {
        this.id = cell.getId();
        this.species = cell.getSpecies();
        this.strength = cell.getStrength();
    }


    public DaoPetriDishCell(DaoPetriDishPrimaryKey id, DaoSpecies species, double strength) {
        this.id = id;
        this.species = species;
        this.strength = strength;
    }

    public DaoPetriDishPrimaryKey getId() {
        return id;
    }

    public DaoSpecies getSpecies() {
        return species;
    }

    public double getStrength() {
        return strength;
    }

    public void setId(DaoPetriDishPrimaryKey id) {
        this.id = id;
    }

    public void setSpecies(DaoSpecies species) {
        this.species = species;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public void lowerStrength(double hits) {
        this.strength -= Math.min(0.1, hits);
    }

}
