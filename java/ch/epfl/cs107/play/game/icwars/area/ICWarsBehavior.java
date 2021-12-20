package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.city.ICWarsCity;
import ch.epfl.cs107.play.game.icwars.handler.ICWarInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ICWarsBehavior extends AreaBehavior {


    //-----------------------------------API-------------------------------------//

    /**
     * Default ICWarsBehavior Constructor
     * @param window (Window) : Window in which the game is launched
     * @param name (String) : Name of the file containing the behavior image
     * */
    public ICWarsBehavior (Window window, String name, ICWarsArea ownerArea) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ICWarsCellType type = ICWarsCellType.toType(getRGB(height-1-y, x));
                setCell(x, y, new ICWarsCell(x, y, type));
            }
        }
    }

    public void registerCity(ICWarsArea area, DiscreteCoordinates position){
        ICWarsCity city = new ICWarsCity(area, position);
        area.registerActor(city);
    }

    /**
     * Enumeration of all different types of cells in the grid
     */
    public enum ICWarsCellType {
        NONE(0, 0, false), //Should be used except in the toType method
        ROAD(-16777216, 0, true),
        PLAIN(-14112955, 1, true),
        WOOD(-65536, 3, true),
        RIVER(-16776961, 0, false),
        MOUNTAIN(-256, 4, true),
        CITY(-1, 2, true);

        final int type;
        final int defenseStars ;
        final boolean walkable ;
        // defenseStars getter
        public int getDefenseStars() {
            return defenseStars;
        }

        /**
         * Default constructor for ICWarsCellType
         * @param type : RGB color on image behavior
         * @param defenseStars : number of defense stars of the cell
         */
        ICWarsCellType(int type, int defenseStars, boolean walkable){
            this.type = type ;
            this.defenseStars = defenseStars ;
            this.walkable = walkable ;
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

        public String typeToString(){
            return name();
        }
    }

    //----------------------------------ICWarsCell-------------------------------------//

    public class ICWarsCell extends AreaBehavior.Cell {

        private final ICWarsCellType type;

        //-----------------------------------API-------------------------------------//
        /**
         * Default ICWarsCell Constructor
         * @param x (int): x coordinate of the cell
         * @param y (int): y coordinate of the cell
         */
        public ICWarsCell(int x, int y, ICWarsCellType type){
            super(x, y);
            this.type = type;
        }

        //Getter for type
        public ICWarsCellType getType() {
            return type;
        }

        public int getDefenseStars(ICWarsCellType type) {
            return type.defenseStars;
        }
        public boolean isWalkable(ICWarsCellType type){
            return type.walkable;
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
            ((ICWarInteractionVisitor) v).interactWith(this);
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
            if((entity.takeCellSpace() && cellSpaceTaken) || (!isWalkable(type) && entity.takeCellSpace())) return false ;
            return true ;
        }
    }
}
