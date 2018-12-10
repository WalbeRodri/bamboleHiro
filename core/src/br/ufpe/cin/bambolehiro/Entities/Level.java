package br.ufpe.cin.bambolehiro.Entities;

import java.util.HashMap;

import br.ufpe.cin.bambolehiro.Constants;

public class Level {

    private HashMap<String, HashMap<String, String>> data;

    public Level() {
        this.data = this.createLevels();
    }

    private HashMap<String, HashMap<String, String>> createLevels() {
        HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>();

        // movements 0 - center, 1 - right, 2 - left

        // sandy&jr
        HashMap<String, String> levelBasic = new HashMap<String, String>();
        levelBasic.put("level", "FASE 1");
        levelBasic.put("velocity", Constants.RING_BASIC_VELOCITY + "");
        levelBasic.put("dropRingDuration", Constants.BASIC_DROP_RING_DURATION + "");
        levelBasic.put("music", "level1.mp3");
        levelBasic.put("musicDuration", "199");
        levelBasic.put("movements", "1,2");
        levelBasic.put("regression", "6");
        data.put("1", levelBasic);

        // e o tchan
        HashMap<String, String> levelMedium = new HashMap<String, String>();
        levelMedium.put("level", "FASE 2");
        levelMedium.put("velocity", Constants.RING_MEDIUM_VELOCITY + "");
        levelMedium.put("dropRingDuration", Constants.MEDIUM_DROP_RING_DURATION + "");
        levelMedium.put("music", "level2.mp3");
        levelMedium.put("musicDuration", "184");
        levelMedium.put("movements", "0,1,2");
        levelMedium.put("regression", "6");
        data.put("2", levelMedium);

        // MC troinha
        HashMap<String, String> levelAdvanced = new HashMap<String, String>();
        levelAdvanced.put("level", "FASE 3");
        levelAdvanced.put("velocity", Constants.RING_ADVANCED_VELOCITY + "");
        levelAdvanced.put("dropRingDuration", Constants.ADVANCED_DROP_RING_DURATION + "");
        levelAdvanced.put("music", "level3.mp3");
        levelAdvanced.put("musicDuration", "276");
        levelAdvanced.put("movements", "0,1,2");
        levelAdvanced.put("regression", "18");
        data.put("3", levelAdvanced);


        return data;
    }

    public HashMap<String, String> getLevelByDifficulty(String difficulty) {
        return this.data.get(difficulty);
    }

}
