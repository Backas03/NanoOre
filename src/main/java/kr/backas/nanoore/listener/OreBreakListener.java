package kr.backas.nanoore.listener;

import kr.backas.nanoore.MineManager;
import kr.backas.nanoore.model.Mine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OreBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Mine mine = MineManager.getInstance().findMine(e.getBlock());
        e.setCancelled(mine != null && mine.breakBlock(e.getPlayer(), e.getBlock()));
    }

}
