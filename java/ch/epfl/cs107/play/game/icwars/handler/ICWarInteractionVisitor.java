package ch.epfl.cs107.play.game.icwars.handler;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.RealPlayer;

public interface ICWarInteractionVisitor extends AreaInteractionVisitor {

    default void interactWith(Interactable interactable){
        if(interactable instanceof Unit){
            Unit unit = (Unit) interactable ;
            interactWith(unit);
        }
    }

    default void interactWith(RealPlayer player){

    }

    default void interactWith(Unit unit){

    }

}