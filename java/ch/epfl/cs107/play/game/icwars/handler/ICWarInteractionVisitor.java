package ch.epfl.cs107.play.game.icwars.handler;

import  ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.city.ICWarsCity;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;

public interface ICWarInteractionVisitor extends AreaInteractionVisitor {

    default void interactWith(Interactable other){

    }

    default void interactWith(ICWarsBehavior.ICWarsCell cell){

    }

    default void interactWith(Unit unit){

    }

    default void interactWith(ICWarsCity city) {

    }
}
