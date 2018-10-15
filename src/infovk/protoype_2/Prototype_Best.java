package infovk.protoype_2;


import robocode.HitByBulletEvent;
import robocode.ScannedRobotEvent;

public class Prototype_Best extends RobotBase{
    double distance =60; //Movement when hit

    @Override
    protected void start() {
        super.start();

    }

    @Override
    protected void loop() {
        super.loop();
       setTurnRadarRight(20);

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double bearing = event.getBearing();
        double absoluteBearing = bearing + getHeading();
        double radarHeading = getRadarHeading();
        double toTurn = Utils.normalRelativeAngle(absoluteBearing - radarHeading);
        toTurn = toTurn > 0 ? toTurn + 5 : toTurn - 5;
        setTurnRadarRight(toTurn);
        double gunDirection = getGunHeading();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - gunDirection);
        setTurnGunRight(gunTurn);
        fireRelativeToEnergyAndDistance(3,event.getDistance());
        //ahead(30);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        double bearing = event.getHeading();
        double dodge = Utils.normalRelativeAngle(90-(bearing-event.getHeading()));
        turnRight(dodge);

        ahead(distance);
        distance *=-1;


    }
}
