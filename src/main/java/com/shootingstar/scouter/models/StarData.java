package com.shootingstar.scouter.models;

import lombok.Data;

public @Data class StarData
{
    private final String world;
    private final String location;
    private final int tier;
    private final boolean backup;
    private final String firstFound;
    private final String foundBy;
}