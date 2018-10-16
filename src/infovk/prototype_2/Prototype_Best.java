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

    //Initializing StartSetup
    @Override
    protected void start() {
        super.start();
        i = 0;
        rainbow();
        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
    }

    //SettingLoop
    @Override
    protected void loop() {
        super.loop();
        setTurnRadarRight(360);
        double turn = (RobotHelper.RANDOM.nextDouble() * 180) - 90;
        double move = (RobotHelper.RANDOM.nextDouble() * 100) - 50;
        setTurnRight(turn);
        ahead(move);
        rainbow();
    }

    //Searching Enemy, holding Radar on it, firing Cannon, trying to dodge Enemy`s Bullets
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        Point coordinates = getRecentCache(event.getName()).getScannerInfo().getPos();
        Point enemyCoordinates = getRecentCache(event.getName()).getTargetInfo().getPos();
        Point distance = new Point(enemyCoordinates.getX() - coordinates.getX(), enemyCoordinates.getY() - coordinates.getY());

        double absoluteBearing = getHeading() + event.getBearing();
        double turnRadar = absoluteBearing - getRadarHeading();
        double toTurnRadar = Utils.normalRelativeAngle(turnRadar);
        double distanceToEnemy = event.getDistance();

        setTurnRadarRight(toTurnRadar);
        targetGun(event.getName(), distance, enemyCoordinates, coordinates, absoluteBearing, distanceToEnemy);
        double oldEnergy = getCache(event.getName(), 1).getEnergy();
        double newEnergy = event.getEnergy();

        if (oldEnergy > newEnergy) {
            double turn = (RobotHelper.RANDOM.nextDouble() * 90) - 45;
            double move = (RobotHelper.RANDOM.nextDouble() * 300) - 150;
            setTurnRight(turn);
            setAhead(move);
        }
        rainbow();
        scan();
    }

    //Redirecting and correcting Gun toward Enemy, still uncorrect
    public void targetGun(String enemy, Point distance, Point enemyCoordinates, Point coordinates, double bearing, double distanceToEnemy) {
        RobotCache.PositionalRobotCache cache = getRecentCache(enemy);
        double direction = cache.getHeading();

        double turnGun = bearing - getGunHeading();
        double toTurnGun = Utils.normalRelativeAngle(turnGun);

        Point movement = Point.fromPolarCoordinates(direction, getEstimatedVelocity(enemy));
        Point target = enemyCoordinates.add(movement).add(movement).add(movement);//nTurns vorraus, beliebig erweiterbar
        Point toTarget = new Point(target.getX() - coordinates.getX(), target.getY() - coordinates.getY());
        double correctionGun = Utils.normalRelativeAngle(toTarget.angle() - distance.angle());

        setTurnGunRight(toTurnGun + correctionGun);

        scan();
        // System.out.println(toTurnGun);
        //System.out.println(correctionGun);
    }

    //Change Position after hit by Enemy
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        double absoluteBearing = getHeading() + event.getBearing();
        setTurnRight(absoluteBearing);
        double move = RobotHelper.RANDOM.nextDouble() * 199 + 1;
        double random = RobotHelper.RANDOM.nextInt();
        if (random > 0.5) {
            move = move * -1;
        }
        ahead(move);
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


    //Redirecting after Collision with Enemy
    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);

        setTurnRight(90);
        ahead(400);
    }

    //Redirecting after hitting Wall, could be ignored if Method to WallDodge ready
    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        double turnHitWall = 90;
        ahead(200 * -1);
        setTurnRight(turnHitWall);
    }

    //Setting Color from own Bot
    private void rainbow() {

        this.setColors(Color.BLACK, new Color(colorFunction((int) (this.getGunHeat() * 75)), colorFunction(120 - (int) (this.getGunHeat() * 75)), 0), Color.BLACK, Color.WHITE, Color.BLUE);
        if (i > 358)
            i = 0;
        else
            i = i + (int) (10 * Math.random()) + 1;
    }

    //Working through ColorSpectrum
    private int colorFunction(int x) {
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
