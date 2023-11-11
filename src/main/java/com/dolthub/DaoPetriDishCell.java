package com.dolthub;

import jakarta.persistence.*;

/**
 * The single cell representation of the game board. Each cell is keyed by it's X x Y position
 * on the board.
 *
 * We have 1600 cell positions, and when the game is running there are many updates. For this reason
 * we have a copy constructor to create detached objects which are not managed by Hibernate.
 */
@Entity
@Table(name = "petridish")
public class DaoPetriDishCell {

    /**
     * Use of a composite key is required. This maps to the composite primary key defined on the data schema.
     */
    @EmbeddedId
    private DaoPetriDishPrimaryKey id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private DaoSpecies species;

    /**
     * The strength is the health of the cell. It is required to be  > 0.0 and  <= 1.0
     */
    @Column(name = "strength")
    private double strength;

    /**
     * Empty object constructor. This is only used within the Hibernate code to create managed objects.
     * Our application code doesn't use it.
     */
    public DaoPetriDishCell() { }

    /**
     * Copy constructor to create a detached object.
     * @param cell
     */
    public DaoPetriDishCell(DaoPetriDishCell cell) {
        this.id = cell.getId();
        this.species = cell.getSpecies();
        this.strength = cell.getStrength();
    }

    /**
     * Direct construct for all state values.
     *
     * @param id
     * @param species
     * @param strength
     */
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

    /**
     * Alter the state of the object in as application specific way. Better than using getter/setters everywhere. The health
     * of this cell will be decreased by value `hits`. This may result in health which is below 0.0, which will
     * be handled in application logic.
     * @param hits
     */
    public void lowerStrength(double hits) {
        this.strength -= Math.min(0.1, hits);
    }
}