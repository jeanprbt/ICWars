package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.player.AIPlayer;
import ch.epfl.cs107.play.game.icwars.area.Level0;
import ch.epfl.cs107.play.game.icwars.area.Level1;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsGameOverPanel;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsOpponentPanel;
import ch.epfl.cs107.play.game.icwars.music.AudioFilePlayer;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;

public class ICWars extends AreaGame {

    public static final float CAMERA_SCALE_FACTOR = 16.f;

    private ArrayList<ICWarsPlayer> players;
    private ArrayList<ICWarsPlayer> nextRoundPlayers;
    private ArrayList<ICWarsPlayer> currentRoundPlayers;
    private ICWarsPlayer currentPlayer;
    private ICWarsRoundState currentRoundState;
    private ICWarsOpponentPanel opponentPanel;
    private boolean playerHasBeenSelected = false ;
    private ICWarsGameOverPanel gameOverPanel ;
    private boolean gameOverDisplay = false ;

    //-----------------------------------API-------------------------------------//

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            players = new ArrayList<ICWarsPlayer>();
            currentRoundState = ICWarsRoundState.INIT;
            initArea("icwars/Level0");
            opponentPanel = new ICWarsOpponentPanel(getCurrentArea().getCameraScaleFactor());
            gameOverPanel = new ICWarsGameOverPanel(getCurrentArea().getCameraScaleFactor());
            return true;
        }
        return false;
    }

    @Override
    public void end() {
        System.out.println("Game Over");
        System.exit(0);
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getWindow().getKeyboard();

        //If opponent is not selected
        selectPlayer();

        //When game over happens
        selectEnd();

        //Resetting game is key "N" is pressed
        if (keyboard.get(Keyboard.N).isPressed() && currentPlayer != null) {
            changeArea();
        }
        //Resetting game if key "S" is pressed
        if (keyboard.get(Keyboard.S).isPressed()) {
            resetGame();
        }
        super.update(deltaTime);
        updateRoundState();
        controlPlayers();
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    //-----------------------------------Private-------------------------------------//

    /**
     * Enumeration of all possible states of a round :
     * INIT, CHOOSE_PLAYER, START_PLAYER_TURN, PLAYER_TURN, EN_PLAYER_TURN,
     * END_TURN, END
     */
    private enum ICWarsRoundState {
        INIT,
        CHOOSE_PLAYER,
        START_PLAYER_TURN,
        PLAYER_TURN,
        END_PLAYER_TURN,
        END_TURN,
        END
    }

    private void updateRoundState() {
        switch (currentRoundState) {
            case INIT:
                currentRoundPlayers = new ArrayList<ICWarsPlayer>();
                nextRoundPlayers = new ArrayList<ICWarsPlayer>();
                if(playerHasBeenSelected) {
                    currentRoundPlayers.addAll(players);
                    currentRoundState = ICWarsRoundState.CHOOSE_PLAYER;
                }
                break;
            case CHOOSE_PLAYER:
                if (currentRoundPlayers.size() == 0) currentRoundState = ICWarsRoundState.END_TURN;
                else {
                    currentPlayer = currentRoundPlayers.get(0);
                    currentRoundPlayers.remove(currentPlayer);
                    currentRoundState = ICWarsRoundState.START_PLAYER_TURN;
                }
                break;
            case START_PLAYER_TURN:
                if (currentPlayer.isVanquished()) {
                    currentRoundState = ICWarsRoundState.END_TURN;
                    break;
                }
                currentPlayer.startTurn();
                currentRoundState = ICWarsRoundState.PLAYER_TURN;
                break;
            case PLAYER_TURN:
                if (currentPlayer.getCurrentPlayerState() == ICWarsPlayer.ICWarsPlayerState.IDLE)
                    currentRoundState = ICWarsRoundState.END_PLAYER_TURN;
                break;
            case END_PLAYER_TURN:
                if (currentPlayer.isVanquished()) {
                    getCurrentArea().unregisterActor(currentPlayer);
                } else {
                    nextRoundPlayers.add(currentPlayer);
                    currentPlayer.setUnitsAvailable();
                }
                currentRoundState = ICWarsRoundState.CHOOSE_PLAYER;
                break;
            case END_TURN:
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


    /**
     * Initialises area by creating all that needs to be initialised
     *
     * @param areaKey : key area
     */
    private void initArea(String areaKey) {
        ICWarsArea area = (ICWarsArea) setCurrentArea(areaKey, true);
        Tank tank = new Tank(getCurrentArea(), Tank.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
        Soldier soldier = new Soldier(getCurrentArea(), Soldier.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
        RocketMan rocketMan = new RocketMan(getCurrentArea(), RocketMan.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
        players.add(new RealPlayer(area, area.getAllySpawnCoordinates(), ICWarsActor.Faction.ALLY, tank, soldier, rocketMan));
        players.get(0).enterArea(area, area.getAllySpawnCoordinates());
    }

    /**
     * Changing area to the following level, and
     * ending game if final level has been reached
     */
    private void changeArea() {
        if (getCurrentArea().getTitle() == "icwars/Level0") {
            ICWarsArea area = (ICWarsArea) setCurrentArea("icwars/Level1", false);
            area.clearUnitList();
            playerHasBeenSelected = false ;

            players = new ArrayList<ICWarsPlayer>();
            Tank tank = new Tank(getCurrentArea(), Tank.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
            Soldier soldier = new Soldier(getCurrentArea(), Soldier.getSpawnCoordinates(ICWarsActor.Faction.ALLY), ICWarsActor.Faction.ALLY);
            players.add(new RealPlayer(area, area.getAllySpawnCoordinates(), ICWarsActor.Faction.ALLY, tank, soldier));
            players.get(0).enterArea(area, area.getAllySpawnCoordinates());

            currentRoundState = ICWarsRoundState.INIT;
            currentPlayer.startTurn();
        } else {
            gameOverDisplay = true ;
            selectEnd();
        }
    }

    /**
     * Method aiming at giving the ability to the user to select its opponent : real or AI
     * It displays the ICWarsOpponentPanel while the boolean playerHasBeenSelected is false,
     * and when keys "A" or "R" are pressed it adds the corresponding players and makes
     * playerHasBeenSelected true
     */
    private void selectPlayer(){
        Keyboard keyboard = getWindow().getKeyboard();
        ICWarsArea area = (ICWarsArea) getCurrentArea() ;
        if(!playerHasBeenSelected && !gameOverDisplay){
            opponentPanel.draw(getWindow());

            if(keyboard.get(Keyboard.R).isPressed()){
                playerHasBeenSelected = true ;
                Tank tank = new Tank(getCurrentArea(), Tank.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                Soldier soldier = new Soldier(getCurrentArea(), Soldier.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                RocketMan rocketMan = new RocketMan(getCurrentArea(), RocketMan.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                players.add(new RealPlayer(area, area.getEnemySpawnCoordinates(), ICWarsActor.Faction.ENEMY, tank, soldier, rocketMan));
                players.get(1).enterArea(area, area.getEnemySpawnCoordinates());
            }
            if(keyboard.get(Keyboard.A).isPressed()){
                playerHasBeenSelected = true ;
                Tank tank = new Tank(getCurrentArea(), Tank.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                Soldier soldier = new Soldier(getCurrentArea(), Soldier.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                RocketMan rocketMan = new RocketMan(getCurrentArea(), RocketMan.getSpawnCoordinates(ICWarsActor.Faction.ENEMY), ICWarsActor.Faction.ENEMY);
                players.add(new AIPlayer(area, area.getEnemySpawnCoordinates(), ICWarsActor.Faction.ENEMY, tank, soldier, rocketMan));
                players.get(1).enterArea(area, area.getEnemySpawnCoordinates());
            }
        }
    }

    private void selectEnd(){
        Keyboard keyboard = getWindow().getKeyboard();
        if(gameOverDisplay){
            gameOverPanel.draw(getWindow());

            if(keyboard.get(Keyboard.G).isPressed()){
               gameOverDisplay = false;
               resetGame();
            }
            if(keyboard.get(Keyboard.Q).isPressed()){
                playerHasBeenSelected = false ;
                end();
            }
        }
    }

    /**
     * Method to call at the end of each turn on order to check
     * how many players left there are in the game
     */
    private void controlPlayers() {
        ArrayList<ICWarsPlayer> playersToRemove = new ArrayList<ICWarsPlayer>();
        for (ICWarsPlayer player : players) {
            if (player.isVanquished()) {
                nextRoundPlayers.remove(player);
                playersToRemove.add(player);
            }
        }
        players.removeAll(playersToRemove);
    }

    /**
     * Resets game
     */
    private void resetGame() {
        createAreas();
        players = new ArrayList<ICWarsPlayer>();
        playerHasBeenSelected = false ;
        currentRoundState = ICWarsRoundState.INIT;
        initArea("icwars/Level0");
    }

    /**
     * Add all the areas
     */
    private void createAreas() {
        addArea(new Level0());
        addArea(new Level1());
    }
}

