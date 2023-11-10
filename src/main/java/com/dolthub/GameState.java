package com.dolthub;

import java.util.*;
import java.util.List;

public class GameState {

    enum Direction {
        NORTH,
        NORTHWEST,
        WEST,
        SOUTHWEST,
        SOUTH,
        SOUTHEAST,
        EAST,
        NORTHEAST,
    }

    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;

    // Hibernate session obejcts are stored here. When an update is required, we'll use them to persist. For performance
    // reasons we don't used them as the live objects.
    private Map<PetriDishPrimaryKey, PetriDishCell> sessionObjects;

    private Map<PetriDishPrimaryKey, PetriDishCell> liveBoard;

    private long score = 0;

//    private final Map<String, Species> idSpeciesMap;


//    private final Map<String, Map<String, Double>> detachedSpeciesDamages = new HashMap<>();

    private List<Species> species;

    private final DatabaseInterface db;

    public GameState(DatabaseInterface db, long seed, List<PetriDishCell> petridish, List<Species> species){
        /*  NM4
        idSpeciesMap = new LinkedHashMap<>();
        for(Species s : species) {
            idSpeciesMap.put(s.getId(), s);

            Map<String,Double> damages = new HashMap<>();
            for (Species victim : species) {
                damages.put(victim.getId(), s.getDamage(victim));
            }
            detachedSpeciesDamages.put(s.getId(), damages);
        }

         */

        this.db = db;
        this.species = species;

        Random rand = new Random(seed);

        this.liveBoard = new LinkedHashMap<>();
        this.sessionObjects = new LinkedHashMap<>();

        /*
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                if (rand.nextBoolean()) {
                    int r = rand.nextInt(species.size());
                    Organism org = new Organism(species.get(r), rand.nextDouble());
                    board.put(new Point(x, y), org);
                }
            }
        }
         */

        for (PetriDishCell p : petridish) {
            liveBoard.put(p.getId(), new PetriDishCell(p));
            sessionObjects.put(p.getId(), p);
        }
    }

    /* NM4
    public class DetachedCellData {
        String species;

        double strength;

        DetachedCellData(PetriDishCell cell) {
            this.species = cell.getSpecies().getId();
            this.strength = cell.getStrength();
        }

        DetachedCellData(String species, double strength) {
            this.species = species;
            this.strength = strength;
        }

        public void lowerStrength(double hits) {
            this.strength -= Math.min(0.1, hits);
        }
    }
     */

    public void tick() {
        Set<String> bioDiversity = new HashSet<>();
        Map<PetriDishPrimaryKey, PetriDishCell> newBoard = new HashMap<>();
        for(int x = 0; x < HEIGHT; x++) {
            for( int y = 0; y < WIDTH; y++) {
                PetriDishPrimaryKey location = new PetriDishPrimaryKey(x,y);

                PetriDishCell cell = liveBoard.get(location);
                Set<PetriDishCell> neighbors = getNeighbors(location);

                if (cell != null) {
                    for (PetriDishCell org : neighbors) {
                        double damage = org.getSpecies().getDamage(cell.getSpecies());
                        cell.lowerStrength(damage);
                    }

                    // Each cell ages by losing strength.
                    cell.lowerStrength(cell.getSpecies().getTickHealthImpact());

                    if (cell.getStrength() > 0.0) {
                        bioDiversity.add(cell.getSpecies().getId());
                        newBoard.put(location, cell);
                    }
                } else if (neighbors.size() >= 3 && neighbors.size() <= 5) {
                    Map<String, Integer> neighborCount = new HashMap<>();
                    for (PetriDishCell cellAlt : neighbors) {
                        int currentVal = neighborCount.getOrDefault(cellAlt.getSpecies(), 0);
                        neighborCount.put(cellAlt.getSpecies().getId(), currentVal + 1);
                    }

                    // find the max count
                    int maxCount = Integer.MIN_VALUE;
                    for (int value : neighborCount.values()) {
                        maxCount = Math.max(maxCount, value);
                    }

                    // Then ensure there is only one.
                    String match = null;
                    Map.Entry<String, Integer> maxEntry = null;
                    for (Map.Entry<String, Integer> entry : neighborCount.entrySet()) {
                        if (entry.getValue() == maxCount) {
                            if (match == null) {
                                match = entry.getKey();
                            } else {
                                match = null;
                                break;
                            }
                        }
                    }

                    if (match != null) {
                        bioDiversity.add(match);

                        Species found = null;
                        // this is a little silly, but shortest path.
                        for(Species s: species) {
                            if (s.getId().equals(match)) {
                                found = s;
                                break;
                            }
                        }

                        PetriDishCell newCell = new PetriDishCell(location, found, 1.0);
                        newBoard.put(location, newCell);
                    }
                }
            }
        }

        this.score += tickPoints(bioDiversity.size());
        this.liveBoard = newBoard;
    }

    public void persist() {
        sessionObjects = this.db.updateBoard(sessionObjects, liveBoard);

        liveBoard.clear();
        for(PetriDishPrimaryKey key : sessionObjects.keySet()) {
            PetriDishCell sessObj = sessionObjects.get(key);
            liveBoard.put(key, new PetriDishCell(key, sessObj.getSpecies(), sessObj.getStrength()));
        }
    }

    private long tickPoints(int speciesCount) {
           if (speciesCount <= 1) {
               return 0;
           }

           speciesCount--;
           return 1L << speciesCount;
    }

    // Null returned if it is not living.
    public PetriDishCell getCell(PetriDishPrimaryKey key) {
        return liveBoard.get(key);
    }

    public long getScore(){
        return this.score;
    }

    public List<Species> getSpecies() {
        return species;
    }

    private Set<PetriDishCell> getNeighbors(PetriDishPrimaryKey point) {
        HashSet<PetriDishCell> answer = new HashSet<>();
        for (Direction dir : Direction.values()) {
            PetriDishCell cell = liveBoard.get(neighborPoint(point, dir));
            if (cell != null) {
                answer.add(cell);
            }
        }
        return answer;
    }

    public static PetriDishPrimaryKey neighborPoint(PetriDishPrimaryKey cell, Direction direction) {
        int newVal;
        switch (direction) {
            case NORTH:
                newVal = (cell.getX() - 1) < 0 ? (GameState.HEIGHT - 1) : cell.getX() - 1;
                return new PetriDishPrimaryKey(newVal, cell.getY());
            case SOUTH:
                newVal = (cell.getX() + 1) == GameState.HEIGHT ? 0 : cell.getX() + 1;
                return new PetriDishPrimaryKey(newVal, cell.getY());
            case WEST:
                newVal = (cell.getY() + 1) == GameState.WIDTH ? 0 : cell.getY() + 1;
                return new PetriDishPrimaryKey(cell.getX(), newVal);
            case EAST:
                newVal = (cell.getY() - 1) < 0 ? GameState.WIDTH : cell.getY() - 1;
                return new PetriDishPrimaryKey(cell.getX(), newVal);
            case NORTHWEST:
                return neighborPoint(neighborPoint(cell, Direction.NORTH), Direction.WEST);
            case NORTHEAST:
                return neighborPoint(neighborPoint(cell, Direction.NORTH), Direction.EAST);
            case SOUTHWEST:
                return neighborPoint(neighborPoint(cell, Direction.SOUTH), Direction.WEST);
            case SOUTHEAST:
                return neighborPoint(neighborPoint(cell, Direction.SOUTH), Direction.EAST);
            default:
        }
        return null;
    }
}
