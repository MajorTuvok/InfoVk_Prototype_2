package infovk.protoype_2.helper;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class RobotCache implements Comparable<RobotCache> {
    private final ScannedRobotEvent mEvent;

    private RobotCache(ScannedRobotEvent event) {
        mEvent = event;
    }

    public static RobotCache fromEvent(ScannedRobotEvent event) {
        return new RobotCache(event);
    }

    public static RobotCache fromEventAndPosition(ScannedRobotEvent event) {
        return new RobotCache(event);
    }

    public double getBearing() {
        return mEvent.getBearing();
    }

    public double getBearingRadians() {
        return mEvent.getBearingRadians();
    }

    public double getDistance() {
        return mEvent.getDistance();
    }

    public double getEnergy() {
        return mEvent.getEnergy();
    }

    public double getHeading() {
        return mEvent.getHeading();
    }

    public double getHeadingRadians() {
        return mEvent.getHeadingRadians();
    }

    public String getName() {
        return mEvent.getName();
    }

    public double getVelocity() {
        return mEvent.getVelocity();
    }

    public boolean isSentryRobot() {
        return mEvent.isSentryRobot();
    }

    public long getTime() {
        return mEvent.getTime();
    }

    public void setTime(long newTime) {
        mEvent.setTime(newTime);
    }

    public int getPriority() {
        return mEvent.getPriority();
    }

    public void setPriority(int newPriority) {
        mEvent.setPriority(newPriority);
    }

    public int compareTo(RobotCache cache) {
        return mEvent.compareTo(cache.mEvent);
    }

    @Override
    public int hashCode() {
        return mEvent != null ? mEvent.getName().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RobotCache)) return false;

        RobotCache that = (RobotCache) o;

        return mEvent != null ? mEvent.getName().equals(that.mEvent.getName()) : that.mEvent == null;
    }

    public static final class PositionalRobotCache extends RobotCache {
        private final RobotInfo scannerInfo;
        private final RobotInfo targetInfo;

        private PositionalRobotCache(ScannedRobotEvent event, Robot scanner) {
            super(event);
            scannerInfo = RobotInfo.getInstance(scanner);
            double absoluteBearing = RobotHelper.absoluteBearing(scanner, event.getBearing());
            Point relCood = Point.fromPolarCoordinates(absoluteBearing, event.getDistance());
            targetInfo = RobotInfo.getInstance(scannerInfo.getPos().add(relCood), event.getHeading(), event.getVelocity());
        }

        public RobotInfo getScannerInfo() {
            return scannerInfo;
        }

        public RobotInfo getTargetInfo() {
            return targetInfo;
        }
    }
}
