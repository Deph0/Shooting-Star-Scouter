package com.shootingstar.scouter.models;

import com.google.gson.JsonObject;

import lombok.Data;

public @Data class StarData
{
    private final String world;
    private final String location;
    private final String locationAlias;
    private final int tier;
    private final boolean backup;
    private final String firstFound;
    private final String foundBy;

    public StarData withUpdates(String world, String location, String locationAlias, int tier, boolean backup, String firstFound, String foundBy)
    {
        return new StarData(
            world != null ? world : this.world,
            location != null ? location : this.location,
            locationAlias != null ? locationAlias : this.locationAlias,
            tier >= 0 ? tier : this.tier,
            backup,
            firstFound != null ? firstFound : this.firstFound,
            foundBy != null ? foundBy : this.foundBy
        );
    }

    public StarData withTier(int tier)
    {
        return new StarData(this.world, this.location, this.locationAlias, tier, this.backup, this.firstFound, this.foundBy);
    }

    public StarData withLocation(String location, String locationAlias)
    {
        return new StarData(this.world, location, locationAlias, this.tier, this.backup, this.firstFound, this.foundBy);
    }

    public StarData withBackup(boolean backup)
    {
        return new StarData(this.world, this.location, this.locationAlias, this.tier, backup, this.firstFound, this.foundBy);
    }
    
    public static StarData fromJson(JsonObject json)
    {
        String world = json.has("world") ? json.get("world").getAsString() : null;
        String location = json.has("location") ? json.get("location").getAsString() : null;
        String locationAlias = json.has("locationAlias") ? json.get("locationAlias").getAsString() : null;
        int tier = json.has("tier") ? json.get("tier").getAsInt() : -1;
        boolean backup = json.has("backup") && json.get("backup").getAsBoolean();
        String firstFound = json.has("firstFound") ? json.get("firstFound").getAsString() : null;
        String foundBy = json.has("foundBy") ? json.get("foundBy").getAsString() : null;
        
        return new StarData(world, location, locationAlias, tier, backup, firstFound, foundBy);
    }

    public JsonObject toJson()
    {
        JsonObject json = new JsonObject();
        if (world != null) json.addProperty("world", world);
        if (location != null) json.addProperty("location", location);
        if (locationAlias != null) json.addProperty("locationAlias", locationAlias);
        json.addProperty("tier", tier);
        json.addProperty("backup", backup);
        if (firstFound != null) json.addProperty("firstFound", firstFound);
        if (foundBy != null) json.addProperty("foundBy", foundBy);
        
        return json;
    }
}