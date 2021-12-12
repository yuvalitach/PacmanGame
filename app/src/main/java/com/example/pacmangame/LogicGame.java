package com.example.pacmangame;

import java.util.Arrays;
import java.util.Random;

public class LogicGame {

    private int[][] values;
    private boolean newMonster;
    public LogicGame(int cols, int rows) {
        values = new int[cols][rows];
        newMonster = true;
        initializationArray();
    }

    public int[][] getValues() {
        return values;
    }

    public LogicGame setValues(int[][] values) {
        this.values = values;
        return this;
    }

    private void initializationArray() {
        for (int[] value : values) Arrays.fill(value, 0);
    }

    public void runLogic() {
        for (int i = values.length - 1; i > 0; i--) {
            System.arraycopy(values[i - 1], 0, values[i], 0, values[i].length - 1 + 1);
        }
        Arrays.fill(values[0], 0);
    }

    public void randNumber(int charctersLength, int mainCharacterLength ) {
        int randomNum, type;
        type = new Random().nextInt(charctersLength + 1);
        if (type == 0)
            type++;
        if (newMonster) {
            randomNum = new Random().nextInt(mainCharacterLength);
            values[0][randomNum] = type;
            newMonster = false;
        } else
            newMonster = true;
    }

}



