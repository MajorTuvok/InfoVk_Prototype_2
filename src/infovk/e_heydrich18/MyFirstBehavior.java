package infovk.e_heydrich18;

public class MyFirstBehavior extends SimpleRobotBehavior {
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
