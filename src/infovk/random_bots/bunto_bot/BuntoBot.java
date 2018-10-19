
package infovk.random_bots.bunto_bot;


import infovk.random_bots.bunto_bot.helper.RobotCache.PositionalRobotCache;
import infovk.random_bots.bunto_bot.helper.RobotHelper;
import infovk.random_bots.bunto_bot.helper.Vector2D;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.*;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.util.Map;

public class BuntoBot extends RobotBase {
    private static final int MIN_ROBOT_DISTANCE = 100;
    private static final double MIN_WALL_PERCENTAGE = 0.5;
    private double leftBorder = 0;
    private double lowerBorder = 0;
    private double rightBorder = Double.MAX_VALUE;
    private double upperBorder = Double.MAX_VALUE;
    private long time;
    private boolean positiveMovement;
    private Vector2D attackVector = null;
    private String target = null;
    @Override
    protected BehaviourType getBehaviourType() {
        return BehaviourType.DEFAULT;
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
     * Setting Radar on Enemy
     */
    private void setRadar(double heading) {
        double toTurnRadar = Utils.normalRelativeAngle(heading);
        if (toTurnRadar < 0) {
            toTurnRadar = toTurnRadar + randomFixedRange(1, 4, 2.5, 2.5);
        } else {
            toTurnRadar = toTurnRadar - randomFixedRange(1, 4, 2.5, 2.5);
        }

        setTurnRadarRight(toTurnRadar);

    }

    /**
     * Redirecting after Collision with Enemy
     */
    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);

        setTurnRight(randomFixedRange(-135, 135, -46, 46));
        setAhead(randomFixedRange(-300, 300, -70, 70));
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        if (attackVector != null) {
            g.setColor(Color.RED);
            g.drawLine((int) this.getX(), (int) this.getY(), (int) (attackVector.getX() + this.getX()), (int) (attackVector.getY() + this.getY()));
            g.drawRect((int) (attackVector.getX() + this.getX() - this.getWidth() / 2), (int) (attackVector.getY() + this.getY() - this.getWidth() / 2), (int) this.getWidth(), (int) this.getHeight());
        }
        g.setColor(Color.gray);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 + 3), (int) (this.getY() - this.getWidth() / 2 - 7), 30, 50);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 - 7), (int) (this.getY() - this.getWidth() / 2 + 3), 50, 30);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 - 4), (int) (this.getY() - this.getWidth() / 2 - 4), 44, 44);
        g.setColor(Color.green);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 + 10), (int) (this.getY() - this.getWidth() / 2 + 6), 16, 24);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 + 6), (int) (this.getY() - this.getWidth() / 2 + 10), 24, 16);
        g.fillRect((int) (this.getX() - this.getWidth() / 2 + 8), (int) (this.getY() - this.getWidth() / 2 + 8), 20, 20);
        g.setColor(Color.white);
        g.fillRect((int) (this.getX() + 2), (int) (this.getY() + 2), 6, 6);
    }

    /**
     * Setting Loop
     */
    @Override
    protected void loop() {
        super.loop();
        setAhead(randomFixedRange(-150, 150, -41, 41));
        setTurnRadarRight(360);
        //rainbow();
    }

    /**
     * Searching Enemy, pointing Radar, firing Cannon, trying to dodge
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        target = event.getName();
        if (target == null) return;
        performFire();

        PositionalRobotCache cache = getRecentCache(target);
        double absoluteBearing = RobotHelper.absoluteBearing(this, cache.getBearing());
        PositionalRobotCache lastValue = getCache(target, 1);
        //System.out.println("lastValue=" + lastValue);
        double turnRadar = RobotHelper.absoluteBearing(this, event.getBearing()) - getRadarHeading();
        setRadar(turnRadar);
        dodgeBullet(lastValue, cache.getEnergy(), absoluteBearing);
        scan();
    }

    private void performFire() {
        PositionalRobotCache cache = getRecentCache(target);
        Vector2D coordinates = cache.getScannerInfo().getPos();
        Vector2D enemyCoordinates = cache.getTargetInfo().getPos();
        Vector2D vecToEnemy = enemyCoordinates.subtract(coordinates);

        evaluateAttackVector(cache, vecToEnemy);
        targetGun();
        fireRelativeToEnergyAndDistance(cache.getTargetInfo(), 3, attackVector.length());
    }

    private void evaluateAttackVector(PositionalRobotCache cache, Vector2D vecToEnemy) {
        double velocity = cache.getVelocity();
        double rotation = getEstimatedRotation(cache);
        double startHeading = cache.getHeading();

        attackVector = vecToEnemy;
        double distance = attackVector.length();
        double bulletSpeed = Rules.getBulletSpeed(getPowerRelToEnergyAndDistance(3, distance));
        long newMovedTurns = Math.round(distance / bulletSpeed);

        defineAttackVectorMorePrecisely(vecToEnemy, newMovedTurns, velocity, rotation, startHeading);
    }

    private void defineAttackVectorMorePrecisely(Vector2D vecToEnemy, long newMovedTurns, double velocity, double rotation, double startHeading) {
        int iterationCounter = 0;
        long enemyMovedTurns;
        double distance;
        double bulletSpeed;
        do {
            enemyMovedTurns = newMovedTurns;
            attackVector = getFastEnemyPredictedPosition(vecToEnemy, startHeading, velocity, rotation, enemyMovedTurns);
            distance = attackVector.length();
            bulletSpeed = Rules.getBulletSpeed(getPowerRelToEnergyAndDistance(3, distance));
            newMovedTurns = (long) Math.ceil(distance / bulletSpeed);
            ++iterationCounter;
        }
        while (newMovedTurns - enemyMovedTurns > 1); //if we are correcting less then 5 turns, it should be good
        System.out.println("Evaluated Targeting with " + iterationCounter + " iterations");
    }

    /*
      Our original Method of Predicting the enemies position was too slow (manually retracing every of our enemies future steps), so we tried doing some Math and here it is,
      the ultimate fast Enemy position Prediction. How do you get it (x-Coord):
         enemyMovedVec = enemyVec + sum[0; turns](velocity*sin(enemyHeading+rotation*i))
      The sum now collects all values of velocity*sin(enemyHeading+rotation*i) until turns. Because turns are the smallest possible units, this is essentially an Integral.
      <=>enemyMovedVec = enemyVec + integral(velocity*sin(enemyHeading+rotation)
      And Bronstein then tells us the integral shown below in the Mehtod
      !!!!!:
      Notice however, that acceleration no longer is taken into account, but because we are estimating
     */
    private Vector2D getFastEnemyPredictedPosition(Vector2D vecToEnemy, double enemyHeading, double velocity, double rotation, long turns) {
        if (Math.abs(rotation) > 0) {
            double speedPerRotation = velocity / rotation;
            double rotatedHeading = enemyHeading - rotation * turns;
            double integralX = (Math.cos(rotatedHeading) - Math.cos(enemyHeading)) * speedPerRotation;
            double integralY = (Math.sin(rotatedHeading) - Math.sin(enemyHeading)) * speedPerRotation;
            return new Vector2D(
                    vecToEnemy.getX() - integralX,
                    vecToEnemy.getY() - integralY
            );
        }
        return vecToEnemy.add(Vector2D.fromPolarCoordinates(enemyHeading, velocity * turns));
    }

    /**
     * Dodging Bullet, depends on Enemy`s Energy, could triggered by EnergyLoss from hitting Walls or Robots
     */
    private void dodgeBullet(PositionalRobotCache lastValue, double enemyEnergy, double bearing) {
        if (lastValue != null) {
            double oldEnergy = lastValue.getEnergy();
            double turn;
            if (oldEnergy > enemyEnergy) {
                if (bearing < 0) {
                    turn = bearing + 60;
                    //System.out.println(turn);
                } else {
                    turn = bearing - 60;
                    //System.out.println(turn);
                }

                setTurnRight(turn);
                setAhead(randomFixedRange(-200, 200, -60, 60));
            }
        }
    }

    private void targetGun() {
        double targetAngle = attackVector.angle();
        double desiredMovement = targetAngle - Utils.normalRelativeAngle(getGunHeading());
        setTurnGunRight(desiredMovement);
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
     * Change Position after Hit by Enemy
     */
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        double absoluteBearing = getHeading() + event.getBearing();
        setTurnRight(absoluteBearing);
        double move = RobotHelper.RANDOM.nextDouble() * 99 + 1;
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
        setAhead(randomFixedRange(-300, 300, -100, 100));

    }


    /**
     * Testing if Movement possible
     */
    @Override
    public void setAhead(double distance) {

        if (getTime() > time + 10) {
            if (distance > 0) {
                positiveMovement = true;
            } else {
                positiveMovement = false;
            }
            time = getTime();

        } else {
            if (positiveMovement) {
                distance = Math.abs(distance);
            } else {
                distance = Math.abs(distance) * -1;
            }

        }
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
