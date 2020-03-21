// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.commands;

import lucemans.protect.Protect;
import lucemans.protect.obj.LandClaim;
import lucemans.protect.managers.LanguageManager;
import lucemans.protect.managers.LandManager;
import lucemans.protect.item.ItemManager;
import org.bukkit.entity.Player;
import lucemans.protect.util.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class AdminClaimCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtil.c("&6------ Corona lucemans.protect.Protect ------"));
            sender.sendMessage(ChatUtil.c("&7A Protection plugin by &rLucemans"));
            sender.sendMessage(ChatUtil.c("&7See &rhttps://github.com/lucemans/buildprotection/wiki&7 for more info"));
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatUtil.c("&6------ Corona lucemans.protect.Protect ------"));
                sender.sendMessage(ChatUtil.c("&7&l» &6give [name]&7 - Give all items to a player"));
                sender.sendMessage(ChatUtil.c("&7&l» &6info&7 - Show the claim info"));
                sender.sendMessage(ChatUtil.c("&7&l» &6help&7 - Show this help menu"));
            }
            if (args[0].equalsIgnoreCase("give")) {
                ItemManager.giveMarker(p);
                ItemManager.giveFuel(p);
            }
            if (args[0].equalsIgnoreCase("info")) {
                LandClaim c = LandManager.isInClaim(p.getLocation());
                if (c != null) {
                    c.openMenu(p);
                }
                else {
                    p.sendMessage(LanguageManager.prefix + "Please stand in a claim when executing this command.");
                }
            }
            if (args[0].equalsIgnoreCase("toggle")) {
                if (Protect.instance.admins.contains(p.getUniqueId().toString())) {
                    Protect.instance.admins.remove(p.getUniqueId().toString());
                    p.sendMessage(ChatUtil.c(LanguageManager.prefix + "Admin Mode &c&lDisabled"));
                }
                else {
                    Protect.instance.admins.add(p.getUniqueId().toString());
                    p.sendMessage(ChatUtil.c(LanguageManager.prefix + "Admin Mode &a&lEnabled"));
                }
            }
            if (args[0].equalsIgnoreCase("s")) {
                LandClaim c = LandManager.isInClaim(p.getLocation());
                if (c != null) {
                    c.showBorder(p);
                }
                else {
                    p.sendMessage("You are not in a land claim.");
                }
            }
        }
        return true;
    }
}
