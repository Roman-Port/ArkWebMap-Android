package com.romanport.arkwebmap.NetEntities.Dinos;

import com.romanport.arkwebmap.NetEntities.Vector3;

import java.util.List;

public class ArkTribeDino {
    //altNames
    public float babyAge;
    public int baseLevel;
    public ArkTribeDinoStats baseLevelupsApplied;
    //classname
    public String classnameString;
    public String colors;
    public List<String> colors_hex;
    public ArkTribeDinoStats currentStats;
    public DinoEntry dino_entry;
    public long dinosaurId;
    public float experience;
    public String guid;
    public Boolean isBaby;
    public Boolean isFemale;
    public Boolean isInit;
    public Boolean isItem;
    public Boolean isTamed;
    public int level;
    public Vector3 location;
    public float nextImprintTime;
    //rawProperties
    public ArkTribeDinoStats tamedLevelupsApplied;
    public String tamedName;
    public String tamerName;
    public int tribeId;
}
