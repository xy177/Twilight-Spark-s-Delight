package xy177.twilightsparksdelight.common.util;

import xy177.twilightsparksdelight.common.config.TSDConfig;

public final class GloryCrucibleCapacity
{
    public static final int MAX_LEVEL = 6;
    public static final int LEVELS_PER_BUCKET = 3;
    public static final int BUCKET_MB = 1000;
    public static final int VOLUME_UNITS_PER_MB = 3;
    public static final int BOTTLE_VOLUME_UNITS = BUCKET_MB;

    private GloryCrucibleCapacity()
    {
    }

    public static int getCapacityMb()
    {
        return TSDConfig.gloryCrucibleCapacityMb;
    }

    public static int getCapacityVolumeUnits()
    {
        return getCapacityMb() * VOLUME_UNITS_PER_MB;
    }

    public static int clampVolumeUnits(int volumeUnits)
    {
        return Math.max(0, Math.min(getCapacityVolumeUnits(), volumeUnits));
    }

    public static int getRenderLevel(int volumeUnits)
    {
        int clamped = clampVolumeUnits(volumeUnits);
        if (clamped <= 0) {
            return 0;
        }
        int capacity = getCapacityVolumeUnits();
        return Math.min(MAX_LEVEL, (clamped * MAX_LEVEL + capacity - 1) / capacity);
    }

    public static double getFillRatio(int volumeUnits)
    {
        return (double) clampVolumeUnits(volumeUnits) / (double) getCapacityVolumeUnits();
    }

    public static int normalizeCapacityMb(int capacityMb)
    {
        return Math.max(BUCKET_MB, capacityMb / BUCKET_MB * BUCKET_MB);
    }
}
