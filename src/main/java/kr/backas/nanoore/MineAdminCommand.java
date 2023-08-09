package kr.backas.nanoore;

import kr.backas.nanoore.model.Mine;
import kr.backas.nanoore.model.MineReward;
import kr.backas.nanoore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class MineAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage("/광산관리 도구");
                player.sendMessage("/광산관리 리젠시간 [이름] [초]");
                player.sendMessage("/광산관리 강제리젠 [이름]");
                player.sendMessage("/광산관리 생성 [이름]");
                player.sendMessage("/광산관리 [로드/언로드] [이름/모두]");
                player.sendMessage("/광산관리 타입추가 [이름] [타입]");
                player.sendMessage("/광산관리 확률 [이름] [타입] [확률]");
                player.sendMessage("/광산관리 보상추가 [이름] [타입] [확률]- 손에 든 아이템의 보상으로 설정");
                player.sendMessage("/광산관리 블럭 [이름] - 바라보는 블럭으로 설정");
                player.sendMessage("/광산관리 리로드 [이름/모두]");
                player.sendMessage("/광산관리 저장 [이름]");
                player.sendMessage("/광산관리 삭제 [이름]");
                return false;
            }
            if (args[0].equals("블럭")) {
                if (args.length == 2) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    Block block = player.getTargetBlock(null, 10);
                    if (block == null) {
                        player.sendMessage("10 블럭 이내에 바라보는 블럭이 없습니다!");
                        return false;
                    }
                    mine.setBlock(block.getType());
                    player.sendMessage("설정 완료");
                }
                player.sendMessage("/광산관리 블럭 [이름] - 바라보는 블럭으로 설정");
                return false;
            }
            if (args[0].equals("보상추가")) {
                if (args.length == 4) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    if (mine.getTypes().contains(args[2])) {
                        ItemStack i = player.getInventory().getItemInMainHand();
                        if (i == null || i.getType() == Material.AIR) {
                            player.sendMessage("손에 아이템을 들어주세요.");
                            return false;
                        }
                        int chance;
                        try {
                            chance = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("자연수를 입력해주세요");
                            return false;
                        }
                        MineReward mineReward = new MineReward(i, chance);
                        mine.addReward(args[2], mineReward);
                        player.sendMessage("추가 완료");
                        return false;
                    }
                    player.sendMessage("추가 실패");
                    return false;
                }
                player.sendMessage("/광산관리 보상추가 [이름] [타입] [확률]- 손에 든 아이템의 보상으로 설정");
                return false;
            }
            if (args[0].equals("확률")) {
                if (args.length == 4) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    int chance;
                    try {
                        chance = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("자연수를 입력해주세요");
                        return false;
                    }
                    mine.setChance(args[2], chance);
                    player.sendMessage("설정 완료");
                }
                player.sendMessage("/광산관리 확률 [이름] [타입] [확률]");
                return false;
            }
            if (args[0].equals("타입추가")) {
                if (args.length == 3) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    mine.addType(args[2]);
                    player.sendMessage("추가 완료");
                }
                player.sendMessage("/광산관리 타입추가 [이름] [타입]");
                return false;
            }
            if (args[0].equals("저장")) {
                if (args.length == 2) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    player.sendMessage("처리중입니다... 잠시만 기다려주세요.");
                    Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
                        new ConfigManager<>(MineManager.getInstance().getFile(), Mine.class).set(mine.getName(), mine);
                        player.sendMessage("처리 완료.");
                    });
                    return false;
                }
                player.sendMessage("/광산관리 저장 [이름]");
                return false;
            }
            if (args[0].equals("리로드")) {
                if (args.length == 2) {
                    if (args[1].equals("모두")) {
                        MineManager.getInstance().unloadAllSections();
                        MineManager.getInstance().loadAllSections(true);
                        return false;
                    }
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    player.sendMessage("처리중입니다... 잠시만 기다려주세요.");
                    Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
                        MineManager.getInstance().unloadSection(args[1], false);
                        MineManager.getInstance().loadSection(args[1], false);
                        player.sendMessage("처리 완료");
                    });
                    return false;
                }
                player.sendMessage("/광산관리 리로드 [이름/모두]");
                return false;
            }
            if (args[0].equals("삭제")) {
                if (args.length == 2) {
                    player.sendMessage("삭제 처리중입니다...");
                    Bukkit.getScheduler().runTaskAsynchronously(NanoOre.getInstance(), () -> {
                        MineManager.getInstance().deleteSection(args[1]);
                        player.sendMessage("...삭제 완료");
                    });
                    return false;
                }
            }
            if (args[0].equals("강제리젠")) {
                if (args.length == 2) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    mine.forceRegenerateAll();
                    player.sendMessage("완료");
                    return false;
                }
                player.sendMessage("/광산관리 강제리젠 [이름]");
                return false;
            }
            if (args[0].equals("리젠시간")) {
                if (args.length == 3) {
                    Mine mine = MineManager.getInstance().getSection(args[1]);
                    if (mine == null) {
                        player.sendMessage(args[1] + " 섹션은 없거나 로딩되지 않은 섹션입니다.");
                        return false;
                    }
                    int seconds;
                    try {
                        seconds = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("자연수를 입력해주세요");
                        return false;
                    }
                    if (seconds < 0) {
                        player.sendMessage("시간은 0초 미만으로 설정될 수 없습니다.");
                        return false;
                    }
                    mine.setPeriod(seconds);
                    player.sendMessage("설정 완료.");
                    return false;
                }
                player.sendMessage("/광산관리 리젠시간 [이름] [초]");
                return false;
            }
            if (args[0].equals("로드")) {
                if (args.length == 2) {
                    if (args[1].equals("모두")) {
                        MineManager.getInstance().loadAllSections(true);
                        return false;
                    }
                    MineManager.getInstance().loadSection(args[1], true);
                }
                player.sendMessage("/광산관리 로드 [이름/모두]");
                return false;
            }
            if (args[0].equals("언로드")) {
                if (args.length == 2) {
                    if (args[1].equals("모두")) {
                        MineManager.getInstance().unloadAllSections();
                        return false;
                    }
                    MineManager.getInstance().unloadSection(args[1], true);
                }
                player.sendMessage("/광산관리 언로드 [이름/모두]");
                return false;
            }
            if (args[0].equals("도구")) {
                player.getInventory().addItem(MineManager.getInstance().getMineManagingItem());
                return false;
            }
            if (args[0].equals("생성")) {
                if (args.length == 2) {
                    MineManager.getInstance().getPlayerSection(player).setName(args[1]);
                    MineManager.getInstance().tryCreateSection(player);
                    return false;
                }
            }
        }
        return false;
    }
}
