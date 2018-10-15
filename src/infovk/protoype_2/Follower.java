package infovk.protoype_2;

import java.util.List;

public class Follower extends SimpleRobot {
    private static final double RADAR_TURN = 180D;

    public Follower() {
        behavior = new FollowerBehaviour(this);
    }

    private static class FollowerBehaviour extends SimpleRobotBehavior<Follower> {
        private String target;

        public FollowerBehaviour(Follower robot) {
            super(robot);
            target = null;
        }

        @Override
        void start() {
        }

        @Override
        void execute() {
            List<ScannedRobotEvent> robotEvents = getScannedRobotEvents();
            if (robotEvents.size() == 0) {
                turnRadar(RADAR_TURN);
                return;
            }
            turnRadar(0);
            if (target == null) {
                target = robotEvents.get(0).getName();
            }
            robotEvents.stream().filter(e -> e.getName().equals(target)).forEach(this::enemyFound);
        }

        private void enemyFound(ScannedRobotEvent e) {
            if (e.getBearing() != 0 && getTurnRemaining() == 0) {
                turn(e.getBearing());
                turnGun(e.getBearing());
            } else if (e.getBearing() == 0) {
                turn(0);
                turnGun(0);
                ahead(e.getDistance());
                fireBullet(1);
            }
        }
    }
}
