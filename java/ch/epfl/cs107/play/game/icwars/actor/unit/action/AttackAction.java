package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.player.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class AttackAction extends ICWarsAction{

    public AttackAction(ICWarsArea area, Unit ownerUnit) {
        super(area, ownerUnit, Keyboard.A, "(A)ttack");
    }

    public ArrayList<Integer> findTargetsIndexes() {
        ArrayList<Integer> targetsIndexes = new ArrayList<Integer>();
        for (Unit unit  : area.unitList) {
            if(unit.getFaction() == ownerUnit.getFaction()) continue ;
            DiscreteCoordinates position = new DiscreteCoordinates((int)unit.getPosition().x, (int)unit.getPosition().y);
            if(isInRange(position)) targetsIndexes.add(area.getIndexInUnitList(unit));
        }
        return targetsIndexes ;
    }

    private boolean isInRange(DiscreteCoordinates position) {
        if(Math.abs(ownerUnit.getPosition().x - position.x) <= ownerUnit.getRadius() && Math.abs(ownerUnit.getPosition().y - position.y) <= ownerUnit.getRadius()) return true;
        return false;
    }


    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        targetsIndexes = findTargetsIndexes();
        while (!keyboard.get(Keyboard.ENTER).isPressed()) {
            if (targetsIndexes.size() == 0 || keyboard.get(Keyboard.TAB).isPressed()) {
                player.centerCamera();
                player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.ACTION_SELECTION);
                break;
            }
            targetIndex = targetsIndexes.get(index);
            if (keyboard.get(Keyboard.RIGHT).isPressed() && index < targetsIndexes.size() - 1) {
                ++index;
                targetIndex = targetsIndexes.get(index);
            }
            if (keyboard.get(Keyboard.LEFT).isPressed() && index > 0) {
                --index;
                targetIndex = targetsIndexes.get(index);
            }
            if (keyboard.get(Keyboard.ENTER).isPressed()) attack(area.unitList.get(targetIndex), player);
        }
    }

    private void attack(Unit unit, ICWarsPlayer player){
        unit.takeInjure(ownerUnit.getDamage());
        ownerUnit.setHasBeenUsed(true);
        player.setCurrentPlayerState(ICWarsPlayer.ICWarsPlayerState.NORMAL);

    }

    @Override
    public void draw(Canvas canvas) {

    }

}

