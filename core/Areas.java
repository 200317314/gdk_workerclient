package core;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class Areas {
    public static final Area DRAGON_AREA_NORTH = new Area(3122, 3722, 3164, 3689);
    public static final Area DRAGON_AREA_EAST = new Area(3318, 3709, 3348, 3660);
    public static final Area DRAGON_AREA_WEST = new Area(2963, 3629, 2995, 3598);
    public static final Tile SAFE_SPOT = new Tile(3142, 3646);
    public static final Area EDGEVILLE = new Area(3087, 3500, 3100, 3485);
    public static final Area CANNON_NORTH = new Area(3142, 3706, 3148, 3701);
    public static final Area CANNON_EAST = new Area(3328, 3691, 3331, 3688);
    //public static final Area CANNON_WEST = new Area(2975, 3617, 2978, 3614);
    public static final Area NULODION = new Area(
            new Tile(3008, 3455, 0),
            new Tile(3015, 3455, 0),
            new Tile(3015, 3452, 0),
            new Tile(3011, 3452, 0),
            new Tile(3011, 3449, 0),
            new Tile(3008, 3449, 0));
    public static final Area CORP = new Area(2961, 4391, 2974, 4375, 2);
    public static final Area DUNGEON_STAIRS = new Area(3254, 3673, 3270, 3660);
    public static final Area SLAYER_DUNGEON = new Area(3267, 10229, 3465, 10013);
    public static final Area SLAYER_DUNGEON_NORTH = new Area(3384, 10131, 3410, 10111);
    public static final Area SLAYER_DUNGEON_SOUTH = new Area(3406, 10073, 3423, 10058);
    public static final Area CANNON_DUNGEON_NORTH = new Area(3398, 10124, 3401, 10122);
    public static final Area CANNON_DUNGEON_SOUTH = new Area(3415, 10069, 3418, 10066);
}
