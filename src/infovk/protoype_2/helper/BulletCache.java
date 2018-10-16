package infovk.protoype_2.helper;

import robocode.Bullet;

public class BulletCache {
    private final Bullet mBullet;

    public BulletCache(Bullet bullet) {
        mBullet = bullet;
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
}
