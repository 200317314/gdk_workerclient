package core;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.walking.web.node.CustomWebPath;
import org.dreambot.api.methods.walking.web.node.impl.BasicWebNode;

public class CreateWebNode {
    public static void init(API api) {
        PassableObstacle ditch = new PassableObstacle("Wilderness Ditch", "Cross", null, null, null);
        Walking.getAStarPathFinder().addObstacle(ditch);
        Walking.getDijPathFinder().addObstacle(ditch);

        if (api.designated_id == 0) {
            CustomWebPath northPath = new CustomWebPath(new Tile(3197, 3687), new Tile(3189, 3694), new Tile(3176, 3699), new Tile(3163, 3701), new Tile(3150, 3703), new Tile(3137, 3706));
            northPath.connectToStart(4856);
            Walking.getWebPathFinder().addCustomWebPath(northPath);
            MethodProvider.sleep(500);

            CustomWebPath eastPath = new CustomWebPath(new Tile(3207, 3682), new Tile(3220, 3682), new Tile(3228, 3687), new Tile(3236, 3692), new Tile(3245, 3693), new Tile(3253, 3696), new Tile(3262, 3698), new Tile(3272, 3698), new Tile(3281, 3696), new Tile(3291, 3693), new Tile(3301, 3691), new Tile(3310, 3691), new Tile(3319, 3692), new Tile(3327, 3692), new Tile(3331, 3697), new Tile(3335, 3703), new Tile(3342, 3701), new Tile(3343, 3692), new Tile(3342, 3683), new Tile(3337, 3678), new Tile(3333, 3674), new Tile(3325, 3672), new Tile(3326, 3682));
            eastPath.connectToStart(2302);
            Walking.getWebPathFinder().addCustomWebPath(eastPath);
            MethodProvider.sleep(500);

            CustomWebPath westPath = new CustomWebPath(new Tile(3096, 3652), new Tile(3084, 3655), new Tile(3069, 3651), new Tile(3055, 3649), new Tile(3042, 3650));
            westPath.connectToStart(2437);
            westPath.connectToEnd(3372);
            Walking.getWebPathFinder().addCustomWebPath(westPath);
        }

//        CustomWebPath randPath = new CustomWebPath(new Tile(3209, 3681), new Tile(3218, 3682), new Tile(3231, 3682), new Tile(3242, 3685), new Tile(3257, 3692), new Tile(3272, 3690));
//        randPath.connectToStart(4856);
//        randPath.connectToEnd(4858);
//        Walking.getWebPathFinder().addCustomWebPath(randPath);

        if (api.designated_id == 2) {
            AbstractWebNode webNode0 = new BasicWebNode(3385, 10053);
            AbstractWebNode webNode1 = new BasicWebNode(3382, 10057);
            AbstractWebNode webNode10 = new BasicWebNode(3374, 10096);
            AbstractWebNode webNode11 = new BasicWebNode(3375, 10102);
            AbstractWebNode webNode12 = new BasicWebNode(3377, 10107);
            AbstractWebNode webNode13 = new BasicWebNode(3382, 10112);
            AbstractWebNode webNode14 = new BasicWebNode(3381, 10117);
            AbstractWebNode webNode15 = new BasicWebNode(3380, 10124);
            AbstractWebNode webNode16 = new BasicWebNode(3377, 10131);
            AbstractWebNode webNode17 = new BasicWebNode(3383, 10136);
            AbstractWebNode webNode18 = new BasicWebNode(3392, 10138);
            AbstractWebNode webNode19 = new BasicWebNode(3399, 10137);
            AbstractWebNode webNode2 = new BasicWebNode(3377, 10060);
            AbstractWebNode webNode20 = new BasicWebNode(3401, 10131);
            AbstractWebNode webNode21 = new BasicWebNode(3401, 10120);
            AbstractWebNode webNode3 = new BasicWebNode(3378, 10065);
            AbstractWebNode webNode4 = new BasicWebNode(3381, 10069);
            AbstractWebNode webNode5 = new BasicWebNode(3386, 10072);
            AbstractWebNode webNode6 = new BasicWebNode(3384, 10077);
            AbstractWebNode webNode7 = new BasicWebNode(3383, 10082);
            AbstractWebNode webNode8 = new BasicWebNode(3382, 10087);
            AbstractWebNode webNode9 = new BasicWebNode(3377, 10091);
            webNode0.addConnections(webNode1);
            webNode1.addConnections(webNode0);
            webNode1.addConnections(webNode2);
            webNode10.addConnections(webNode11);
            webNode10.addConnections(webNode9);
            webNode11.addConnections(webNode10);
            webNode11.addConnections(webNode12);
            webNode12.addConnections(webNode11);
            webNode12.addConnections(webNode13);
            webNode13.addConnections(webNode12);
            webNode13.addConnections(webNode14);
            webNode14.addConnections(webNode13);
            webNode14.addConnections(webNode15);
            webNode15.addConnections(webNode14);
            webNode15.addConnections(webNode16);
            webNode16.addConnections(webNode15);
            webNode16.addConnections(webNode17);
            webNode17.addConnections(webNode16);
            webNode17.addConnections(webNode18);
            webNode18.addConnections(webNode17);
            webNode18.addConnections(webNode19);
            webNode19.addConnections(webNode18);
            webNode19.addConnections(webNode20);
            webNode2.addConnections(webNode1);
            webNode2.addConnections(webNode3);
            webNode20.addConnections(webNode19);
            webNode20.addConnections(webNode21);
            webNode21.addConnections(webNode20);
            webNode3.addConnections(webNode2);
            webNode3.addConnections(webNode4);
            webNode4.addConnections(webNode3);
            webNode4.addConnections(webNode5);
            webNode5.addConnections(webNode4);
            webNode5.addConnections(webNode6);
            webNode6.addConnections(webNode5);
            webNode6.addConnections(webNode7);
            webNode7.addConnections(webNode6);
            webNode7.addConnections(webNode8);
            webNode8.addConnections(webNode7);
            webNode8.addConnections(webNode9);
            webNode9.addConnections(webNode10);
            webNode9.addConnections(webNode8);
            WebFinder webFinder = Walking.getWebPathFinder();
            AbstractWebNode[] webNodes = {webNode21, webNode20, webNode19, webNode18, webNode14, webNode13, webNode15, webNode17, webNode16, webNode12, webNode11, webNode10, webNode9, webNode8, webNode7, webNode6, webNode5, webNode4, webNode3, webNode2, webNode1, webNode0};
            for (AbstractWebNode webNode : webNodes) {
                webFinder.addWebNode(webNode);
            }
        }

        if (api.designated_id == 3) {
            AbstractWebNode webNode0a = new BasicWebNode(3386, 10052);
            AbstractWebNode webNode1a = new BasicWebNode(3381, 10057);
            AbstractWebNode webNode10a = new BasicWebNode(3408, 10061);
            AbstractWebNode webNode11a = new BasicWebNode(3413, 10067);
            AbstractWebNode webNode12a = new BasicWebNode(3421, 10068);
            AbstractWebNode webNode2a = new BasicWebNode(3377, 10061);
            AbstractWebNode webNode3a = new BasicWebNode(3379, 10067);
            AbstractWebNode webNode4a = new BasicWebNode(3386, 10072);
            AbstractWebNode webNode5a = new BasicWebNode(3382, 10077);
            AbstractWebNode webNode6a = new BasicWebNode(3387, 10082);
            AbstractWebNode webNode7a = new BasicWebNode(3395, 10079);
            AbstractWebNode webNode8a = new BasicWebNode(3399, 10072);
            AbstractWebNode webNode9a = new BasicWebNode(3401, 10064);
            webNode0a.addConnections(webNode1a);
            webNode1a.addConnections(webNode0a);
            webNode1a.addConnections(webNode2a);
            webNode10a.addConnections(webNode11a);
            webNode10a.addConnections(webNode9a);
            webNode11a.addConnections(webNode10a);
            webNode11a.addConnections(webNode12a);
            webNode12a.addConnections(webNode11a);
            webNode2a.addConnections(webNode1a);
            webNode2a.addConnections(webNode3a);
            webNode3a.addConnections(webNode2a);
            webNode3a.addConnections(webNode4a);
            webNode4a.addConnections(webNode3a);
            webNode4a.addConnections(webNode5a);
            webNode5a.addConnections(webNode4a);
            webNode5a.addConnections(webNode6a);
            webNode6a.addConnections(webNode5a);
            webNode6a.addConnections(webNode7a);
            webNode7a.addConnections(webNode6a);
            webNode7a.addConnections(webNode8a);
            webNode8a.addConnections(webNode7a);
            webNode8a.addConnections(webNode9a);
            webNode9a.addConnections(webNode10a);
            webNode9a.addConnections(webNode8a);
            WebFinder webFinder = Walking.getWebPathFinder();
            AbstractWebNode[] webNodess = {webNode12a, webNode11a, webNode10a, webNode9a, webNode8a, webNode7a, webNode4a, webNode6a, webNode0a, webNode5a, webNode1a, webNode3a, webNode2a};
            for (AbstractWebNode webNode : webNodess) {
                webFinder.addWebNode(webNode);
            }
        }
    }
}
