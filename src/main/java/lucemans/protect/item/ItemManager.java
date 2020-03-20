// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.Material;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import lucemans.NovaItems.NItem;

public class ItemManager
{
    static NItem marker;
    static NItem fuel;
    
    public static void giveMarker(final Player p) {
        p.getInventory().addItem(new ItemStack[] { getMarker() });
    }
    
    public static void giveFuel(final Player p) {
        p.getInventory().addItem(new ItemStack[] { getFuel() });
    }
    
    public static ItemStack getMarker() {
        ItemStack s = ItemManager.marker.make();
        final NBTItem item = new NBTItem(s);
        item.setBoolean("lucclaimmarker", Boolean.valueOf(true));
        s = item.getItem();
        return s;
    }
    
    public static ItemStack getFuel() {
        ItemStack s = ItemManager.fuel.make();
        final NBTItem item = new NBTItem(s);
        item.setBoolean("lucclaimfuel", Boolean.valueOf(true));
        s = item.getItem();
        return s;
    }
    
    public static boolean isMarker(final ItemStack item) {
        return new NBTItem(item).getBoolean("lucclaimmarker");
    }
    
    public static boolean isFuel(final ItemStack item) {
        return new NBTItem(item).getBoolean("lucclaimfuel");
    }
    
    static {
        ItemManager.marker = NItem.create(Material.LANTERN).setName("&6Land Marker").setDescription("&7Claims and protects land in a surrounding area.", "&7").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0);
        ItemManager.fuel = NItem.create(Material.FIREWORK_STAR).setName("&6Ethereal &8Fuel").setDescription("&7Used to fuel claim protectors.", "").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0);
    }
}
