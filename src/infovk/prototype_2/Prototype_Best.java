package infovk.prototype_2;


import infovk.prototype_2.helper.Point;
import infovk.prototype_2.helper.RobotCache;
import infovk.prototype_2.helper.RobotHelper;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class Prototype_Best extends RobotBase {

	private int i; //int für farbschleife


    @Override
    protected void start() {
        super.start();
        i = 0;
        rainbow();
        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
    }

    @Override
    protected void loop() {
        super.loop();
        setTurnRadarRightRadians(720);
        //double turn = (RobotHelper.RANDOM.nextDouble() * 180) - 90;
        //double move = (RobotHelper.RANDOM.nextDouble() * 100) - 50;
        //setTurnRight(turn);
        //ahead(move);
        rainbow();

    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) { //not random enough
        super.onHitByBullet(event);
        double bearing = getHeading();
        double dodge = Utils.normalRelativeAngle((bearing - getHeading()));
        // turnRight(dodge);
        System.out.println(dodge);
        // distance = (distance + 10) * -1;
        //ahead(distance);



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

        setTurnRight(90);
        ahead(100);


    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        double turnHitWall = 90;
        ahead(100 * -1);
        setTurnRight(turnHitWall);

        //System.out.println(turnHitWall);

    }

/*
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
        fireRelativeToEnergyAndDistance(3, event.getDistance());

       /* double oldEnergy = getCache(event.getName(), 1).getEnergy();
        double newEnergy = event.getEnergy();

        if (oldEnergy > newEnergy) {
            double turn = (RobotHelper.RANDOM.nextDouble() * 90) - 45;
            double move = (RobotHelper.RANDOM.nextDouble() * 300) - 150;
            setTurnRight(turn);
            ahead(move);
        }*/
    // rainbow();

        //dodgeWall();

    // }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        Point coordinates = getRecentCache(event.getName()).getScannerInfo().getPos();
        Point enemyCoordinates = getRecentCache(event.getName()).getTargetInfo().getPos();
        Point distance = new Point(enemyCoordinates.getX() - coordinates.getX(), enemyCoordinates.getY() - coordinates.getY());

        double absoluteBearing = getHeading() + event.getBearing();
        double turnRadar = absoluteBearing - getRadarHeading();
        double toTurnRadar = Utils.normalRelativeAngle(turnRadar);

        setTurnRadarRight(toTurnRadar);
        targetGun(event.getName(), distance, enemyCoordinates, coordinates, absoluteBearing);
        fireRelativeToEnergyAndDistance(1, event.getDistance());
        scan();
    }

    public void targetGun(String enemy, Point distance, Point enemyCoordinates, Point coordinates, double bearing) { //close to finish
        RobotCache.PositionalRobotCache cache = getRecentCache(enemy);
        double velocity = cache.getVelocity();
        double direction = cache.getHeading();

        double turnGun = bearing - getGunHeading();
        double toTurnGun = Utils.normalRelativeAngle(turnGun);




        Point movement = Point.fromPolarCoordinates(direction, getEstimatedVelocity(enemy));
        Point target = enemyCoordinates.add(movement).add(movement);//nTurns vorraus, beliebig erweiterbar
        Point toTarget = new Point(target.getX() - coordinates.getX(), target.getY() - coordinates.getY());
        double correctionGun = Utils.normalRelativeAngle(toTarget.angle() - distance.angle());

        setTurnGunRight(toTurnGun + correctionGun);
        System.out.println(toTurnGun);
        System.out.println(correctionGun);

    }


    private void rainbow() {

        this.setColors(Color.BLACK, new Color(colorfunction((int) (this.getGunHeat() * 75)), colorfunction(120 - (int) (this.getGunHeat() * 75)), 0), Color.BLACK, Color.WHITE, Color.BLUE);
        if (i > 358)
            i = 0;
        else
            i = i + (int) (10 * Math.random()) + 1;

    }

    private int colorfunction(int x) {
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
