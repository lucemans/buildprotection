// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.managers;

import lucemans.protect.obj.SLandClaim;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.Location;
import lucemans.protect.obj.LandClaim;
import java.util.ArrayList;

public class LandManager
{
    public static Integer defaultRange = 2;
    public static ArrayList<LandClaim> claims = new ArrayList<LandClaim>();
    
    public static LandClaim isInClaim(final Location loc) {
        for (LandClaim lc : LandManager.claims) {
            if (lc.isInArea(loc)) {
                return lc;
            }
        }
        return null;
    }
    
    public static LandClaim canPlaceMarker(Location loc) {
        for (LandClaim lc : LandManager.claims) {
            for (int i = -LandManager.defaultRange; i <= LandManager.defaultRange; ++i) {
                for (int j = -LandManager.defaultRange; j <= LandManager.defaultRange; ++j) {
                    if (lc.isInArea(loc.clone().add((double)i, 0.0, (double)j))) {
                        return lc;
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean canBuildMarker(Player p) {
        return claimsByPerson(p).size() < 8 || p.hasPermission("claim.admin");
    }
    
    public static ArrayList<LandClaim> claimsByPerson(Player p) {
        ArrayList<LandClaim> c = new ArrayList<LandClaim>();
        for (LandClaim lc : LandManager.claims) {
            if (lc.uuid.equalsIgnoreCase(p.getUniqueId().toString())) {
                c.add(lc);
            }
        }
        return c;
    }
    
    public static ArrayList<SLandClaim> serialize() {
        final ArrayList<SLandClaim> l = new ArrayList<SLandClaim>();
        for (LandClaim lc : LandManager.claims) {
            l.add(lc.serialize());
        }
        return l;
    }
}
