/**
 * Attribution:
 * Adapted from the RuneLite plugin sources in the F2P-Star-Assist project {@url https://github.com/Jannyboy11/F2P-Star-Assist}.
 *
 * The MIT License (MIT)
 * Copyright (c) 2022 Jan Boerman <jannyboy11@gmail.com>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.shootingstar.scouter.models;

public enum StarLocation {

    WILDERNESS_RUNITE_MINE("Wilderness runite mine"),
    WILDERNESS_CENTRE_MINE("Wilderness centre mine"),
    WILDERNESS_SOUTH_WEST_MINE("Wilderness south west mine"),
    WILDERNESS_SOUTH_MINE("Wilderness south mine"),

    DWARVEN_MINE("Dwarven Mine"),
    MINING_GUILD("Mining Guild"),
    CRAFTING_GUILD("Crafting Guild"),
    RIMMINGTON_MINE("Rimmington mine"),

    DRAYNOR_VILLAGE_BANK("Draynor Village bank"),
    LUMBRIDGE_SWAMP_SOUTH_WEST_MINE("Lumbridge Swamp west mine"),
    LUMBRIDGE_SWAMP_SOUTH_EAST_MINE("Lumbridge Swamp east mine"),

    VARROCK_SOUTH_WEST_MINE("Varrock south west mine"),
    VARROCK_SOUTH_EAST_MINE("Varrock south east mine"),
    VARROCK_AUBURY("Varrock east bank"),

    AL_KHARID_MINE("Al Kharid mine"),
    AL_KHARID_BANK("Al Kharid bank"),
    PVP_ARENA("PvP Arena"),

    CRANDOR_NORTH_MINE("Crandor north mine"),
    CRANDOR_SOUTH_MINE("Crandor south mine"),
    CORSAIR_COVE_BANK("Corsair Cove bank"),
    CORSAIR_COVE_RESOURCE_AREA("Corsair Cove resource area"),

    // P2P areas
    SOUTH_CITHAREDE_ABBEY("South (East) of Citharede Abbey"),
    MYTHS_GUILD("Myths' Guild");
    // Not detectable from f2p:
    // FALADOR_WEST_MINE("Falador west mine");

    private final String humanFriendlyName;

    private StarLocation(String name) {
        this.humanFriendlyName = name;
    }

    @Override
    public String toString() {
        return humanFriendlyName;
    }

    public boolean isInWilderness() {
        switch (this) {
            case WILDERNESS_RUNITE_MINE:
            case WILDERNESS_CENTRE_MINE:
            case WILDERNESS_SOUTH_WEST_MINE:
            case WILDERNESS_SOUTH_MINE:
                return true;
            default:
                return false;
        }
    }

}
