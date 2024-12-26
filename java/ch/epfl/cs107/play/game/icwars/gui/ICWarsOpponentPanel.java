package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ShapeGraphics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.math.Polygon;
import ch.epfl.cs107.play.math.Shape;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

public class ICWarsOpponentPanel implements Graphics {

    private final ShapeGraphics background;
    private final float fontSize;
    private TextGraphics[] choicesText ;

    //-----------------------------------API-------------------------------------//

    /**
     * Default Dialog Constructor
     */
    public ICWarsOpponentPanel(float cameraScaleFactor) {
        final float height = cameraScaleFactor/6;
        final float width = cameraScaleFactor/3;
        choicesText = new TextGraphics[3];
        fontSize = cameraScaleFactor/ICWarsPlayerGUI.FONT_SIZE;
        TextGraphics text1 = new TextGraphics("Select opponent : ", fontSize, Color.WHITE, null, 0.0f,
                false, false, new Vector(0, 1f*fontSize-0.35f),
                TextAlign.Horizontal.LEFT, TextAlign.Vertical.MIDDLE, 1.0f, 3001f);

        TextGraphics text2 = new TextGraphics("(A)IPlayer", fontSize, Color.WHITE, null, 0.0f,
                false, false, new Vector(0, -0*1.25f*fontSize-0.35f),
                TextAlign.Horizontal.LEFT, TextAlign.Vertical.MIDDLE, 1.0f, 3001f);

        TextGraphics text3 = new TextGraphics("(R)ealPlayer", fontSize, Color.WHITE, null, 0.0f,
                false, false, new Vector(0, -1f*fontSize-0.35f),
                TextAlign.Horizontal.LEFT, TextAlign.Vertical.MIDDLE, 1.0f, 3001f);

        text1.setFontName("Kenney Pixel");
        text2.setFontName("Kenney Pixel");

        choicesText[0] = text1;
        choicesText[1] = text2;
        choicesText[2] = text3;

        Shape rect = new Polygon(0,0, 0,height, width,height, width,0);
        background = new ShapeGraphics(rect, Color.BLACK, Color.BLACK, 0f, 1f, 3000f);
    }

    @Override
    public void draw(Canvas canvas) {
        final Transform transform = Transform.I.translated(canvas.getPosition().add(0, 0));
        background.setRelativeTransform(transform);
        background.draw(canvas);

        final Transform textTransform = Transform.I.translated(canvas.getPosition().add(0, 2));
        for (TextGraphics text : choicesText) {
            text.setRelativeTransform(textTransform);
            text.draw(canvas);
        }
    }
}
