package io.github.lucfr1746.LLib.Item.Tier;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TierAPI {

    private final ItemStack itemStack;

    public ItemStack getItemStack() {
        return itemStack;
    }

    public TierAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setTier(@NotNull Tier tier) {
        if (this.itemStack.getType() == Material.AIR) return;
        NBT.modify(itemStack, nbt -> {
            nbt.getOrCreateCompound("ExtraAttributes").setString("tier", tier.toString());
        });
    }

    public Tier getTier() {
        return NBT.modify(itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("tier")) {
                nbtList.setString("tier", Tier.COMMON.name());
            }
            try {
                return Tier.valueOf(nbtList.getString("tier"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }

    public List<Tier> getNearTiersCircle() {
        Tier currentTier = getTier();

        List<Tier> result = new ArrayList<>();
        Tier[] tiers = Tier.values();
        int numCategories = tiers.length;
        int currentIndex = currentTier.ordinal();

        // Find the front tier, skipping VERRY_SPECIAL
        int frontIndex = (currentIndex - 1 + numCategories) % numCategories;
        while (tiers[frontIndex] == Tier.VERY_SPECIAL) {
            frontIndex = (frontIndex - 1 + numCategories) % numCategories;
        }
        Tier frontTier = tiers[frontIndex];

        // Find the back tier, skipping VERRY_SPECIAL
        int backIndex = (currentIndex + 1) % numCategories;
        while (tiers[backIndex] == Tier.VERY_SPECIAL) {
            backIndex = (backIndex + 1) % numCategories;
        }
        Tier backTier = tiers[backIndex];

        result.add(frontTier);
        result.add(currentTier);
        result.add(backTier);

        return result;
    }
}
