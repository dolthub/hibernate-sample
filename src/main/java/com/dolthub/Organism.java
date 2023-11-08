package com.dolthub;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Organism {

    private final Species species;
    private double strength;

    public Organism(Species species) {
        this.species = species;
        this.strength = 1.0;
    }

    public Organism(Species species, double strength){
        this.species = species;
        this.strength = strength;
    }

    public Organism lowerStrength(double hits) {
        hits = Math.min(0.1, hits);
        return new Organism(this.species, this.strength -= hits);
    }

    public Species getSpecies() {
        return species;
    }

    public double getStrength() {
        return strength;
    }
}
