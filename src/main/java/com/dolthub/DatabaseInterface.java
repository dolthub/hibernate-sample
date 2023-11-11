package com.dolthub;

import java.util.List;
import java.util.Map;

/**
 * All read and write operations for the Petri Dish Application. This interface is effectively passed
 * to all parts of the application to manage state.
 */
public interface DatabaseInterface {

    abstract public void speciesUpdate(DaoSpecies species);

    /**
     * Create a Dolt commit for all changes in the current session. This is equivalent to the
     * CLI execution of: `dolt commit -A -m "message" --author "author"
     */
    abstract public void commit(String author, String message);

    /**
     * Switch to another branch. Branch name must match a value provided by the `branches` method.
     *
     * Implementation note - this must make use of a unique session for each branch. The use
     * of the `dolt_checkout()` stored procedure confuses Hibernate's object cache,
     *
     * @param branch Branch name to check out. Null to use the default branch.
     */
    abstract public void checkout(String branch);

    /**
     * Retrieve all branches. Dolt branches are not session specific, and the current
     * HEAD of each is returned, so it may not reflect uncommitted changes in each branch.
     */
    abstract public List<DaoBranch> branches();

    /**
     * Local branch name for the current session. eg. "main"
     *
     * @return name of the branch for the current session
     */
    abstract public String activeBranch();

    /**
     * Query the Database to determine if there is state which has not been committed
     * to the dolt commit history.
     *
     * @return true is there are uncommitted changes, false otherwise.
     */
    abstract public boolean dirtyWorkspace();

    /**
     * Update the persisted board to match the current in memory board state.
     *
     * @param before state of the board at the last time it was loaded or persisted. This
     *               map consists of objects which Hibernate manages
     * @param after state of the in memory board. This map consists strictly of detached objects.
     * @return the new state of the board. This map contains the Hibernate manages ojbects, and it
     *         will be used for the next updateBoard call as the `before` input.
     */
    abstract public Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> updateBoard(Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> before, Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> after);

    /**
     * Update the seed table with a new value. The Seed table consists of a single column row
     * which is a `bigint`. persistSeed will overwrite any value which is currently there. If the
     * current value matches the new value, no query will be performed.
     *
     * @param seed new seed value
     */
    abstract public void persistSeed(long seed);
}
