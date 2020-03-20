// 
// Decompiled by Procyon v0.5.36
// 

package lucemans.protect.managers;

import org.bukkit.ChatColor;

public class LanguageManager
{
    public static String prefix;
    public static String noPermission;
    public static String break_block;
    public static String place_block;
    public static String open_inventory;
    public static String passiveAttack;
    public static String pvp;
    public static String pvp2;
    public static String onCreate;
    public static String markerLimit;
    public static String cantCreate;
    public static String cantOverlap;
    public static String buttonInteract;
    public static String doorInteract;
    public static String animalTouch;
    public static String horse;
    public static String fenceGate;
    public static String horseDismount;
    public static String itemframe;
    public static String itemframe_place;
    public static String itemframe_touch;
    public static String liquid_place;
    public static String sethome;
    public static String action_enter;
    public static String action_leave;
    
    static {
        LanguageManager.prefix = ChatColor.translateAlternateColorCodes('&', "&r[&eProtect&r] ");
        LanguageManager.noPermission = LanguageManager.prefix + "You don't have sufficient permissions to execute that command.";
        LanguageManager.break_block = LanguageManager.prefix + "You do not have permission to break this block.";
        LanguageManager.place_block = LanguageManager.prefix + "You do not have permission to place blocks here.";
        LanguageManager.open_inventory = LanguageManager.prefix + "You do not have permission to open this block.";
        LanguageManager.passiveAttack = LanguageManager.prefix + "This entity is protected.";
        LanguageManager.pvp = LanguageManager.prefix + "You are in a protected region. This would be unfair!";
        LanguageManager.pvp2 = LanguageManager.prefix + "This person is in a protected region.";
        LanguageManager.onCreate = LanguageManager.prefix + "You have created a marker.";
        LanguageManager.markerLimit = LanguageManager.prefix + "You have reached the maximum.";
        LanguageManager.cantCreate = LanguageManager.prefix + "There is not enough space for a marker.";
        LanguageManager.cantOverlap = LanguageManager.prefix + "Sorry you cannot place a marker that overlaps with another marker.";
        LanguageManager.buttonInteract = LanguageManager.prefix + "Buttons have not been enabled.";
        LanguageManager.doorInteract = LanguageManager.prefix + "All doors have been locked by the claim.";
        LanguageManager.animalTouch = LanguageManager.prefix + "This animal would rather not be touched.";
        LanguageManager.horse = LanguageManager.animalTouch;
        LanguageManager.fenceGate = LanguageManager.prefix + "All fences have been locked by the claim.";
        LanguageManager.horseDismount = LanguageManager.prefix + "Sorry if you dismount your horse in this area you will be unable to re-mount it";
        LanguageManager.itemframe = LanguageManager.prefix + "Itemframes shouldn't be destroyed by non-members.";
        LanguageManager.itemframe_place = LanguageManager.prefix + "No, you shouldn't be placing itemframes here.";
        LanguageManager.itemframe_touch = LanguageManager.prefix + "No, you shouldn't be touching itemframes here.";
        LanguageManager.liquid_place = LanguageManager.prefix + "Euhm, please dont do that.";
        LanguageManager.sethome = LanguageManager.prefix + "Euhm, this claim doesn't like it when you do that.";
        LanguageManager.action_enter = "Entered a protected region";
        LanguageManager.action_leave = "You have left protection";
    }
}
