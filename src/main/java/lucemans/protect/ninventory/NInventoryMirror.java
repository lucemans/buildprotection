package lucemans.protect.ninventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class NInventoryMirror implements InventoryHolder {

    Integer id = 0;

    NInventoryMirror(Integer id) {
        this.id = id;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
