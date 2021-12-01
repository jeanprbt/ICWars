package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.Tank;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.area.Level0;
import ch.epfl.cs107.play.game.icwars.area.Level1;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.tutosSolution.area.Tuto2Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

public class ICWars extends AreaGame {

    public static final float CAMERA_SCALE_FACTOR = 16.f ;

    private RealPlayer player;

    /**
     * Add all the areas
     */
    private void createAreas(){
        addArea(new Level0());
        addArea(new Level1());
    }

    @Override
    public void update(float deltaTime) {

        //Changing area if key "N" is pressed
        Keyboard keyboard= getWindow().getKeyboard();
        Button key = keyboard.get(Keyboard.N);
        if(key.isPressed()) changeArea();

        //Resetting game if key "R" is pressed
        key = keyboard.get(Keyboard.R);
        if(key.isPressed()) begin(getWindow(), getFileSystem());
        super.update(deltaTime);
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            initArea("icwars/Level0");
            return true;
        }
        return false;
    }

    /**
     * Initialises area by creating all that needs to be initialised
     * @param areaKey : key area
     */
    private void initArea(String areaKey) {
        ICWarsArea area = (ICWarsArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getPlayerSpawnCoordinates();
        Tank tank1 = new Tank(area, new DiscreteCoordinates(2, 5), ICWarsActor.Faction.ALLY);
        Soldier soldier1 = new Soldier(area, new DiscreteCoordinates(3, 5), ICWarsActor.Faction.ALLY);
        player = new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, tank1, soldier1);
        player.enterArea(area, coords);
    }

    /**
     * Changing area to the following level, and
     * ending game if final level has been reached
     */
    protected void changeArea() {
        player.leaveArea();
        if(getCurrentArea().getTitle() == "icwars/Level0") {
            ICWarsArea currentArea = (ICWarsArea) setCurrentArea("icwars/Level1", false);
            player.enterArea(currentArea, currentArea.getPlayerSpawnCoordinates());
        } else {
            end();
        }
    }

    @Override
    public void end() {
        System.out.println("Game Over");
    }

}

