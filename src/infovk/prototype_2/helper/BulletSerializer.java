package infovk.prototype_2.helper;

import infovk.prototype_2.RobotBase.BulletManager;
import infovk.prototype_2.RobotBase.BulletView;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BulletSerializer {
    private static final String KEY_COMMENT = "#";
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

    public static void deserializeBullets(LineNumberReader stream, BulletManager manager) throws IOException {
        BulletView constructing = new BulletView(new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
        boolean deserialize = readToNewEntry(stream);
        while (deserialize) {
            deserialize = deserializeTarget(stream, constructing);
        }
        manager.injectBulletView(constructing);
    }

    private static boolean deserializeTarget(LineNumberReader stream, BulletView viewInConstruction) throws IOException {
        String read = stream.readLine();
        String name = null;
        Integer hits = null;
        Integer missed = null;
        Integer shielded = null;
        while (read != null && !read.startsWith(KEY_NEW_ENTRY)) {
            if (read.startsWith(KEY_COMMENT)) continue;
            if (read.startsWith(KEY_HIT)) {
                if (hits != null) continue;
                hits = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_MISSED)) {
                if (missed != null) continue;
                missed = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_SHIELDED)) {
                if (shielded != null) continue;
                shielded = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_NAME) && name == null) {
                name = getValPart(read);
            }
            read = stream.readLine();
        }
        injectTargetValues(viewInConstruction, name, hits, missed, shielded);
        return read != null;
    }

    private static boolean readToNewEntry(LineNumberReader stream) throws IOException {
        String read;
        do {
            read = stream.readLine();
        }
        while (read != null && !read.startsWith(KEY_NEW_ENTRY));
        return read != null;
    }

    private static Integer readIntegerOrNull(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String getValPart(String s) {
        int index = s.indexOf(KEY_VAL) + 1;
        return index > 0 && s.length() > index ? s.substring(index) : "";
    }

    private static void injectTargetValues(BulletView viewInConstruction, String name, Integer hits, Integer missed, Integer shielded) {
        if (name == null || hits == null || missed == null || shielded == null) return;
        viewInConstruction.getHitBullets().put(name, hits);
        viewInConstruction.getMissedBullets().put(name, missed);
        viewInConstruction.getShieldedBullets().put(name, shielded);
    }
}
