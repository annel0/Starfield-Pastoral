package com.stardew.craft.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Client-side cache for museum donation status.
 */
public final class ClientMuseumDonationCache {
    private static final Set<String> DONATED = new HashSet<>();

    private ClientMuseumDonationCache() {
    }

    public static void setDonated(Collection<String> donatedIds) {
        DONATED.clear();
        if (donatedIds != null) {
            DONATED.addAll(donatedIds);
        }
    }

    public static boolean isDonated(String itemId) {
        return DONATED.contains(itemId);
    }

    public static void clear() {
        DONATED.clear();
    }
}