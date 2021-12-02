package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.Unit ;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsPlayerGUI implements Graphics {
    private ICWarsPlayer player ;

    /**
     * Default constructor for ICWarsPlayerGUI
     * @param cameraScaleFactor : not important
     * @param player : Player whose turn it is
     */
    public ICWarsPlayerGUI(float cameraScaleFactor, ICWarsPlayer player){
        this.player = player;
    }

    /**
     * Alternate draw method that allows a Unit selectedUnit in parameter
     * in order to avoid intrusive getters in RealPlayer.java
     * This method calls drawRangeAndPathTo of ilts player until its
     * selected unit.
     * @param canvas : canvas
     * @param selectedUnit
     */
    public void draw(Canvas canvas, Unit selectedUnit) {
        int x = (int) player.getPosition().x ;
        int y = (int) player.getPosition().y ;
        selectedUnit.drawRangeAndPathTo(new DiscreteCoordinates(x, y), canvas);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
