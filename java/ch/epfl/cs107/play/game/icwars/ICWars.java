package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
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
    //List of players waiting for next round
    private ArrayList<ICWarsPlayer> nextRoundPlayers ;
    //List of players waiting for current round
    private ArrayList<ICWarsPlayer> currentRoundPlayers ;

    private ICWarsPlayer currentPlayer;
    private ICWarsRoundState currentRoundState ;

    /**
     * Add all the areas
     */
    private void createAreas(){
        addArea(new Level0());
        addArea(new Level1());
    }

    @Override
    public void update(float deltaTime) {
         Keyboard keyboard = getWindow().getKeyboard();

         //Resetting game is key "N" is pressed
        if(keyboard.get(Keyboard.N).isPressed()) {
            changeArea();
        }

        //Resetting game if key "R" is pressed
        if (keyboard.get(Keyboard.R).isPressed()) {
            resetGame();
        }


        super.update(deltaTime);
        updateRoundState();
    }

    /**
     * Enumeration of all possible states of a round :
     * INIT, CHOOSE_PLAYER, START_PLAYER_TURN, PLAYER_TURN, EN_PLAYER_TURN,
     * END_TURN, END
     */
    private enum ICWarsRoundState{
        INIT,
        CHOOSE_PLAYER,
        START_PLAYER_TURN,
        PLAYER_TURN,
        END_PLAYER_TURN,
        END_TURN,
        END
    }

    private void updateRoundState(){
        switch(currentRoundState) {
            case INIT:
                currentRoundPlayers = new ArrayList<ICWarsPlayer>();
                nextRoundPlayers= new ArrayList<ICWarsPlayer>();
                currentRoundPlayers.addAll(players);
                currentRoundState = ICWarsRoundState.CHOOSE_PLAYER ;
                break;
            case CHOOSE_PLAYER:
                if(currentRoundPlayers.size() == 0) currentRoundState = ICWarsRoundState.END_TURN;
                else {
                    currentPlayer = currentRoundPlayers.get(0);
                    currentRoundPlayers.remove(currentPlayer);
                    currentRoundState = ICWarsRoundState.START_PLAYER_TURN;
                }
                break;
            case START_PLAYER_TURN:
                if (currentPlayer.isVanquished()) currentRoundState = ICWarsRoundState.END_TURN;
                currentPlayer.startTurn();
                currentRoundState = ICWarsRoundState.PLAYER_TURN;
                break;
            case PLAYER_TURN:
              if (currentPlayer.getCurrentPlayerState() == ICWarsPlayer.ICWarsPlayerState.IDLE) currentRoundState = ICWarsRoundState.END_PLAYER_TURN;
                break;
            case END_PLAYER_TURN:
                if(currentPlayer.isVanquished()) {
                    getCurrentArea().unregisterActor(currentPlayer);
                }
                else {
                    nextRoundPlayers.add(currentPlayer);
                    currentPlayer.setUnitsAvailable();
                }
                currentRoundState = ICWarsRoundState.CHOOSE_PLAYER;
                break;
            case END_TURN:
                controlPlayers();
                if (players.size() == 1) currentRoundState = ICWarsRoundState.END;
                else {
                    currentRoundPlayers.addAll(nextRoundPlayers);
                    nextRoundPlayers.clear();
                    currentRoundState = ICWarsRoundState.CHOOSE_PLAYER;
                }
                break;
            case END:
                changeArea();
        }
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
            currentRoundState = ICWarsRoundState.INIT;
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
        Tank tank1 = new Tank(area, Tank.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
        Tank tank2 = new Tank(area, Tank.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
        Soldier soldier1 = new Soldier(area, Soldier.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
        Soldier soldier2 = new Soldier(area, Soldier.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
        players.add(new RealPlayer(area, area.getAllySpawnCoordinates(), ICWarsActor.Faction.ALLY, tank1, soldier1));
        players.add(new AIPlayer(area, area.getEnemySpawnCoordinates(), ICWarsActor.Faction.ENEMY, tank2, soldier2));
        players.get(0).enterArea(area, area.getAllySpawnCoordinates());
        players.get(1).enterArea(area, area.getEnemySpawnCoordinates());
    }

    /**
     * Changing area to the following level, and
     * ending game if final level has been reached
     */
    protected void changeArea() {
        if(getCurrentArea().getTitle() == "icwars/Level0") {
            ICWarsArea currentArea = (ICWarsArea) setCurrentArea("icwars/Level1", false);
            currentArea.clearUnitList();
            players = new ArrayList<ICWarsPlayer>();

            DiscreteCoordinates [] coords = {currentArea.getAllySpawnCoordinates(), currentArea.getEnemySpawnCoordinates()};
            ICWarsActor.Faction [] factions = {ICWarsActor.Faction.ALLY, ICWarsActor.Faction.ENEMY};
            Tank [] tanks = new Tank[2];
            Soldier [] soldiers = new Soldier[2];

            for (int i = 0; i < 2 ; i++) {
                Tank tank = new Tank(currentArea, Tank.getSpawnCoordinates(factions[i]), factions[i]);
                tanks[i] = tank;
                    Soldier soldier = new Soldier(currentArea, Soldier.getSpawnCoordinates(factions[i]), factions[i]);
                soldiers[i] = soldier;
                players.add(i, new RealPlayer(currentArea, coords[i], factions[i], tanks[i], soldiers[i]));
                players.get(i).enterArea(currentArea, coords[i]);
            }
            currentRoundState = ICWarsRoundState.INIT;
            currentPlayer.startTurn();
        } else end();
    }

    /**
     * Method to call at the end of each turn on order to check
     * how many players left there are in the game
     */
    public void controlPlayers() {
        ArrayList<ICWarsPlayer> playersToRemove = new ArrayList<ICWarsPlayer>();
        for (ICWarsPlayer player : players) {
            if (player.isVanquished()) {
                nextRoundPlayers.remove(player);
                playersToRemove.add(player);
            }
        }
        players.removeAll(playersToRemove);
    }


    @Override
    public void end() {
        System.out.println("Game Over");
        System.exit(0);
    }

    /**
     * Resets game
     */
    public void resetGame(){
        createAreas();
        players = new ArrayList<ICWarsPlayer>();
        currentRoundState = ICWarsRoundState.INIT;
        initArea("icwars/Level0");
    }

    /**
     * Private function used in begin() method to randomly choose the player which starts
     */
    private int random() {
        return (int) Math.floor(2*Math.random());
    }
}

