package loyfael;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
/*
 * This plugin allows players to open a written book from a lectern by right-clicking it.
 * It checks if the clicked block is a lectern and if it contains a written book.
 * If so, it opens the book for the player.
 */
public class Main extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Block clickedBlock = event.getClickedBlock();
    Player player = event.getPlayer();

    // Vérifie que c'est un clic droit sur un bloc
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    // Verify if the player is in the "tuto" world (You can change this to your desired world)
    // If not, we do nothing. I need this functionality only for my tuto world.
    if (!player.getWorld().getName().equals("tuto")) {
      return;
    }

    if (clickedBlock != null && clickedBlock.getType() == Material.LECTERN) {
      BlockState state = clickedBlock.getState();
      if (state instanceof Lectern lectern) {
        ItemStack book = lectern.getInventory().getItem(0);
        if (book != null && book.getType() == Material.WRITTEN_BOOK) {
          // Annule l'événement pour empêcher l'ouverture de l'interface du pupitre
          event.setCancelled(true);

          // Ouvre le livre avec un léger délai pour s'assurer que l'interface du pupitre ne s'ouvre pas
          new BukkitRunnable() {
            @Override
            public void run() {
              player.openBook(book);
            }
          }.runTaskLater(this, 1L); // 1 tick de délai
        }
      }
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();

    // Vérifie si le joueur est dans le monde "tuto"
    if (!player.getWorld().getName().equals("tuto")) {
      return;
    }

    // Vérifie si l'inventaire fermé est celui d'un pupitre
    if (event.getInventory().getType() == InventoryType.LECTERN) {
      // Trouve le pupitre associé
      if (event.getInventory().getLocation() != null) {
        Block lecternBlock = event.getInventory().getLocation().getBlock();
        if (lecternBlock.getType() == Material.LECTERN) {
          BlockState state = lecternBlock.getState();
          if (state instanceof Lectern lectern) {
            // Remet la page du pupitre à 1 quand le joueur ferme l'interface
            lectern.setPage(0); // Page 0 = première page
            lectern.update();
          }
        }
      }
    }
  }
}