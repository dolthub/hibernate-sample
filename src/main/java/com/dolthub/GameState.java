package com.dolthub;

import java.awt.*;
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

    private Map<Point, Organism> board;

    private final List<Species> species;

    public GameState(long seed, List<Species> species){
        this.species = species;

        Random rand = new Random(seed);

        this.board = new HashMap<>();

        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < WIDTH; y++) {
                if (rand.nextBoolean()) {
                    int r = rand.nextInt(species.size());
                    Organism org = new Organism(species.get(r), rand.nextDouble());
                    board.put(new Point(x, y), org);
                }
            }
        }
    }

    public void tick() {
        Map<Point, Organism> newBoard = new HashMap<Point, Organism>();
        for(int x = 0; x < HEIGHT; x++) {
            for( int y = 0; y < WIDTH; y++) {
                Point location = new Point(x,y);
                Organism thisOrg = board.get(location);
                Set<Organism> neighbors = getNeighbors(location);

                if (thisOrg != null) {
                    Organism newOrg = thisOrg;
                    for (Organism org : neighbors) {
                        newOrg = newOrg.lowerStrength(org.getSpecies().getDamage(thisOrg.getSpecies()));
                    }

                    // Each cell ages by losing strength.
                    newOrg = newOrg.lowerStrength(newOrg.getSpecies().getTickHealthImpact());

                    if (newOrg.getStrength() > 0.0) {
                        newBoard.put(location, newOrg);
                    }
                } else if (neighbors.size() >= 3 && neighbors.size() <= 5) {
                    Map<Species, Integer> neighborCount = new HashMap<>();
                    for (Organism org : neighbors) {
                        int currentVal = neighborCount.getOrDefault(org.getSpecies(), 0);
                        neighborCount.put(org.getSpecies(), currentVal + 1);
                    }

                    // find the max count
                    int maxCount = Integer.MIN_VALUE;
                    for (int value : neighborCount.values()) {
                        maxCount = Math.max(maxCount, value);
                    }

                    // Then ensure there is only one.
                    Species match = null;
                    Map.Entry<String, Integer> maxEntry = null;
                    for (Map.Entry<Species, Integer> entry : neighborCount.entrySet()) {
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
                        newBoard.put(location, new Organism(match));
                    }
                }
            }
        }

        this.board = newBoard;
    }

    // Null returns if it is not living.
    public Organism getCell(Point point) {
        return board.get(point);
    }

    public List<Species> getSpecies() {
        return species;
    }

    private Set<Organism> getNeighbors(Point point) {
        HashSet<Organism> answer = new HashSet<>();

        for (Direction dir : Direction.values()) {
            Organism org = board.get(neighborPoint(point, dir));
            if (org != null) {
                answer.add(org);
            }
        }
        return answer;
    }

    public static Point neighborPoint(Point point, Direction direction) {
        int newVal;
        switch (direction) {
            case NORTH:
                newVal = (point.x - 1) < 0 ? (GameState.HEIGHT - 1) : point.x - 1;
                return new Point(newVal, point.y);
            case SOUTH:
                newVal = (point.x + 1) == GameState.HEIGHT ? 0 : point.x + 1;
                return new Point(newVal, point.y);
            case WEST:
                newVal = (point.y + 1) == GameState.WIDTH ? 0 : point.y + 1;
                return new Point(point.x, newVal);
            case EAST:
                newVal = (point.y - 1) < 0 ? GameState.WIDTH : point.y - 1;
                return new Point(point.x, newVal);
            case NORTHWEST:
                return neighborPoint(neighborPoint(point, Direction.NORTH), Direction.WEST);
            case NORTHEAST:
                return neighborPoint(neighborPoint(point, Direction.NORTH), Direction.EAST);
            case SOUTHWEST:
                return neighborPoint(neighborPoint(point, Direction.SOUTH), Direction.WEST);
            case SOUTHEAST:
                return neighborPoint(neighborPoint(point, Direction.SOUTH), Direction.EAST);
            default:
        }
        return null;
    }
}
