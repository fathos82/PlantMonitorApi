package io.athos.agrocore.plantmonitor.monitorings.measurement;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class TimeBucketCalculator {

    // A standard industry multiplier. It ensures the database returns
    // enough data points for the LTTB algorithm to find the actual peaks.
    private static final int OVERSAMPLING_FACTOR = 10;

    /**
     * Calculates the optimal time bucket interval string for TimescaleDB.
     *
     * @param startTime The start of the requested time window.
     * @param endTime   The end of the requested time window.
     * @param points    The target resolution (e.g., chart width in pixels).
     * @return A PostgreSQL interval string (e.g., "15 seconds", "2 hours").
     */
    public String calculateDynamicBucket(Instant startTime, Instant endTime, int points) {

        // 1. Calculate the total duration in seconds
        long totalSeconds = Duration.between(startTime, endTime).getSeconds();

        // 2. Safeguard against division by zero or negative resolution
        int safePoints = Math.max(points, 1);

        // 3. The Formula: Target resolution * Oversampling Factor
        long desiredSamples = (long) safePoints * OVERSAMPLING_FACTOR;

        // 4. Calculate the bucket size in seconds
        long bucketInSeconds = totalSeconds / desiredSamples;

        // 5. Sanity Check: Minimum bucket size is 1 second.
        // If the window is very small (e.g., last 5 minutes), we don't bucket.
        if (bucketInSeconds < 1) {
            return "1 second";
        }

        // TimescaleDB perfectly understands exact second intervals like "187 seconds"
        return bucketInSeconds + " seconds";
    }
}