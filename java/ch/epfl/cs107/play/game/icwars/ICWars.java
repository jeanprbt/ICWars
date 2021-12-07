package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.Tank;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.area.Level0;
import ch.epfl.cs107.play.game.icwars.area.Level1;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public class ICWars extends AreaGame {

    public static final float CAMERA_SCALE_FACTOR = 16.f ;

    private ArrayList<ICWarsPlayer> players ;
    private ArrayList<ICWarsPlayer> pastPlayers ;
    private ArrayList<ICWarsPlayer> nextPlayers ;
    private ICWarsPlayer currentPlayer;
    private int counter = 1 ;

    /**
     * Add all the areas
     */
    private void createAreas(){
        addArea(new Level0());
        addArea(new Level1());
    }

    @Override
    public void update(float deltaTime) {
        int index = 0;

        //Changing area if key "N" is pressed
        Keyboard keyboard = getWindow().getKeyboard();
        if (keyboard.get(Keyboard.N).isPressed()) changeArea();

        //Resetting game if key "R" is pressed
        if (keyboard.get(Keyboard.R).isPressed()) begin(getWindow(), getFileSystem());
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
            players = new ArrayList<ICWarsPlayer>();
            pastPlayers = new ArrayList<ICWarsPlayer>();
            nextPlayers = new ArrayList<ICWarsPlayer>();
            initArea("icwars/Level0");
            currentPlayer = players.get(random());
            currentPlayer.startTurn();
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

        DiscreteCoordinates [] coords = {area.getAllySpawnCoordinates(), area.getEnemySpawnCoordinates()};
        ICWarsActor.Faction [] factions = {ICWarsActor.Faction.ALLY, ICWarsActor.Faction.ENEMY};
        Tank [] tanks = new Tank[2];
        Soldier [] soldiers = new Soldier[2];

        for (int i = 0; i < 2 ; i++) {
            Tank tank = new Tank(area, Tank.getSpawnCoordinates(factions[i]), factions[i]);
            tanks[i] = tank;
            Soldier soldier = new Soldier(area, Soldier.getSpawnCoordinates(factions[i]), factions[i]);
            soldiers[i] = soldier;
            players.add(i, new RealPlayer(area, coords[i], factions[i], tanks[i], soldiers[i]));
            players.get(i).enterArea(area, coords[i]);
        }
    }

    /**
     * Changing area to the following level, and
     * ending game if final level has been reached
     */
    protected void changeArea() {
        for (ICWarsPlayer player : players) {
            player.leaveArea();
            if(counter <= players.toArray().length) {
                ICWarsArea currentArea = (ICWarsArea) setCurrentArea("icwars/Level1", false);
                DiscreteCoordinates coordinates = player.getFaction() == ICWarsActor.Faction.ALLY ? currentArea.getAllySpawnCoordinates() : currentArea.getEnemySpawnCoordinates();
                player.enterArea(currentArea, coordinates);
            } else {
                end();
            }
            currentPlayer.startTurn();
            ++counter;
        }
    }

    /**
     * Method to call at the end of each turn on order to check
     * how many players left there are in the game
     */
    public void controlPlayers() {
        for (ICWarsPlayer player : players) {
            if (player.isVanquished()) nextPlayers.remove(player);
        }
    }

    @Override
    public void end() {
        System.out.println("Game Over");
        System.exit(0);
    }

}

