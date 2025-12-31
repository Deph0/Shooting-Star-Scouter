package com.shootingstar.scouter.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import com.shootingstar.scouter.models.StarData;

/**
 * Service for managing star data.
 * This class maintains the current state of all stars and can be injected wherever needed.
 */
@Singleton
public class StarDataService
{
    private final Map<String, StarData> stars = new HashMap<>();

    /**
     * Update all stars with a new list
     * @param starList The new list of stars to replace current data
     */
    public void updateAll(List<StarData> starList)
    {
        stars.clear();
        for (StarData star : starList) {
            stars.put(star.getWorld(), star);
        }
    }

    /**
     * Get a star by world number
     * @param world The world number
     * @return Optional containing the star if found
     */
    public Optional<StarData> getStar(String world)
    {
        return Optional.ofNullable(stars.get(world));
    }

    /**
     * Add or update a star
     * @param star The star to add or update
     */
    public void addOrUpdate(StarData star)
    {
        stars.put(star.getWorld(), star);
    }

    /**
     * Remove a star by world number
     * @param world The world number
     * @return true if star was removed, false if not found
     */
    public boolean removeStar(String world)
    {
        return stars.remove(world) != null;
    }

    /**
     * Clear all stars
     */
    public void clearAll()
    {
        stars.clear();
    }

    /**
     * Get the number of stars
     * @return The count of stars
     */
    public int count()
    {
        return stars.size();
    }

    /**
     * Check if a star exists for the given world
     * @param world The world number
     * @return true if star exists
     */
    public boolean hasStar(String world)
    {
        return stars.containsKey(world);
    }

    /**
     * Get an immutable copy of the stars map for iteration/comparison
     * @return Copy of the internal stars map
     */
    public Map<String, StarData> getAllStars()
    {
        return new HashMap<>(stars);
    }
}
