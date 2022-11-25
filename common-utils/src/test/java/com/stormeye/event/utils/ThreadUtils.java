package com.stormeye.event.utils;

/**
 * Utility class to stop having to add sonar suppression annotations
 *
 * @author ian@meywood.com
 */
public class ThreadUtils {

    private ThreadUtils() {
        // Prevent construction
    }

    @SuppressWarnings("java:S2925") // Suppress: Remove this use of "Thread.sleep()
    public static void sleepNoSonarWarnings(final long sleepMilliseconds) {
        try {
            Thread.sleep(sleepMilliseconds);
        } catch (InterruptedException e) {
            //noinspection ResultOfMethodCallIgnored
            Thread.interrupted();
        }
    }
}
