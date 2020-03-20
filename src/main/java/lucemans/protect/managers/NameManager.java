// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.managers;

import org.bukkit.scoreboard.Team;
import org.bukkit.OfflinePlayer;
import lucemans.protect.util.ChatUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class NameManager
{
    public static Scoreboard score;
    
    public static void setSuffix(final Player p) {
        NameManager.score = p.getScoreboard();
        Team t = NameManager.score.getTeam(p.getName());
        if (t != null) {
            t.unregister();
        }
        t = NameManager.score.registerNewTeam(p.getName());
        final String str = PlaceholderAPI.setPlaceholders(p, "%essentials_nickname%");
        t.setSuffix(" (" + str + ")");
        t.setPrefix(ChatUtil.c(""));
        t.setDisplayName("");
        t.addEntry(p.getName());
        t.addPlayer((OfflinePlayer)p);
    }
}
