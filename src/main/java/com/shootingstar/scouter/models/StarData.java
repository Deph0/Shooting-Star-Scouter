package com.shootingstar.scouter.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

public @Data class StarData
{
    @SerializedName("world") private final String world;
    @SerializedName("location") private final String location;
    @SerializedName("locationAlias") private final String locationAlias;
    @SerializedName("tier") private final int tier;
    @SerializedName("backup") private final boolean backup;
    @SerializedName("firstFound") private final String firstFound;
    @SerializedName("foundBy") private final String foundBy;

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
    
    public static StarData fromJsonObject(JsonObject json)
    {
        return new Gson().fromJson(json, StarData.class);
    }

    public JsonObject toJsonObject() {
        return new Gson().toJsonTree(this).getAsJsonObject();
    }
}