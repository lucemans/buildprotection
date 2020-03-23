// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.gui;

import lucemans.protect.Protect;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import lucemans.protect.util.ChatUtil;
import org.bukkit.enchantments.Enchantment;
import java.lang.reflect.Field;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.BannerMeta;
import lucemans.protect.managers.LandManager;
import lucemans.protect.item.ItemManager;

import java.util.List;
import org.bukkit.OfflinePlayer;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import lucemans.protect.NovaItems.NItem;
import org.bukkit.Material;
import lucemans.protect.ninventory.NInventory;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.entity.Player;
import lucemans.protect.obj.LandClaim;

public class GuiManager
{
    public static void openOfficerGui(LandClaim claim, Player p) {
        final NInventory ninv = new NInventory(Bukkit.getOfflinePlayer(UUID.fromString(claim.uuid)).getName() + "'s Marker", 27, Protect.instance);
        claim.invs.add(ninv);
        ninv.setItem(NItem.create(Material.PLAYER_HEAD).setName("&rTeam").setDescription("&7Modify claim permissions.", "").make(), 11);
        ninv.setLClick(11, new Runnable() {
            @Override
            public void run() {
                GuiManager.openTeam(claim, p);
            }
        });
        ninv.setItem(NItem.create(Material.EMERALD).setName("&rActive Effects").setDescription("", "&7This feature is not available yet.", "").make(), 12);
        ninv.setItem(NItem.create(Material.MAP).setName("&rProtected Area").setDescription("&7Edit the surface your marker protects", "", "").make(), 13);
        ninv.setLClick(13, new Runnable() {
            @Override
            public void run() {
                GuiManager.openMap(claim, p);
            }
        });
        ninv.setItem(NItem.create(Material.BARRIER).setName("&rDestroy").setDescription("&7Click this to remove your marker.", "").make(), 14);
        ninv.setLClick(14, new Runnable() {
            @Override
            public void run() {
                GuiManager.openDestroy(claim, p);
            }
        });
        ninv.setItem(NItem.create(Material.REDSTONE).setName("&rSettings").setDescription("&7Modify the flags of your claim.", "", "").make(), 15);
        ninv.setLClick(15, new Runnable() {
            @Override
            public void run() {
                GuiManager.openSettings(claim, p);
                System.out.println(p.getName() + " clicked settings.");
            }
        });
        p.openInventory(ninv.getInv());
    }
    
    public static void openTeam(LandClaim claim, Player p) {
        final int c = (int)(Math.ceil(claim.members.size() / 9.0) * 9.0);
        final NInventory ninv = new NInventory("Team Editor", 9 + ((c > 1) ? c : 9), Protect.instance);
        claim.invs.add(ninv);
        final ItemStack item = NItem.create(Material.BLACK_STAINED_GLASS_PANE).setName("&8Team Editor").make();
        for (int i = 0; i < 9; ++i) {
            ninv.setItem(item, i);
        }
        ninv.setItem(NItem.create(Material.FEATHER).setName("&rAdd New People").make(), 8);
        ninv.setLClick(8, new Runnable() {
            @Override
            public void run() {
                GuiManager.addNewPeople(claim, p);
            }
        });
        int f = 1;
        ItemStack item3 = NItem.create(Material.PLAYER_HEAD).setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&6Owner", "", "&a&lShift + Left Click&r to demote to member.", "&c&lShift + Right Click&r to revoke access.").setName(Bukkit.getOfflinePlayer(UUID.fromString(claim.uuid)).getName()).make();
        SkullMeta meta3 = (SkullMeta)item3.getItemMeta();
        meta3.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(claim.uuid)));
        item3.setItemMeta((ItemMeta)meta3);
        ninv.setItem(item3, 9);
        for (int i = 0; i < claim.officers.size(); ++i) {
            int x = i;
            ItemStack item2 = NItem.create(Material.PLAYER_HEAD).setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&aOfficer", "", "&a&lShift + Left Click&r to demote to member.", "&c&lShift + Right Click&r to revoke access.").setName(Bukkit.getOfflinePlayer(UUID.fromString(claim.officers.get(x))).getName()).make();
            SkullMeta meta = (SkullMeta)item2.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(claim.officers.get(x))));
            item2.setItemMeta((ItemMeta)meta);
            ninv.setItem(item2, f + 9);
            ninv.setShiftLClick(f + 9, new Runnable() {
                @Override
                public void run() {
                    claim.members.add(claim.officers.get(x));
                    claim.officers.remove(claim.officers.get(x));
                    GuiManager.openTeam(claim, p);
                }
            });
            ninv.setShiftRClick(f + 9, new Runnable() {
                @Override
                public void run() {
                    claim.officers.remove(claim.officers.get(x));
                    GuiManager.openTeam(claim, p);
                }
            });
            f++;
        }
        for (int i = 0; i < claim.members.size(); ++i) {
            int x = i;
            ItemStack item2 = NItem.create(Material.PLAYER_HEAD).setDescription("&7Member", "", "&a&lShift + Left Click&r to promote to Officer.", "&c&lShift + Right Click&r to revoke access.").setName(Bukkit.getOfflinePlayer(UUID.fromString(claim.members.get(x))).getName()).make();
            SkullMeta meta = (SkullMeta)item2.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(claim.members.get(x))));
            item2.setItemMeta((ItemMeta)meta);
            ninv.setItem(item2, f + 9);
            ninv.setShiftRClick(f + 9, new Runnable() {
                @Override
                public void run() {
                    claim.members.remove(claim.members.get(x));
                    GuiManager.openTeam(claim, p);
                }
            });
            ninv.setShiftLClick(f + 9, new Runnable() {
                @Override
                public void run() {
                    claim.officers.add(claim.members.get(x));
                    claim.members.remove(claim.members.get(x));
                    GuiManager.openTeam(claim, p);
                }
            });
            f++;
        }
        p.openInventory(ninv.getInv());
    }
    
    public static void addNewPeople(LandClaim claim, Player p) {
        final List<Player> pp = new ArrayList<Player>();
        for (final Player _p : Bukkit.getOnlinePlayers()) {
            if (claim.isInArea(_p.getLocation()) && !claim.members.contains(_p.getUniqueId().toString()) && !claim.uuid.equalsIgnoreCase(_p.getUniqueId().toString())) {
                pp.add(_p);
            }
        }
        final NInventory ninv = new NInventory("Add Members", (pp.size() > 9) ? ((int)(Math.ceil(pp.size() / 9) * 9.0)) : 9, Protect.instance);
        for (int i = 0; i < pp.size(); ++i) {
            final int x = i;
            final ItemStack item = NItem.create(Material.PLAYER_HEAD).setName(pp.get(x).getName()).setDescription("&7Click to add to your team").make();
            final SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwningPlayer((OfflinePlayer)pp.get(x));
            item.setItemMeta((ItemMeta)meta);
            ninv.setItem(item, i);
            ninv.setLClick(i, new Runnable() {
                @Override
                public void run() {
                    claim.members.add(pp.get(x).getUniqueId().toString());
                    GuiManager.openTeam(claim, p);
                }
            });
        }
        p.openInventory(ninv.getInv());
    }
    
    public static void openResources(LandClaim claim, Player p) {
    }
    
    public static void openMap(LandClaim claim, Player p) {
        final NInventory ninv = new NInventory("Area Editor", 45, Protect.instance);
        claim.invs.add(ninv);
        final Integer limit = 32;
        ninv.setUpdate(new Runnable() {
            @Override
            public void run() {
                Integer fuelCount = 0;
                for (final ItemStack i : p.getInventory().getContents()) {
                    if (i != null && ItemManager.isFuel(i)) {
                        fuelCount += i.getAmount();
                    }
                }
                boolean canExpandNorth = true;
                for (int j = -claim.west; j <= claim.east; ++j) {
                    final LandClaim r = LandManager.isInClaim(claim.getLocation().add((double)j, 0.0, (double)(-claim.north - 1)));
                    if (r != null) {
                        canExpandNorth = false;
                        break;
                    }
                }
                boolean canExpandSouth = true;
                for (int k = -claim.west; k <= claim.east; ++k) {
                    final LandClaim r2 = LandManager.isInClaim(claim.getLocation().add((double)k, 0.0, (double)(claim.south + 1)));
                    if (r2 != null) {
                        canExpandSouth = false;
                        break;
                    }
                }
                boolean canExpandEast = true;
                for (int l = -claim.north; l <= claim.south; ++l) {
                    final LandClaim r3 = LandManager.isInClaim(claim.getLocation().add((double)(claim.east + 1), 0.0, (double)l));
                    if (r3 != null) {
                        canExpandEast = false;
                        break;
                    }
                }
                boolean canExpandWest = true;
                for (int m = -claim.north; m <= claim.south; ++m) {
                    final LandClaim r4 = LandManager.isInClaim(claim.getLocation().add((double)(-claim.west - 1), 0.0, (double)m));
                    if (r4 != null) {
                        canExpandWest = false;
                        break;
                    }
                }
                ItemStack b = NItem.create(Material.BLACK_BANNER).setAmount(claim.north).make();
                BannerMeta meta = (BannerMeta)b.getItemMeta();
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_TOP));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                b.setItemMeta((ItemMeta)meta);
                GuiManager.setIncreaseMenu(ninv, claim, p, 1, b, fuelCount, "north", canExpandNorth);
                b = NItem.create(Material.BLACK_BANNER).setAmount(claim.east).make();
                meta = (BannerMeta)b.getItemMeta();
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                b.setItemMeta((ItemMeta)meta);
                GuiManager.setIncreaseMenu(ninv, claim, p, 3, b, fuelCount, "east", canExpandEast);
                b = NItem.create(Material.WHITE_BANNER).setAmount(claim.south).make();
                meta = (BannerMeta)b.getItemMeta();
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                b.setItemMeta((ItemMeta)meta);
                ninv.setItem(b, 23);
                GuiManager.setIncreaseMenu(ninv, claim, p, 5, b, fuelCount, "south", canExpandSouth);
                b = NItem.create(Material.BLACK_BANNER).setAmount(claim.west).make();
                meta = (BannerMeta)b.getItemMeta();
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_BOTTOM));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
                meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
                meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
                b.setItemMeta((ItemMeta)meta);
                ninv.setItem(b, 25);
                GuiManager.setIncreaseMenu(ninv, claim, p, 7, b, fuelCount, "west", canExpandWest);
            }
        });
        p.openInventory(ninv.getInv());
    }
    
    public static void setIncreaseMenu(NInventory ninv, LandClaim claim, Player p, Integer offset, ItemStack banner, Integer fuelCount, String value, boolean canExpand) {
        final ItemStack nf = NItem.create(Material.GRAY_STAINED_GLASS_PANE).setName("&c&lNo Fuel").make();
        final ItemStack lr = NItem.create(Material.BARRIER).setName("&c&lLimit Reached").make();
        final ItemStack overlap = NItem.create(Material.YELLOW_STAINED_GLASS_PANE).setName("&eWARNING &rArea Overlap").make();
        try {
            final Field f = claim.getClass().getField(value);
            final Integer v = (Integer)f.get(claim);
            if (v < 32) {
                if (canExpand) {
                    if (fuelCount > 0) {
                        ninv.setItem(NItem.create(Material.GREEN_STAINED_GLASS_PANE).setName("&aIncrease Range").make(), 9 + offset);

                        final ItemStack[] array;
                        int length;
                        int j = 0;
                        ItemStack i;
                        ninv.setLClick(9 + offset, () -> {
                            try {
                                f.set(claim, v + 1);
                                for (ItemStack item : p.getInventory().getContents()) {
                                        if (item != null && ItemManager.isFuel(item)) {
                                            item.setAmount(item.getAmount() - 1);
                                            break;
                                        }
                                    }
                                }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            openMap(claim, p);
                            return;
                        });
                    }
                    else {
                        ninv.setItem(nf, 9 + offset);
                    }
                }
                else {
                    ninv.setItem(overlap, 9 + offset);
                }
            }
            else {
                ninv.setItem(lr, 9 + offset);
            }
            final NItem item = new NItem(banner);
            item.setItemFlag(ItemFlag.HIDE_ENCHANTS);
            item.setItemFlag(ItemFlag.HIDE_ATTRIBUTES);
            item.setDescription("&7Expansion of land towards the " + value, "&7Current: &r" + v);
            ninv.setItem(item.make(), 18 + offset);
            if (v > 2) {
                ninv.setItem(NItem.create(Material.RED_STAINED_GLASS_PANE).setName("&cDecrease Range" + value).make(), 27 + offset);
                ninv.setLClick(27 + offset, () -> {
                    try {
                        f.set(claim, v - 1);
                        p.getInventory().addItem(ItemManager.getFuel());
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    openMap(claim, p);
                });
            }
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
    }
    
    public static void openDestroy(LandClaim claim, Player p) {
        final NInventory ninv = new NInventory("Destroy Claim", 27, Protect.instance);
        claim.invs.add(ninv);
        final Runnable destroy = new Runnable() {
            @Override
            public void run() {
                claim.triggerDestroy(p);
                p.closeInventory();
            }
        };
        final ItemStack r = NItem.create(Material.RED_STAINED_GLASS_PANE).setName("Cancel").make();
        final Runnable r2 = new Runnable() {
            @Override
            public void run() {
                p.closeInventory();
            }
        };
        for (int i = 0; i < 27; ++i) {
            if (i == 11) {
                ninv.setItem(NItem.create(Material.GREEN_STAINED_GLASS_PANE).setName("Destroy").make(), 11);
                ninv.setLClick(11, destroy);
            }
            else {
                ninv.setItem(r, i);
                ninv.setLClick(i, r2);
            }
        }
        p.openInventory(ninv.getInv());
    }
    
    public static void openSettings(LandClaim claim, Player p) {
        final NInventory ninv = new NInventory("Claim Settings", 45, Protect.instance);
        claim.invs.add(ninv);
        ninv.setUpdate(new Runnable() {
            @Override
            public void run() {
                if (claim.permitPvP) {
                    ninv.setItem(NItem.create(Material.GOLDEN_SWORD).setName("&rCombat").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Prevent players from hitting eachother", "", "&7Status: &a&lENABLED", "").make(), 11);
                }
                else {
                    ninv.setItem(NItem.create(Material.GOLDEN_SWORD).setName("&rCombat").setDescription("&7Prevent players from hitting eachother", "", "&7Status: &c&lDISABLED", "").make(), 11);
                }
                ninv.setLClick(11, new Runnable() {
                    @Override
                    public void run() {
                        claim.permitPvP = !claim.permitPvP;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.mobSpawn) {
                    ninv.setItem(NItem.create(Material.CREEPER_SPAWN_EGG).setName("&rMob Spawning").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow hostile mobs to spawn", "", "&7Status: &a&lENABLED", "").make(), 12);
                }
                else {
                    ninv.setItem(NItem.create(Material.CREEPER_SPAWN_EGG).setName("&rMob Spawning").setDescription("&7Allow hostile mobs to spawn", "", "&7Status: &c&lDISABLED", "").make(), 12);
                }
                ninv.setLClick(12, new Runnable() {
                    @Override
                    public void run() {
                        claim.mobSpawn = !claim.mobSpawn;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.pistonsAllowed) {
                    ninv.setItem(NItem.create(Material.PISTON).setName("&rPistons").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow pistons to push blocks", "", "&7Status: &a&lENABLED", "").make(), 13);
                }
                else {
                    ninv.setItem(NItem.create(Material.PISTON).setName("&rPistons").setDescription("&7Allow pistons to push blocks", "", "&7Status: &c&lDISABLED", "").make(), 13);
                }
                ninv.setLClick(13, new Runnable() {
                    @Override
                    public void run() {
                        claim.pistonsAllowed = !claim.pistonsAllowed;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.chestPeek) {
                    ninv.setItem(NItem.create(Material.ENDER_CHEST).setName("&rChest Peek").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow other players to see the contents of your chests", "", "&7Status: &a&lENABLED", "").make(), 14);
                }
                else {
                    ninv.setItem(NItem.create(Material.CHEST).setName("&rChest Peek").setDescription("&7Allow other players to see the contents of your chests", "", "&7Status: &c&lDISABLED", "").make(), 14);
                }
                ninv.setLClick(14, new Runnable() {
                    @Override
                    public void run() {
                        claim.chestPeek = !claim.chestPeek;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.entityInteract) {
                    ninv.setItem(NItem.create(Material.SHEARS).setName("&rSheep Shear").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow other players to shear sheep", "", "&7Status: &a&lENABLED", "").make(), 15);
                }
                else {
                    ninv.setItem(NItem.create(Material.SHEARS).setName("&rSheep Shear").setDescription("&7Allow other players to shear sheep", "", "&7Status: &c&lDISABLED", "").make(), 15);
                }
                ninv.setLClick(15, new Runnable() {
                    @Override
                    public void run() {
                        claim.entityInteract = !claim.entityInteract;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.blockGeneration) {
                    ninv.setItem(NItem.create(Material.COBBLESTONE).setName("&rCobble Generator").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow blocks to be created from water and lava interaction", "", "&7Status: &a&lENABLED", "").make(), 20);
                }
                else {
                    ninv.setItem(NItem.create(Material.COBBLESTONE).setName("&rCobble Generator").setDescription("&7Allow blocks to be created from water and lava interaction", "", "&7Status: &c&lDISABLED", "").make(), 20);
                }
                ninv.setLClick(20, new Runnable() {
                    @Override
                    public void run() {
                        claim.blockGeneration = !claim.blockGeneration;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.buttonInteract) {
                    ninv.setItem(NItem.create(Material.OAK_BUTTON).setName("&rButton Trigger").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow buttons to be pushed by other players", "", "&7Status: &a&lENABLED", "").make(), 21);
                }
                else {
                    ninv.setItem(NItem.create(Material.OAK_BUTTON).setName("&rButton Trigger").setDescription("&7Allow buttons to be pushed by other players", "", "&7Status: &c&lDISABLED", "").make(), 21);
                }
                ninv.setLClick(21, new Runnable() {
                    @Override
                    public void run() {
                        claim.buttonInteract = !claim.buttonInteract;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.pressureInteract) {
                    ninv.setItem(NItem.create(Material.OAK_PRESSURE_PLATE).setName("&rPressure Trigger").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow pressure plates to be triggered by other players/mobs", "", "&7Status: &a&lENABLED", "").make(), 22);
                }
                else {
                    ninv.setItem(NItem.create(Material.OAK_PRESSURE_PLATE).setName("&rPressure Trigger").setDescription("&7Allow pressure plates to be triggered by other players/mobs", "", "&7Status: &c&lDISABLED", "").make(), 22);
                }
                ninv.setLClick(22, new Runnable() {
                    @Override
                    public void run() {
                        claim.pressureInteract = !claim.pressureInteract;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.doorInteract) {
                    ninv.setItem(NItem.create(Material.DARK_OAK_DOOR).setName("&rUnlocked Doors").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow other players to open doors", "", "&7Status: &a&lENABLED", "").make(), 23);
                }
                else {
                    ninv.setItem(NItem.create(Material.DARK_OAK_DOOR).setName("&rUnlocked Doors").setDescription("&7Allow other players to open doors", "", "&7Status: &c&lDISABLED", "").make(), 23);
                }
                ninv.setLClick(23, new Runnable() {
                    @Override
                    public void run() {
                        claim.doorInteract = !claim.doorInteract;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.attackFriendly) {
                    ninv.setItem(NItem.create(Material.PUFFERFISH_BUCKET).setName("&rKill Friendly Mobs").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow other players to kill animals", "", "&7Status: &a&lENABLED", "").make(), 24);
                }
                else {
                    ninv.setItem(NItem.create(Material.PUFFERFISH_BUCKET).setName("&rKill Friendly Mobs").setDescription("&7Allow other players to kill animals", "", "&7Status: &c&lDISABLED", "").make(), 24);
                }
                ninv.setLClick(24, new Runnable() {
                    @Override
                    public void run() {
                        claim.attackFriendly = !claim.attackFriendly;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.allowSetHome) {
                    ninv.setItem(NItem.create(Material.LIME_BED).setName("&rAllow Set Home").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow non-members to set their homes", "", "&7Status: &a&lENABLED", "").make(), 31);
                }
                else {
                    ninv.setItem(NItem.create(Material.RED_BED).setName("&rAllow Set Home").setDescription("&7Allow non-members to set their homes", "", "&7Status: &c&lDISABLED", "").make(), 31);
                }
                ninv.setLClick(31, new Runnable() {
                    @Override
                    public void run() {
                        claim.allowSetHome = !claim.allowSetHome;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.fenceGate) {
                    ninv.setItem(NItem.create(Material.OAK_FENCE_GATE).setName("&rAllow Fences").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow non-members to open fence gates", "", "&7Status: &a&lENABLED", "").make(), 30);
                }
                else {
                    ninv.setItem(NItem.create(Material.OAK_FENCE_GATE).setName("&rAllow Fences").setDescription("&7Allow non-members to open fence gates", "", "&7Status: &c&lDISABLED", "").make(), 30);
                }
                ninv.setLClick(30, new Runnable() {
                    @Override
                    public void run() {
                        claim.fenceGate = !claim.fenceGate;
                        GuiManager.openSettings(claim, p);
                    }
                });
                if (claim.horseRiding) {
                    ninv.setItem(NItem.create(Material.SADDLE).setName("&rHorse Riding").setItemFlag(ItemFlag.HIDE_ENCHANTS).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).setEnchantment(Enchantment.DAMAGE_ALL, 0).setDescription("&7Allow non-members to ride your horses", "", "&7Status: &a&lENABLED", "").make(), 32);
                }
                else {
                    ninv.setItem(NItem.create(Material.SADDLE).setName("&rHorse Riding").setDescription("&7Allow non-members to ride your horses", "", "&7Status: &c&lDISABLED", "").make(), 32);
                }
                ninv.setLClick(32, new Runnable() {
                    @Override
                    public void run() {
                        claim.horseRiding = !claim.horseRiding;
                        GuiManager.openSettings(claim, p);
                    }
                });
            }
        });
        p.openInventory(ninv.getInv());
    }
    
    public static void openSpectatorGui(LandClaim claim, Player p) {
        p.sendMessage(ChatUtil.c("&8----------- &bMarker Information &8-------------"));
        p.sendMessage(ChatUtil.c("              "));
        String owners = "";
        for (final String uuid : claim.members) {
            owners = owners + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";
        }
        p.sendMessage(ChatUtil.c("&7Owned by &6" + Bukkit.getOfflinePlayer(UUID.fromString(claim.uuid)).getName() + "&7, " + owners).trim());
        p.sendMessage(ChatUtil.c("&7Area &8(North, East, South, West): &7" + claim.north + ", " + claim.east + ", " + claim.south + ", " + claim.west));
        p.sendMessage(ChatUtil.c("&7Size: &7" + (claim.north + claim.south + 1) + "x" + (claim.west + claim.east + 1)));
        p.sendMessage(ChatUtil.c(""));
        final List<String> left = new ArrayList<String>();
        final List<String> right = new ArrayList<String>();
        left.add(_set(claim.permitPvP, "Combat") + "      &l  ");
        left.add(_set(claim.mobSpawn, "Mobs") + "           ");
        left.add(_set(claim.pistonsAllowed, "Pistons") + "        ");
        left.add(_set(claim.chestPeek, "Chest Access"));
        left.add(_set(claim.entityInteract, "Sheep Shear") + " ");
        left.add(_set(claim.allowSetHome, "Set Home") + "      ");
        left.add(_set(claim.horseRiding, "Horse") + "       &l  ");
        right.add(_set(claim.blockGeneration, "Cobble Gen"));
        right.add(_set(claim.buttonInteract, "Buttons"));
        right.add(_set(claim.pressureInteract, "Pressure Plates"));
        right.add(_set(claim.doorInteract, "Doors Unlocked"));
        right.add(_set(claim.attackFriendly, "Attack Animals"));
        right.add(_set(claim.fenceGate, "Fence Gate"));
        right.add(_set(true, "Minecart"));
        for (int i = 0; i < left.size(); ++i) {
            p.sendMessage(ChatUtil.c("" + left.get(i) + "&r | " + right.get(i)));
        }
    }
    
    private static String _set(boolean val, String name) {
        return ChatUtil.c("&7" + name + ": " + (val ? "&aYes" : "&cNo ") + "&r");
    }
    
    public static void openPeekGui(Player p, Container c, Block b) {
        final NInventory ninv = new NInventory("Not allowed", c.getInventory().getSize(), Protect.instance);
        ninv.setUpdate(new Runnable() {
            @Override
            public void run() {
                if (b != null && b.getState() instanceof Container) {
                    for (int i = 0; i < c.getInventory().getSize(); ++i) {
                        ninv.setItem(c.getInventory().getItem(i), i);
                    }
                }
            }
        });
        p.openInventory(ninv.getInv());
    }
}
