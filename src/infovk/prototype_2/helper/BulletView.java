package infovk.prototype_2.helper;

import infovk.prototype_2.helper.BulletCache.PositionalBulletCache;

import java.util.Map;
import java.util.Set;

public final class BulletView {
    private final Set<PositionalBulletCache> mBullets;
    private final Map<String, Integer> mHitBullets;
    private final Map<String, Integer> mMissedBullets;
    private final Map<String, Integer> mShieldedBullets;

    public BulletView(Set<PositionalBulletCache> bullets, Map<String, Integer> hitBullets, Map<String, Integer> missedBullets, Map<String, Integer> shieldedBullets) {
        mBullets = bullets;
        mHitBullets = hitBullets;
        mMissedBullets = missedBullets;
        mShieldedBullets = shieldedBullets;
    }

    public Set<PositionalBulletCache> getActiveBullets() {
        return mBullets;
    }

    public Map<String, Integer> getHitBullets() {
        return mHitBullets;
    }

    public Map<String, Integer> getMissedBullets() {
        return mMissedBullets;
    }

    public Map<String, Integer> getShieldedBullets() {
        return mShieldedBullets;
    }
}
