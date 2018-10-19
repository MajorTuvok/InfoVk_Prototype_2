package infovk.random_bots.bunto_bot.helper;

import infovk.random_bots.bunto_bot.BehaviourType;
import infovk.random_bots.bunto_bot.helper.BulletCache.PositionalBulletCache;

import java.util.Map;
import java.util.Set;

public final class BulletView {
    private final Set<PositionalBulletCache> mBullets;
    private final Map<String, Map<BehaviourType, Integer>> mHitBullets;
    private final Map<String, Map<BehaviourType, Integer>> mMissedBullets;
    private final Map<String, Map<BehaviourType, Integer>> mShieldedBullets;

    public BulletView(Set<PositionalBulletCache> bullets, Map<String, Map<BehaviourType, Integer>> hitBullets, Map<String, Map<BehaviourType, Integer>> missedBullets, Map<String, Map<BehaviourType, Integer>> shieldedBullets) {
        mBullets = bullets;
        mHitBullets = hitBullets;
        mMissedBullets = missedBullets;
        mShieldedBullets = shieldedBullets;
    }

    public Set<PositionalBulletCache> getActiveBullets() {
        return mBullets;
    }

    public Map<String, Map<BehaviourType, Integer>> getHitBullets() {
        return mHitBullets;
    }

    public Map<String, Map<BehaviourType, Integer>> getMissedBullets() {
        return mMissedBullets;
    }

    public Map<String, Map<BehaviourType, Integer>> getShieldedBullets() {
        return mShieldedBullets;
    }

    public Integer getHitBullets(String target, BehaviourType type) {
        Map<BehaviourType, Integer> map = getHitBullets().get(target);
        if (map == null) return 0;
        return map.getOrDefault(type, 0);
    }

    public Integer getMissedBullets(String target, BehaviourType type) {
        Map<BehaviourType, Integer> map = getMissedBullets().get(target);
        if (map == null) return 0;
        return map.getOrDefault(type, 0);
    }

    public Integer getShieldedBullets(String target, BehaviourType type) {
        Map<BehaviourType, Integer> map = getShieldedBullets().get(target);
        if (map == null) return 0;
        return map.getOrDefault(type, 0);
    }
}
