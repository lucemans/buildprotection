
package lucemans.protect.obj;

import org.bukkit.Bukkit;

import java.util.*;

import lucemans.protect.item.ItemManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.block.Container;
import lucemans.protect.managers.LanguageManager;
import org.bukkit.block.Block;
import org.bukkit.Particle;
import org.bukkit.Color;
import lucemans.protect.gui.GuiManager;
import java.sql.Timestamp;
import org.bukkit.entity.Player;
import lucemans.protect.Protect;
import org.bukkit.OfflinePlayer;
import lucemans.protect.managers.LandManager;
import org.bukkit.Location;
import lucemans.ninventory.NInventory;
import org.bukkit.inventory.ItemStack;

public class LandClaim
{
    public String uuid;
    public List<String> members;
    public ArrayList<NInventory> invs;
    public HashMap<String, Long> cooldown;
    public Integer north;
    public Integer south;
    public Integer east;
    public Integer west;
    private Location loc;
    public boolean permitPvP;
    public boolean mobSpawn;
    public boolean pistonsAllowed;
    public boolean chestPeek;
    public boolean entityInteract;
    public boolean blockGeneration;
    public boolean buttonInteract;
    public boolean pressureInteract;
    public boolean doorInteract;
    public boolean attackFriendly;
    public boolean allowSetHome;
    public boolean fenceGate;
    public boolean horseRiding;
    
    public LandClaim(final Location loc, final String uuid) {
        this.uuid = "";
        this.members = new ArrayList<String>();
        this.invs = new ArrayList<NInventory>();
        this.cooldown = new HashMap<String, Long>();
        this.north = LandManager.defaultRange;
        this.south = LandManager.defaultRange;
        this.east = LandManager.defaultRange;
        this.west = LandManager.defaultRange;
        this.permitPvP = false;
        this.mobSpawn = false;
        this.pistonsAllowed = false;
        this.chestPeek = false;
        this.entityInteract = false;
        this.blockGeneration = false;
        this.buttonInteract = false;
        this.pressureInteract = false;
        this.doorInteract = false;
        this.attackFriendly = false;
        this.allowSetHome = false;
        this.fenceGate = false;
        this.horseRiding = false;
        this.loc = loc;
        this.uuid = uuid;
    }
    
    public boolean isInArea(final Location _l) {
        return _l.getWorld().getName().equals(this.loc.getWorld().getName()) && Math.floor(_l.getX()) > this.loc.getX() - this.west - 1.0 && _l.getX() < this.loc.getX() + this.east + 1.0 && Math.floor(_l.getZ()) > this.loc.getZ() - this.north - 1.0 && _l.getZ() < this.loc.getZ() + this.south + 1.0;
    }
    
    public boolean isMarker(final Location _l) {
        return _l.getX() == this.loc.getX() && (_l.getY() == this.loc.getY() || _l.getY() - 1.0 == this.loc.getY()) && _l.getZ() == this.loc.getZ();
    }
    
    public boolean canBuild(final OfflinePlayer p) {
        return p.getUniqueId().toString().equalsIgnoreCase(this.uuid) || this.members.contains(p.getUniqueId().toString()) || Protect.instance.admins.contains(p.getUniqueId().toString());
    }
    
    public boolean isMember(final OfflinePlayer p) {
        return p.getUniqueId().toString().equalsIgnoreCase(this.uuid) || this.members.contains(p.getUniqueId().toString());
    }
    
    public void openMenu(final Player p) {
        if (this.cooldown.containsKey(p.getUniqueId().toString()) && System.nanoTime() - new Timestamp(this.cooldown.get(p.getUniqueId().toString())).getTime() < 1000000000L) {
            return;
        }
        this.cooldown.put(p.getUniqueId().toString(), System.nanoTime());
        if (!this.canBuild((OfflinePlayer)p)) {
            GuiManager.openSpectatorGui(this, p);
            return;
        }
        GuiManager.openGui(this, p);
    }
    
    public void showBorder(final Player p) {
        for (double step = 1.0, y = -4.0; y <= 4.0; y += step) {
            for (double x = -this.west; x <= this.east + 1; x += step) {
                int z = -this.north;
                Location l = this.loc.clone().add(x, y, (double)z);
                l.setY(p.getLocation().getBlockY() + y + 1.75);
                this.showParticleCheck(l, p);
                z = this.south + 1;
                l = this.loc.clone().add(x, y, (double)z);
                l.setY(p.getLocation().getBlockY() + y + 1.75);
                this.showParticleCheck(l, p);
            }
            for (double j = -this.north; j <= this.south + 1; j += step) {
                int x2 = -this.west;
                Location l = this.loc.clone().add((double)x2, y, j);
                l.setY(p.getLocation().getBlockY() + y + 1.75);
                this.showParticleCheck(l, p);
                x2 = this.east + 1;
                l = this.loc.clone().add((double)x2, y, j);
                l.setY(p.getLocation().getBlockY() + y + 1.75);
                this.showParticleCheck(l, p);
            }
        }
    }
    
    private void showParticleCheck(final Location loc, final Player p) {
        if (loc.distance(p.getLocation().clone().add(0.0, 1.75, 0.0)) < 40.0) {
            Particle.DustOptions d;
            if (Math.round(loc.getX()) % 2L == 0L || Math.round(loc.getZ()) % 2L == 0L || Math.round(loc.getY()) % 2L == 0L) {
                d = new Particle.DustOptions(Color.fromRGB(127, 0, 127), 1.0f);
            }
            else {
                d = new Particle.DustOptions(Color.fromRGB(0, 127, 255), 1.0f);
            }
            p.spawnParticle(Particle.REDSTONE, loc, 1, (Object)d);
        }
    }
    
    public boolean onHandleBlockPlace(final Player p, final Block b) {
        if (!this.canBuild((OfflinePlayer)p)) {
            p.sendMessage(LanguageManager.place_block);
            return true;
        }
        return false;
    }
    
    public boolean onHandleBlockBreak(final Player p, final Block b) {
        if (!this.canBuild((OfflinePlayer)p)) {
            p.sendMessage(LanguageManager.break_block);
            return true;
        }
        return false;
    }
    
    public boolean onHandleInteractBlock(final Player p, final Block b) {
        if (this.canBuild((OfflinePlayer)p)) {
            return false;
        }
        if (b.getState() instanceof Container && this.chestPeek) {
            GuiManager.openPeekGui(p, (Container)b.getState(), b);
            return true;
        }
        p.sendMessage(LanguageManager.open_inventory);
        return true;
    }
    
    public boolean onHandleEntityInteract(final Player p, final Entity e) {
        if (!this.canBuild((OfflinePlayer)p) && !this.entityInteract) {
            p.sendMessage(LanguageManager.animalTouch);
            return true;
        }
        return false;
    }
    
    public void triggerDestroy(final Player p) {
        this.loc.getBlock().setType(Material.AIR);
        this.loc.clone().add(0.0, 1.0, 0.0).getBlock().setType(Material.AIR);
        LandManager.claims.remove(this);
        if (p.getGameMode() != GameMode.CREATIVE) {
            Objects.requireNonNull(this.loc.getWorld()).dropItemNaturally(this.loc, ItemManager.getMarker());
            int fuelCount = (this.east + this.west + this.north + this.south - 4 * LandManager.defaultRange);
            ItemStack fuel = ItemManager.getFuel();
            while(fuelCount > 64){
                fuel.setAmount(64);
                this.loc.getWorld().dropItemNaturally(this.loc, fuel);
                fuelCount -= 64;
            }
            fuel.setAmount(fuelCount);
            this.loc.getWorld().dropItemNaturally(this.loc, fuel);
        }
        for (final NInventory ninv : this.invs) {
            ninv.close();
        }
    }
    
    public Location getLocation() {
        return this.loc.clone();
    }
    
    public SLandClaim serialize() {
        final SLandClaim obj = new SLandClaim();
        obj.uu = this.uuid;
        obj.wo = this.loc.getWorld().getName();
        obj.x = this.loc.getBlockX();
        obj.y = this.loc.getBlockY();
        obj.z = this.loc.getBlockZ();
        obj.n = this.north;
        obj.s = this.south;
        obj.e = this.east;
        obj.w = this.west;
        obj.p = this.permitPvP;
        obj.m = this.mobSpawn;
        obj.pi = this.pistonsAllowed;
        obj.cp = this.chestPeek;
        obj.ei = this.entityInteract;
        obj.b = this.blockGeneration;
        obj.bi = this.buttonInteract;
        obj.pin = this.pressureInteract;
        obj.af = this.attackFriendly;
        obj.di = this.doorInteract;
        obj.mem = this.members;
        obj.ash = this.allowSetHome;
        obj.hr = this.horseRiding;
        obj.fg = this.fenceGate;
        return obj;
    }
    
    public static LandClaim deserialize(final SLandClaim l) {
        final Location loc = new Location(Bukkit.getWorld(l.wo), (double)l.x, (double)l.y, (double)l.z);
        final LandClaim lc = new LandClaim(loc, l.uu);
        lc.north = l.n;
        lc.south = l.s;
        lc.west = l.w;
        lc.east = l.e;
        lc.permitPvP = l.p;
        lc.mobSpawn = l.m;
        lc.pistonsAllowed = l.pi;
        lc.chestPeek = l.cp;
        lc.entityInteract = l.ei;
        lc.blockGeneration = l.b;
        lc.buttonInteract = l.bi;
        lc.pressureInteract = l.pin;
        lc.attackFriendly = l.af;
        lc.doorInteract = l.di;
        lc.allowSetHome = l.ash;
        lc.horseRiding = l.hr;
        lc.fenceGate = l.fg;
        lc.members = l.mem;
        return lc;
    }
}
