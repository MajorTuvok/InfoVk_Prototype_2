package infovk.protoype_2;

import com.sun.istack.internal.Nullable;
import infovk.protoype_2.helper.RobotCache;
import infovk.protoype_2.helper.RobotCache.PositionalRobotCache;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class RobotBase extends SimpleRobot {
    private double energyPowerFactor;
    private double disFactor;
    private RobotHistory mHistory;
    public RobotBase() {
        setEnergyPowerFactor(50);
        setDisFactor(25);
        mHistory = new RobotHistory();
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

    @Override
    public void onScannedRobot(ScannedRobotEvent ex) {
        super.onScannedRobot(ex);
        mHistory.updateCache(ex, this);
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

    protected PositionalRobotCache getRecentCache(String target) {
        return mHistory.getRecentCacheForTarget(target);
    }

    @Nullable
    protected PositionalRobotCache getCache(String target, int index) {
        return mHistory.getCache(target, index);
    }

    protected void start() {

    }

    protected void loop() {

    }

    protected void fireRelativeToEnergy(double baseVal) {
        fire(Math.max(Math.min(baseVal * getEnergy() / 50, 3), 0.1));
    }

    protected void fireRelativeToEnergyAndDistance(double baseVal, double distance) {
        fireRelativeToEnergy(baseVal - (int) distance / disFactor);
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
            targets.put(event.getName(), set);
        }

        @Nullable
        private PositionalRobotCache getCache(String target, int index) {
            SortedSet<PositionalRobotCache> set = targets.get(target);
            if (set == null) return null;
            int count = 0;
            for (PositionalRobotCache cache : set) {
                if (count == index)
                    return cache;
                count++;
            }
            return null;
        }

    }

}
