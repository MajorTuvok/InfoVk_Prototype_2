package infovk.prototype_2;

import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import infovk.prototype_2.helper.RobotHelper;
import infovk.prototype_2.helper.RobotInfo;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.*;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.util.Map;

public abstract class BaseRobotBehaviour<T extends RobotBase> {
    private T robot;

    protected BaseRobotBehaviour(T robot) {
        this.robot = robot;
    }

    public void setTurnGunRight(double degrees) {
        getRobot().setTurnGunRight(degrees);
    }

    public void setAhead(double distance) {
        getRobot().setAhead(distance);
    }

    protected void setTurnRadarRight(double degrees) {
        getRobot().setTurnRadarRight(degrees);
    }

    protected void setTurnRight(double degrees) {
        getRobot().setTurnRadarRight(degrees);
    }

    public double getGunTurnRemaining() {
        return getRobot().getGunTurnRemaining();
    }

    public double getRadarTurnRemaining() {
        return getRobot().getRadarTurnRemaining();
    }

    public double getBattleFieldWidth() {
        return getRobot().getBattleFieldWidth();
    }

    public double getBattleFieldHeight() {
        return getRobot().getBattleFieldHeight();
    }

    public double getHeading() {
        return getRobot().getHeading();
    }

    public double getHeight() {
        return getRobot().getHeight();
    }

    public double getWidth() {
        return getRobot().getWidth();
    }

    public String getName() {
        return getRobot().getName();
    }

    public double getX() {
        return getRobot().getX();
    }

    public double getY() {
        return getRobot().getY();
    }

    public double getGunHeading() {
        return getRobot().getGunHeading();
    }

    public long getTime() {
        return getRobot().getTime();
    }

    public double getVelocity() {
        return getRobot().getVelocity();
    }

    public double getEnergy() {
        return getRobot().getEnergy();
    }

    public double getEnergyPowerFactor() {
        return getRobot().getEnergyPowerFactor();
    }

    public void setEnergyPowerFactor(double energyPowerFactor) {
        getRobot().setEnergyPowerFactor(energyPowerFactor);
    }

    public double getDisFactor() {
        return getRobot().getDisFactor();
    }

    public void setDisFactor(double disFactor) {
        getRobot().setDisFactor(disFactor);
    }

    public Map<String, PositionalRobotCache> getLatestTargetView() {
        return getRobot().getLatestTargetView();
    }

    public double getRadarHeading() {
        return getRobot().getRadarHeading();
    }

    protected T getRobot() {
        return robot;
    }

    public void onHitRobot(HitRobotEvent event) {

    }

    public void onHitWall(HitWallEvent ex) {

    }

    public void onRoundEnded(RoundEndedEvent event) {

    }

    public void onScannedRobot(ScannedRobotEvent event) {

    }

    public void onRobotDeath(RobotDeathEvent event) {

    }

    public PositionalRobotCache getRecentCache(String target) {
        return getRobot().getRecentCache(target);
    }

    public PositionalRobotCache getCache(String target, int index) {
        return getRobot().getCache(target, index);
    }

    public double getEstimatedVelocity(PositionalRobotCache target) {
        return getRobot().getEstimatedAcceleration(target);
    }

    public void fireRelativeToEnergy(RobotInfo target, double baseVal) {
        getRobot().fireRelativeToEnergy(target, baseVal);
    }

    public void fireRelativeToEnergyAndDistance(RobotInfo target, double baseVal, double distance) {
        getRobot().fireRelativeToEnergyAndDistance(target, baseVal, distance);
    }

    public double getPowerRelToEnergy(double baseVal) {
        return getRobot().getPowerRelToEnergy(baseVal);
    }

    public double getPowerRelToEnergyAndDistance(double baseVal, double distance) {
        return getRobot().getPowerRelToEnergyAndDistance(baseVal, distance);
    }

    public double getEstimatedHeading(PositionalRobotCache target) {
        return getRobot().getEstimatedRotation(target);
    }

    public void onDeath(DeathEvent event) {

    }

    public void ahead(double distance) {
        getRobot().ahead(distance);
    }

    public void setColors(Color bodyColor, Color gunColor, Color radarColor, Color bulletColor, Color scanArcColor) {
        getRobot().setColors(bodyColor, gunColor, radarColor, bulletColor, scanArcColor);
    }

    public void turnGunRight(double degrees) {
        getRobot().turnGunRight(degrees);
    }

    public void turnRadarRight(double degrees) {
        getRobot().turnRadarRight(degrees);
    }

    public void onBulletHit(BulletHitEvent ex) {

    }

    public void onBulletHitBullet(BulletHitBulletEvent ex) {

    }

    public void onBulletMissed(BulletMissedEvent ex) {

    }

    public void onHitByBullet(HitByBulletEvent ex) {

    }

    public void scan() {
        getRobot().scan();
    }

    protected abstract void start();

    protected abstract void loop();

    /**
     * save Movement over BotSize
     */
    protected double randomFixedRange(double min, double max, double exMin, double exMax) {
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
}
