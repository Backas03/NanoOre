package kr.backas.nanoore;

import kr.backas.nanoore.model.Mine;
import kr.backas.nanoore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MineManager {
    
    private static final MineManager instance = new MineManager();
    
    public static MineManager getInstance() {
        return instance;
    }
    
    private MineManager() {}
    
    private final Map<UUID, Mine> playerSection = new HashMap<>();
    private final Map<String, Mine> sections = new HashMap<>();

    public Mine getPlayerSection(Player player) {
        playerSection.putIfAbsent(player.getUniqueId(), new Mine());
        return playerSection.get(player.getUniqueId());
    }

    public Mine findMine(Block block) {
        Location l = block.getLocation();
        for (Mine mine : sections.values()) {

            double min_x = Math.min(mine.getPos1().getX(), mine.getPos2().getX());
            double max_x = Math.max(mine.getPos1().getX(), mine.getPos2().getX());

            double min_y = Math.min(mine.getPos1().getY(), mine.getPos2().getY());
            double max_y = Math.max(mine.getPos1().getY(), mine.getPos2().getY());

            double min_z = Math.min(mine.getPos1().getZ(), mine.getPos2().getZ());
            double max_z = Math.max(mine.getPos1().getZ(), mine.getPos2().getZ());

            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();

            if (x >= min_x && x<= max_x && y >= min_y && y <= max_y && z >= min_z && z <= max_z) {
                return mine;
            }
        }
        return null;
    }

    public ItemStack getMineManagingItem() {
        ItemStack i = new ItemStack(Material.GOLD_AXE);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName("광산 관리 도구");
        m.setLore(Arrays.asList("§6우클릭 - pos 1", "§6 좌클릭 - pos 2"));
        i.setItemMeta(m);
        return i;
    }

    public void loadAllSections(boolean async) {
        Bukkit.broadcastMessage("§7[광산] §f모든 섹션을 로딩 합니다...");
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
                loadAllSections();
                Bukkit.broadcastMessage("§7[광산] §f...모든 섹션이 정상적으로 로딩 되었습니다.");
            });
            return;
        }
        loadAllSections();
        Bukkit.broadcastMessage("§7[광산] §f...모든 섹션이 정상적으로 로딩 되었습니다.");
    }

    public void unloadAllSections() {
        Bukkit.broadcastMessage("§7[광산] §f...모든 섹션을 언로딩 합니다...");
        sections.values().forEach(Mine::forceRegenerateAll);
        sections.clear();
        Bukkit.broadcastMessage("§7[광산] §f...모든 섹션이 정상적으로 언로딩 되었습니다.");
    }

    public void unloadSection(String name, boolean debug) {
        if (debug) {
            Bukkit.broadcastMessage("§7[광산] §f..." + name + " 섹션을 언로딩 합니다...");
        }
        sections.remove(name);
        if (debug) {
            Bukkit.broadcastMessage("§7[광산] §f..." + name + " 섹션이 정상적으로 언로딩 되었습니다.");
        }
    }

    public void loadSection(String name, boolean debug) {
        if (debug) {
            Bukkit.broadcastMessage("§7[광산] §f..." + name + " 섹션을 로딩 합니다...");
        }
        Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
            Mine mine = new ConfigManager<>(getFile(), Mine.class).get(name);
            if (mine == null || mine.getName() == null) {
                if (debug) {
                    Bukkit.broadcastMessage("§7[광산] §f..." + name + " 섹션 로딩 실패!");
                }
                return;
            }
            sections.put(mine.getName(), mine);
            if (debug) {
                Bukkit.broadcastMessage("§7[광산] §f..." + name + " 섹션이 정상적으로 로딩되었습니다.");
            }
        });
    }

    private void loadAllSections() {
        ConfigManager<Mine> configManager = new ConfigManager<>(getFile(), Mine.class);
        YamlConfiguration.loadConfiguration(getFile()).getKeys(false).forEach(k -> {
            Mine mine = configManager.get(k);
            if (mine != null) {
                sections.put(mine.getName(), mine);
            }
        });
    }

    public Mine getSection(String name) {
        return sections.get(name);
    }

    public void tryCreateSection(Player player) {
        Mine mine = playerSection.get(player.getUniqueId());
        if (mine == null || !mine.canCreate()) {
            player.sendMessage("생성 실패");
            return;
        }
        player.sendMessage("처리중입니다...");
        Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
            if (!getDataFolder().exists()) getDataFolder().mkdir();
            if (!getFile().exists()) {
                try {
                    getFile().createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            new ConfigManager<>(getFile(), Mine.class).set(mine.getName(), mine);
            sections.put(mine.getName(), mine);
            player.sendMessage("... 생성 & 로드 완료!");
        });
    }

    public void deleteSection(String name) {
        Mine mine = sections.get(name);
        if (mine != null) {
            mine.forceRegenerateAll();
        }
        sections.remove(name, null);
        new ConfigManager<>(getFile(), Mine.class).set(name, null);
    }

    public Collection<Mine> getSections() {
        return sections.values();
    }

    public File getDataFolder() {
        return NanoOre.getInstance().getDataFolder();
    }

    public File getFile() {
        return new File(getDataFolder(), "sections.yml");
    }
}
