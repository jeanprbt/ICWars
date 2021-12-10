package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.window.Keyboard;

abstract public class ICWarsAction implements Graphics {

    protected ICWarsArea area ;
    protected Unit ownerUnit ;
    private String name ;
    private int key ;

    public ICWarsAction (ICWarsArea area, Unit ownerUnit, int key, String name){
        this.area = area ;
        this.ownerUnit = ownerUnit ;
        this.name = name ;
        this.key = key ;
    }

    public abstract void doAction(float dt, ICWarsPlayer player , Keyboard keyboard);
}

