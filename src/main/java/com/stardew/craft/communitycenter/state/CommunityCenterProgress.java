package com.stardew.craft.communitycenter.state;

import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;

import java.util.List;

/**
 * Read-only query API for Community Center progress.
 * Wraps {@link CommunityCenterSavedData} with higher-level queries
 * that mirror SDV's CommunityCenter logic.
 */
public final class CommunityCenterProgress {

    private CommunityCenterProgress() {}

    // ── Area Unlock Logic ──
    // Mirrors SDV CommunityCenter.shouldNoteAppearInArea()

    /**
     * Whether the Junimo Note (scroll) should appear in a given area.
     * Determines room unlock order based on total completed bundles.
     */
    public static boolean shouldNoteAppearInArea(int areaId) {
        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        if (data.isAreaComplete(areaId)) return false;

        int completedBundles = data.numberOfCompleteBundles();
        return switch (areaId) {
            case 1 -> true;                    // Crafts Room: always visible
            case 0, 2 -> completedBundles > 0; // Pantry, Fish Tank: after 1st bundle
            case 3 -> completedBundles > 1;    // Boiler Room: after 2nd bundle
            case 5 -> completedBundles > 2;    // Bulletin Board: after 3rd bundle
            case 4 -> completedBundles > 3;    // Vault: after 4th bundle
            case 6 -> false;                   // Abandoned Joja: requires event check
            default -> false;
        };
    }

    /**
     * Check if all bundles within an area are complete.
     * Used to determine when to trigger area completion reward.
     */
    public static boolean areAllBundlesInAreaComplete(int areaId) {
        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        List<BundleDefinition> bundles = BundleDataManager.getBundlesForArea(areaId);
        if (bundles.isEmpty()) return false;

        for (BundleDefinition def : bundles) {
            if (!data.isBundleComplete(def.bundleId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the area completion reward description.
     * Mirrors SDV CommunityCenter.doAreaCompleteReward().
     */
    public static String getAreaRewardMailFlag(int areaId) {
        return switch (areaId) {
            case 0 -> "ccPantry";        // Greenhouse repair
            case 1 -> "ccCraftsRoom";    // Quarry bridge
            case 2 -> "ccFishTank";      // Glittering Boulder / Panning
            case 3 -> "ccBoilerRoom";    // Minecarts
            case 4 -> "ccVault";         // Desert bus
            case 5 -> "ccBulletin";      // Friendship reward
            default -> "";
        };
    }

    /**
     * Get a summary string for debug display.
     */
    public static String getDebugSummary() {
        CommunityCenterSavedData data = CommunityCenterSavedData.get();
        StringBuilder sb = new StringBuilder();
        sb.append("Community Center Progress:\n");

        for (int areaId = 0; areaId <= 6; areaId++) {
            String areaName = BundleDataManager.getAreaName(areaId);
            if (areaName == null) continue;

            boolean complete = data.isAreaComplete(areaId);
            List<BundleDefinition> bundles = BundleDataManager.getBundlesForArea(areaId);

            sb.append(String.format("  [%s] %s (%d/%d bundles)%s\n",
                    complete ? "✓" : " ",
                    areaName,
                    bundles.stream().filter(b -> data.isBundleComplete(b.bundleId())).count(),
                    bundles.size(),
                    shouldNoteAppearInArea(areaId) ? " [UNLOCKED]" : ""));

            for (BundleDefinition def : bundles) {
                boolean bundleDone = data.isBundleComplete(def.bundleId());
                int filled = data.countFilledSlots(def.bundleId());
                sb.append(String.format("    [%s] %s (%d/%d slots, need %d)\n",
                        bundleDone ? "✓" : " ",
                        def.internalName(),
                        filled,
                        def.totalSlots(),
                        def.requiredCount()));
            }
        }

        sb.append(String.format("  Total: %d bundles complete, all areas: %s\n",
                data.numberOfCompleteBundles(),
                data.areAllAreasComplete() ? "YES" : "NO"));

        return sb.toString();
    }
}
