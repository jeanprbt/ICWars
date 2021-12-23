package ch.epfl.cs107.play.game.icwars.exception;

public class WrongLocationException extends Exception {

    //-----------------------------------API-------------------------------------//

    public WrongLocationException(){
        super("The coords are invalid for AI");
    }

    public WrongLocationException(String str){
        super(str);
    }
}
