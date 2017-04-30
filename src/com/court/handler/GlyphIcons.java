/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 *
 * @author shanaka
 */
public class GlyphIcons {

    private final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private final GlyphFont icoMoon = GlyphFontRegistry.font("icomoon");

    static {
       GlyphFontRegistry.register("icomoon", GlyphIcons.class.getResourceAsStream("/com/court/asserts/IcoMoon-Free.ttf"), 16);
       GlyphFontRegistry.register("FontAwesome", GlyphIcons.class.getResourceAsStream("/com/court/asserts/fontawesome-webfont.ttf"), 16);
    }

    public Glyph setFontAwesomeIconGlyph(char icon, Color color, double size) {
        Glyph customGlyph = fontAwesome.create(icon);
        customGlyph.setColor(color);
        customGlyph.setFontSize(size);
        return customGlyph;
    }

    public Glyph setIcoMoonGlyph(char icon, Color color, double size) {
        Glyph customGlyph = icoMoon.create(icon)
                .color(color)
                .size(size)
                .useHoverEffect();
        return customGlyph;

    }
}
