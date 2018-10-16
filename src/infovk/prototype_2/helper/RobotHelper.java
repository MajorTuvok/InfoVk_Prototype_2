package infovk.prototype_2.helper;

import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.util.Random;

public class RobotHelper {
    public static final Random RANDOM = Utils.getRandom();
    public static final Point Y_AXIS = new Point(0, 1);
    public static final Point X_AXIS = new Point(1, 0);

    public static final double absoluteBearing(Robot robot, double relBearing) {
        return robot.getHeading() + relBearing;
    }

    public static final ScannedRobotEvent scannedEventFromRobot(Robot robot) {
        return new ScannedRobotEvent(robot.getName(), robot.getEnergy(), 0, 0, robot.getHeading(), robot.getVelocity(), false);
    }

    /*
    public static final ScannedRobotEvent scannedRobot(Robot scanned, Robot scanner) {
        return new ScannedRobotEvent(scanned.getName(),scanned.getEnergy(),0,0,scanned.getHeading(),scanned.getVelocity(),false);
    }*/
}