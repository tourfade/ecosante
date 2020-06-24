package com.kamitsoft.ecosante.constant;

public enum OperatorType {
    //-1-> !=; 0-> ==;1 1-> <; 2-> >; 3-> <=; 4-> >=;
    EQUAL(0, 0,"="),//1
    DIFF(1,-1, "<>"), //0
    LT(2,1,"<"),//2
    GT(3,2,">"),
    LET(4,3,"<="),
    GET(5, 4,">=");//-1
    public final int  operator;
    public final String title;
    public final int index;

    OperatorType(int index, int op, String title){
        this.index = index;
        this.operator = op;
        this.title = title;
    }

    public static OperatorType of(int v){
        for (OperatorType g: values()) {
            if (g.operator == v){
                return g;
            }
        }
        return EQUAL;
    }
    public static int indexOf(int v){
        for (OperatorType g: values()) {
            if (g.operator == v){
                return g.index;
            }
        }
        return EQUAL.index;
    }


}
