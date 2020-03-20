// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.commands;

import lucemans.protect.obj.LandClaim;
import lucemans.protect.managers.LanguageManager;
import lucemans.protect.gui.GuiManager;
import lucemans.protect.managers.LandManager;
import lucemans.protect.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class ClaimCommand implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (args.length == 0) {
                p.sendMessage(ChatUtil.c("&6------ Corona Protect ------"));
                p.sendMessage(ChatUtil.c("&7A Protection plugin by &rLucemans"));
                sender.sendMessage(ChatUtil.c("&7See &rhttps://github.com/lucemans/buildprotection/wiki&7 for more info"));
                p.sendMessage(ChatUtil.c("&7or do &6/claim help&7 for a list of commands."));
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatUtil.c("&6------ Corona Protect ------"));
                sender.sendMessage(ChatUtil.c("&7&l» &6open/menu/gui&7 - Opens your marker remotely"));
                sender.sendMessage(ChatUtil.c("&7&l» &6info&7 - Shows information on the plot you are standing in"));
                sender.sendMessage(ChatUtil.c("&7&l» &6help&7 - Show this help menu"));
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                LandClaim c = LandManager.isInClaim(p.getLocation());
                if (c != null) {
                    GuiManager.openSpectatorGui(c, p);
                }
                else {
                    p.sendMessage(LanguageManager.prefix + "Please stand in a claim when executing this command.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("gui") || args[0].equalsIgnoreCase("menu")) {
                LandClaim c = LandManager.isInClaim(p.getLocation());
                if (c != null) {
                    c.openMenu(p);
                }
                else {
                    p.sendMessage(LanguageManager.prefix + "Please stand in a claim when executing this command.");
                }
            }
        }
        return true;
    }
}
