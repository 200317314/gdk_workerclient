package core.nodes;

import core.API;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.TaskNode;

import java.util.Objects;

public class Potion extends TaskNode {
    private API api;

    public Potion(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public boolean accept() {
        if (Skills.getRealLevel(Skill.RANGED) != Skills.getBoostedLevels(Skill.RANGED)) {
            return false;
        }

        return canPotion();
    }

    @Override
    public int execute() {
        api.STATUS = "Using potion...";
        if (Tabs.isOpen(Tab.INVENTORY)) {
            if (Inventory.get(i -> Objects.nonNull(i) && i.getName().contains("Ranging potion")).interact("Drink")) {
                sleepUntil(() -> Skills.getRealLevel(Skill.RANGED) != Skills.getBoostedLevels(Skill.RANGED), 2400 + api.sleep());
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

    private boolean canPotion() {
        return Inventory.contains(i -> Objects.nonNull(i) && i.getName().contains("Ranging potion") && !i.isNoted()) && Skills.getRealLevel(Skill.RANGED) <= Skills.getBoostedLevels(Skill.RANGED) && api.selectedArea.contains(getLocalPlayer());
    }
}
