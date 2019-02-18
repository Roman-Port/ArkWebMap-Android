package com.romanport.arkwebmap;

import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.NetEntities.Dinos.BabyDinoInfoPackage;
import com.romanport.arkwebmap.NetEntities.Dinos.DinoEntryFood;
import com.romanport.arkwebmap.NetEntities.Dinos.DinoEntryStatus;
import com.romanport.arkwebmap.NetEntities.Items.ArkFoodAddStatusValues;
import com.romanport.arkwebmap.NetEntities.Items.ArkItem;
import com.romanport.arkwebmap.NetEntities.Items.ArkItemEntry;

import java.util.List;

public class BabyDinoStatsCalculator {

    public static double CalculateCurrentDinoFood(ArkDinosReply dinoData, double gameTimeOffset) {
        DinoEntryStatus entryStatusComponent = dinoData.dino_entry.statusComponent;
        double dinoFoodLossPerSecond = entryStatusComponent.baseFoodConsumptionRate * entryStatusComponent.extraBabyDinoConsumingFoodRateMultiplier * entryStatusComponent.babyDinoConsumingFoodRateMultiplier * entryStatusComponent.foodConsumptionMultiplier;
        return dinoData.dino.currentStats.food + (dinoFoodLossPerSecond * gameTimeOffset);
    }

    public static double CalculateTotalInventoryFood(ArkDinosReply dinoData) {
        //Get the food list of this dino class.
        DinoEntryFood[] dinoFoodData = dinoData.dino_entry.childFoods;
        if(dinoFoodData == null) {
            //Fallback
            dinoFoodData = dinoData.dino_entry.adultFoods;
        }
        if(dinoFoodData == null) {
            //TODO: Throw error
            //throw "No dino food info found";
        }

        //Calculates the total food energy inside the inventory of this dino.
        double total = 0;
        for(int i = 0; i < dinoData.inventory_items.length; i+=1) {
            //Loop through inventory items and get the total food energy.
            ArkItem item = dinoData.inventory_items[i];
            ArkItemEntry item_data = dinoData.item_class_data.get(item.classnameString);

            //Check if this item data can give food
            if(item_data.addStatusValues.containsKey("EPrimalCharacterStatusValue::Food")) {
                ArkFoodAddStatusValues foodData = item_data.addStatusValues.get("EPrimalCharacterStatusValue::Food");
                double foodEnergy = foodData.baseAmountToAdd * item.stackSize; //Get the total energy, multiplied by the stack size, but before calculating the dino's food data.

                //Check if we have data for this in the dino food data
                for(int j = 0; j<dinoFoodData.length; j+=1) {
                    DinoEntryFood thisFoodData = dinoFoodData[i];
                    if(thisFoodData.classname.equals(item.classnameString)) {
                        //Great. We got the info. Add it and break.
                        total += thisFoodData.foodEffectivenessMultiplier * foodEnergy;
                        break;
                    }
                }
            }
        }

        return total;
    }

    public static double CalculateTotalDinoFood(ArkDinosReply dinoData, double gameTimeOffset) {
        return CalculateCurrentDinoFood(dinoData, gameTimeOffset) + CalculateTotalInventoryFood(dinoData);
    }

    public static double CalculateFoodLossPerSecond(ArkDinosReply dinoData) {
        DinoEntryStatus entryStatusComponent = dinoData.dino_entry.statusComponent;
        return entryStatusComponent.baseFoodConsumptionRate * entryStatusComponent.extraBabyDinoConsumingFoodRateMultiplier * entryStatusComponent.babyDinoConsumingFoodRateMultiplier * entryStatusComponent.foodConsumptionMultiplier;
    }

    public static double CalculateTimeToFoodDepletionMs(double foodAmount, ArkDinosReply dinoData) {
        double dinoFoodLossPerSecond = CalculateFoodLossPerSecond(dinoData);
        return (foodAmount / Math.abs(dinoFoodLossPerSecond)) * 1000;
    }

    public static BabyDinoInfoPackage GetFullDinoInfo(ArkDinosReply dinoData, double gameTimeOffset) {
        //Calculate
        BabyDinoInfoPackage p = new BabyDinoInfoPackage();
        p.currentFood = CalculateCurrentDinoFood(dinoData, gameTimeOffset);
        p.inventoryFood = CalculateTotalInventoryFood(dinoData);
        p.totalCurrentFood = p.currentFood + p.inventoryFood;
        p.foodLossPerSecond = CalculateFoodLossPerSecond(dinoData);
        p.timeToDepletionMs = CalculateTimeToFoodDepletionMs(p.totalCurrentFood, dinoData);

        return p;
    }
}
