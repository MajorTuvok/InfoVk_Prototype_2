package infovk.protoype_2;

public class MyFirstBehavior extends SimpleRobotBehavior<SimpleRobot> {
    //Ged�chtnissektion

    public MyFirstBehavior(SimpleRobot robot) {
        super(robot);
    }

    @Override
    public void start() {
        turnRadar(720);
    }

    @Override
    void execute() {
        ahead(10);
    }

    //Eigene Funktionen Sektion
}
