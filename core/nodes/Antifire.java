package core.nodes;

import core.API;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class Antifire extends TaskNode {
    private API api;

    public Antifire(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 998;
    }

    @Override
    public boolean accept() {
        return canAntifire();
    }

    @Override
    public int execute() {
        if (Tabs.isOpen(Tab.INVENTORY)) {
            if (Inventory.get(i -> Objects.nonNull(i) && i.getName().contains("Extended antifire")).interact("Drink")) {
                sleepUntil(() -> !api.canAntifire, 2400 + api.sleep());
                sleep(api.sleep());
            }

            if (Inventory.contains("Vial")) {
                if (Inventory.get("Vial").interact("Drop")) {
                    sleepUntil(() -> !Inventory.contains("Vial"), 2400);
                    sleep(api.sleep());
                }
            }

            if (Inventory.contains("Lamp")) {
                if (Inventory.get("Lamp").interact("Drop")) {
                    sleepUntil(() -> !Inventory.contains("Lamp"), 2400);
                }
            }
        } else {
            Tabs.open(Tab.INVENTORY);
        }
        return api.sleep();
    }

    private boolean canAntifire() {
        return Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Extended antifire") && !i.isNoted()) && api.selectedArea.contains(getLocalPlayer()) && api.canAntifire;
    }
}
