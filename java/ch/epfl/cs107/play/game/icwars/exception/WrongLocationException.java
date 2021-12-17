package ch.epfl.cs107.play.game.icwars.exception;

public class WrongLocationException extends Exception {
    public WrongLocationException(){
        super("Wrong location, please try another one");
    }
}
