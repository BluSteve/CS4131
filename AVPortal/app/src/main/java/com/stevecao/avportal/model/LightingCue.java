package com.stevecao.avportal.model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LightingCue {
    public final static int FAST = 1000;
    public final static int NORMAL = 2500;
    public final static int SLOW = 4000;
    Random r = new Random();
    private int faderNo;
    private int destination, timeTaken;
    private boolean goingUp;

    public LightingCue(ArrayList<Integer> possibleFaderNo, ArrayList<Integer> currents) {
        faderNo = possibleFaderNo.get(r.nextInt(possibleFaderNo.size()));
        int current = currents.get(faderNo-1);
        if ( current < 30){
            current += 20;
            this.destination = r.nextInt((100 - current) + 1) + current;
            goingUp = true;
        }
        else if (current > 70) {
            current -= 20;
            this.destination = r.nextInt(current);
            goingUp = false;
        }
        else {
            if (r.nextBoolean()) {
                current += 20;
                this.destination = r.nextInt((100 - current) + 1) + current;
                goingUp = true;
            }
            else {
                current -= 20;
                this.destination = r.nextInt(current);
                goingUp = false;
            }
        }
        this.timeTaken = (new int[]{FAST, NORMAL, SLOW})[r.nextInt(3)];
    }

    public String formattedLightingCue() {
        String s = "";
        if (goingUp) {
            switch (timeTaken) {
                case FAST:
                    s = faderNo + "↑↑" + destination;
                    break;
                case NORMAL:
                    s = faderNo + "↑" + destination;
                    break;
                case SLOW:
                    s = faderNo + "▲" + destination;
            }
        } else {
            switch (timeTaken) {
                case FAST:
                    s = faderNo + "↓↓" + destination;
                    break;
                case NORMAL:
                    s = faderNo + "↓" + destination;
                    break;
                case SLOW:
                    s = faderNo + "▼" + destination;
            }
        }
        return s;
    }

    public int getFaderNo() {
        return faderNo;
    }

    public void setFaderNo(int faderNo) {
        this.faderNo = faderNo;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
}
