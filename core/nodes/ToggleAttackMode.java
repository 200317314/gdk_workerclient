package core.nodes;

import core.API;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.Objects;

public class ToggleAttackMode extends TaskNode {
    private int defence = 3, rapid = 1;
    private API api;

    public ToggleAttackMode(API api) {
        this.api = api;
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean accept() {
        if (Bank.isOpen() || Trade.isOpen()) {
            return false;
        }

        return canToggle();
    }

    @Override
    public int execute() {
        if (Tabs.isOpen(Tab.COMBAT)) {
            WidgetChild select = (toggleDefence()) ? Widgets.getWidgetChild(593,16) : Widgets.getWidgetChild(593,8);

            if (Objects.nonNull(select)) {
                if (select.interact()) {
                    sleep(api.sleep());
                }
            }
        } else {
            Tabs.open(Tab.COMBAT);
        }

        return api.sleep();
    }

    private boolean canToggle() {
        return toggleDefence() || toggleRapid();
    }

    private boolean toggleDefence() {
        return Skills.getRealLevel(Skill.DEFENCE) < 40 && PlayerSettings.getConfig(43) != defence;
    }

    private boolean toggleRapid() {
        return Skills.getRealLevel(Skill.DEFENCE) >= 40 && PlayerSettings.getConfig(43) != rapid;
    }
}
