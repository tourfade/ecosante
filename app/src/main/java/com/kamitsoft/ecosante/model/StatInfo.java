package com.kamitsoft.ecosante.model;


import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;


public class StatInfo {
    public final int type;
    List<EncounterInfo> encounterInfos;
    public StatInfo(List<EncounterInfo> encounterInfos, int type){
        this.encounterInfos = encounterInfos;
        this.type = type;
    }

    public List<EncounterInfo> getEncounterInfos() {
        return encounterInfos;
    }

    public List<Timestamp> getDate(){
        return  encounterInfos.stream()
                .map(e-> e.getCreatedAt())
                .collect(Collectors.toList());

    }
    public List<Float> getGlycemy(){
        return  encounterInfos.stream()
                              .map(e-> e.getGlycemy())
                              .collect(Collectors.toList());

    }
    public List<Float[]> getSysDias(){
        return  encounterInfos.stream()
                .map(e-> new Float[]{e.getPressureSystolic(),e.getPressureDiastolic()})
                .collect(Collectors.toList());

    }

    public List<Float> getWeight(){
        return  encounterInfos.stream()
                .map(e-> e.getWeight())
                .collect(Collectors.toList());
    }

    public List<Float> getWaistsize(){
        return  encounterInfos.stream()
                .map(e-> e.getWaistSize())
                .collect(Collectors.toList());
    }

    public List<Float> getHeight(){
        return  encounterInfos.stream()
                .map(e-> e.getHeight())
                .collect(Collectors.toList());
    }
}
