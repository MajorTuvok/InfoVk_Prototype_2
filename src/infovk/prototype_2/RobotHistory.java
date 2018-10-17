package infovk.prototype_2;

import infovk.prototype_2.helper.RobotCache;
import infovk.prototype_2.helper.RobotCache.PositionalRobotCache;
import infovk.prototype_2.helper.Tuple;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.util.*;

final class RobotHistory {
    private static int HISTORY_SIZE = 5;
    private RobotRoundStatistic mRoundStatistic;
    private Map<String, SortedSet<PositionalRobotCache>> targets;

    RobotHistory() {
        targets = new HashMap<>();
        mRoundStatistic = new RobotRoundStatistic();
    }

    Map<String, PositionalRobotCache> getLatestTargetView() {
        Map<String, PositionalRobotCache> mapping = new HashMap<>();
        for (Map.Entry<String, SortedSet<PositionalRobotCache>> entry : targets.entrySet()) {
            mapping.put(entry.getKey(), entry.getValue().first());
        }
        return mapping;
    }

    public void onRobotDied(String robot, BehaviourType behaviourType, boolean survived) {
        mRoundStatistic.onRobotDied(robot, behaviourType, survived);
    }

    public Map<BehaviourType, Tuple<Integer, Integer>> getWinsAndLosses(String robot) {
        return mRoundStatistic.getWinsAndLosses(robot);
    }

    PositionalRobotCache getRecentCacheForTarget(String target) {
        return targets.get(target).first();
    }

    void updateCache(ScannedRobotEvent event, Robot scanner) {
        SortedSet<PositionalRobotCache> set = targets.getOrDefault(event.getName(), new TreeSet<>(RobotCache.TIME_COMPARATOR));
        set.add(RobotCache.fromEventAndScanner(event, scanner));
        while (set.size() > HISTORY_SIZE)
            set.remove(set.last());
        targets.put(event.getName(), set);
    }

    PositionalRobotCache getCache(String target, int index) {
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

    SortedSet<PositionalRobotCache> getCompleteCacheForTarget(String target) {
        return targets.getOrDefault(target, Collections.emptySortedSet());
    }

    private static class RobotRoundStatistic {
        private Map<String, Map<BehaviourType, Tuple<Integer, Integer>>> statistic;

        private RobotRoundStatistic() {
            statistic = new HashMap<>();
        }

        private void onLoad() {

        }

        private void onSave() {

        }

        private void onRobotDied(String robot, BehaviourType behaviourType, boolean survived) {
            Map<BehaviourType, Tuple<Integer, Integer>> winsByBehaviour = getWinsAndLosses(robot);
            Tuple<Integer, Integer> tuple = winsByBehaviour.getOrDefault(behaviourType, new Tuple<>(0, 0));
            if (survived) {
                tuple = new Tuple<>(tuple.getFirst() + 1, tuple.getSecond());
            } else {
                tuple = new Tuple<>(tuple.getFirst() + 1, tuple.getSecond());
            }
            winsByBehaviour.put(behaviourType, tuple);
            statistic.put(robot, winsByBehaviour);
        }

        private Map<BehaviourType, Tuple<Integer, Integer>> getWinsAndLosses(String robot) {
            return statistic.getOrDefault(robot, new EnumMap<>(BehaviourType.class));
        }
    }

}
