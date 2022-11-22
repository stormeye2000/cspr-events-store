package com.stormeye.event.store.service.storage.impl;

import java.util.regex.Pattern;

/**
 * Utility class for working with versions
 *
 * @author ian@meywood.com
 */
public class VersionUtils {

    private VersionUtils() {
        // Prevent construction
    }

    public static boolean isVersionGreaterOrEqual(final String v1, final String v2) {

        var v1Parts = getVersionParts(v1);
        var v2Parts = getVersionParts(v2);

        return (v1Parts[0] > v2Parts[0]) ||
                (v1Parts[0] == v2Parts[0] && v1Parts[1] > v2Parts[1]) ||
                (v1Parts[0] == v2Parts[0] && v1Parts[1] == v2Parts[1] && v1Parts[2] > v2Parts[2]) ||
                (v1Parts[0] == v2Parts[0] && v1Parts[1] == v2Parts[1] && v1Parts[2] == v2Parts[2]);
    }

    private static int[] getVersionParts(final String version) {
        final String[] split = version.split(Pattern.quote("."));
        final int[] parts = new int[split.length];
        int i = 0;
        for (String part : split) {
            parts[i++] = Integer.parseInt(part);
        }
        return parts;
    }
}
