package com.romanport.arkwebmap.NetEntities.Dinos;

import com.romanport.arkwebmap.NetEntities.Items.ArkItem;
import com.romanport.arkwebmap.NetEntities.Items.ArkItemEntry;

import java.util.List;
import java.util.Map;

public class ArkDinosReply {
    public ArkTribeDino dino;
    public DinoEntry dino_entry;
    public ArkTribeDinoStats max_stats;
    public ArkItem[] inventory_items;
    public Map<String, ArkItemEntry> item_class_data;
}
