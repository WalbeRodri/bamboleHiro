package br.ufpe.cin.bambolehiro.Entities;

import com.badlogic.gdx.utils.ObjectMap;

import br.ufpe.cin.bambolehiro.Constants;

public class Level {

    private ObjectMap<String, ObjectMap<String, String>> data;

    public Level() {
        this.data = this.createLevel();
    }

    private ObjectMap<String, ObjectMap<String, String>> createLevel() {
        ObjectMap<String, ObjectMap<String, String>> data = new ObjectMap<String, ObjectMap<String, String>>();

        // movements 0 - center, 1 - right, 2 - left
        ObjectMap<String, String> levelBasic = new ObjectMap<String, String>();
        levelBasic.put("level", "FASE 1");
        levelBasic.put("velocity", Constants.RING_BASIC_VELOCITY + "");
        levelBasic.put("dropRingDuration", Constants.BASIC_DROP_RING_DURATION + "");
        levelBasic.put("music", "level1.mp3");
        levelBasic.put("musicDuration", "199");
        levelBasic.put("movements", "1,2");
        levelBasic.put("regression", "6");
        data.put("1", levelBasic);

        ObjectMap<String, String> levelAdvanced = new ObjectMap<String, String>();
        levelBasic.put("level", "FASE 3");
        levelBasic.put("velocity", Constants.RING_MEDIUM_VELOCITY + "");
        levelBasic.put("dropRingDuration", Constants.MEDIUM_DROP_RING_DURATION + "");
        levelBasic.put("music", "level3.mp3");
        levelBasic.put("musicDuration", "184");
        levelBasic.put("movements", "0,1,2");
        levelBasic.put("regression", "6");
        data.put("3", levelAdvanced);

        ObjectMap<String, String> levelMedium = new ObjectMap<String, String>();
        levelBasic.put("level", "FASE 2");
        levelBasic.put("velocity", Constants.RING_ADVANCED_VELOCITY + "");
        levelBasic.put("dropRingDuration", Constants.ADVANCED_DROP_RING_DURATION + "");
        levelBasic.put("music", "level2.mp3");
        levelBasic.put("musicDuration", "265");
        levelBasic.put("movements", "0,1,2");
//        levelBasic.put("regression", "18");
        levelBasic.put("regression", "5");
        data.put("2", levelMedium);

        return data;
    }

    public ObjectMap<String, String> getLevelByDifficulty(String difficulty) {
        return this.data.get(difficulty);
    }

}
