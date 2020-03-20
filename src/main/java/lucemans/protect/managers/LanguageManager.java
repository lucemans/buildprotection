// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.managers;

import org.bukkit.ChatColor;

public class LanguageManager {

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&r[&eProtect&r] ");
    public static String noPermission = LanguageManager.prefix + "You don't have sufficient permissions to execute that command.";
    public static String break_block = LanguageManager.prefix + "You do not have permission to break this block.";
    public static String place_block = LanguageManager.prefix + "You do not have permission to place blocks here.";
    public static String open_inventory = LanguageManager.prefix + "You do not have permission to open this block.";
    public static String passiveAttack = LanguageManager.prefix + "This entity is protected.";
    public static String pvp = LanguageManager.prefix + "You are in a protected region. This would be unfair!";
    public static String pvp2 = LanguageManager.prefix + "This person is in a protected region.";
    public static String onCreate = LanguageManager.prefix + "You have created a marker.";
    public static String markerLimit = LanguageManager.prefix + "You have reached the maximum.";
    public static String cantCreate = LanguageManager.prefix + "There is not enough space for a marker.";
    public static String cantOverlap = LanguageManager.prefix + "Sorry you cannot place a marker that overlaps with another marker.";
    public static String buttonInteract = LanguageManager.prefix + "Buttons have not been enabled.";
    public static String doorInteract = LanguageManager.prefix + "All doors have been locked by the claim.";
    public static String animalTouch = LanguageManager.prefix + "This animal would rather not be touched.";
    public static String horse = LanguageManager.animalTouch;
    public static String fenceGate = LanguageManager.prefix + "All fences have been locked by the claim.";
    public static String horseDismount = LanguageManager.prefix + "Sorry if you dismount your horse in this area you will be unable to re-mount it";
    public static String itemframe = LanguageManager.prefix + "Itemframes shouldn't be destroyed by non-members.";
    public static String itemframe_place = LanguageManager.prefix + "No, you shouldn't be placing itemframes here.";
    public static String itemframe_touch = LanguageManager.prefix + "No, you shouldn't be touching itemframes here.";
    public static String liquid_place = LanguageManager.prefix + "Euhm, please dont do that.";
    public static String sethome = LanguageManager.prefix + "Euhm, this claim doesn't like it when you do that.";
    public static String action_enter = "Entered a protected region";
    public static String action_leave = "You have left protection";

}
