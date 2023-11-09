package com.dolthub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.query.Query;

public class App implements DatabaseInterface {

    //StatelessSession session;
    private Session currentBranchSession;
    private final Map<String, Session> branchSessions = new HashMap<>();

    private final GuiMainWindow window;

    private App() {
        this.currentBranchSession = HibernateUtil.getSessionForDefaultBranch();
        String activeBranch = activeBranch();
        branchSessions.put(activeBranch, this.currentBranchSession);

        window = new GuiMainWindow(fullDataReload(), this);
    }

    private GameState fullDataReload() {
        Query<Species> q1 = currentBranchSession.createQuery("FROM Species", Species.class);
        List<Species> speciesList = q1.list();

        Query<DaoSeed> q2 = currentBranchSession.createQuery("FROM DaoSeed", DaoSeed.class);
        List<DaoSeed> seedList = q2.list();
        int seed = new Random().nextInt();
        if (seedList.size() != 1) {
            // TODO handle this.
            System.out.println("no seed in db, or more than 1");
        } else {
            seed = seedList.get(0).getSeed();
        }
        return new GameState(seed, speciesList);
    }

    public static void main( String[] args ) {
        App app = new App();
    }

    @Override
    public void speciesUpdated(Species species) {
        currentBranchSession.beginTransaction();
        currentBranchSession.persist(species);
        currentBranchSession.getTransaction().commit();

        System.out.println("We got a persister call: " + species);
    }

    @Override
    public void checkout(String branch) {
        Session session = branchSessions.get(branch);
        if (session == null) {
            session = HibernateUtil.getSessionFactoryForBranch(branch);
            branchSessions.put(branch, session);
        }
        currentBranchSession = session;

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
    public void commit(String message) {
        Query<String> q = currentBranchSession.createNativeQuery("call dolt_commit('-A','-m','test test test')", String.class);
        String hash = q.getSingleResult();

        System.out.println(hash);
    }
}