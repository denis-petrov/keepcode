package com.keepcode.gui;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

public final class StyleUtils {
    private StyleUtils() {}

    private static final String FONT = "SansSerif";

    public static Color getBackgroundColor(int position) {
        return position % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE;
    }

    public static void styleCenterH1Text(JLabel textElem) {
        textElem.setHorizontalAlignment(SwingConstants.CENTER);
        textElem.setFont(new Font(FONT, Font.BOLD, 20));
    }

    public static void styleLeftH3Text(JLabel textElem) {
        textElem.setHorizontalAlignment(SwingConstants.LEFT);
        textElem.setFont(new Font(FONT, Font.BOLD, 14));
    }
}
