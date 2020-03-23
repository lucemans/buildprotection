package lucemans.protect;//
// Decompiled by Procyon v0.5.36
// 

import lucemans.protect.ninventory.NInventory;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import lucemans.protect.managers.NameManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import lucemans.protect.item.ItemManager;
import org.bukkit.plugin.Plugin;
import lucemans.protect.events.ClaimHandler;
import lucemans.protect.commands.AdminClaimCommand;
import org.bukkit.command.CommandExecutor;
import lucemans.protect.commands.ClaimCommand;
import org.bukkit.Bukkit;
import lucemans.protect.managers.FileManager;
import java.util.ArrayList;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Protect extends JavaPlugin implements Listener
{
    public static Protect instance;
    NamespacedKey key;
    NamespacedKey key2;
    NamespacedKey key3;
    NamespacedKey key4;
    public ArrayList<String> admins;
    
    public Protect() {
        this.admins = new ArrayList<String>();
    }
    
    public void onEnable() {
        Protect.instance = this;
        try {
            FileManager.loadFile();
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("ERROR LOADING FILE, SHUTTING DOWN SERVER FOR SAFETY REASONS");
            Bukkit.getServer().shutdown();
        }
        Bukkit.getPluginCommand("nprotect").setExecutor((CommandExecutor)new ClaimCommand());
        Bukkit.getPluginCommand("nprotectadmin").setExecutor((CommandExecutor)new AdminClaimCommand());
        Bukkit.getPluginManager().registerEvents((Listener)new ClaimHandler(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        try {
            this.key = new NamespacedKey((Plugin)this, "claim_marker");
            final ShapedRecipe recipe = new ShapedRecipe(this.key, ItemManager.getMarker());
            recipe.shape(new String[] { "EEE", "ESE", "DGD" });
            recipe.setIngredient('E', Material.GLASS);
            recipe.setIngredient('S', Material.LANTERN);
            recipe.setIngredient('D', Material.GOLD_INGOT);
            recipe.setIngredient('G', Material.DIAMOND);
            this.key2 = new NamespacedKey((Plugin)this, "claim_fuel");
            final ItemStack f3 = ItemManager.getFuel();
            f3.setAmount(2);
            final ShapelessRecipe recipe2 = new ShapelessRecipe(this.key2, f3);
            recipe2.addIngredient(2, Material.REDSTONE);
            recipe2.addIngredient(Material.LAPIS_LAZULI);
            this.key3 = new NamespacedKey((Plugin)this, "claim_fuel2");
            final ShapelessRecipe recipe3 = new ShapelessRecipe(this.key3, ItemManager.getFuel());
            recipe3.addIngredient(4, Material.COAL);
            recipe3.addIngredient(2, Material.REDSTONE);
            this.key4 = new NamespacedKey((Plugin)this, "claim_fuel3");
            final ItemStack f4 = ItemManager.getFuel();
            f4.setAmount(8);
            final ShapelessRecipe recipe4 = new ShapelessRecipe(this.key4, f4);
            recipe4.addIngredient(8, Material.REDSTONE);
            recipe4.addIngredient(1, Material.DIAMOND);
            Bukkit.addRecipe((Recipe)recipe);
            Bukkit.addRecipe((Recipe)recipe4);
            Bukkit.addRecipe((Recipe)recipe3);
            Bukkit.addRecipe((Recipe)recipe2);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    NameManager.setSuffix(player);
                }
            }
        }, 1L, 2000L);

        for (World w : Bukkit.getWorlds()) {
            w.setKeepSpawnInMemory(false);
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        NameManager.setSuffix(event.getPlayer());
    }
    
    public void onDisable() {
        try {
            NInventory.destroyAll();
        } catch (Exception e) {

        }
        FileManager.saveFile();
        try {
            Bukkit.removeRecipe(this.key);
            Bukkit.removeRecipe(this.key2);
            Bukkit.removeRecipe(this.key3);
            Bukkit.removeRecipe(this.key4);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
