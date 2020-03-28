// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.events;

import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import lucemans.protect.Protect;
import lucemans.protect.ninventory.NInventory;
import org.bukkit.World;
import org.bukkit.block.EnderChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

import lucemans.protect.util.ChatUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.Iterator;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockBreakEvent;
import lucemans.protect.item.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Container;
import lucemans.protect.managers.LandManager;
import lucemans.protect.obj.LandClaim;
import lucemans.protect.managers.LanguageManager;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class ClaimHandler implements Listener {

    public static void createAttempt(BlockPlaceEvent event) {
        event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
        event.getBlockPlaced().setType(Material.BEACON);
        event.getBlockPlaced().getLocation().add(0.0, 1.0, 0.0).getBlock().setType(Material.SEA_LANTERN);
        event.getPlayer().sendMessage(LanguageManager.onCreate);
        LandClaim lc = new LandClaim(event.getBlockPlaced().getLocation(), event.getPlayer().getUniqueId().toString());
        LandManager.claims.add(lc);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.SEA_LANTERN || event.getClickedBlock().getType() == Material.BEACON) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null) {
                    if (r.isMarker(event.getClickedBlock().getLocation())) {
                        event.setCancelled(true);
                        r.openMenu(event.getPlayer());
                    } else if (r.onHandleInteractBlock(event.getPlayer(), event.getClickedBlock())) {
                        event.setCancelled(true);
                    }
                }
            } else if (event.getClickedBlock().getState() instanceof Container) {
                LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && r.onHandleInteractBlock(event.getPlayer(), event.getClickedBlock())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && r.onHandleBlockPlace(event.getPlayer(), event.getBlockPlaced())) {
            event.setCancelled(true);
            return;
        }
        if (ItemManager.isMarker(event.getItemInHand())) {
            if (event.getPlayer().hasPermission("claim.user")) {
                LandClaim r2 = LandManager.canPlaceMarker(event.getBlockPlaced().getLocation());
                if (r2 != null) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.cantOverlap);
                } else {
                    if (event.getBlockPlaced().getLocation().add(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR) {
                        event.getPlayer().sendMessage(LanguageManager.cantCreate);
                        event.setCancelled(true);
                        return;
                    }
                    if (!LandManager.canBuildMarker(event.getPlayer())) {
                        event.getPlayer().sendMessage(LanguageManager.markerLimit);
                        return;
                    }
                    createAttempt(event);
                }
            } else {
                event.getPlayer().sendMessage(LanguageManager.noPermission);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerBlockBreak(BlockBreakEvent event) {
        LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && r.onHandleBlockBreak(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pushInClaim(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            LandClaim r = LandManager.isInClaim(b.getLocation());
            if (r != null && !r.pistonsAllowed) {
                event.setCancelled(true);
                return;
            }
            r = LandManager.isInClaim(b.getLocation().add(event.getDirection().getDirection()));
            if (r != null && !r.pistonsAllowed) {
                event.setCancelled(true);
                return;
            }
        }
        final LandClaim r2 = LandManager.isInClaim(event.getBlock().getLocation());
        if (r2 != null && !r2.pistonsAllowed) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void pullFromClaim(BlockPistonRetractEvent event) {
        for (final Block b : event.getBlocks()) {
            final LandClaim r = LandManager.isInClaim(b.getLocation());
            if (r != null && !r.pistonsAllowed) {
                event.setCancelled(true);
                return;
            }
        }
        final LandClaim r2 = LandManager.isInClaim(event.getBlock().getLocation());
        if (r2 != null && !r2.pistonsAllowed) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ExplodeBlock(BlockExplodeEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ExplosionsEnabled(EntityExplodeEvent event) {
        for (final Block b : event.blockList()) {
            final LandClaim r = LandManager.isInClaim(b.getLocation());
            if (r != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void shearSheep(PlayerShearEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null && r.onHandleEntityInteract(event.getPlayer(), event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void tntDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void leashAnimal(PlayerLeashEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cobbleGen(BlockFormEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && !r.blockGeneration) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelPrime(ExplosionPrimeEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() != null) {
            LandClaim r = LandManager.isInClaim(event.getTo());
            if (r != null) {
                if (!r.isInArea(event.getFrom())) {
                    final LandClaim r2 = LandManager.isInClaim(event.getFrom());
                    if (r2 != null && r2.uuid.equalsIgnoreCase(r.uuid)) {
                        return;
                    }
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(LanguageManager.action_enter + " " + (r.isMember((OfflinePlayer) event.getPlayer()) ? ChatUtil.c(" &a&l[Member]") : ChatUtil.c(" &7[" + Bukkit.getOfflinePlayer(UUID.fromString(r.uuid)).getName() + "]"))));
                }
                return;
            }
            r = LandManager.isInClaim(event.getFrom());
            if (r != null && event.getTo() != null && !r.isInArea(event.getTo())) {
                LandClaim r2 = LandManager.isInClaim(event.getTo());
                if (r2 != null && r2.uuid.equalsIgnoreCase(r.uuid)) {
                    return;
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent) new TextComponent(LanguageManager.action_leave));
            }
        }
    }

    @EventHandler
    public void pvp(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            int b = 0;
            LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                b = 1;
            }
            if (b == 0) {
                r = LandManager.isInClaim(event.getDamager().getLocation());
                if (r != null) {
                    b = 2;
                }
            }
            if (b > 0) {
                event.getDamager().sendMessage((b == 1) ? LanguageManager.pvp2 : LanguageManager.pvp);
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Projectile p = (Projectile) event.getDamager();
            if (p.getShooter() instanceof Player) {
                Player p2 = (Player) p.getShooter();
                int b = 0;
                LandClaim r = LandManager.isInClaim(p.getLocation());
                if (r != null) {
                    b = 1;
                }
                if (b == 0) {
                    r = LandManager.isInClaim(p2.getLocation());
                    if (r != null) {
                        b = 2;
                    }
                }
                if (b > 0) {
                    p2.sendMessage((b == 1) ? LanguageManager.pvp2 : LanguageManager.pvp);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void passiveDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals && event.getDamager() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getDamager()) && !r.attackFriendly) {
                event.getDamager().sendMessage(LanguageManager.passiveAttack);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void hostileSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.mobSpawn) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void pPressurePlateInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            final LandClaim r = LandManager.isInClaim(event.getPlayer().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer()) && !r.pressureInteract) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityPressurePlateInteract(EntityInteractEvent event) {
        if (event.getBlock().getType().toString().toUpperCase().contains("PRESSURE_PLATE")) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.pressureInteract) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void shootButton(EntityInteractEvent event) {
        if (event.getBlock().getType().toString().toUpperCase().contains("BUTTON")) {
            LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (event.getEntity() instanceof Projectile && r != null) {
                if (((Projectile) event.getEntity()).getShooter() instanceof Player) {
                    if (!r.canBuild((OfflinePlayer) Objects.requireNonNull(((Projectile) event.getEntity()).getShooter())) && !r.buttonInteract) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void pressButton(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("BUTTON")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer()) && !r.buttonInteract) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.buttonInteract);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("DOOR")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.doorInteract && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.doorInteract);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("FENCE_GATE")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.fenceGate && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.fenceGate);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("REDSTONE") || event.getClickedBlock().getType().toString().toUpperCase().contains("REPEATER") || event.getClickedBlock().getType().toString().toUpperCase().contains("COMPARATOR")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.redstone);
                }
            }
            if (event.getClickedBlock().getType() == Material.FLOWER_POT) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.flowerpot2);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("POTTED")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.flowerpot);
                }
            }
        }
    }

    @EventHandler
    public void itemFrameProjectile(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Hanging) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                if (event.getDamager() instanceof Player) {
                    if (!r.canBuild((OfflinePlayer) event.getDamager())) {
                        ((Player) event.getDamager()).sendMessage(LanguageManager.itemframe);
                        event.setCancelled(true);
                    }
                } else if (event.getDamager() instanceof Projectile) {
                    final Projectile a = (Projectile) event.getDamager();
                    if (a.getShooter() instanceof Player) {
                        if (!r.canBuild((OfflinePlayer) a.getShooter())) {
                            ((Player) a.getShooter()).sendMessage(LanguageManager.itemframe);
                            event.setCancelled(true);
                        } else {
                            event.setCancelled(false);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void rCItemFrame(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Hanging) {
            final LandClaim r = LandManager.isInClaim(event.getRightClicked().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                event.getPlayer().sendMessage(LanguageManager.itemframe_touch);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void placeItemFrame(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.ITEM_FRAME && event.getClickedBlock() != null) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.getPlayer().sendMessage(LanguageManager.itemframe_place);
                    event.setCancelled(true);
                }
            }
            if (event.getItem().getType().toString().toUpperCase().contains("BUCKET") && event.getClickedBlock() != null) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer())) {
                    event.getPlayer().sendMessage(LanguageManager.liquid_place);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void itemFramePlayerBreak(HangingBreakByEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null) {
            if (event.getRemover() instanceof Player) {
                if (!r.canBuild((OfflinePlayer) event.getRemover())) {
                    ((Player) event.getRemover()).sendMessage(LanguageManager.itemframe);
                    event.setCancelled(true);
                }
            } else if (event.getRemover() instanceof Projectile) {
                final Projectile a = (Projectile) event.getRemover();
                if (a.getShooter() instanceof Player) {
                    if (!r.canBuild((OfflinePlayer) a.getShooter())) {
                        ((Player) a.getShooter()).sendMessage(LanguageManager.itemframe);
                        event.setCancelled(true);
                    } else {
                        event.setCancelled(false);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void homeInClaim(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].contains("sethome")) {
            final LandClaim r = LandManager.isInClaim(event.getPlayer().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer()) && !r.allowSetHome) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(LanguageManager.sethome);
            }
        }
    }

    @EventHandler
    public void rightClickHorse(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Horse) {
            final LandClaim r = LandManager.isInClaim(event.getRightClicked().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getPlayer()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(LanguageManager.horse);
            }
        }
    }

    @EventHandler
    public void mountHorse(EntityMountEvent event) {
        if (event.getMount() instanceof Animals && event.getMount() instanceof Vehicle && event.getEntity() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getMount().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getEntity()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getEntity().sendMessage(LanguageManager.horse);
            }
        }
    }

    @EventHandler
    public void dismountHorse(EntityDismountEvent event) {
        if (event.getDismounted() instanceof Animals && event.getDismounted() instanceof Vehicle && event.getEntity() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getDismounted().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getEntity()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getEntity().sendMessage(LanguageManager.horseDismount);
            }
        }
    }

    @EventHandler
    public void rightClickVehicle(EntityMountEvent event) {
        if (event.getEntity() instanceof Player && event.getMount() instanceof Vehicle) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer) event.getEntity()) && !r.entityInteract) {
                event.setCancelled(true);
                event.getEntity().sendMessage(LanguageManager.vehicle);
            }
        }
    }

    @EventHandler
    public void breakVehicle(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Vehicle) {
            int b = 0;
            LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                b = 1;
            }
            if (b == 0) {
                r = LandManager.isInClaim(event.getDamager().getLocation());
                if (r != null) {
                    b = 2;
                }
            }
            if (b > 0) {
                if (!r.canBuild((Player) event.getDamager())) {
                    event.getDamager().sendMessage((b == 1) ? LanguageManager.vehicleBreak : LanguageManager.vehicleBreak);
                    event.setCancelled(true);
                }
            }
        }

        if (event.getEntity() instanceof Vehicle && event.getDamager() instanceof Projectile) {
            Projectile p = (Projectile) event.getDamager();
            if (p.getShooter() instanceof Player) {
                Player p2 = (Player) p.getShooter();
                int b = 0;
                LandClaim r = LandManager.isInClaim(p.getLocation());
                if (r != null) {
                    b = 1;
                }
                if (b == 0) {
                    r = LandManager.isInClaim(p2.getLocation());
                    if (r != null) {
                        b = 2;
                    }
                }
                if (b > 0) {
                    if (!r.canBuild((Player) p.getShooter())) {
                        p2.sendMessage((b == 1) ? LanguageManager.vehicleBreak : LanguageManager.vehicleBreak);
                        event.setCancelled(true);
                    }
                }
            } else if (p.getShooter() instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if (!event.isShiftClick())
            return;
        if (!event.isRightClick())
            return;
        if (event.getClickedInventory().equals(event.getWhoClicked().getEnderChest())) {
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getType().toString().toUpperCase().contains("SHULKER_BOX")) {
                    Bukkit.getLogger().info("fs");
                    Bukkit.getLogger().info(event.getCurrentItem().getItemMeta().getClass().toString());
                    ItemStack s = event.getCurrentItem();
                    BlockStateMeta meta = (BlockStateMeta) s.getItemMeta();
                    if (meta.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox b = (ShulkerBox) meta.getBlockState();
                        NInventory ninv = new NInventory("Shulker", b.getInventory().getSize(), Protect.instance);
                        for (int i = 0; i < ninv.getInv().getSize(); i++) {
                            if (b.getInventory().getItem(i) != null) {
                                ninv.setItem(b.getInventory().getItem(i), i);
                            }
                        }
                        ninv.unlockAll();
                        ninv.onClosed = new Runnable() {
                            @Override
                            public void run() {
                                b.getInventory().clear();
                                b.getInventory().setContents(ninv.getInv().getContents());
                                meta.setBlockState(b);
                                s.setItemMeta(meta);
                            }
                        };
                        event.getWhoClicked().openInventory(ninv.getInv());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onThaThing(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Fish)) {
            if (event.getCaught() != null) {
                LandClaim r = LandManager.isInClaim(event.getCaught().getLocation());
                if (r != null) {
                    if (!r.canBuild(event.getPlayer())) {
                        event.getPlayer().sendMessage(LanguageManager.fishing);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
