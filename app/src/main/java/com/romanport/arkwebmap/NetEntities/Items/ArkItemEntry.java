package com.romanport.arkwebmap.NetEntities.Items;

import com.romanport.arkwebmap.NetEntities.ArkImageAsset;

import java.util.Map;

public class ArkItemEntry {
    public Map<String, ArkFoodAddStatusValues> addStatusValues;
    public Boolean allowUseWhileRiding;
    public float baseCraftingXP;
    public float baseItemWeight;
    public float baseRepairingXP;
    public String blueprintPath;
    public ArkImageAsset broken_icon;
    public String classname;
    public String description;
    public Boolean hideFromInventoryDisplay;
    public ArkImageAsset icon;
    public float increasePerQuanity_Food;
    public float increasePerQuanity_Health;
    public float increasePerQuanity_Stamina;
    public float increasePerQuanity_Water;
    public float increasePerQuanity_Weight;
    public Boolean isTekItem;
    public int maxItemQuantity;
    public String name;
    public double spoilingTime;
    public float useCooldownTime;
    public Boolean useItemDurability;
}
