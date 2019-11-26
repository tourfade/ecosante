package com.kamitsoft.ecosante.client;

@FunctionalInterface
public interface HasFinishedSelection{
    /*@param uuid
    Keep reference to the param, it's going to be null
       */
    void onSelectionFinished(String uuid);
}
