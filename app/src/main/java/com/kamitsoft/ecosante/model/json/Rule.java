package com.kamitsoft.ecosante.model.json;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private List<IsRule> ruleLines;

    public Rule(){
        ruleLines = new ArrayList<>();
    }
    public void push(IsRule r){
        ruleLines.add(r);
    }

}
