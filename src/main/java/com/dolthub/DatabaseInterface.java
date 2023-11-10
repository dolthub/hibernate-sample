package com.dolthub;

import java.util.List;
import java.util.Map;

public interface DatabaseInterface {

    abstract public void speciesUpdated(Species species);

    abstract public void commit(String message);

    abstract public void checkout(String branch);

    abstract public List<Branch> branches();

    abstract public String activeBranch();

    abstract public boolean dirtyWorkspace();

    abstract public Map<PetriDishPrimaryKey, PetriDishCell> updateBoard(Map<PetriDishPrimaryKey, PetriDishCell> before, Map<PetriDishPrimaryKey, PetriDishCell> after);

}
