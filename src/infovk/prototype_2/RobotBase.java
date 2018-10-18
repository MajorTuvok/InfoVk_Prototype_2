package infovk.prototype_2;

import infovk.prototype_2.helper.*;
import infovk.prototype_2.helper.BulletCache.PositionalBulletCache;
import infovk.prototype_2.helper.BulletCache.TargetedBulletCache;
import infovk.prototype_2.helper.Point;
import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import robocode.Bullet;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.*;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.*;

public abstract class RobotBase extends SimpleRobot implements Constants {
    private static final String BULLET_HISTORY = "bullets.stats";
    private static final double ANGLE_FACTOR = 0.5;
    private static final int MAX_TURN_ANGLE = 90;
    private double energyPowerFactor;
    private double disFactor;
    private BulletManager mBulletManager;
    private RobotHistory mRobotHistory;
    private ColorHandler mColorHandler;

    public RobotBase() {
        setEnergyPowerFactor(40);
        setDisFactor(100);
        mRobotHistory = new RobotHistory();
        mBulletManager = new BulletManager();
        mColorHandler = new ColorHandler();
    }

    protected abstract BehaviourType getBehaviourType();

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

    public ColorHandler getColorHandler() {
        return mColorHandler;
    }

    protected Map<String, PositionalRobotCache> getLatestTargetView() {
        return mRobotHistory.getLatestTargetView();
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

        setAdjustToTurns();
        if (behavior != null) {
            behavior.start();
        }
        start();
        getColorHandler().rainbow();
        execute();

        while (true) {

            if (behavior != null) {
                behavior.execute();
            }
            loop();
            getColorHandler().rainbow();
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
        mBulletManager.onBulletHit(ex);
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent ex) {
        mBulletManager.onBulletHitBullet(ex);
    }

    @Override
    public void onBulletMissed(BulletMissedEvent ex) {
        mBulletManager.onBulletMissed(ex);
        getColorHandler().rainbow();
    }

    @Override
    public void onHitByBullet(HitByBulletEvent ex) {

    }

    @Override
    public void onHitRobot(HitRobotEvent ex) {

    }

    @Override
    public void onHitWall(HitWallEvent ex) {

    }

    protected void loop() {

    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        super.onRoundEnded(event);
        writeBulletHistory();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent ex) {
        mRobotHistory.updateCache(ex, this);
        getColorHandler().rainbow();
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);
        BulletSerializer.serializeBullets(System.out, mBulletManager.getView(), event.getName());
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
        fire(getPowerRelToEnergy(baseVal), target);
    }

    protected void fireRelativeToEnergyAndDistance(RobotInfo target, double baseVal, double distance) {
        fire(getPowerRelToEnergyAndDistance(baseVal, distance), target);
    }

    protected double getPowerRelToEnergy(double baseVal) {
        return RobotHelper.getRelativeEnergyPower(baseVal, getEnergy(), energyPowerFactor);
    }

    protected double getPowerRelToEnergyAndDistance(double baseVal, double distance) {
        return RobotHelper.getRelativeEnergyAndDistancePower(baseVal, getEnergy(), energyPowerFactor, distance, disFactor);
    }

    private Bullet performSetFireBullet(double power) {
        return super.setFireBullet(power);
    }

    protected double getEstimatedHeading(String target) {
        PositionalRobotCache cur = getRecentCache(target);
        PositionalRobotCache last = getCache(target, 1);
        if (cur == null) {
            return 0;
        }
        if (last == null) {
            return cur.getHeading();
        }
        Point moved = cur.getTargetInfo().getPos().subtract(last.getTargetInfo().getPos());
        Point dir = Point.fromPolarCoordinates(cur.getHeading(), 1);
        double angle = Utils.normalRelativeAngle(moved.angleFrom(dir));
        if (angle > MAX_TURN_ANGLE || angle < -MAX_TURN_ANGLE) {
            return cur.getHeading();
        }
        return cur.getHeading() + angle * ANGLE_FACTOR;
    }

    @Override
    public void onDeath(DeathEvent event) {
        super.onDeath(event);
        BulletSerializer.serializeBullets(System.out, mBulletManager);
    }

    protected void start() {
        readBulletHistory();
    }

    private void readBulletHistory() {
        try (LineNumberReader reader = new LineNumberReader(new FileReader(getDataFile(BULLET_HISTORY)))) {
            BulletSerializer.deserializeBullets(reader, mBulletManager);
        } catch (IOException e) {
            System.err.println("Failed to write data for " + BULLET_HISTORY);
        }
    }

    private void writeBulletHistory() {
        try (PrintStream writer = new PrintStream(new RobocodeFileOutputStream(getDataFile(BULLET_HISTORY)))) {
            BulletSerializer.serializeBullets(writer, mBulletManager);
        } catch (IOException e) {
            System.err.println("Failed to identify Data File " + BULLET_HISTORY);
        }
    }

    public final class BulletManager {
        private final Set<PositionalBulletCache> mBullets;
        private final Map<String, Map<BehaviourType, Integer>> mHitBullets;
        private final Map<String, Map<BehaviourType, Integer>> mMissedBullets;
        private final Map<String, Map<BehaviourType, Integer>> mShieldedBullets;
        private final BulletView mView;

        private BulletManager() {
            mBullets = new HashSet<>();
            mHitBullets = new HashMap<>();
            mMissedBullets = new HashMap<>();
            mShieldedBullets = new HashMap<>();
            mView = new BulletView(
                    Collections.unmodifiableSet(mBullets),
                    Collections.unmodifiableMap(mHitBullets),
                    Collections.unmodifiableMap(mMissedBullets),
                    Collections.unmodifiableMap(mShieldedBullets)
            );
        }

        public BulletView getView() {
            return mView;
        }

        public void injectBulletView(BulletView view) {
            System.out.println("Injecting BulletView");
            mBullets.clear();
            mHitBullets.clear();
            mMissedBullets.clear();
            mShieldedBullets.clear();
            mBullets.addAll(view.getActiveBullets());
            mHitBullets.putAll(view.getHitBullets());
            mMissedBullets.putAll(view.getMissedBullets());
            mShieldedBullets.putAll(view.getShieldedBullets());
            System.out.println("Bullet History now consists of " + mHitBullets.entrySet().size() + " elements.");
        }

        private void onFire(FireInfo info) {
            Bullet b = RobotBase.this.performSetFireBullet(info.getPower());
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
                incrementHitBullets(e.getName(), RobotBase.this.getBehaviourType());
            } else {
                incrementMissedBullets(e.getName(), RobotBase.this.getBehaviourType());
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
            incrementShieldedBullets(((TargetedBulletCache) cache).getTarget().getName(), RobotBase.this.getBehaviourType());
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
            incrementMissedBullets(((TargetedBulletCache) cache).getTarget().getName(), RobotBase.this.getBehaviourType());
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

        private void incrementHitBullets(String target, BehaviourType type) {
            Map<BehaviourType, Integer> map = mHitBullets.getOrDefault(target, new EnumMap<>(BehaviourType.class));
            map.put(type, map.getOrDefault(type, 0) + 1);
            mHitBullets.put(target, map);
        }

        private void incrementMissedBullets(String target, BehaviourType type) {
            Map<BehaviourType, Integer> map = mMissedBullets.getOrDefault(target, new EnumMap<>(BehaviourType.class));
            map.put(type, map.getOrDefault(type, 0) + 1);
            mMissedBullets.put(target, map);
        }

        private void incrementShieldedBullets(String target, BehaviourType type) {
            Map<BehaviourType, Integer> map = mShieldedBullets.getOrDefault(target, new EnumMap<>(BehaviourType.class));
            map.put(type, map.getOrDefault(type, 0) + 1);
            mShieldedBullets.put(target, map);
        }
    }

    private final class ColorHandler {
        private int i; //int für farbschleife

        public ColorHandler() {
            this.i = 0;
        }

        //Setting Color from own Bot
        private void rainbow() {

            RobotBase.this.setColors(
                    Color.BLACK,
                    new Color(colorFunction((int) (RobotBase.this.getGunHeat() * 75)), colorFunction(120 - (int) (RobotBase.this.getGunHeat() * 75)), 0),
                    Color.BLACK,
                    Color.WHITE,
                    Color.BLUE);
            if (i > 358) {
                i = 0;
            } else {
                i = i + (int) (10 * Math.random()) + 1;
            }
        }

        //Working through ColorSpectrum
        private int colorFunction(int x) {
            double a = (double) x;
            double b = 0;

            while (a >= 360) {
                a = a - 360;
            }
            if (a >= 0 && a < 60) {
                b = 4.25 * a;
            } else if (a >= 60 && a < 180) {
                b = 255;
            } else if (a >= 180 && a < 240) {
                b = -4.25 * (a - 180) + 255;
            } else if (a >= 240 && a < 360) {
                b = 0;
            } else if (a >= 360 && a < 420) {
                b = 4.25 * (a - 360);
            } else if (a >= 420 && a < 540) {
                b = 255;
            } else if (a >= 540 && a < 600) {
                b = -4.25 * (a - 540) + 255;
            } else {
                b = 255;
            }
            return (int) b;
        }

    }

}
