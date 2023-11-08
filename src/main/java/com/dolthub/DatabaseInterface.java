package com.dolthub;

import java.util.List;

public interface DatabaseInterface {

    abstract public void speciesUpdated(Species species);

    abstract public void reset(String commitId);

    abstract public void checkout(String branch);

    abstract public List<Branch> branches();

    abstract public String activeBranch();

    abstract public boolean dirtyWorkspace();

}
