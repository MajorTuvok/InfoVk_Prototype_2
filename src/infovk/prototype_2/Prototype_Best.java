package infovk.prototype_2;


import infovk.prototype_2.helper.Point;
import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import infovk.prototype_2.helper.RobotHelper;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.util.Map;

public class Prototype_Best extends RobotBase {

    /**
     * Initializing StartSetup
     */
    @Override
    protected void start() {
        super.start();
    }

    /**
     * Setting Loop
     */
    @Override
    protected void loop() {
        super.loop();
        setTurnRadarRight(360);
        double turn = randomFixedRange(-90, 90, -10, 10);
        double move = randomFixedRange(-150, 150, -41, 41
        );
        setTurnRight(turn);
        setAhead(move);
        //rainbow();
    }

    /**
     * Searching Enemy, pointing Radar, firing Cannon, trying to dodge
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        PositionalRobotCache cache = getRecentCache(event.getName());
        Point coordinates = cache.getScannerInfo().getPos();
        Point enemyCoordinates = cache.getTargetInfo().getPos();
        Point distance = new Point(enemyCoordinates.getX() - coordinates.getX(), enemyCoordinates.getY() - coordinates.getY());

        double absoluteBearing = getHeading() + event.getBearing();
        double turnRadar = absoluteBearing - getRadarHeading();
        double distanceToEnemy = event.getDistance();

        setRadar(turnRadar);
        targetGun(cache, distance, enemyCoordinates, coordinates, absoluteBearing, distanceToEnemy);
        fireRelativeToEnergyAndDistance(cache.getTargetInfo(), 3, distanceToEnemy);

        PositionalRobotCache lastValue = getCache(event.getName(), 1);
        //System.out.println("lastValue=" + lastValue);

        if (lastValue != null) {
            double oldEnergy = lastValue.getEnergy();
            double newEnergy = event.getEnergy();

            if (oldEnergy > newEnergy) {
                double turn = (RobotHelper.RANDOM.nextDouble() * 90) - 45;
                setTurnRight(turn);
                setAhead(randomFixedRange(-150, 150, -41, 41));
            }
        }
        scan();
    }

    /**
     * Setting Radar on Enemy
     */
    public void setRadar(double heading) {
        double toTurnRadar = Utils.normalRelativeAngle(heading);
        if (toTurnRadar < 0) {
            toTurnRadar = toTurnRadar + 1;
        } else {
            toTurnRadar = toTurnRadar - 1;
        }

        setTurnRadarRight(toTurnRadar);

    }

    /**
     * save Movement over BotSize
     */
    public double randomFixedRange(double min, double max, double exMin, double exMax) {
        assert min <= exMin && exMin <= exMax && exMax <= max;
        boolean aboveEx = RobotHelper.RANDOM.nextBoolean();
        double movement = 0;
        if (aboveEx) {
            movement = RobotHelper.RANDOM.nextDouble() * (max - exMax) + exMax;
        } else {
            movement = RobotHelper.RANDOM.nextDouble() * (exMin - min) + min;
        }
        return movement;
    }

    /**
     * Redirecting and correcting Gun toward Enemy, still uncorrect
     */
    public void targetGun(PositionalRobotCache cache, Point distance, Point enemyCoordinates, Point coordinates, double bearing, double distanceToEnemy) {
        double direction = cache.getHeading();

        double turnGun = bearing - getGunHeading();
        double toTurnGun = Utils.normalRelativeAngle(turnGun);

        Point movement = Point.fromPolarCoordinates(direction, getEstimatedVelocity(cache.getName()));
        Point target = enemyCoordinates.add(movement).add(movement);//nTurns vorraus, beliebig erweiterbar
        Point toTarget = new Point(target.getX() - coordinates.getX(), target.getY() - coordinates.getY());
        double correctionGun = Utils.normalRelativeAngle(toTarget.angle() - distance.angle());

        setTurnGunRight(toTurnGun + correctionGun);
    }

    /**
     * Change Position after Hit by Enemy
     */
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
        setAhead(move);
    }

    /**
     * Dodging Walls
     */
    public void dodgeWall(double distance) {
        double x = getX();
        double y = getY();

        double wallTop = getBattleFieldHeight() * 0.7;
        double wallRight = getBattleFieldWidth() * 0.7;
        double wallBottom = getBattleFieldHeight() * 0.3;
        double wallLeft = getBattleFieldWidth() * 0.3;

        if (y >= wallTop) {
            setTurnRight(getHeading() * -1);
            dodgeRobot(distance * -1);
            //System.out.println("Oben");
        } else {
            if (y <= wallBottom) {
                setTurnRight(getHeading() * -1);
                dodgeRobot(distance * -1);
                //System.out.println("Unten");

            } else {
                if (x >= wallRight) {
                    setTurnRight(getHeading() * -1);
                    dodgeRobot(distance * -1);
                    //System.out.println("Rechts");

                } else {
                    if (x <= wallLeft) {
                        setTurnRight(getHeading() * -1);
                        dodgeRobot(distance * -1);
                        //System.out.println("Links");

                    } else {
                        dodgeRobot(distance);
                    }
                }
            }
        }
    }

    /**
     * Dodging Robot
     */
    public void dodgeRobot(double distance) {

        double x = getX();
        double y = getY();

        Map<String, PositionalRobotCache> enemy = getLatestTargetView();

        for (Map.Entry<String, PositionalRobotCache> entry : enemy.entrySet()) {
            PositionalRobotCache enemyCoordinates = entry.getValue();
            double xEnemy = enemyCoordinates.getTargetInfo().getX();
            double yEnemy = enemyCoordinates.getTargetInfo().getY();

            if (80 > Math.sqrt(((x - xEnemy) * (x - xEnemy)) + ((y - yEnemy) * (y - yEnemy)))) {
                super.setAhead(distance * -1);
            } else {
                super.setAhead(distance);
            }
        }


    }

    /**
     * Testing if Movement possible
     */
    @Override
    public void setAhead(double distance) {
        dodgeWall(distance);
        dodgeRobot(distance);
        //System.out.println("distance=" + distance);
    }

    /**
     * Redirecting after Collision with Enemy
     */
    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);

        setTurnRight(90);
        setAhead(400);
    }

    /**
     * Redirecting after hitting Wall, not in use during dodgeWall active
     */
    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        double turnHitWall = 120;
        setTurnRight(turnHitWall);
        setAhead(200);

    }
}
