package infovk.protoype_2;


import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class Prototype_Best extends RobotBase{

    double distance =50; //Movement when hit

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
    //Nicht funktionsfähig, da grad und Bogenmaß vorkommt ->erstelle neue Klasse
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        //ziemlich ungenau?
     /*   double bearing = event.getBearing();
        double absoluteBearing = bearing + event.getHeading();
        double radarHeading = getRadarHeading();
        double toTurn = Utils.normalRelativeAngle(absoluteBearing - radarHeading);
        if (toTurn > 0) {
            toTurn = toTurn + 5;
        } else toTurn = toTurn - 5;
        setTurnRadarRight(toTurn);
        double gunDirection = getGunHeading();
        double gunTurn = Utils.normalRelativeAngle(absoluteBearing - gunDirection);
        setTurnGunRight(gunTurn);
        System.out.println(gunTurn);
        setTurnRight(gunTurn);
        double correction = Utils.normalRelativeAngle(absoluteBearing - gunTurn);
       // setTurnRight(correction);
        System.out.println(correction);
        //fireRelativeToEnergyAndDistance(3,event.getDistance());
        setFire(1.0);
        double number=((Math.random()*49)+1);
        double travel;
        //if (Math.random()>0.5){
        //    travel = number;}
        //else{
        //    travel = number * -1;}
        //ahead(travel);
        scan();
*/
     //altes Programm vor erweiterung
    double bearing = event.getBearing();
    double absoluteBearing = getHeading() + bearing; //getHeading statt event.getHeading = Unterschied?
    double radarDirection = getRadarHeading();
    double toTurn = Utils.normalRelativeAngle(absoluteBearing - radarDirection);
    toTurn = toTurn>0? toTurn +5:toTurn-5;
    setTurnRadarRight(toTurn);

    double gunDirection = getGunHeading();
    double gunTurn = Utils.normalRelativeAngle(absoluteBearing - gunDirection);
    setTurnGunRight(gunTurn);
    fireRelativeToEnergyAndDistance(3,event.getDistance());

    }

   @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        double bearing = getHeading();
        double dodge = Utils.normalRelativeAngle((bearing-getHeading())*-1);
        turnRight(dodge);
        System.out.println(dodge);
        distance = (distance+10)*-1;
        ahead(distance);


    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        double turnHitWall= 90;
        ahead(distance*-1);
        setTurnRight(turnHitWall);
        //System.out.println(turnHitWall);

    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);
        ahead(distance*-1);
        setTurnRight(90);

    }
}
