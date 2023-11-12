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

    // Hibernate session objects are stored here. When an update is required, we'll use them to persist. For performance
    // reasons we don't use them as the live objects.
    private Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> sessionObjects;

    private Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> liveBoard;

    private long score = 0;

    private List<DaoSpecies> species;

    private final long seed;

    private final DatabaseInterface db;

    public GameState(DatabaseInterface db, long seed, List<DaoPetriDishCell> petridish, List<DaoSpecies> species){
        this.db = db;
        this.species = species;
        this.seed = seed;

        this.liveBoard = new LinkedHashMap<>();
        this.sessionObjects = new LinkedHashMap<>();

        for (DaoPetriDishCell p : petridish) {
            liveBoard.put(p.getId(), new DaoPetriDishCell(p));
            sessionObjects.put(p.getId(), p);
        }
    }

    public void tick() {
        Set<String> bioDiversity = new HashSet<>();
        Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> newBoard = new HashMap<>();
        for(int x = 0; x < HEIGHT; x++) {
            for( int y = 0; y < WIDTH; y++) {
                DaoPetriDishPrimaryKey location = new DaoPetriDishPrimaryKey(x,y);

                DaoPetriDishCell cell = liveBoard.get(location);
                Set<DaoPetriDishCell> neighbors = getNeighbors(location);

                if (cell != null) {
                    for (DaoPetriDishCell org : neighbors) {
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
                    for (DaoPetriDishCell cellAlt : neighbors) {
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

                        DaoSpecies found = null;
                        // this is a little silly, but shortest path.
                        for(DaoSpecies s: species) {
                            if (s.getId().equals(match)) {
                                found = s;
                                break;
                            }
                        }

                        DaoPetriDishCell newCell = new DaoPetriDishCell(location, found, 1.0);
                        newBoard.put(location, newCell);
                    }
                }
            }
        }

        this.score += tickPoints(bioDiversity.size());
        this.liveBoard = newBoard;
    }

    public void randomize(long seed) {
        liveBoard.clear();

        Random rand = new Random(seed);

        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                if (rand.nextBoolean()) {
                    int r = rand.nextInt(species.size());
                    DaoPetriDishPrimaryKey key = new DaoPetriDishPrimaryKey(x,y);
                    DaoPetriDishCell cell = new DaoPetriDishCell(key, species.get(r), rand.nextDouble());
                    liveBoard.put(key, cell);
                }
            }
        }
        this.persist();
    }


    public void persist() {
        sessionObjects = this.db.updateBoard(sessionObjects, liveBoard);

        liveBoard.clear();
        for(DaoPetriDishPrimaryKey key : sessionObjects.keySet()) {
            DaoPetriDishCell sessObj = sessionObjects.get(key);
            liveBoard.put(key, new DaoPetriDishCell(key, sessObj.getSpecies(), sessObj.getStrength()));
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
    public DaoPetriDishCell getCell(DaoPetriDishPrimaryKey key) {
        return liveBoard.get(key);
    }

    public long getScore(){
        return this.score;
    }

    public List<DaoSpecies> getSpecies() {
        return species;
    }

    private Set<DaoPetriDishCell> getNeighbors(DaoPetriDishPrimaryKey point) {
        HashSet<DaoPetriDishCell> answer = new HashSet<>();
        for (Direction dir : Direction.values()) {
            DaoPetriDishCell cell = liveBoard.get(neighborPoint(point, dir));
            if (cell != null) {
                answer.add(cell);
            }
        }
        return answer;
    }

    public static DaoPetriDishPrimaryKey neighborPoint(DaoPetriDishPrimaryKey cell, Direction direction) {
        int newVal;
        switch (direction) {
            case NORTH:
                newVal = (cell.getX() - 1) < 0 ? (GameState.HEIGHT - 1) : cell.getX() - 1;
                return new DaoPetriDishPrimaryKey(newVal, cell.getY());
            case SOUTH:
                newVal = (cell.getX() + 1) == GameState.HEIGHT ? 0 : cell.getX() + 1;
                return new DaoPetriDishPrimaryKey(newVal, cell.getY());
            case WEST:
                newVal = (cell.getY() + 1) == GameState.WIDTH ? 0 : cell.getY() + 1;
                return new DaoPetriDishPrimaryKey(cell.getX(), newVal);
            case EAST:
                newVal = (cell.getY() - 1) < 0 ? GameState.WIDTH : cell.getY() - 1;
                return new DaoPetriDishPrimaryKey(cell.getX(), newVal);
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

    public long getSeed() {
        return seed;
    }
}
