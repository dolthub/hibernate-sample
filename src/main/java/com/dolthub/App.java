package com.dolthub;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;

public class App implements Persister {

    Session session;

    private App() {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Query<Species> q1 = session.createQuery("FROM Species", Species.class);
        List<Species> speciesList = q1.list();

        Query<DaoSeed> q2 = session.createQuery("FROM DaoSeed", DaoSeed.class);
        List<DaoSeed> seedList = q2.list();
        int seed = new Random().nextInt();
        if (seedList.size() != 1) {
            // handle this.
            System.out.println("no seed in db, or more than 1");
        } else {
            seed = seedList.get(0).getSeed();
        }

        GameState gameState = new GameState(seed, speciesList);
        GuiMainWindow window = new GuiMainWindow(gameState, this);
    }


    public static void main( String[] args ) {
        App app = new App();
    }


    @Override
    public void speciesUpdated(Species species) {
        session.beginTransaction();
        session.persist(species);
        session.getTransaction().commit();

        System.out.println("We got a persister call: " + species);
    }
}
