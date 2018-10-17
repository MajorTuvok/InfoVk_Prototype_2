package infovk.prototype_2;

import infovk.prototype_2.helper.BulletCache;
import infovk.prototype_2.helper.BulletCache.PositionalBulletCache;
import infovk.prototype_2.helper.BulletCache.TargetedBulletCache;
import infovk.prototype_2.helper.Constants;
import infovk.prototype_2.helper.RobotCache;
import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import infovk.prototype_2.helper.RobotInfo;
import robocode.Bullet;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.*;
import robocode.ScannedRobotEvent;

import java.util.*;

public class RobotBase extends SimpleRobot implements Constants {
    private static int HISTORY_SIZE = 5;
    private double energyPowerFactor;
    private double disFactor;
    private BulletManager mBulletManager;
    private RobotHistory mRobotHistory;

    public RobotBase() {
        setEnergyPowerFactor(40);
        setDisFactor(50);
        mRobotHistory = new RobotHistory();
        mBulletManager = new BulletManager();
    }

    protected double getEnergyPowerFactor() {
        return energyPowerFactor;
    }

    protected void setEnergyPowerFactor(double energyPowerFactor) {
        this.energyPowerFactor = energyPowerFactor;
    }

    protected double getDisFactor() {
        return disFactor;
    }

    protected void setDisFactor(double disFactor) {
        this.disFactor = disFactor;
    }

    /**
     * use {@link #fire(double, RobotInfo)}
     */
    @Override
    @Deprecated
    public void fire(double power) {
        mBulletManager.onFire(new FireInfo(getTime(), power, null));
    }

    /**
     *
     * use {@link #fire(double, RobotInfo)}
     */
    @Override
    @Deprecated
    public Bullet fireBullet(double power) {
        throw new UnsupportedOperationException("No longer supported!");
    }

    public void fire(double power, RobotInfo target) {
        mBulletManager.onFire(new FireInfo(getTime(), power, target));
    }

    @Override
    public void run() {

        if (behavior != null) {
            behavior.start();
        }
        start();
        execute();

        while (true) {

            if (behavior != null) {
                behavior.execute();
            }
            loop();
            execute();
        }
    }

    protected void setAdjustToTurns() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
    }

    @Override
    public void onBulletHit(BulletHitEvent ex) {
        super.onBulletHit(ex);
        mBulletManager.onBulletHit(ex);
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent ex) {
        super.onBulletHitBullet(ex);
        mBulletManager.onBulletHitBullet(ex);
    }

    protected void start() {

    }

    protected void loop() {

    }

    @Override
    public void onBulletMissed(BulletMissedEvent ex) {
        super.onBulletMissed(ex);
        mBulletManager.onBulletMissed(ex);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent ex) {
        super.onScannedRobot(ex);
        mRobotHistory.updateCache(ex, this);
    }

    protected PositionalRobotCache getRecentCache(String target) {
        return mRobotHistory.getRecentCacheForTarget(target);
    }

    protected PositionalRobotCache getCache(String target, int index) {
        return mRobotHistory.getCache(target, index);
    }

    protected double getEstimatedVelocity(String target) {
        Set<PositionalRobotCache> caches = mRobotHistory.getCompleteCacheForTarget(target);
        double fac = 1;
        int count = caches.size() - 1;
        double velocity = 0;
        for (PositionalRobotCache cache : caches) {
            if (count >= 1) fac /= 2;
            velocity += fac * count * cache.getVelocity();
            count--;
        }
        return velocity;
    }

    protected void fireRelativeToEnergy(RobotInfo target, double baseVal) {
        fire(Math.max(Math.min(baseVal * getEnergy() / energyPowerFactor, Rules.MAX_BULLET_POWER), Rules.MIN_BULLET_POWER), target);
    }

    protected void fireRelativeToEnergyAndDistance(RobotInfo target, double baseVal, double distance) {
        fireRelativeToEnergy(target, baseVal - (int) distance / disFactor);
    }

    private Bullet performFireBullet(double power) {
        return super.fireBullet(power);
    }

    private final class BulletManager {
        private Set<PositionalBulletCache> mBullets;
        private Map<String, Integer> mHitBullets;
        private Map<String, Integer> mMissedBullets;
        private Map<String, Integer> mShieldedBullets;

        private BulletManager() {
            mBullets = new HashSet<>();
            mHitBullets = new HashMap<>();
            mMissedBullets = new HashMap<>();
            mShieldedBullets = new HashMap<>();
        }

        public void onFire(FireInfo info) {
            Bullet b = RobotBase.this.performFireBullet(info.getPower());
            if (info.getTarget() == null) {
                System.err.println("Firing Bullet (power=" + info.getPower() + ") without target!");
                onFireBullet(b);
            } else {
                if (DEBUG) {
                    System.out.println("Firing Bullet at " + info.getTarget().getName() + " with Power " + info.getPower());
                }
                onFireBullet(b, info.getTarget());
            }
        }

        private void onFireBullet(Bullet bullet) {
            if (bullet == null) {
                if (DEBUG) System.out.println("Could not fire null Bullet!");
                return;
            }
            mBullets.add(BulletCache.getInstanceFiredByRobot(bullet, RobotBase.this));
            System.err.println("Fired Bullet without specifying Target. This is not recommended, because it prevents recognizing missed Bullets.");
        }

        private void onFireBullet(Bullet bullet, RobotInfo target) {
            if (bullet == null) {
                if (DEBUG) System.out.println("Could not fire null Bullet!");
                return;
            }
            mBullets.add(BulletCache.getInstanceFiredByTargetingRobot(bullet, RobotBase.this, target));
        }

        private void onBulletHit(BulletHitEvent e) {
            PositionalBulletCache cache = getCacheForBullet(e.getBullet());
            if (cache == null) {
                System.err.println("Bullet hit " + e.getName() + " which was not registered to the Bullet History, cannot determine statistic!");
                return;
            }
            if (!mBullets.remove(cache)) {
                System.err.println("Failed to remove Bullet from Cache!");
                return;
            }
            handleBulletHit(e, cache);
        }

        private void handleBulletHit(BulletHitEvent e, PositionalBulletCache cache) {
            boolean hitTarget = true;
            if (cache instanceof TargetedBulletCache) {
                hitTarget = e.getName().equals(((TargetedBulletCache) cache).getTarget().getName());
            } else {
                if (DEBUG) {
                    System.out.println("Because no target was specified when firing Bullet, assuming that " + e.getName() + " was targeted and hit.");
                }
            }
            if (hitTarget) {
                incrementHitBullets(e.getName());
            } else {
                incrementMissedBullets(e.getName());
            }
        }

        private void onBulletHitBullet(BulletHitBulletEvent e) {
            PositionalBulletCache cache = getCacheForBullet(e.getBullet());
            if (cache == null) {
                System.err.println("A Bullet which was not registered to the Bullet History another Bullet, cannot determine statistic!");
                return;
            }
            if (!mBullets.remove(cache)) {
                System.err.println("Failed to remove Bullet from Cache!");
                return;
            }
            if (!(cache instanceof TargetedBulletCache)) {
                System.err.println("Because Cached Bullet (which hit another Bullet) was not registered with a target, cannot determine Targeting Statistic!");
                return;
            }
            incrementShieldedBullets(((TargetedBulletCache) cache).getTarget().getName());
        }

        private void onBulletMissed(BulletMissedEvent e) {
            PositionalBulletCache cache = getCacheForBullet(e.getBullet());
            if (cache == null) {
                System.err.println("A Bullet which was not registered to the Bullet History missed completly, cannot determine statistic!");
                return;
            }
            if (!mBullets.remove(cache)) {
                System.err.println("Failed to remove Bullet from Cache!");
                return;
            }
            if (!(cache instanceof TargetedBulletCache)) {
                System.err.println("Because Cached Bullet (which missed) was not registered with a target, cannot determine Targeting Statistic!");
                return;
            }
            incrementMissedBullets(((TargetedBulletCache) cache).getTarget().getName());
        }

        private PositionalBulletCache getCacheForBullet(Bullet bullet) {
            BulletCache eBullet = BulletCache.getInstance(bullet);
            PositionalBulletCache cache = null;
            for (PositionalBulletCache c : mBullets) {
                if (c.equals(eBullet)) {
                    cache = c;
                    break;
                }
            }
            return cache;
        }

        private void incrementHitBullets(String target) {
            mHitBullets.put(target, mHitBullets.getOrDefault(target, 0) + 1);
        }

        private void incrementMissedBullets(String target) {
            mMissedBullets.put(target, mMissedBullets.getOrDefault(target, 0) + 1);
        }

        private void incrementShieldedBullets(String target) {
            mShieldedBullets.put(target, mShieldedBullets.getOrDefault(target, 0) + 1);
        }
    }

    private static final class RobotHistory {
        private Map<String, SortedSet<PositionalRobotCache>> targets;

        public RobotHistory() {
            targets = new HashMap<>();
        }

        private PositionalRobotCache getRecentCacheForTarget(String target) {
            return targets.get(target).first();
        }

        private void updateCache(ScannedRobotEvent event, Robot scanner) {
            SortedSet<PositionalRobotCache> set = targets.getOrDefault(event.getName(), new TreeSet<>(RobotCache.TIME_COMPARATOR));
            set.add(RobotCache.fromEventAndScanner(event, scanner));
            while (set.size() > HISTORY_SIZE)
                set.remove(set.last());
            targets.put(event.getName(), set);
        }

        private PositionalRobotCache getCache(String target, int index) {
            if (index == 0) {
                return getRecentCacheForTarget(target);
            }
            SortedSet<PositionalRobotCache> set = getCompleteCacheForTarget(target);
            if (set == null || set.isEmpty()) return null;
            int count = 0;
            for (PositionalRobotCache cache : set) {
                if (count == index) {
                    return cache;
                }
                count++;
            }
            return null;
        }

        private SortedSet<PositionalRobotCache> getCompleteCacheForTarget(String target) {
            return targets.getOrDefault(target, Collections.emptySortedSet());
        }

    }

    private static final class FireInfo {
        private final double mPower;
        private final RobotInfo mTarget;
        private final long mTime;

        public FireInfo(long time, double power, RobotInfo target) {
            mPower = power;
            mTarget = target;
            mTime = time;
        }

        public double getPower() {
            return mPower;
        }

        public RobotInfo getTarget() {
            return mTarget;
        }

        public long getTime() {
            return mTime;
        }
    }

}
