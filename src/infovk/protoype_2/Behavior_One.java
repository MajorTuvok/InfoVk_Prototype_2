package infovk.protoype_2;

public class Behavior_One extends SimpleRobotBehavior {
    //Ged�chtnissektion

    public Behavior_One(SimpleRobot robot) {
        super(robot);
    }

    @Override
    public void start() {
turnRadar(720);
    }

    @Override
    void execute() {
      /*for (ScannedRobotEvent event : getScannedRobotEvents()) {

            double bearing = event.getBearing();
            double absoluteBearing = bearing + getHeading();
            double radarHeading = getRadarHeading();
            double toTurn = RobotHelper.normalRelativeAngle(absoluteBearing - radarHeading);
            toTurn = toTurn > 0 ? toTurn + 5 : toTurn - 5;
            turnRadar(toTurn);
            //turn(event.getBearing()); double gunDirection = getGunHeading();
            double gunTurn = RobotHelper.normalRelativeAngle(absoluteBearing - gunDirection);
            turnGun(gunTurn);
            fireBullet(1.75);

        }
        */
        }




    //Eigene Funktionen Sektion
}