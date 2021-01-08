package core.nodes;

import core.API;
import core.Areas;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.items.Item;

import java.util.Objects;

public class Eat extends TaskNode {
    private API api;

    public Eat(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 9;
    }

    @Override
    public boolean accept() {
        if (Bank.isOpen()) {
            return false;
        }

        return canEat();
    }

    @Override
    public int execute() {
        api.STATUS = "Eating...";
        if (!Tabs.isOpen(Tab.INVENTORY)) {
            Tabs.open(Tab.INVENTORY);
        }

        Item food = Inventory.get(i -> Objects.nonNull(i) && (i.getName().contains(api.FOOD) || i.getName().contains(api.FOOD2)));
        int foodLeft = Inventory.count(i -> Objects.nonNull(i) && (i.getName().contains(api.FOOD) || i.getName().contains(api.FOOD2)));
        if (Objects.nonNull(food)) {
            if (food.interact("Eat")) {
                sleepUntil(() -> Inventory.count(i -> Objects.nonNull(i) && (i.getName().contains(api.FOOD) || i.getName().contains(api.FOOD2))) != foodLeft, 2400 + api.sleep());
            }
        }
        return api.sleep();
    }

    private boolean canEat() {
        return (api.hasFood() && Combat.getHealthPercent() <= Calculations.random(60,70)) || (Inventory.isFull() && api.hasFood() && !Areas.EDGEVILLE.contains(getLocalPlayer()) && !Areas.CORP.contains(getLocalPlayer()));
    }
}
