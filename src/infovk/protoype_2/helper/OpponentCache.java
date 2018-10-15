package infovk.protoype_2.helper;

import robocode.ScannedRobotEvent;

public final class OpponentCache implements Comparable<OpponentCache> {
    private final ScannedRobotEvent mEvent;

    public OpponentCache(ScannedRobotEvent event) {
        mEvent = event;
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

    public int compareTo(OpponentCache cache) {
        return mEvent.compareTo(cache.mEvent);
    }

    @Override
    public int hashCode() {
        return mEvent != null ? mEvent.getName().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpponentCache)) return false;

        OpponentCache that = (OpponentCache) o;

        return mEvent != null ? mEvent.getName().equals(that.mEvent.getName()) : that.mEvent == null;
    }
}
