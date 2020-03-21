
package lucemans.protect.obj;

import lucemans.protect.Protect;
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
import org.bukkit.OfflinePlayer;
import lucemans.protect.managers.LandManager;
import org.bukkit.Location;
import lucemans.protect.ninventory.NInventory;
import org.bukkit.inventory.ItemStack;

public class LandClaim
{
    public String uuid = "";
    public List<String> members = new ArrayList<String>();
    public List<String> officers = new ArrayList<String>();
    public ArrayList<NInventory> invs = new ArrayList<NInventory>();
    public HashMap<String, Long> cooldown = new HashMap<String, Long>();
    public Integer north = LandManager.defaultRange;
    public Integer south = LandManager.defaultRange;
    public Integer east = LandManager.defaultRange;
    public Integer west = LandManager.defaultRange;
    private Location loc;
    public boolean permitPvP = false;
    public boolean mobSpawn = false;
    public boolean pistonsAllowed = false;
    public boolean chestPeek = false;
    public boolean entityInteract = false;
    public boolean blockGeneration = false;
    public boolean buttonInteract = false;
    public boolean pressureInteract = false;
    public boolean doorInteract = false;
    public boolean attackFriendly = false;
    public boolean allowSetHome = false;
    public boolean fenceGate = false;
    public boolean horseRiding = false;
    
    public LandClaim(Location loc, String uuid) {
        this.loc = loc;
        this.uuid = uuid;
    }
    
    public boolean isInArea(Location _l) {
        return _l.getWorld().getName().equals(this.loc.getWorld().getName()) && Math.floor(_l.getX()) > this.loc.getX() - this.west - 1.0 && _l.getX() < this.loc.getX() + this.east + 1.0 && Math.floor(_l.getZ()) > this.loc.getZ() - this.north - 1.0 && _l.getZ() < this.loc.getZ() + this.south + 1.0;
    }
    
    public boolean isMarker(Location _l) {
        return _l.getX() == this.loc.getX() && (_l.getY() == this.loc.getY() || _l.getY() - 1.0 == this.loc.getY()) && _l.getZ() == this.loc.getZ();
    }

    // Permission management
    public boolean canBuild(OfflinePlayer p) {
        return isPartyMember(p) || isAdmin(p);
    }

    public boolean isPartyMember(OfflinePlayer p) {
        return isOfficer(p) || isMember(p);
    }

    public boolean isMember(OfflinePlayer p) {
        return this.members.contains(p.getUniqueId().toString());
    }

    public boolean isOfficer(OfflinePlayer p) {
        return this.officers.contains(p.getUniqueId().toString()) || p.getUniqueId().toString().equalsIgnoreCase(this.uuid);
    }

    public boolean isAdmin(OfflinePlayer p) {
        return Protect.instance.admins.contains(p.getUniqueId().toString());
    }

    // Gui Handling
    public void openMenu(Player p) {
        if (this.cooldown.containsKey(p.getUniqueId().toString()) && System.nanoTime() - new Timestamp(this.cooldown.get(p.getUniqueId().toString())).getTime() < 1000000000L) {
            return;
        }
        this.cooldown.put(p.getUniqueId().toString(), System.nanoTime());
        if (!this.isOfficer(p) && !isAdmin(p)) {
            GuiManager.openSpectatorGui(this, p);
            return;
        }
        GuiManager.openOfficerGui(this, p);
    }
    
    public void showBorder(Player p) {
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
    
    private void showParticleCheck(Location loc, Player p) {
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
    
    public boolean onHandleBlockPlace(Player p, Block b) {
        if (!this.canBuild((OfflinePlayer)p)) {
            p.sendMessage(LanguageManager.place_block);
            return true;
        }
        return false;
    }
    
    public boolean onHandleBlockBreak(Player p, Block b) {
        if (!this.canBuild((OfflinePlayer)p)) {
            p.sendMessage(LanguageManager.break_block);
            return true;
        }
        return false;
    }
    
    public boolean onHandleInteractBlock(Player p, Block b) {
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
    
    public boolean onHandleEntityInteract(Player p, Entity e) {
        if (!this.canBuild((OfflinePlayer)p) && !this.entityInteract) {
            p.sendMessage(LanguageManager.animalTouch);
            return true;
        }
        return false;
    }
    
    public void triggerDestroy(Player p) {
        this.loc.getBlock().setType(Material.AIR);
        this.loc.clone().add(0.0, 1.0, 0.0).getBlock().setType(Material.AIR);
        LandManager.claims.remove(this);
        if (p.getGameMode() != GameMode.CREATIVE) {
            Objects.requireNonNull(this.loc.getWorld()).dropItemNaturally(this.loc, ItemManager.getMarker());
            int fuelCount = (this.east + this.west + this.north + this.south - 4 * LandManager.defaultRange);
            while (fuelCount > 0) {
                ItemStack fuel = ItemManager.getFuel();
                fuel.setAmount(Math.min(fuelCount, 64));
                fuelCount -= fuel.getAmount();
                this.loc.getWorld().dropItemNaturally(this.loc, fuel);
            }
        }
        for (NInventory ninv : this.invs) {
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
        obj.off = this.officers;
        return obj;
    }
    
    public static LandClaim deserialize(SLandClaim l) {
        Location loc = new Location(Bukkit.getWorld(l.wo), (double)l.x, (double)l.y, (double)l.z);
        LandClaim lc = new LandClaim(loc, l.uu);
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
        lc.officers = l.off;
        return lc;
    }
}
