package infovk.prototype_2.helper;

import infovk.prototype_2.RobotBase.BulletManager;
import infovk.prototype_2.RobotBase.BulletManager.BulletView;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BulletSerializer {
    private static final String KEY_HIT = "hit";
    private static final String KEY_MISSED = "missed";
    private static final String KEY_SHIELDED = "shielded";
    private static final List<String> VALID_KEYS = Collections.unmodifiableList(Stream.of(KEY_HIT, KEY_MISSED, KEY_SHIELDED).collect(Collectors.toList()));

    public static void serializeBullets(PrintStream stream, BulletManager manager) {
        BulletView view = manager.getView();
        Set<String> targets = new HashSet<>();
        targets.addAll(view.getHitBullets().keySet());
        targets.addAll(view.getShieldedBullets().keySet());
        targets.addAll(view.getMissedBullets().keySet());
        for (String s : targets) {
            serializeBullets(stream, manager, s);
        }
    }

    public static void serializeBullets(PrintStream stream, BulletManager manager, String target) {
        BulletView view = manager.getView();
        stream.println(target);
        int missed = view.getMissedBullets().getOrDefault(target, 0);
        int hit = view.getHitBullets().getOrDefault(target, 0);
        int shielded = view.getShieldedBullets().getOrDefault(target, 0);
        stream.print(KEY_MISSED);
    }
}
