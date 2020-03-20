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

public class ItemManager {

    public static void giveMarker(final Player p) {
        p.getInventory().addItem(getMarker());
    }

    public static void giveFuel(final Player p) {
        p.getInventory().addItem(getFuel());
    }

    public static ItemStack getMarker() {
        ItemStack s = ItemManager.marker.make();
        final NBTItem item = new NBTItem(s);
        item.setBoolean("lucclaimmarker", true);
        s = item.getItem();
        return s;
    }

    public static ItemStack getFuel() {
        ItemStack s = ItemManager.fuel.make();
        final NBTItem item = new NBTItem(s);
        item.setBoolean("lucclaimfuel", true);
        s = item.getItem();
        return s;
    }

    public static boolean isMarker(ItemStack item) {
        return new NBTItem(item).getBoolean("lucclaimmarker");
    }

    public static boolean isFuel(ItemStack item) {
        return new NBTItem(item).getBoolean("lucclaimfuel");
    }

    static NItem marker = NItem.create(Material.LANTERN).setName("&6Land Marker").setDescription("&7Claims and protects land in a surrounding area.", "&7").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0);
    static NItem fuel = NItem.create(Material.FIREWORK_STAR).setName("&6Ethereal &8Fuel").setDescription("&7Used to fuel claim protectors.", "").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0);
}
