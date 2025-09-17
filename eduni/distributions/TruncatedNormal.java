// java
package eduni.distributions;

import java.util.Random;

/**
 * Truncated normal distribution generator.
 * Provides a convenience constructor TruncatedNormal(mean, sd) which
 * uses default bounds mean +/- 3*sd.
 */
public class TruncatedNormal implements ContinuousGenerator {
    private final double mean;
    private final double sd;
    private final double min;
    private final double max;
    private final int maxAttempts;
    private Random rng;
    private long seed;

    // Convenience constructor used in MyEngine: new TruncatedNormal(10, 6)
    public TruncatedNormal(double mean, double sd) {
        this(mean, sd, mean - 3.0 * sd, mean + 3.0 * sd, 1000, System.nanoTime());
    }

    public TruncatedNormal(double mean, double sd, double min, double max) {
        this(mean, sd, min, max, 1000, System.nanoTime());
    }

    public TruncatedNormal(double mean, double sd, double min, double max, int maxAttempts, long seed) {
        if (sd <= 0) throw new IllegalArgumentException("sd must be > 0");
        if (min >= max) throw new IllegalArgumentException("min must be < max");
        if (maxAttempts <= 0) throw new IllegalArgumentException("maxAttempts must be > 0");
        this.mean = mean;
        this.sd = sd;
        this.min = min;
        this.max = max;
        this.maxAttempts = maxAttempts;
        setSeed(seed);
    }

    @Override
    public double sample() {
        for (int i = 0; i < maxAttempts; i++) {
            double v = mean + sd * rng.nextGaussian();
            if (v >= min && v <= max) return v;
        }
        // fallback: clamp to nearest bound
        if (mean < min) return min;
        if (mean > max) return max;
        return Math.min(Math.max(mean, min), max);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
        this.rng = new Random(seed);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public void reseed() {
        setSeed(System.nanoTime());
    }
}