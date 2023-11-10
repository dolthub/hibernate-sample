package com.dolthub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.annotations.Parameter;
import org.hibernate.query.Query;

public class App implements DatabaseInterface {

    private Session currentBranchSession;
    private final Map<String, Session> branchSessions = new HashMap<>();
    private final GuiMainWindow window;

    private App() {
        this.currentBranchSession = HibernateUtil.getSessionForDefaultBranch();
        String activeBranch = activeBranch();
        branchSessions.put(activeBranch, this.currentBranchSession);

        window = new GuiMainWindow(fullDataReload(), this);
    }

    public static void main( String[] args ) {
        new App();
    }

    private GameState fullDataReload() {
        Query<PetriDishCell> q = currentBranchSession.createQuery("FROM PetriDishCell", PetriDishCell.class);
        List<PetriDishCell> petridish = q.list();

        Query<Species> q1 = currentBranchSession.createQuery("FROM Species", Species.class);
        List<Species> speciesList = q1.list();

        Query<DaoSeed> q2 = currentBranchSession.createQuery("FROM DaoSeed", DaoSeed.class);
        List<DaoSeed> seedList = q2.list();
        long seed = new Random().nextLong();
        if (seedList.size() != 1) {
            System.out.println("no seed in db, or more than 1. Using a random number.");
        } else {
            seed = seedList.get(0).getSeed();
        }
        return new GameState(this, seed, petridish, speciesList);
    }

    @Override
    public void speciesUpdated(Species species) {
        currentBranchSession.beginTransaction();
        currentBranchSession.persist(species);
        currentBranchSession.getTransaction().commit();

        GameState state = fullDataReload();
        window.newGameState(state);
    }

    @Override
    public void checkout(String branch) {
        if (branch != null) {
            Session session = branchSessions.get(branch);
            if (session == null) {
                session = HibernateUtil.getSessionFactoryForBranch(branch);
                branchSessions.put(branch, session);
            }
            currentBranchSession = session;
        }

        GameState state = fullDataReload();
        window.newGameState(state);
    }

    @Override
    public List<Branch> branches() {
        Query<Branch> q = currentBranchSession.createNativeQuery("select name,hash from dolt_branches", Branch.class);
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
    public Map<PetriDishPrimaryKey, PetriDishCell> updateBoard(Map<PetriDishPrimaryKey, PetriDishCell> sessionStateObjects, Map<PetriDishPrimaryKey, PetriDishCell> detachedObjects) {
        Map<PetriDishPrimaryKey, PetriDishCell> result = new HashMap<>();

        currentBranchSession.beginTransaction();
        // Any cells which are in the before, but not after are deletes.
        for (PetriDishPrimaryKey key : sessionStateObjects.keySet()){
            if (!detachedObjects.containsKey(key)){
                currentBranchSession.remove(sessionStateObjects.get(key));
            }
        }

        // For any cells which are in both, we copy values into the session objects and persist them
        // For any new cell, we persist directly.
        for (Map.Entry<PetriDishPrimaryKey, PetriDishCell> entry : detachedObjects.entrySet()) {
            if(sessionStateObjects.containsKey(entry.getKey())){
                PetriDishCell sessionObj = sessionStateObjects.get(entry.getKey());
                sessionObj.setSpecies(entry.getValue().getSpecies());
                sessionObj.setStrength(entry.getValue().getStrength());

                currentBranchSession.persist(sessionObj);
                result.put(entry.getKey(), sessionObj);
            } else {
                currentBranchSession.persist(entry.getValue());
                result.put(entry.getKey(), entry.getValue());
            }
        }

        currentBranchSession.getTransaction().commit();

        return result;
    }

    @Override
    public void persistSeed(long newSeed) {
        Query<DaoSeed> qry = currentBranchSession.createQuery("FROM DaoSeed", DaoSeed.class);
        List<DaoSeed> seedList = qry.list();

        if (seedList.size() > 1) {
            System.out.println("Too many entries in the seed table.");
            return;
        }

        // could be a no op.
        if (seedList.size() == 1 && seedList.get(0).getSeed() == newSeed) {
            return;
        }

        currentBranchSession.beginTransaction();
        if (seedList.size() == 1) {
            currentBranchSession.remove(seedList.get(0));
        }
        currentBranchSession.persist(new DaoSeed(newSeed));
        currentBranchSession.getTransaction().commit();
    }

    @Override
    public void commit(String author, String message) {
        Query<String> q = currentBranchSession.createNativeQuery("call dolt_commit('-A','-m','"+ message +"','--author', '"+author+"')", String.class);
        String hash = q.getSingleResult();

        System.out.println(hash);

        GameState state = fullDataReload();
        window.newGameState(state);
    }
}