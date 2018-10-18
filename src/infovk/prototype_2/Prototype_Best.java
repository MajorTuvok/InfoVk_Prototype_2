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
    private static final int MIN_ROBOT_DISTANCE = 80;
    private static final double MIN_WALL_PERCENTAGE = 0.3;
    private double leftBorder = 0;
    private double lowerBorder = 0;
    private double rightBorder = Double.MAX_VALUE;
    private double upperBorder = Double.MAX_VALUE;

    @Override
    protected BehaviourType getBehaviourType() {
        return BehaviourType.DEFAULT;
    }

    /**
     * Redirecting after Collision with Enemy
     */
    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);

        setTurnRight(randomFixedRange(-135, 135, -46, 46));
        setAhead(randomFixedRange(-500, 500, -300, 300));
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
        double enemyEnergy = event.getEnergy();
        //System.out.println("lastValue=" + lastValue);
        dodgeBullet(lastValue, enemyEnergy);
        scan();
    }

    /**
     * Dodging Bullet, depends on Enemy`s Energy, could triggered by EnergyLoss from hitting Walls or Robots
     */
    private void dodgeBullet(PositionalRobotCache lastValue, double enemyEnergy) {
        if (lastValue != null) {
            double oldEnergy = lastValue.getEnergy();

            if (oldEnergy > enemyEnergy) {
                double turn = (RobotHelper.RANDOM.nextDouble() * 90) - 45;
                setTurnRight(turn);
                setAhead(randomFixedRange(-150, 150, -41, 41));
            }
        }
    }

    /**
     * Setting Radar on Enemy
     */
    private void setRadar(double heading) {
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
    private double randomFixedRange(double min, double max, double exMin, double exMax) {
        assert min <= exMin && exMin <= exMax && exMax <= max;
        boolean aboveEx = RobotHelper.RANDOM.nextBoolean();
        double movement;
        if (aboveEx) {
            movement = RobotHelper.RANDOM.nextDouble() * (max - exMax) + exMax;
        } else {
            movement = RobotHelper.RANDOM.nextDouble() * (exMin - min) + min;
        }
        return movement;
    }

    /**
     * Redirecting and correcting Gun toward Enemy, still incorrect
     */
    private void targetGun(PositionalRobotCache cache, Point distance, Point enemyCoordinates, Point coordinates, double bearing, double distanceToEnemy) {
        double direction = getEstimatedHeading(cache.getName());

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
     * Redirecting after hitting Wall, not in use during dodgeWall active
     */
    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        setTurnRight(randomFixedRange(-180, 180, -91, 91));
        setAhead(randomFixedRange(-500, 500, -300, 300));

    }

    /**
     * Initializing StartSetup
     */
    @Override
    protected void start() {
        super.start();
        lowerBorder = getBattleFieldHeight() * MIN_WALL_PERCENTAGE;
        upperBorder = getBattleFieldHeight() * (1 - MIN_WALL_PERCENTAGE);
        leftBorder = getBattleFieldWidth() * MIN_WALL_PERCENTAGE;
        rightBorder = getBattleFieldWidth() * (1 - MIN_WALL_PERCENTAGE);
    }

    /**
     * Testing if Movement possible
     */
    @Override
    public void setAhead(double distance) {
        dodgeWall(distance);
        //System.out.println("distance=" + distance);
    }

    /**
     * Dodging Walls
     */
    private void dodgeWall(double distance) {
        double x = getX();
        double y = getY();

        if (y >= upperBorder) {
            dodgeRobot(distance * -1);
            //System.out.println("Oben");
        } else {
            if (y <= lowerBorder) {
                dodgeRobot(distance * -1);
                //System.out.println("Unten");

            } else {
                if (x >= rightBorder) {
                    dodgeRobot(distance * -1);
                    //System.out.println("Rechts");

                } else {
                    if (x <= leftBorder) {
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
    private void dodgeRobot(double distance) {

        double x = getX();
        double y = getY();

        Map<String, PositionalRobotCache> enemy = getLatestTargetView();

        for (Map.Entry<String, PositionalRobotCache> entry : enemy.entrySet()) {
            PositionalRobotCache enemyCoordinates = entry.getValue();
            double xEnemy = enemyCoordinates.getTargetInfo().getX();
            double yEnemy = enemyCoordinates.getTargetInfo().getY();

            if (MIN_ROBOT_DISTANCE > Math.sqrt(((x - xEnemy) * (x - xEnemy)) + ((y - yEnemy) * (y - yEnemy)))) {
                setTurnRight(RobotHelper.RANDOM.nextBoolean() ? 90 : -90);
                super.setAhead(distance * -1);
            } else {
                super.setAhead(distance);
            }
        }


    }
}
