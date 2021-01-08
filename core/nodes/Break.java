package core.nodes;

import core.API;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Timer;

import java.util.Objects;

public class Break extends TaskNode {
    private final API api;
    private int breaks = 0;

    public Break(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 9999999;
    }

    @Override
    public boolean accept() {
        return canReset() || canSleep();
    }

    @Override
    public int execute() {
        if (canReset()) {
            api.shouldBreak = new Timer(Calculations.random(10800000, 14400000));
            return api.sleep();
        }

        if (canSleep()) {
            if (breaks < 4) {
                breaks++;
                teleport();
                sleep(10800, 11200);
                api.disableLogin();
                sleep(api.sleep());
                Tabs.logout();
                api.sleepTimer = new Timer(Calculations.random(1800000,2700000));
            } else {
                breaks = 0;
                teleport();
                sleep(10800, 11200);
                api.disableLogin();
                sleep(api.sleep());
                Tabs.logout();
                api.sleepTimer = new Timer(Calculations.random(27000000,30600000));
            }
        }
        return api.sleep();
    }

    private boolean canReset() {
        return Objects.isNull(api.shouldBreak);
    }

    private boolean canSleep() {
        return Objects.nonNull(api.shouldBreak) && api.shouldBreak.finished() && Objects.isNull(api.sleepTimer);
    }

    private void teleport() {
        if (api.hasAmulet()) {
            if (!Tabs.isOpen(Tab.EQUIPMENT)) {
                if (Tabs.open(Tab.EQUIPMENT)) {
                    sleep(api.sleep());
                }
            }

            if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                sleepUntil(() -> !api.inWilderness(), 3400);
            }
        }
    }
}
