package infovk.protoype_2;


import infovk.protoype_2.helper.RobotHelper;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class Prototype_Best extends RobotBase {

    private double distance = 50; //Movement when hit

    @Override
    protected void start() {
        super.start();
        setBodyColor(Color.BLACK);
        setGunColor(Color.GREEN);
        setRadarColor(Color.BLACK);
    }

    @Override
    protected void loop() {
        super.loop();
        setTurnRadarRight(360);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        double bearing = getHeading();
        double dodge = Utils.normalRelativeAngle((bearing - getHeading()) * -1);
        turnRight(dodge);
        System.out.println(dodge);
        distance = (distance + 10) * -1;
        ahead(distance);


    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);
        ahead(distance * -1);
        setTurnRight(90);

    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        double turnHitWall = 90;
        ahead(distance * -1);
        setTurnRight(turnHitWall);
        //System.out.println(turnHitWall);

    }

    //Nicht funktionsfähig, da grad und Bogenmaß vorkommt ->erstelle neue Klasse
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        //altes Programm vor erweiterung
        double bearing = event.getBearing();
        double absoluteBearing = getHeading() + bearing;
        double radarDirection = getRadarHeading();
        double toTurn = Utils.normalRelativeAngle(absoluteBearing - radarDirection);
        toTurn = toTurn > 0 ? toTurn + 5 * (RobotHelper.RANDOM.nextDouble() + 1) : toTurn - 5 * (RobotHelper.RANDOM.nextDouble() + 1);
        setTurnRadarRight(toTurn);

        double gunDirection = getGunHeading();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - gunDirection);
        setTurnGunRight(gunTurn);
        fireRelativeToEnergyAndDistance(3, event.getDistance());

    }
}
