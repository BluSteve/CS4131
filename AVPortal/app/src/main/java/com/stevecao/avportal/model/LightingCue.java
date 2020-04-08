package com.stevecao.avportal.model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LightingCue {
    public final static int FAST = 300;
    public final static int NORMAL = 1000;
    public final static int SLOW = 3000;
    Random r = new Random();
    private int faderNo;
    private int destination, timeTaken;
    private boolean goingUp;

    public LightingCue(ArrayList<Integer> possibleFaderNo, ArrayList<Integer> currents) {
        faderNo = possibleFaderNo.get(r.nextInt(possibleFaderNo.size()));
        if (r.nextBoolean() || Collections.min(currents)<1) {
            int maxc = Collections.max(currents);
            this.destination = r.nextInt((100 - maxc) + 1) + maxc;
            goingUp = true;
        } else {
            Log.d("currents", currents.toString());
            this.destination = r.nextInt(Collections.min(currents));
            goingUp = false;
        }
        this.timeTaken = (new int[]{FAST, NORMAL, SLOW})[r.nextInt(3)];
    }

    public String formattedLightingCue() {
        String s = "";
        if (goingUp) {
            switch (timeTaken) {
                case FAST:
                    s = faderNo + "↑↑" + destination;
                case NORMAL:
                    s = faderNo + "↑" + destination;
                case SLOW:
                    s = faderNo + "▲" + destination;
            }
        } else {
            switch (timeTaken) {
                case FAST:
                    s = faderNo + "↓↓" + destination;
                case NORMAL:
                    s = faderNo + "↓" + destination;
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
