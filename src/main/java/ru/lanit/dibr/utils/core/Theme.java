package ru.lanit.dibr.utils.core;

import ru.lanit.dibr.utils.CmdLineConfiguration;

import java.awt.*;

public class Theme {
    public Color backgroundColor = new Color(36, 17, 11);
    public Color backgroundSelectedColor = new Color(77, 95, 114);
    public Color textColor = new Color(214, 203, 176);
    public Color textSelectedColor = new Color(74, 247, 51);

    public String fontName = "Courier New";
    public int fontStyle = Font.PLAIN;
    public int fontSize = CmdLineConfiguration.fontSize;
}
