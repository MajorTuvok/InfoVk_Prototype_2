package infovk.protoype_2.helper;

import robocode.Robot;
import robocode.util.Utils;

import java.util.Random;

public class RobotHelper {
    public static final Random RANDOM = Utils.getRandom();

    public static final double absoluteBearing(Robot robot, double relBearing) {
        return robot.getHeading() + relBearing;
    }
}
