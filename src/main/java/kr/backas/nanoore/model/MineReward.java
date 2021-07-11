package kr.backas.nanoore.model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("MineReward")
public class MineReward implements ConfigurationSerializable {

    private ItemStack itemStack;
    private int chance;

    public MineReward(ItemStack itemStack, int chance) {
        this.itemStack = itemStack;
        this.chance = chance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("itemStack", itemStack);
        map.put("chance", chance);
        return map;
    }

    public static MineReward deserialize(Map<String, Object> data) {
        return new MineReward(
                (ItemStack) data.get("itemStack"),
                (int) data.get("chance")
        );
    }
}
