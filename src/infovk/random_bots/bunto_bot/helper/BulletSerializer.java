package infovk.random_bots.bunto_bot.helper;

import infovk.random_bots.bunto_bot.BehaviourType;
import infovk.random_bots.bunto_bot.RobotBase.BulletManager;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BulletSerializer {
    private static final String KEY_COMMENT = "#";
    private static final String KEY_NAME = "name";
    private static final String KEY_BEHAVIOUR = "behave";
    private static final String KEY_HIT = "hit";
    private static final String KEY_MISSED = "missed";
    private static final String KEY_SHIELDED = "shielded";
    private static final String KEY_NEW_ENTRY = "******************************";
    private static final String KEY_NEW_BEHAVIOUR = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
    private static final String KEY_VAL = "=";
    private static final List<String> VALID_KEYS = Collections.unmodifiableList(Stream.of(KEY_NAME, KEY_HIT, KEY_MISSED, KEY_SHIELDED).collect(Collectors.toList()));

    public static void serializeBullets(PrintStream stream, BulletManager manager) {
        BulletView view = manager.getView();
        Set<String> targets = new HashSet<>();
        targets.addAll(view.getHitBullets().keySet());
        targets.addAll(view.getShieldedBullets().keySet());
        targets.addAll(view.getMissedBullets().keySet());
        for (String s : targets) {
            serializeBullets(stream, view, s);
        }
    }

    public static void serializeBullets(PrintStream stream, BulletView view, String target) {
        stream.println(KEY_NEW_ENTRY);
        printVal(stream, KEY_NAME, target);
        for (BehaviourType type : BehaviourType.values()) {
            printBehaviourType(stream, view, target, type);
        }
    }

    private static void printBehaviourType(PrintStream stream, BulletView view, String target, BehaviourType type) {
        stream.println(KEY_NEW_BEHAVIOUR);
        printVal(stream, KEY_BEHAVIOUR, type.toString());
        printVal(stream, KEY_MISSED, view.getMissedBullets(target, type));
        printVal(stream, KEY_HIT, view.getHitBullets(target, type));
        printVal(stream, KEY_SHIELDED, view.getShieldedBullets(target, type));
        stream.println(KEY_NEW_BEHAVIOUR);
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
        if (read == null) return false;
        String name = getValPart(read);
        Map<BehaviourType, Integer> hits = new EnumMap<>(BehaviourType.class);
        Map<BehaviourType, Integer> missed = new EnumMap<>(BehaviourType.class);
        Map<BehaviourType, Integer> shielded = new EnumMap<>(BehaviourType.class);
        while (readBehaviour(stream, hits, missed, shielded)) ;
        injectTargetValues(viewInConstruction, name, hits, missed, shielded);
        return true;
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

    private static boolean readBehaviour(LineNumberReader stream, Map<BehaviourType, Integer> hitMap, Map<BehaviourType, Integer> missedMap, Map<BehaviourType, Integer> shieldedMap) throws IOException {
        String read = stream.readLine();
        if (read == null || !read.startsWith(KEY_NEW_BEHAVIOUR)) return false;
        read = stream.readLine();
        BehaviourType type = null;
        Integer hits = 0;
        Integer missed = 0;
        Integer shielded = 0;
        while (read != null && !read.startsWith(KEY_NEW_BEHAVIOUR)) {
            if (read.startsWith(KEY_COMMENT)) continue;
            if (read.startsWith(KEY_HIT)) {
                hits = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_MISSED)) {
                missed = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_SHIELDED)) {
                shielded = readIntegerOrNull(getValPart(read));
            } else if (read.startsWith(KEY_BEHAVIOUR)) {
                if (type != null) continue;
                type = BehaviourType.valueOf(getValPart(read));
            }
            read = stream.readLine();
        }
        if (type != null) {
            hitMap.put(type, hits);
            missedMap.put(type, missed);
            shieldedMap.put(type, shielded);
        }
        return read != null;
    }

    private static void injectTargetValues(BulletView viewInConstruction, String name, Map<BehaviourType, Integer> hits, Map<BehaviourType, Integer> missed, Map<BehaviourType, Integer> shielded) {
        viewInConstruction.getHitBullets().put(name, hits);
        viewInConstruction.getMissedBullets().put(name, missed);
        viewInConstruction.getShieldedBullets().put(name, shielded);
    }
}
