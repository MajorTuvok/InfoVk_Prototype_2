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
    private static final String KEY_NAME = "name";
    private static final String KEY_HIT = "hit";
    private static final String KEY_MISSED = "missed";
    private static final String KEY_SHIELDED = "shielded";
    private static final String KEY_NEW_ENTRY = "******************************";
    private static final String KEY_VAL = "=";
    private static final List<String> VALID_KEYS = Collections.unmodifiableList(Stream.of(KEY_NAME, KEY_HIT, KEY_MISSED, KEY_SHIELDED).collect(Collectors.toList()));

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
        stream.println(KEY_NEW_ENTRY);
        BulletView view = manager.getView();
        int missed = view.getMissedBullets().getOrDefault(target, 0);
        int hit = view.getHitBullets().getOrDefault(target, 0);
        int shielded = view.getShieldedBullets().getOrDefault(target, 0);
        printVal(stream, KEY_NAME, target);
        printVal(stream, KEY_MISSED, missed);
        printVal(stream, KEY_HIT, hit);
        printVal(stream, KEY_SHIELDED, shielded);
    }

    private static void printVal(PrintStream stream, String key, int value) {
        stream.print(key);
        stream.print(KEY_VAL);
        stream.println(value);
    }

    private static void printVal(PrintStream stream, String key, String value) {
        stream.print(key);
        stream.print(KEY_VAL);
        stream.println(value);
    }
}
