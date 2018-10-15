package infovk.protoype_2;


public class Destructor_Behavior extends SimpleRobotBehavior<SimpleRobot> {
    //Ged�chtnissektion

    public Destructor_Behavior(SimpleRobot robot) {
        super(robot);
    }

    @Override
    public void start() {
        turnRadar(20);
    }

    @Override
    void execute() {
        ahead(10);
    }

    //Eigene Funktionen Sektion
}