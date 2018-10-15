package infovk.protoype_2;

public class Destructor extends SimpleRobot {
    public Destructor() { behavior = new Behavior_One(this);}

    @Override
    public void run() {
        super.run();
    }
}

