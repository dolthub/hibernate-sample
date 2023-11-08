package com.dolthub;

import java.util.List;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class App implements DatabaseInterface {

    Session session;

    private final GuiMainWindow window;

    private App() {
        this.session = HibernateUtil.getSessionFactory().openSession();

        window = new GuiMainWindow(fullDataReload(), this);
    }

    private GameState fullDataReload() {
        Query<Species> q1 = session.createQuery("FROM Species", Species.class);
        List<Species> speciesList = q1.list();

        Query<DaoSeed> q2 = session.createQuery("FROM DaoSeed", DaoSeed.class);
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
        session.beginTransaction();
        session.persist(species);
        session.getTransaction().commit();

        // TODO - Dolt Commit.

        System.out.println("We got a persister call: " + species);
    }

    @Override
    public void reset(String commit) {
        System.out.println("Unimplemented: reset to " + commit);
    }

    @Override
    public void checkout(String branch) {
        // Checkout requires a full restart of the game, effectively.

        Query<CheckoutResult> q = session.createNativeQuery("call dolt_checkout('" +  branch + "')", CheckoutResult.class);
        CheckoutResult result = q.getSingleResult();

        if (result.success()) {
            GameState state = fullDataReload();
            window.newGameState(state);


        } else {
            System.out.println("Error when checking out:" + result.getMessage());
        }
    }

    @Override
    public List<Branch> branches() {
        Query<Branch> q = session.createNativeQuery("select name,hash from dolt_branches", Branch.class);
        return q.list();
    }

    @Override
    public String activeBranch() {
        Query<String> q = session.createNativeQuery("select active_branch()",String.class);
        return q.getSingleResult();
    }

    @Override
    public boolean dirtyWorkspace() {
        // This query will always return one row as a result, true if dirty, false otherwise.
        Query<Boolean> q = session.createNativeQuery("select count(table_name) > 0 as dirty from  dolt_diff_stat(active_branch(),'WORKING')", Boolean.class);
        return q.getSingleResult();
    }


}
