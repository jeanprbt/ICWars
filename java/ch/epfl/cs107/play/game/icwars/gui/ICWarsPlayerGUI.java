package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit ;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsPlayerGUI implements Graphics {
    private ICWarsPlayer player ;
    private ICWarsActionsPanel actionsPanel;
    private ICWarsInfoPanel infoPanel;
    public final static float FONT_SIZE = 20.f;

    /**
     * Default constructor for ICWarsPlayerGUI
     * @param cameraScaleFactor : not important
     * @param player : Player whose turn it is
     */
    public ICWarsPlayerGUI(float cameraScaleFactor, ICWarsPlayer player){
        infoPanel = new ICWarsInfoPanel(ICWars.CAMERA_SCALE_FACTOR);
        actionsPanel = new ICWarsActionsPanel(ICWars.CAMERA_SCALE_FACTOR);
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

    public void draw(Canvas canvas, Unit selectedUnit, boolean bool){
        actionsPanel.setActions(selectedUnit.actionsList);
        actionsPanel.draw(canvas);
    }

    public void draw(Canvas canvas, Unit unitOnCell, ICWarsBehavior.ICWarsCellType type) {
        infoPanel.setUnit(unitOnCell);
        infoPanel.setCurrentCell(type);
        infoPanel.draw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
