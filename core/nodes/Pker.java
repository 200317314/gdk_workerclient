package core.nodes;

import core.API;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Timer;

import java.util.Objects;

public class Pker extends TaskNode {
    private Timer sleep;
    public static boolean teleBlocked = false;
    private API api;
    private int counter = 0;

    public Pker(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 999;
    }

    @Override
    public boolean accept() {
        if (api.muling) {
            return false;
        }

        return pkerPresent() || Objects.nonNull(sleep);
    }

    @Override
    public int execute() {
        api.STATUS = "PKER RUN!!!";

        if (!teleBlocked || Objects.nonNull(sleep)) {
            api.STATUS = "Pker teleporting... ";

            if (!Tabs.isOpen(Tab.EQUIPMENT)) {
                if (Tabs.open(Tab.EQUIPMENT)) {
                    sleep(api.sleep());
                }
            }

            if (Objects.isNull(sleep)) {
                if (Equipment.get(i -> Objects.nonNull(i) && i.getName().contains(api.AMULET)).interact("Edgeville")) {
                    counter++;
                    sleepUntil(() -> !api.inWilderness(), 6000);
                    sleep(api.sleep());

                    if (api.inWilderness()) {
                        teleBlocked = true;
                        return api.sleep();
                    } else {
                        if (counter >= 5) {
                            sleep = new Timer(Calculations.random(20000,40000));
                            counter = 0;
                            return api.sleep();
                        } else {
                            sleep = new Timer(Calculations.random(10000,20000));
                        }
                        return 0;
                    }
                }
            } else {
                if (sleep.finished()) {
                    sleep = null;
                    return api.sleep();
                } else {
                    if (Objects.isNull(sleep)) {
                        sleep = new Timer(Calculations.random(10000,20000));
                    }
                    return 0;
                }
            }
        } else {
            if (counter >= 5) {
                sleep = new Timer(Calculations.random(20000,40000));
                counter = 0;
                return api.sleep();
            } else {
                sleep = new Timer(Calculations.random(10000,20000));
                return api.sleep();
            }
        }
        return api.sleep();
    }

    private boolean pkerPresent() {
        if (api.inWilderness()) {
            if (api.attackableExists()) {
                return true;
            }
        }

        return false;
    }
}
