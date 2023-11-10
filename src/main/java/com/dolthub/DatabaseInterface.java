package com.dolthub;

import java.util.List;
import java.util.Map;

public interface DatabaseInterface {

    abstract public void speciesUpdated(DaoSpecies species);

    abstract public void commit(String author, String message);

    abstract public void checkout(String branch);

    abstract public List<DaoBranch> branches();

    abstract public String activeBranch();

    abstract public boolean dirtyWorkspace();

    abstract public Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> updateBoard(Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> before, Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> after);

    abstract public void persistSeed(long seed);

}
