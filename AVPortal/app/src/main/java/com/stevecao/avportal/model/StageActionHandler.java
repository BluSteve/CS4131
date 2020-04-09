package com.stevecao.avportal.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class StageActionHandler {
    private static ArrayList<StageAction> stageActions = new ArrayList<>(0);
    static Context context;

    public static void updateActionsFromCache(Context context) {
        StageActionHandler.context = context;
        stageActions.clear();
        File file = new File(context.getExternalFilesDir(null),
                "stageActions.txt");
        if (file.exists()) {
            try {
                Scanner s = new Scanner(file);
                while (s.hasNext()) {
                    String line= s.nextLine();
                    String[] inline = line.split("\\|");
                    Log.d("stageinline", line);
                    StageAction sa = new StageAction(inline[0], inline[1], inline[2], inline[3],
                            Float.parseFloat(inline[4]), Float.parseFloat(inline[5]));
                    stageActions.add(sa);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addAction(StageAction sa) {
        File file = new File(context.getExternalFilesDir(null),
                "stageActions.txt");
        try {
            file.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(file,true));
            out.println(sa.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateActionsFromCache(context);
    }

    public static void updateXYofAction(String id, float x, float y) {
        for (StageAction sa : stageActions) {
            if (sa.getId().equals(id)) {
                sa.setX(x);
                sa.setY(y);
                break;
            }
        }
        File file = new File(context.getExternalFilesDir(null),
                "stageActions.txt");
        try {
            PrintWriter writer = new PrintWriter(file);
            String toWrite = "";
            for (StageAction sa: stageActions) {
                toWrite += sa.toString() + "\n";
            }
            writer.print(toWrite);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAction(String id) {
        for (StageAction sa : stageActions) {
            if (sa.getId().equals(id)) {
                stageActions.remove(sa);
                break;
            }
        }
        File file = new File(context.getExternalFilesDir(null),
                "stageActions.txt");
        try {
            PrintWriter writer = new PrintWriter(file);
            String toWrite = "";
            for (StageAction sa: stageActions) {
                toWrite += sa.toString() + "\n";
            }
            writer.print(toWrite);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<StageAction> getStageActions() {
        return stageActions;
    }

    public static void setStageActions(ArrayList<StageAction> stageActions) {
        StageActionHandler.stageActions = stageActions;
    }
}
