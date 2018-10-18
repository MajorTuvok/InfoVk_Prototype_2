package infovk.prototype_2.helper;

import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.util.Random;

public class RobotHelper {
    public static final Random RANDOM = Utils.getRandom();
    public static final Vector2D X_AXIS = new Vector2D(1, 0);
    public static final Vector2D Y_AXIS = new Vector2D(0, 1);

    public static final double absoluteBearing(Robot robot, double relBearing) {
        return robot.getHeading() + relBearing;
    }

    public static final ScannedRobotEvent scannedEventFromRobot(Robot robot) {
        return new ScannedRobotEvent(robot.getName(), robot.getEnergy(), 0, 0, robot.getHeading(), robot.getVelocity(), false);
    }

    public static final double getRelativeEnergyPower(double baseVal, double energy, double energyPowerFactor) {
        return Math.max(Math.min(baseVal * energy / energyPowerFactor, Rules.MAX_BULLET_POWER), Rules.MIN_BULLET_POWER);
    }

    public static final double getRelativeEnergyAndDistancePower(double baseVal, double energy, double energyPowerFactor, double distance, double distanceFactor) {
        return getRelativeEnergyPower(baseVal - (int) (distance / distanceFactor), energy, energyPowerFactor);
    }

    public static final double clamp(double in, double min, double max) {
        return Math.min(Math.max(in, min), max);
    }
    /*
    public static final ScannedRobotEvent scannedRobot(Robot scanned, Robot scanner) {
        return new ScannedRobotEvent(scanned.getName(),scanned.getEnergy(),0,0,scanned.getHeading(),scanned.getVelocity(),false);
    }*/
}
