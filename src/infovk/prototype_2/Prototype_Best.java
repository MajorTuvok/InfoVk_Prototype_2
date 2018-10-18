
package infovk.prototype_2;


import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import infovk.prototype_2.helper.RobotHelper;
import infovk.prototype_2.helper.Vector2D;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.*;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.util.Map;

public class Prototype_Best extends RobotBase {
    private static final int MIN_ROBOT_DISTANCE = 100;
    private static final double MIN_WALL_PERCENTAGE = 0.5;
    private double leftBorder = 0;
    private double lowerBorder = 0;
    private double rightBorder = Double.MAX_VALUE;
    private double upperBorder = Double.MAX_VALUE;
    private long time;
    private boolean positiveMovement;
    private Vector2D toTarget;

    private String lastTarget = "";
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
     * Setting Loop
     */
    @Override
    protected void loop() {
        super.loop();
        double move = randomFixedRange(-150, 150, -41, 41);
        setAhead(move);
        setTurnRadarRight(360);

        //rainbow();
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


    /**
     * Searching Enemy, pointing Radar, firing Cannon, trying to dodge
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        lastTarget = event.getName();
        PositionalRobotCache cache = getRecentCache(event.getName());
        Vector2D coordinates = cache.getScannerInfo().getPos();
        Vector2D enemyCoordinates = cache.getTargetInfo().getPos();
        Vector2D vecToEnemy = enemyCoordinates.subtract(coordinates);

        double absoluteBearing = RobotHelper.absoluteBearing(this, event.getBearing());
        double turnRadar = absoluteBearing - getRadarHeading();

        setRadar(turnRadar);
        targetGun(cache, vecToEnemy, enemyCoordinates, coordinates, absoluteBearing);
        fireRelativeToEnergyAndDistance(cache.getTargetInfo(), 3, vecToEnemy.length());

        PositionalRobotCache lastValue = getCache(event.getName(), 1);
        double enemyEnergy = event.getEnergy();
        //System.out.println("lastValue=" + lastValue);
        dodgeBullet(lastValue, enemyEnergy, absoluteBearing);
        scan();
    }

    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);
        if (toTarget != null) {
        	g.setColor(Color.RED);
    		g.drawLine((int)this.getX(),(int)this.getY(),(int) (toTarget.getX()+ this.getX() -this.getWidth()/2 + 36*Math.random()),(int)(toTarget.getY()+this.getY()-this.getWidth()/2 + 36*Math.random()));
    		g.drawRect((int)(toTarget.getX()+ this.getX() -this.getWidth()/2), (int)(toTarget.getY()+this.getY()-this.getWidth()/2), (int)this.getWidth(), (int)this.getHeight());
        }
        g.setColor(Color.gray);
		g.fillRect((int)(this.getX()-this.getWidth()/2+3), (int)(this.getY()-this.getWidth()/2-7), 30, 50);
		g.fillRect((int)(this.getX()-this.getWidth()/2-7), (int)(this.getY()-this.getWidth()/2+3), 50, 30);
		g.fillRect((int)(this.getX()-this.getWidth()/2-4), (int)(this.getY()-this.getWidth()/2-4), 44, 44);
		g.setColor(Color.green);
		g.fillRect((int)(this.getX()-this.getWidth()/2+10), (int)(this.getY()-this.getWidth()/2+6), 16, 24);
		g.fillRect((int)(this.getX()-this.getWidth()/2+6), (int)(this.getY()-this.getWidth()/2+10), 24, 16);
		g.fillRect((int)(this.getX()-this.getWidth()/2+8), (int)(this.getY()-this.getWidth()/2+8), 20, 20);
		g.setColor(Color.white);
        g.fillRect((int) (this.getX() + 2), (int) (this.getY() + 2), 6, 6);
    }

    /**
     * Redirecting and correcting Gun toward Enemy, still uncorrect
     */
    private void targetGun(PositionalRobotCache cache, Vector2D distance, Vector2D enemyCoordinates, Vector2D coordinates, double bearing) {
        double velocity = cache.getVelocity();

        double turnGun = bearing - getGunHeading();
        double toTurnGun = Utils.normalRelativeAngle(turnGun);

        this.toTarget = evaluateTargetPoint(null, enemyCoordinates, coordinates, bearing, -velocity, 1);
        double correctionGun = Utils.normalRelativeAngle(toTarget.angleFrom(distance));

        setTurnGunRight(toTurnGun + correctionGun);
    }

    private Vector2D evaluateTargetPoint(Graphics2D g, Vector2D enemyCoordinates, Vector2D coordinates, double direction, double velocity, int turns) {
        if (velocity == 0) return enemyCoordinates;
        Vector2D movement = Vector2D.fromPolarCoordinates(direction, velocity);
        Vector2D target = enemyCoordinates.add(movement);
        if (g != null) {
            g.drawLine((int) Math.round(enemyCoordinates.getX()), (int) Math.round(enemyCoordinates.getY()), (int) Math.round(target.getX()), (int) Math.round(target.getY()));
        }
        Vector2D toTarget = coordinates.subtract(target);

        double targetDis = toTarget.length();
        double speed = Rules.getBulletSpeed(getPowerRelToEnergyAndDistance(3, targetDis));

        Vector2D bulletMovement = toTarget.normalize().multiply(turns * speed);
        if (bulletMovement.length() >= targetDis || turns > 10) return toTarget;
        return evaluateTargetPoint(g, target, coordinates, direction, velocity, turns + 1);
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
