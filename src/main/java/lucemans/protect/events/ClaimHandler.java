// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.events;

import org.spigotmc.event.entity.EntityDismountEvent;
import org.bukkit.entity.Vehicle;
import org.spigotmc.event.entity.EntityMountEvent;
import org.bukkit.entity.Horse;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import java.util.UUID;
import lucemans.protect.util.ChatUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
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
import org.bukkit.event.player.PlayerInteractEvent;
import lucemans.protect.managers.LandManager;
import lucemans.protect.obj.LandClaim;
import lucemans.protect.managers.LanguageManager;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class ClaimHandler implements Listener
{
    public static void createAttempt(final BlockPlaceEvent event) {
        event.getItemInHand().setAmount(event.getItemInHand().getAmount() - 1);
        event.getBlockPlaced().setType(Material.BEACON);
        event.getBlockPlaced().getLocation().add(0.0, 1.0, 0.0).getBlock().setType(Material.SEA_LANTERN);
        event.getPlayer().sendMessage(LanguageManager.onCreate);
        final LandClaim lc = new LandClaim(event.getBlockPlaced().getLocation(), event.getPlayer().getUniqueId().toString());
        LandManager.claims.add(lc);
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.SEA_LANTERN || event.getClickedBlock().getType() == Material.BEACON) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null) {
                    if (r.isMarker(event.getClickedBlock().getLocation())) {
                        event.setCancelled(true);
                        r.openMenu(event.getPlayer());
                    }
                    else if (r.onHandleInteractBlock(event.getPlayer(), event.getClickedBlock())) {
                        event.setCancelled(true);
                    }
                }
            }
            else if (event.getClickedBlock().getState() instanceof Container) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && r.onHandleInteractBlock(event.getPlayer(), event.getClickedBlock())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void blockPlace(final BlockPlaceEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && r.onHandleBlockPlace(event.getPlayer(), event.getBlockPlaced())) {
            event.setCancelled(true);
            return;
        }
        if (ItemManager.isMarker(event.getItemInHand())) {
            if (event.getPlayer().hasPermission("claim.user")) {
                final LandClaim r2 = LandManager.canPlaceMarker(event.getBlockPlaced().getLocation());
                if (r2 != null) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.cantOverlap);
                }
                else {
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
            }
            else {
                event.getPlayer().sendMessage(LanguageManager.noPermission);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock(final BlockBreakEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && r.onHandleBlockBreak(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlock2(final BlockPistonExtendEvent event) {
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
    public void onBlock2(final BlockPistonRetractEvent event) {
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
    public void onBlock4(final BlockExplodeEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlock5(final EntityExplodeEvent event) {
        for (final Block b : event.blockList()) {
            final LandClaim r = LandManager.isInClaim(b.getLocation());
            if (r != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock6(final PlayerShearEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null && r.onHandleEntityInteract(event.getPlayer(), event.getEntity())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlock7(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof TNTPrimed) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock8(final PlayerLeashEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlock9(final BlockFormEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getBlock().getLocation());
        if (r != null && !r.blockGeneration) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlock10(final ExplosionPrimeEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        if (event.getTo() != null) {
            LandClaim r = LandManager.isInClaim(event.getTo());
            if (r != null) {
                if (!r.isInArea(event.getFrom())) {
                    final LandClaim r2 = LandManager.isInClaim(event.getFrom());
                    if (r2 != null && r2.uuid.equalsIgnoreCase(r.uuid)) {
                        return;
                    }
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(LanguageManager.action_enter + " " + (r.isMember((OfflinePlayer)event.getPlayer()) ? ChatUtil.c(" &a&l[Member]") : ChatUtil.c(" &7[" + Bukkit.getOfflinePlayer(UUID.fromString(r.uuid)).getName() + "]"))));
                }
                return;
            }
            r = LandManager.isInClaim(event.getFrom());
            if (r != null && event.getTo() != null && !r.isInArea(event.getTo())) {
                final LandClaim r2 = LandManager.isInClaim(event.getTo());
                if (r2 != null && r2.uuid.equalsIgnoreCase(r.uuid)) {
                    return;
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(LanguageManager.action_leave));
            }
        }
    }
    
    @EventHandler
    public void onBlock11(final EntityDamageByEntityEvent event) {
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
    }
    
    @EventHandler
    public void onBlock12(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Animals && event.getDamager() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getDamager()) && !r.attackFriendly) {
                event.getDamager().sendMessage(LanguageManager.passiveAttack);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock13(final EntitySpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.mobSpawn) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock14(final PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            final LandClaim r = LandManager.isInClaim(event.getPlayer().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer()) && !r.pressureInteract) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock15(final EntityInteractEvent event) {
        if (event.getBlock().getType().toString().toUpperCase().contains("PRESSURE_PLATE")) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null && !r.pressureInteract) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock16(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("BUTTON")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer()) && r != null && !r.buttonInteract) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.buttonInteract);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("DOOR")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.doorInteract && !r.canBuild((OfflinePlayer)event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.doorInteract);
                }
            }
            if (event.getClickedBlock().getType().toString().toUpperCase().contains("FENCE_GATE")) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.fenceGate && !r.canBuild((OfflinePlayer)event.getPlayer())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(LanguageManager.fenceGate);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlock16(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Hanging) {
            final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
            if (r != null) {
                if (event.getDamager() instanceof Player) {
                    if (!r.canBuild((OfflinePlayer)event.getDamager())) {
                        ((Player)event.getDamager()).sendMessage(LanguageManager.itemframe);
                        event.setCancelled(true);
                    }
                }
                else if (event.getDamager() instanceof Projectile) {
                    final Projectile a = (Projectile)event.getDamager();
                    if (a.getShooter() instanceof Player) {
                        if (!r.canBuild((OfflinePlayer)a.getShooter())) {
                            ((Player)a.getShooter()).sendMessage(LanguageManager.itemframe);
                            event.setCancelled(true);
                        }
                        else {
                            event.setCancelled(false);
                        }
                    }
                }
                else {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlock17(final PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Hanging) {
            final LandClaim r = LandManager.isInClaim(event.getRightClicked().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer())) {
                event.getPlayer().sendMessage(LanguageManager.itemframe_touch);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock18(final PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.ITEM_FRAME && event.getClickedBlock() != null) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer())) {
                    event.getPlayer().sendMessage(LanguageManager.itemframe_place);
                    event.setCancelled(true);
                }
            }
            if (event.getItem().getType().toString().toUpperCase().contains("BUCKET") && event.getClickedBlock() != null) {
                final LandClaim r = LandManager.isInClaim(event.getClickedBlock().getLocation());
                if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer())) {
                    event.getPlayer().sendMessage(LanguageManager.liquid_place);
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlock19(final HangingBreakByEntityEvent event) {
        final LandClaim r = LandManager.isInClaim(event.getEntity().getLocation());
        if (r != null) {
            if (event.getRemover() instanceof Player) {
                if (!r.canBuild((OfflinePlayer)event.getRemover())) {
                    ((Player)event.getRemover()).sendMessage(LanguageManager.itemframe);
                    event.setCancelled(true);
                }
            }
            else if (event.getRemover() instanceof Projectile) {
                final Projectile a = (Projectile)event.getRemover();
                if (a.getShooter() instanceof Player) {
                    if (!r.canBuild((OfflinePlayer)a.getShooter())) {
                        ((Player)a.getShooter()).sendMessage(LanguageManager.itemframe);
                        event.setCancelled(true);
                    }
                    else {
                        event.setCancelled(false);
                    }
                }
            }
            else {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlock20(final PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].contains("sethome")) {
            final LandClaim r = LandManager.isInClaim(event.getPlayer().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer()) && !r.allowSetHome) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(LanguageManager.sethome);
            }
        }
    }
    
    @EventHandler
    public void onBlock21(final PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Horse) {
            final LandClaim r = LandManager.isInClaim(event.getRightClicked().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getPlayer()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(LanguageManager.horse);
            }
        }
    }
    
    @EventHandler
    public void onBlock22(final EntityMountEvent event) {
        if (event.getMount() instanceof Animals && event.getMount() instanceof Vehicle && event.getEntity() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getMount().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getEntity()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getEntity().sendMessage(LanguageManager.horse);
            }
        }
    }
    
    @EventHandler
    public void onBlock23(final EntityDismountEvent event) {
        if (event.getDismounted() instanceof Animals && event.getDismounted() instanceof Vehicle && event.getEntity() instanceof Player) {
            final LandClaim r = LandManager.isInClaim(event.getDismounted().getLocation());
            if (r != null && !r.canBuild((OfflinePlayer)event.getEntity()) && !r.horseRiding) {
                event.setCancelled(true);
                event.getEntity().sendMessage(LanguageManager.horseDismount);
            }
        }
    }
}
