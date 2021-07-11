package kr.backas.nanoore.listener;

import kr.backas.nanoore.MineManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SectionCreateListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        try {
            Player player = e.getPlayer();
            if (player.getInventory().getItemInMainHand() == null) return;
            if (e.getHand() != EquipmentSlot.HAND) return;
            if (player.getInventory().getItemInMainHand().equals(MineManager.getInstance().getMineManagingItem()) && player.isOp()) {
                Location l = e.getClickedBlock().getLocation();
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    MineManager.getInstance().getPlayerSection(player).setPos2(l);
                    player.sendMessage("pos 2 지정 완료 §7" + l);
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    MineManager.getInstance().getPlayerSection(player).setPos1(l);
                    player.sendMessage("pos 1 지정 완료 §7" + l);
                }
                e.setCancelled(true);
            }
        } catch (Exception ignore) {

        }
    }
}
