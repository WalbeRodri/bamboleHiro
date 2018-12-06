package br.ufpe.cin.bambolehiro;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Utils {

    public static BitmapFont createFont(FreeTypeFontGenerator generator, int size, Color colorName) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = colorName;
        parameter.borderWidth = 2;
        return generator.generateFont(parameter);
    }
}
