package core.nodes;

import core.API;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class NewGear extends TaskNode {
    private API api;

    public NewGear(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        if (api.muling) {
            return false;
        }

        if (api.inWilderness()) {
            return false;
        }

        return canEquip();
    }

    @Override
    public int execute() {
        if (Tabs.isOpen(Tab.INVENTORY)) {
            for (String item : api.getGearItems()) {
                if (api.inInventory(item) && !api.inEquipment(item) || (item.contains("Mithril bolts") && api.inInventory("Mithril bolts") && api.inEquipment("Mithril bolts"))) {
                    if (item.contains("Mithril bolts") || item.contains("crossbow")) {
                        Inventory.interact(i -> Objects.nonNull(i) && i.getName().contains(item) && !i.isNoted(), "Wield");
                        sleepUntil(() -> api.inEquipment(item), 3400);
                        sleep(api.sleep()*4);
                    } else {
                        Inventory.interact(i -> Objects.nonNull(i) && i.getName().contains(item) && !i.isNoted(), "Wear");
                        sleepUntil(() -> api.inEquipment(item), 3400);
                        sleep(api.sleep()*4);
                    }
                }
            }
        } else {
            Tabs.open(Tab.INVENTORY);
        }
        return api.sleep();
    }

    private boolean canEquip() {
        for (String item : api.getGearItems()) {
            if (api.inInventory(item) && !api.inEquipment(item) || (item.contains("Mithril bolts") && api.inInventory("Mithril bolts") && api.inEquipment("Mithril bolts"))) {
                if (item.contains("Mithril bolts")) {
                    if (Inventory.count("Mithril bolts") <= 250) {
                        return true;
                    }
                    continue;
                }

                return true;
            }
        }

        return false;
    }
}
