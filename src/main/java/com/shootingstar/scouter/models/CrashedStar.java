package com.shootingstar.scouter.models;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

public enum CrashedStar
{
    TIER_1(ObjectID.STAR_SIZE_ONE_STAR, 1),
    TIER_2(ObjectID.STAR_SIZE_TWO_STAR, 2),
    TIER_3(ObjectID.STAR_SIZE_THREE_STAR, 3),
    TIER_4(ObjectID.STAR_SIZE_FOUR_STAR, 4),
    TIER_5(ObjectID.STAR_SIZE_FIVE_STAR, 5),
    TIER_6(ObjectID.STAR_SIZE_SIX_STAR, 6),
    TIER_7(ObjectID.STAR_SIZE_SEVEN_STAR, 7),
    TIER_8(ObjectID.STAR_SIZE_EIGHT_STAR, 8),
    TIER_9(ObjectID.STAR_SIZE_NINE_STAR, 9);

    @Getter private int objectID;
    @Getter @Setter private GameObject object;
    @Getter @Setter private WorldPoint worldPoint;
    @Getter @Setter private StarLocation starLocation;
    @Getter @Setter private int tier;
    @Getter @Setter private String world;

    private CrashedStar(int objectID, int tier) {
        this.objectID = objectID;
        this.tier = tier;
    }

	public static CrashedStar fromGameObject(GameObject obj, String world)
    {
        for (CrashedStar star : values())
        {
            if (star.getObjectID() == obj.getId())
            {
                star.setObject(obj);
                WorldPoint worldPoint = star.getObject().getWorldLocation(); // Translate to readable location, eg "Varrock East"
                StarLocation starLocation = StarPoints.toLocation(worldPoint); // With help from F2P-Star-Assist's StarPoints class
                star.setWorldPoint(obj.getWorldLocation());
                star.setStarLocation(starLocation);
                star.setWorld(world);
                return star;
            }
        }
        return null;
    }
}