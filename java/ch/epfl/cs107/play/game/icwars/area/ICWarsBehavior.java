package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.tutosSolution.Tuto2Behavior;
import ch.epfl.cs107.play.window.Window;

import javax.swing.*;
import java.util.function.ToDoubleBiFunction;

public class ICWarsBehavior extends AreaBehavior {

    /**
     * Default ICWarsBehavior Constructor
     * @param window (Window) : Window in which the game is launched
     * @param name (String) : Name of the file containing the behavior image
     * */
    public ICWarsBehavior (Window window, String name){
        super(window, name);
    }

    /**
     * Enumeration of all different types of cells in the grid
     */
    public enum ICWarsCellType {
        NONE(0, 0), //Should be used except in the toType method
        ROAD(-16777216, 0),
        PLAIN(-14112955, 1),
        WOOD(-65536, 3),
        RIVER(-16776961, 0),
        MOUNTAIN(-256, 4),
        CITY(-1, 2);

        final int type;
        final int defenseStars ;

        /**
         * Default constructor for ICWarsCellType
         * @param type : RGB color on image behavior
         * @param defenseStars : number of defense stars of the cell
         */
        ICWarsCellType(int type, int defenseStars){
            this.type = type ;
            this.defenseStars = defenseStars ;
        }

        /**
         * Function that returns the type of a cell taking a rgb color in argument
         * @param type : rgbColor of the asked cell
         * @return ICWarsCellType of the asked cell
         */
        public static ICWarsCellType toType(int type){
            for(ICWarsCellType cellType : ICWarsCellType.values()){
                if(cellType.type == type)
                    return cellType;
            }
            System.out.println(type);
            return NONE;
        }
    }

    public class ICWarsCell extends AreaBehavior.Cell {
        /**
         * Default ICWarsCell Constructor
         * @param x (int): x coordinate of the cell
         * @param y (int): y coordinate of the cell
         */
        public ICWarsCell(int x, int y){
            super(x, y);
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity){
            boolean cellSpaceTaken = false ;
            for (Interactable interactable : entities) {
                if (interactable.takeCellSpace()) cellSpaceTaken = true ;
            }
            if(entity.takeCellSpace() && cellSpaceTaken) return false ;
            return true ;
        }


        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v) {
        }

    }
}
