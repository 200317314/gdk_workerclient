package core.nodes;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.script.TaskNode;

public class Dialouge extends TaskNode {
    @Override
    public int priority() {
        return 98;
    }

    @Override
    public boolean accept() {
        return Dialogues.canContinue() && !Cannon.canObtain;
    }

    @Override
    public int execute() {
        if (Dialogues.clickContinue()) {
            sleepUntil(() -> !Dialogues.inDialogue(), 2400);
        }
        return 100; //api.sleep();
    }
}
