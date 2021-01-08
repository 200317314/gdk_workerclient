package core.nodes;

import core.API;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.Objects;

public class Collect extends TaskNode {
    private API api;
    private GroundItem loot;

    public Collect(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean accept() {
        if (Cannon.canObtain || !Client.isLoggedIn()) {
            return false;
        }

        return canCollect() && (api.hasFood() || Combat.getHealthPercent() >= 60);
    }

    @Override
    public int execute() {
        if (getLocalPlayer().isMoving()) {
           return api.sleep()*4;
        }

        if (Objects.isNull(loot) || !loot.exists()) {
            if (!api.items.isEmpty()) {
                loot = api.items.remove(0);
            }
        }

        if (Objects.nonNull(loot) && loot.exists()) {
            if (loot.distance() <= 6) {
                if (loot.interact("Take")) {
                    sleepUntil(() -> !loot.exists(), 4600 + api.sleep());
                }
            } else if (api.shouldWalk()) {
                Walking.walk(loot);
            }
        }
        return api.sleep()*3;
    }

    private boolean canCollect() {
        if (!api.inWilderness() || Inventory.isFull()) {
            return false;
        }

        if (Objects.nonNull(loot) && loot.exists() || !api.items.isEmpty()) {
            return true;
        } else {
            api.items = GroundItems.all(i -> Objects.nonNull(i) && api.lootList.contains(i.getName()) && api.selectedArea.contains(i) && Map.isTileOnMap(i.getTile()));
        }

        return Objects.nonNull(loot) && loot.exists() || !api.items.isEmpty();
    }
}
