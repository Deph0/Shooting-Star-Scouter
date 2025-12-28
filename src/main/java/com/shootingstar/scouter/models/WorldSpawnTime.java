package com.shootingstar.scouter.models;

import lombok.Getter;

public class WorldSpawnTime
{
    @Getter
    public final String world;
    @Getter
    public final String spawnTime;
    
    public WorldSpawnTime(String world, String spawnTime)
    {
        this.world = world;
        this.spawnTime = spawnTime;
    }
}