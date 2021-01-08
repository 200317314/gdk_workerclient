package core.nodes;

import core.API;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;

import java.util.Objects;

public class Attack extends TaskNode {
    private API api;
    private NPC dragon;
    private Player attackingDragon;

    public Attack(API api) {
        this.api = api;
    }

    @Override
    public boolean accept() {
        if (!api.inWilderness() || !Client.isLoggedIn()) {
            return false;
        }

        return canAttack() && api.hasBolts() && Cannon.isPlaced;
    }

    @Override
    public int execute() {
        if (Objects.nonNull(getLocalPlayer().getCharacterInteractingWithMe())) {
            dragon = (NPC) getLocalPlayer().getCharacterInteractingWithMe();
        }

        if (Objects.isNull(dragon) || !dragon.exists() || !dragon.getName().contains("Green dragon") || dragon.getHealthPercent() <= 0) {
            dragon = NPCs.closest(n -> Objects.nonNull(n) && n.getName().contains("Green dragon") && !n.isInCombat() && api.getCannonArea().getCenter().distance(n) <= 12);
        }

        if (Objects.nonNull(dragon) && Objects.nonNull(dragon.getCharacterInteractingWithMe())) {
            attackingDragon = (Player) dragon.getCharacterInteractingWithMe();

            if (!attackingDragon.equals(getLocalPlayer())) {
                dragon = null;
                attackingDragon = null;
                return api.sleep();
            }
        }

        api.STATUS = "Attacking dragon...";
        if (Objects.nonNull(dragon)) {
            if (dragon.distance() <= 6) {
                if (dragon.interact("Attack")) {
                    sleepUntil(() -> getLocalPlayer().isInCombat(), 2400 + api.sleep());
                }
            } else if (Walking.shouldWalk(5)) {
                Walking.walk(dragon.getTile());
            }
        }
        return api.sleep();
    }

    private boolean canAttack() {
        if (!api.selectedArea.contains(getLocalPlayer())) {
            return false;
        }

        return !getLocalPlayer().isInCombat();
    }

    /*private boolean canAttack() {
        Character character = getLocalPlayer().getCharacterInteractingWithMe();

        if (Objects.nonNull(character) && character.getName().contains("Green dragon")) {
            return false;
        }
        return  !getLocalPlayer().isInCombat() && Objects.isNull(character) && Areas.DRAGON_AREA.contains(getLocalPlayer());
    }

    private boolean canReAttack() {
        Character character = getLocalPlayer().getCharacterInteractingWithMe();

        if (Objects.isNull(character)) {
            return false;
        }

        return !getLocalPlayer().isInCombat() && character.getName().contains("Green dragon");
    }*/

    /*private boolean canMove() {
        return Cannon.isPlaced && Areas.DRAGON_AREA.contains(getLocalPlayer()) && !Cannon.doubleShot.equals(getLocalPlayer().getTile()) && !Cannon.refill;
    }*/
}
