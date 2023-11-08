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
        Query<Species> q = session.createQuery("FROM Species", Species.class);
        List<Species> speciesList = q.list();

        GameState gameState = new GameState(11877, speciesList);
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
