package infovk.protoype_2;


import infovk.protoype_2.helper.Point;
import infovk.protoype_2.helper.RobotCache;
import infovk.protoype_2.helper.RobotHelper;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class Prototype_Best extends RobotBase {

	private int i; //int für farbschleife
    private double distance = 50; //Movement when hit

    @Override
    protected void start() {
        super.start();
        i = 0;

        rainbow();
    }

    @Override
    protected void loop() {
        super.loop();
        rainbow();
        setTurnRadarRight(360); //passiert wenn gegner raus dann erst wieder ne volle drehung
                                // lösung über boolean array?
                                //schusskorrektur?
        setTurnRadarRight(360);
        double turn = (RobotHelper.RANDOM.nextDouble() * 180) - 90;
        double move = (RobotHelper.RANDOM.nextDouble() * 100) - 50;
        setTurnRight(turn);
        ahead(move);

    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) { //not random enough
        super.onHitByBullet(event);
        double bearing = getHeading();
        double dodge = Utils.normalRelativeAngle((bearing - getHeading()));
        turnRight(dodge);
        System.out.println(dodge);
        distance = (distance + 10) * -1;
        ahead(distance);


    }


     /*

     public void dodgeWall(){
     double x = getX();   //Eigene Koordinaten
     double y = getY();
        Point pos = new Point(x,y);
        Point robotDirection = Point.fromPolarCoordinates(getHeading(),1).subtract(pos);
     //Wand koords linke x0,y-achse
     //Wand koords unten x-achse, y0
     //Wand koords oben xachse, ymax
     //wand koords rechts xmax, yachse

     if (0<=x && x<=40){ //Linke wand
         Point y_axis = RobotHelper.Y_AXIS;
         double angle = y_axis.angleFrom(robotDirection);
         System.out.println("zu y" +angle);
         setTurnRight(angle);
     }
     else {
         if (0 <= y && y >= 40) { //Untere Wand
             Point x_axis = RobotHelper.X_AXIS;
             double angle = x_axis.angleFrom(robotDirection);
             System.out.println("zu x" +angle);
             setTurnRight(angle);
         } else {
             if (getBattleFieldHeight() <= y && y >= (getBattleFieldHeight() - 40)) { //obere Wand
                 Point x_axis = RobotHelper.X_AXIS;
                 double angle = x_axis.angleFrom(robotDirection);
                 System.out.println("zu oben" +angle);
                 setTurnRight(angle);
             } else {
                 if (getBattleFieldWidth() <= x && x >= getBattleFieldWidth() - 40) { //rechte wand
                     Point y_axis = RobotHelper.Y_AXIS;
                     double angle = y_axis.angleFrom(robotDirection);
                     System.out.println("zu rechts" +angle);
                     setTurnRight(angle);
                 }
             }
         }
     }
}


*/



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
    public void onScannedRobot(ScannedRobotEvent event) { //finished
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
        //setTurnGunRight(gunTurn);
        targetGun(gunTurn, event.getName());
        fireRelativeToEnergyAndDistance(1, event.getDistance());

        double oldEnergy = getCache(event.getName(), 1).getEnergy();
        double newEnergy = event.getEnergy();

        if (oldEnergy > newEnergy) {
            double turn = (RobotHelper.RANDOM.nextDouble() * 180) - 90;
            double move = (RobotHelper.RANDOM.nextDouble() * 400) - 200;
            setTurnRight(turn);
            ahead(move);
        }
        rainbow();

        //dodgeWall();

    }

    public void targetGun(double a, String enemy) { //close to finish
        RobotCache.PositionalRobotCache cache = getRecentCache(enemy);
        double velocity = cache.getVelocity();
        double direction = cache.getHeading();
        double x = cache.getTargetInfo().getX();
        double y = cache.getTargetInfo().getY();
        double actualAngle = a;

        Point movement = Point.fromPolarCoordinates(direction, velocity);
        Point distance = new Point(x - getX(), y - getY());

        Point pointEnemy = new Point(x, y);
        Point target = pointEnemy.add(movement).add(movement);//nTurns vorraus, beliebig erweiterbar
        Point own = new Point(getX(), getY());
        Point toTarget = new Point(target.getX() - own.getX(), target.getY() - own.getY());
        double toTurnGun = Utils.normalRelativeAngle(toTarget.angle() - distance.angle());

        setTurnGunRight(Utils.normalRelativeAngle(actualAngle + toTurnGun));
        System.out.println(actualAngle + toTurnGun);


    }

    private void rainbow() {
        this.setColors(Color.BLACK, new Color(farbfunktion((int) (this.getGunHeat() * 75)), farbfunktion(120 - (int) (this.getGunHeat() * 75)), 0), Color.BLACK, new Color(farbfunktion(i), farbfunktion(i + 120), farbfunktion(i + 240)), new Color(farbfunktion(i), farbfunktion(i + 120), farbfunktion(i + 240)));
        if (i > 358)
            i = 0;
        else
            i = i + (int) (10 * Math.random()) + 1;

    }

    private int farbfunktion(int x) {
        double a = (double) x;
        double b = 0;

        while (a >= 360) {
            a = a - 360;
        }

        if (a >= 0 && a < 60)
            b = 4.25 * a;
        else if (a >= 60 && a < 180)
            b = 255;
        else if (a >= 180 && a < 240)
            b = -4.25 * (a - 180) + 255;
        else if (a >= 240 && a < 360)
            b = 0;
        else if (a >= 360 && a < 420)
            b = 4.25 * (a - 360);
        else if (a >= 420 && a < 540)
            b = 255;
        else if (a >= 540 && a < 600)
            b = -4.25 * (a - 540) + 255;
        else
            b = 255;
        return (int) b;

    }
}
