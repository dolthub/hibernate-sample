package com.dolthub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import jakarta.persistence.EntityExistsException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * main() Class for the Dolt Petri Dish Hibernate Sample App.
 *
 * This class delegates all application logic to other classes, except for the implementation of
 * the DatabaseInterface class. All interactions with the database are in this class, while the specification
 * of what each interface done is in the interface class.
 */
public class PetriDishMain implements DatabaseInterface {
    /**
     * currentBranchSession is the open connection to a running Dolt database. Hibernate expects some stability
     * of the data schema so in order to switch the branch being uses, a unique session is used for each branch.
     */
    private Session currentBranchSession;

    /**
     * Open sessions. These are lazily loaded as the user switches branches.
     */
    private final Map<String, Session> branchSessions = new HashMap<>();

    private final GuiMainWindow window;

    private PetriDishMain() {
        this.currentBranchSession = HibernateUtil.getSessionForDefaultBranch();
        String activeBranch = activeBranch();
        this.branchSessions.put(activeBranch, this.currentBranchSession);

        this.window = new GuiMainWindow(fullDataReload(), this);
    }

    public static void main( String[] args ) {
        new PetriDishMain();
    }

    /**
     * Load all necessary game state with a full load of all tables.
     *
     * @return GameState
     */
    private GameState fullDataReload() {
        currentBranchSession.clear();

        Query<DaoPetriDishCell> dishCells = currentBranchSession.createQuery("FROM DaoPetriDishCell", DaoPetriDishCell.class);
        List<DaoPetriDishCell> petridish = dishCells.list();

        Query<DaoSpecies> allSpecies = currentBranchSession.createQuery("FROM DaoSpecies", DaoSpecies.class);
        List<DaoSpecies> speciesList = allSpecies.list();

        Query<DaoSeed> seedQuery = currentBranchSession.createQuery("FROM DaoSeed", DaoSeed.class);
        List<DaoSeed> seedList = seedQuery.list();
        long seed = new Random().nextLong();
        if (seedList.size() != 1) {
            System.out.println("no seed in db, or more than 1. Using a random number.");
        } else {
            seed = seedList.get(0).getSeed();
        }
        return new GameState(this, seed, petridish, speciesList);
    }

    @Override
    public void speciesUpdate(DaoSpecies species) {
        currentBranchSession.beginTransaction();
        currentBranchSession.persist(species);
        currentBranchSession.getTransaction().commit();

        window.refreshUncommitedMessage();
    }

    @Override
    public void checkout(String branch) {
        if (branch != null && !branch.isEmpty()) {
            Session session = branchSessions.get(branch);
            if (session == null) {
                session = HibernateUtil.getSessionForBranch(branch);
                branchSessions.put(branch, session);
            }
            currentBranchSession = session;
        }

        window.newGameState(fullDataReload());
    }

    @Override
    public List<DaoBranch> branches() {
        Query<DaoBranch> q = currentBranchSession.createNativeQuery("select name,hash from dolt_branches", DaoBranch.class);
        return q.list();
    }

    @Override
    public String activeBranch() {
        Query<String> q = currentBranchSession.createNativeQuery("select active_branch()",String.class);
        return q.getSingleResult();
    }

    @Override
    public boolean dirtyWorkspace() {
        // This query will always return one row as a result, true if dirty, false otherwise.
        Query<Boolean> q = currentBranchSession.createNativeQuery("select count(table_name) > 0 as dirty from  dolt_diff_stat(active_branch(),'WORKING')", Boolean.class);
        return q.getSingleResult();
    }

    @Override
    public Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> updateBoard(Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> sessionStateObjects, Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> detachedObjects) {
        Map<DaoPetriDishPrimaryKey, DaoPetriDishCell> result = new HashMap<>();

        // This is unfortunate. Honestly I was trying very hard to avoid this. Seems to impact performance significantly,
        // but there was a persistent error I couldn't find the cause for stating that there were duplicate objects.
        // This works, but is slow.
        currentBranchSession.clear();

        Transaction trans = currentBranchSession.beginTransaction();
        try
            // Any cells which are in the before, but not after, are deletes.
            for (DaoPetriDishPrimaryKey key : sessionStateObjects.keySet()) {
                if (!detachedObjects.containsKey(key) || !(detachedObjects.get(key).getStrength() > 0.0)) {
                    try {

                        currentBranchSession.remove(sessionStateObjects.get(key));

                    } catch (EntityExistsException e) {
                        System.out.println("Swallowing error possition: "+ key.getX() + "   " + key.getY());
                    }

                }
            }

            // For any cells which are in both, we copy values into the session objects and persist them
            // For any new cell, we persist directly.
            for (Map.Entry<DaoPetriDishPrimaryKey, DaoPetriDishCell> entry : detachedObjects.entrySet()) {
                DaoPetriDishCell cell = entry.getValue();
                // Ensure we don't break out DB constraints.
                if (cell.getStrength() > 0.0) {
                    // User beware: Batch processing doesn't seem to work with merge due to a bunch up selects being
                    // required. This seems like a hibernate bug to me. While this works, it is slow when attempting to update
                    // 1K rows or more.
                    currentBranchSession.merge(cell);
                    result.put(cell.getId(), cell);
                }
            }

            currentBranchSession.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            trans.rollback();
        }
        return result;
    }

    @Override
    public void persistSeed(long newSeed) {
        try {
            currentBranchSession.beginTransaction();

            Query<DaoSeed> qry = currentBranchSession.createQuery("FROM DaoSeed", DaoSeed.class);
            List<DaoSeed> seedList = qry.list();

            if (seedList.size() > 1) {
                System.out.println("Too many entries in the seed table.");
                return;
            }

            // could be a no op.
            if (seedList.size() == 1 && seedList.get(0).getSeed() == newSeed) {
                // Why can't cancel? NM4
                currentBranchSession.getTransaction().rollback();
                return;
            }

            if (seedList.size() == 1) {
                currentBranchSession.remove(seedList.get(0));
            }
            currentBranchSession.persist(new DaoSeed(newSeed));
            currentBranchSession.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Bad sitch: " + e);

            currentBranchSession.getTransaction().rollback();
        }
    }

    @Override
    public void commit(String author, String message) {
        // Big fat SQL injection bug here. If you don't trust the input, don't do this.
        // https://bobby-tables.com/
        String cmtTemplate = "call dolt_commit('-A', '-m', '%s', '--author', '%s')";
        String query = String.format(cmtTemplate, message, author);

        Query<String> q = currentBranchSession.createNativeQuery(query, String.class);
        String hash = q.getSingleResult();
        System.out.println("Current state committed as : " +hash);

        window.refreshUncommitedMessage();
    }
}