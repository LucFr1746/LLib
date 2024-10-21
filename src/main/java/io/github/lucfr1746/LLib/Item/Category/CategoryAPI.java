package io.github.lucfr1746.LLib.Item.Category;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryAPI {

    private final ItemStack itemStack;

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public CategoryAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setCategory(@NotNull Category category) {
        if (this.itemStack.getType() == Material.AIR) return;
        NBT.modify(this.itemStack, nbt -> {
            nbt.getOrCreateCompound("ExtraAttributes").setString("category", category.name());
        });
    }

    public Category getCategory() {
        if (this.itemStack.getType() == Material.AIR) return Category.UNCLASSIFIED;
        return NBT.modify(itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("category")) {
                nbtList.setString("category", getDefaultCategory().name());
            }
            try {
                return Category.valueOf(nbtList.getString("category"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }

    private Category getDefaultCategory() {
        String[] types = this.itemStack.getType().toString().split("_");
        String typeSuffix = types[types.length - 1];

        for (Category category : Category.values()) {
            if (category.name().equals(typeSuffix)) {
                return category;
            }
        }

        return Category.NONE;
    }

    public List<Category> getNearCircleCategory() {
        Category currentCategory = getCategory();

        List<Category> result = new ArrayList<>();

        if (currentCategory == Category.UNCLASSIFIED) {
            return Arrays.asList(Category.UNCLASSIFIED, Category.UNCLASSIFIED, Category.UNCLASSIFIED);
        }

        int numCategories = Category.values().length;
        int currentIndex = currentCategory.ordinal();

        // Find the category before the current category
        Category frontCategory = null;
        for (int i = 1; i < numCategories; i++) {
            int prevIndex = (currentIndex - i + numCategories) % numCategories;
            Category previousCategory = Category.values()[prevIndex];
            if (previousCategory != Category.UNCLASSIFIED) {
                switch (previousCategory) {
                    case CHESTPLATE:
                        if (this.itemStack.getType().name().endsWith("CHESTPLATE")) {
                            frontCategory = previousCategory;
                        }
                        break;
                    case LEGGINGS:
                        if (this.itemStack.getType().name().endsWith("LEGGINGS")) {
                            frontCategory = previousCategory;
                        }
                        break;
                    case BOOTS:
                        if (this.itemStack.getType().name().endsWith("BOOTS")) {
                            frontCategory = previousCategory;
                        }
                        break;
                    default:
                        frontCategory = previousCategory;
                        break;
                }
                if (frontCategory != null) {
                    break;
                }
            }
        }

        // Find the category after the current category
        Category backCategory = null;
        for (int i = 1; i < numCategories; i++) {
            int nextIndex = (currentIndex + i) % numCategories;
            Category nextCategory = Category.values()[nextIndex];
            if (nextCategory != Category.UNCLASSIFIED) {
                switch (nextCategory) {
                    case CHESTPLATE:
                        if (this.itemStack.getType().name().endsWith("CHESTPLATE")) {
                            backCategory = nextCategory;
                        }
                        break;
                    case LEGGINGS:
                        if (this.itemStack.getType().name().endsWith("LEGGINGS")) {
                            backCategory = nextCategory;
                        }
                        break;
                    case BOOTS:
                        if (this.itemStack.getType().name().endsWith("BOOTS")) {
                            backCategory = nextCategory;
                        }
                        break;
                    default:
                        backCategory = nextCategory;
                        break;
                }

                if (backCategory != null) {
                    break;
                }
            }
        }

        result.add(frontCategory);
        result.add(currentCategory);
        result.add(backCategory);

        return result;
    }
}
