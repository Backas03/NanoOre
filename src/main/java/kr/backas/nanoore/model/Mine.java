package kr.backas.nanoore.model;

import kr.backas.nanoore.NanoOre;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;

@SerializableAs("Mine")
public class Mine implements ConfigurationSerializable {

    private Location pos1, pos2;
    private String name;
    private List<String> type = new ArrayList<>();
    private Map<String, List<MineReward>> rewards = new HashMap<>();
    private Map<String, Integer> chance = new HashMap<>();
    private String block;
    private int regenerate = 20;
    private final Map<Block, Material> types = new HashMap<>();
    private final Map<Block, Byte> data = new HashMap<>();

    public Mine() {}

    public Mine(Location pos1, Location pos2, String name, Map<String, List<MineReward>> rewards, int regenerate, Map<String, Integer> chance, List<String> type, String block) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.name = name;
        this.rewards = rewards;
        this.regenerate = regenerate;
        this.chance = chance;
        this.type = type;
        this.block = block;
    }

    public void runRegenerateTask(Block block) {
        types.put(block, block.getType());
        data.put(block, block.getData());
        block.setType(Material.BEDROCK);
        Bukkit.getScheduler().runTaskLater(NanoOre.getInstance(), () -> {
            block.setType(types.get(block));
            block.setData(data.get(block));
            types.remove(block);
            data.remove(block);
        }, 20L * regenerate);
    }

    public void setChance(String custom, int chance) {
        this.chance.put(custom, chance);
    }

    public void forceRegenerateAll() {
        types.forEach(Block::setType);
    }

    public boolean breakBlock(Player player, Block block) {
        if (block == null || player == null) return false;
        if (!block.getType().name().equals(String.valueOf(this.block))) return false;
        int total = 0;
        for (int i : chance.values()) {
            total += i;
        }
        if (total != 100) return true;
        String key = null;
        int rate = 0;
        int random = getRandomChance();
        for (String temp : chance.keySet()) {
            int c = chance.get(temp);
            rate += c;
            if (rate >= random) {
                key = temp;
                break;
            }
        }
        if (key == null) return false;
        List<MineReward> rewards = this.rewards.get(key);
        if (rewards == null || rewards.isEmpty()) return true;
        total = 0;
        for (MineReward reward : rewards) {
            total += reward.getChance();
        }
        if (total != 100) return true;
        runRegenerateTask(block);
        rate = 0;
        random = getRandomChance();
        int index;
        for (index=0; index<rewards.size(); index++) {
            rate += rewards.get(index).getChance();
            if (rate >= random) break;
        }
        player.getInventory().addItem(rewards.get(index-1).getItemStack());
        return true;
    }

    public int getRandomChance() {
        return (int) (Math.random() * 100);
    }
    
    public void addReward(String custom, MineReward reward) {
        rewards.putIfAbsent(custom, new ArrayList<>());
        rewards.get(custom).add(reward);
    }

    public int getRegeneratePeriod() {
        return regenerate;
    }

    public void setPeriod(int seconds) {
        this.regenerate = seconds;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public List<MineReward> getRewards(String type) {
        return rewards.get(type);
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean canCreate() {
        return pos1 != null && pos2 != null && name != null;
    }

    public void addType(String custom) {
        if (this.type.contains(custom)) return;
        this.type.add(custom);
    }

    public List<String> getTypes() {
        return type;
    }

    public void setBlock(Material block) {
        this.block = block.name();
    }

    public Material getBlock() {
        return Material.valueOf(block);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("pos1", pos1);
        map.put("pos2", pos2);
        map.put("name", name);
        map.put("rewards", rewards);
        map.put("period", regenerate);
        map.put("chance", chance);
        map.put("type", type);
        map.put("block", block);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static Mine deserialize(Map<String, Object> data) {
        return new Mine(
                (Location) data.get("pos1"),
                (Location) data.get("pos2"),
                (String) data.get("name"),
                (Map<String, List<MineReward>>) data.get("rewards"),
                (int) data.get("period"),
                (Map<String, Integer>) data.get("chance"),
                (List<String>) data.get("type"),
                (String) data.get("block")
        );
    }
}
