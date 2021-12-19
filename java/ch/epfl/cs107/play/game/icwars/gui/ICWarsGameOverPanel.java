package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ShapeGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.math.Polygon;
import ch.epfl.cs107.play.math.Shape;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

public class ICWarsGameOverPanel implements Graphics {

    private final ShapeGraphics background;
    private final float fontSize;
    private TextGraphics[] lines;

    public ICWarsGameOverPanel(float cameraScaleFactor) {
        final float height = cameraScaleFactor / 7;
        final float width = cameraScaleFactor / 3;
        lines = new TextGraphics[2];
        fontSize = cameraScaleFactor / ICWarsPlayerGUI.FONT_SIZE;

        Shape rect = new Polygon(0, 0, 0, height, width, height, width, 0);
        background = new ShapeGraphics(rect, Color.BLACK, Color.BLACK, 0f, 1f, 3000f);

        TextGraphics text1 = new TextGraphics("Game Over ! ", fontSize, Color.WHITE, null, 0.0f,
                false, false, new Vector(3, 0f * fontSize - 0.35f),
                TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 1.0f, 3001f);

        TextGraphics text2 = new TextGraphics("New Game (G) - Quit (Q)", fontSize, Color.WHITE, null, 0.0f,
                false, false, new Vector(0, -1f * fontSize - 0.35f),
                TextAlign.Horizontal.LEFT, TextAlign.Vertical.MIDDLE, 1.0f, 3001f);

        text1.setFontName("Kenney Pixel");
        text2.setFontName("Kenney Pixel");

        lines[0] = text1;
        lines[1] = text2;
    }




    @Override
    public void draw(Canvas canvas) {
        final Transform transform = Transform.I.translated(canvas.getPosition().add(-2, 0));
        background.setRelativeTransform(transform);
        background.draw(canvas);

        final Transform textTransform = Transform.I.translated(canvas.getPosition().add(-2, 2));
        for (TextGraphics text : lines) {
            text.setRelativeTransform(textTransform);
            text.draw(canvas);
        }
    }
}
