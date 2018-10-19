package infovk.random_bots.bunto_bot.helper;

import robocode.Bullet;
import robocode.Robot;

import java.util.Objects;

public class BulletCache {
    private final Bullet mBullet;

    private BulletCache(Bullet bullet) {
        mBullet = Objects.requireNonNull(bullet, "Cannot construct BulletCache without a Bullet to cache!");
    }

    public static BulletCache getInstance(Bullet bullet) {
        return new BulletCache(bullet);
    }

    public static PositionalBulletCache getInstanceFiredByRobot(Bullet bullet, RobotInfo info) {
        return new PositionalBulletCache(bullet, info);
    }

    public static PositionalBulletCache getInstanceFiredByRobot(Bullet bullet, Robot robot) {
        return getInstanceFiredByRobot(bullet, RobotInfo.getInstance(robot));
    }

    public static TargetedBulletCache getInstanceFiredByTargetingRobot(Bullet bullet, RobotInfo info, RobotInfo target) {
        return new TargetedBulletCache(bullet, info, target);
    }

    public static TargetedBulletCache getInstanceFiredByTargetingRobot(Bullet bullet, Robot source, RobotInfo target) {
        return getInstanceFiredByTargetingRobot(bullet, RobotInfo.getInstance(source), target);
    }

    public double getHeading() {
        return mBullet.getHeading();
    }

    public double getHeadingRadians() {
        return mBullet.getHeadingRadians();
    }

    public String getName() {
        return mBullet.getName();
    }

    public double getPower() {
        return mBullet.getPower();
    }

    public double getVelocity() {
        return mBullet.getVelocity();
    }

    public String getVictim() {
        return mBullet.getVictim();
    }

    public double getX() {
        return mBullet.getX();
    }

    public double getY() {
        return mBullet.getY();
    }

    public boolean isActive() {
        return mBullet.isActive();
    }

    @Override
    public int hashCode() {
        return mBullet.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BulletCache)) return false;

        BulletCache that = (BulletCache) o;

        return mBullet.equals(that.mBullet);
    }

    public static class PositionalBulletCache extends BulletCache {
        private final RobotInfo mSource;

        private PositionalBulletCache(Bullet bullet, RobotInfo source) {
            super(bullet);
            mSource = source;
        }

        public RobotInfo getSource() {
            return mSource;
        }
    }

    public static final class TargetedBulletCache extends PositionalBulletCache {
        private final RobotInfo mTarget;

        public TargetedBulletCache(Bullet bullet, RobotInfo source, RobotInfo target) {
            super(bullet, source);
            mTarget = target;
        }

        public RobotInfo getTarget() {
            return mTarget;
        }
    }
}
