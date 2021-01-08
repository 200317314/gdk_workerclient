package core.nodes;

import core.utils.ClientRender;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Timer;

public class TimerNode extends TaskNode {
    private Timer timer = new Timer(timerTime);
    private static long timerTime = 60000;

    @Override
    public int priority() {
        return 9999999;
    }

    @Override
    public boolean accept() {
        return timer != null && timer.finished();
    }

    @Override
    public int execute() {
        ClientRender.setRender(this);
        timer = new Timer(timerTime);
        return 0;
    }
}
