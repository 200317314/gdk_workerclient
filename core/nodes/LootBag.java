package core.nodes;

import core.API;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class LootBag extends TaskNode {
    private API api;

    public LootBag(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean accept() {
        return canLootBag() || canAutoRetaliate();
    }

    @Override
    public int execute() {
        api.STATUS = "Looting bag...";
        if (canAutoRetaliate()) {
            if (Tabs.isOpen(Tab.COMBAT)) {
                if (Combat.toggleAutoRetaliate(true)) {
                    sleepUntil(() -> Combat.isAutoRetaliateOn(), 2400);
                }
            } else {
                Tabs.open(Tab.COMBAT);
            }
        } else {
            if (Tabs.isOpen(Tab.INVENTORY)) {
                if (Inventory.get("Looting bag").interact("Open")) {
                    sleepUntil(() -> Inventory.get("Looting bag").hasAction("Close"), 2400);
                }
            } else {
                Tabs.open(Tab.INVENTORY);
            }
        }
        return api.sleep();
    }

    private boolean canLootBag() {
        return api.hasLootBag() && api.inWilderness()
                && Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Looting bag") && i.hasAction("Open"));
    }

    private boolean canAutoRetaliate() {
        return !Combat.isAutoRetaliateOn();
    }
}
