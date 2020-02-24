package com.kamitsoft.ecosante;

import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.sql.Timestamp;

public class SubscriptionManager {
    private static SubscriptionManager instance;
    private final EcoSanteApp app;
    private final UsersRepository repo;
    private  SubConsumerInfo consumer;
    private  SubInstanceInfo subscription;
    private boolean isExpired;
    private int remainingPhysicians, remainingNurses;
    private int remainingPatients;

    public static SubscriptionManager getInstance(EcoSanteApp app){
        if(instance == null){
            instance = new SubscriptionManager(app);
        }
        return instance;
    }

    private SubscriptionManager(EcoSanteApp app){
        this.app = app;
        this.repo = new UsersRepository(app);
        this.repo.getSubIntanceInfo().observeForever(sub->{
            this.subscription = sub;
            onStateChanger();
        });
        this.repo.getConsumer().observeForever(cons->{
            this.consumer = cons;
            onStateChanger();
        });
    }

    private void onStateChanger(){
        boolean newExpireState = subscription==null
                || (subscription.getValidUntil()!=null
                && subscription.getValidUntil().before(new Timestamp(System.currentTimeMillis())));
        if(newExpireState != isExpired){
            isExpired = newExpireState;

        }
        int newRemaining = subscription==null|| consumer == null ? 0:
                (subscription.getMaxPhysicians() - consumer.getNbPhysicians());
        if(this.remainingPhysicians != newRemaining){
            remainingPhysicians = newRemaining;
        }

        newRemaining = subscription==null|| consumer == null ? 0:
                (subscription.getMaxNurses() - consumer.getNbNurses());

        if(this.remainingNurses != newRemaining){
            remainingNurses = newRemaining;
        }

        newRemaining =  subscription==null|| consumer == null ? 0:
                (subscription.getMaxPatPerNurse() - consumer.getNbPatPerNurse());
        if(this.remainingPatients != newRemaining){
            remainingPatients = newRemaining;
        }

    }
}
