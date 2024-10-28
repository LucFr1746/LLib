package io.github.lucfr1746.LLib.Item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.lucfr1746.LLib.Item.Category.Category;
import io.github.lucfr1746.LLib.Item.Category.CategoryAPI;
import io.github.lucfr1746.LLib.Item.Tier.Tier;
import io.github.lucfr1746.LLib.Item.Tier.TierAPI;
import io.github.lucfr1746.LLib.Text.TextAPI;
import io.github.lucfr1746.LLib.Utils.UtilsAPI;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * API for creating and manipulating ItemStacks in a simplified way.
 * <p>
 * This class provides methods to modify various aspects of an ItemStack,
 * including its type, amount, display name, lore, and specific NBT data.
 * It also supports custom features like setting textures on player heads.
 *
 * <p>
 * Hypixel Skyblock Items Format
 *
 * <p>
 * Usage example:
 * <pre>
 * ItemBuilderAPI itemAPI = new ItemCreationAPI(Material.DIAMOND_SWORD)
 *     .setDisplayName("&6Epic Sword")
 *     .setAmount(1);
 * ItemStack item = itemAPI.build();
 * </pre>
 *
 * @author LucFr1746
 * @version 1.0
 */
public class ItemBuilderAPI {

    private final @NotNull ItemStack itemStack;
    private String displayName;
    private String internalName;

    private Tier tier;
    private List<Tier> nearestTiers;

    private Category category;
    private Category defaultCategory;
    private List<Category> nearestCategories;

    private String skullTexture;
    private boolean isDungeonItem;
    private boolean isLoreNumbered;
    private boolean isGlowing;
    private boolean isUnique;
    private boolean isRecombobulated;
    private Long timestamp;

    public ItemBuilderAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        recognizeItem();
    }

    public ItemBuilderAPI(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
        recognizeItem();
    }

    public ItemBuilderAPI() {
        this.itemStack = new ItemStack(Material.AIR);
        recognizeItem();
    }

    public ItemBuilderAPI from(@NotNull Material material) {
        return new ItemBuilderAPI(material);
    }

    public ItemBuilderAPI recognizeItem() {
        this.displayName = getDisplayName();
        this.internalName = getInternalName();
        this.skullTexture = getSkullTexture();

        TierAPI tierAPI = new TierAPI(this.itemStack);
        this.tier = tierAPI.getTier();
        this.nearestTiers = tierAPI.getNearTiersCircle();

        CategoryAPI categoryAPI = new CategoryAPI(this.itemStack);
        this.category = categoryAPI.getCategory();
        this.defaultCategory = categoryAPI.getDefaultCategory();
        this.nearestCategories = categoryAPI.getNearCategoriesCircle();

        ItemUtilsAPI itemUtilsAPI = new ItemUtilsAPI(this.itemStack);
        this.isDungeonItem = itemUtilsAPI.isDungeonItem();
        this.isLoreNumbered = itemUtilsAPI.isLoreNumbered();
        this.isGlowing = itemUtilsAPI.isGlowing();
        this.isUnique = itemUtilsAPI.isUnique();
        this.isRecombobulated = itemUtilsAPI.isRecombobulated();
        this.timestamp = itemUtilsAPI.getTimestamp();
        return this;
    }

    public @NotNull ItemStack build() {
        if (category == Category.UNCLASSIFIED) return this.itemStack;

        getItemMeta().setDisplayName(this.displayName);

//        net.minecraft.world.item.ItemStack test = CraftItemStack.asNMSCopy(this.itemStack);
//        @NotNull ItemEnchantments enchantments = Objects.requireNonNull(test.get(DataComponents.ENCHANTMENTS));
//
//        for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {
//            Holder<Enchantment> enchantmentHolder = entry.getKey();
//            Enchantment enchantment = enchantmentHolder.value();
//            enchantment.definition(); // -> return all the definitions of the Enchantment
//        }

        List<String> finalLores = new ArrayList<>();
        if (getType().name().startsWith("LEATHER")) finalLores.add("&7Color: " + getLeatherArmorColor());

    // Category description
        if (!finalLores.isEmpty()) finalLores.add("");
        String categoryDes = category.getDescription();
        if (!categoryDes.isBlank()) finalLores.add(categoryDes);
    // Rarity lore
        String rarityLore = tier.getNameHolder() + (isDungeonItem ? " DUNGEON" + (category == Category.NONE ? " ITEM" : "") : "");
        rarityLore += category.getNameHolder().isBlank() ? "" : " " + category.getNameHolder();
        if (isRecombobulated) {
            ChatColor colorCode = tier.getUpgrade().getColor();
            String obfuscatedChar = colorCode + "" + ChatColor.BOLD + ChatColor.MAGIC + "a";
            rarityLore = obfuscatedChar + " " + colorCode + ChatColor.BOLD + rarityLore + " " + obfuscatedChar;
        } else {
            rarityLore = tier.getColor() + "&l" + rarityLore;
        }
        finalLores.add(rarityLore);

        return setLores(finalLores).setAllFlags().itemStack;
    }

    public ItemBuilderAPI setType(@NotNull Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public Material getType() {
        return this.itemStack.getType();
    }

    public String getTypeName() {
        return this.itemStack.getType().name();
    }

    public ItemBuilderAPI setItemMeta(@NotNull ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public @NotNull ItemMeta getItemMeta() {
        return Objects.requireNonNull(this.itemStack.getItemMeta());
    }

    public ItemBuilderAPI setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return this.itemStack.getAmount();
    }

    public ItemBuilderAPI setDamaged(int value) {
        if (isInvalidItem() || value < 0) return this;

        ItemMeta meta = getItemMeta();
        if (meta instanceof Damageable damage) {
            damage.setDamage(value);
            setItemMeta(meta);
        }
        return this;
    }

    public int getDamaged() {
        if (isInvalidItem()) return 0;

        ItemMeta meta = getItemMeta();
        if (meta instanceof Damageable damage) {
            return damage.getDamage();
        }
        return 0;
    }

    public ItemBuilderAPI setMaxDurability(int value) {
        if (isInvalidItem() || value < 0) return this;

        ItemMeta meta = getItemMeta();
        if (meta instanceof Damageable damage) {
            damage.setMaxDamage(value);
            setItemMeta(meta);
        }
        return this;
    }

    public int getMaxDurability() {
        if (isInvalidItem()) return 0;

        ItemMeta meta = getItemMeta();
        if (meta.isUnbreakable()) return -1;
        if (meta instanceof Damageable damage) {
            return damage.hasMaxDamage() ? damage.getMaxDamage() : getType().getMaxDurability();
        }
        return 0;
    }

    public ItemBuilderAPI setUnbreakable(boolean value) {
        if (isInvalidItem()) return this;

        ItemMeta meta = getItemMeta();
        meta.setUnbreakable(value);
        setItemMeta(meta);
        return this;
    }

    public boolean isUnbreakable() {
        if (isInvalidItem()) return false;
        return getItemMeta().isUnbreakable();
    }

    public ItemBuilderAPI setPotionType(@NotNull PotionType potType) {
        PotionMeta potionMeta = (PotionMeta) getItemMeta();
        potionMeta.setBasePotionType(potType);
        setItemMeta(potionMeta);
        return this;
    }

    public PotionType getPotionType() {
        return ((PotionMeta) getItemMeta()).getBasePotionType();
    }

    public ItemBuilderAPI setLeatherArmorColor(String hexColor) {
        if (!(getItemMeta() instanceof LeatherArmorMeta meta)) {
            throw new IllegalArgumentException("The item must be a leather armor piece");
        }

        Color color = new UtilsAPI().hexToColor(hexColor);
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public String getLeatherArmorColor() {
        if (!(getItemMeta() instanceof LeatherArmorMeta meta)) {
            throw new IllegalArgumentException("The item must be a leather armor piece");
        }
        Color color = meta.getColor();
        return new UtilsAPI().colorToHex(color);
    }

    public ItemBuilderAPI setSkullTexture(String texture) {
        if (getType() != Material.PLAYER_HEAD) return this;

        NBT.modifyComponents(this.itemStack, nbt -> {
            ReadWriteNBT profileNbt = nbt.getOrCreateCompound("minecraft:profile");
            profileNbt.setUUID("id", UUID.randomUUID());
            ReadWriteNBTCompoundList propertiesList = profileNbt.getCompoundList("properties");

            // Find if a 'textures' compound already exists
            ReadWriteNBT textureCompound = null;
            for (ReadWriteNBT compound : propertiesList) {
                if ("textures".equals(compound.getString("name"))) {
                    textureCompound = compound;
                    break;
                }
            }

            // If the 'textures' compound is found, update the texture, otherwise add a new one
            if (textureCompound != null) {
                textureCompound.setString("value", texture); // Update existing texture
            } else {
                // Add a new 'textures' compound if not found
                ReadWriteNBT newTextureCompound = propertiesList.addCompound();
                newTextureCompound.setString("name", "textures");
                newTextureCompound.setString("value", texture);
                this.skullTexture = texture;
            }
        });
        return this;
    }

    public String getSkullTexture() {
        if (getType() != Material.PLAYER_HEAD) return "none";

        return NBT.modifyComponents(this.itemStack, nbt -> {
            ReadWriteNBT profileNbt = nbt.getCompound("minecraft:profile");
            if (profileNbt == null) return "none";

            // Get the 'properties' list (no casting)
            ReadWriteNBTCompoundList propertiesList = profileNbt.getCompoundList("properties");
            if (propertiesList == null || propertiesList.isEmpty()) return "none"; // Return "none" if properties list is empty

            // Get the first compound in the properties list
            ReadWriteNBT propertiesNbt = propertiesList.get(0); // No need to cast to List<ReadWriteNBT>

            // Return the texture value or "none" if it's not found
            return propertiesNbt.getOrDefault("value", "none");
        });
    }

    public ItemBuilderAPI removeSkullTexture() {
        if (getType() != Material.PLAYER_HEAD) return this;

        NBT.modifyComponents(this.itemStack, nbt -> {
            if (nbt.hasTag("minecraft:profile"))
                nbt.removeKey("minecraft:profile");
        });
        return this;
    }

    public ItemBuilderAPI setFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilderAPI removeFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.removeItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilderAPI setAllFlags() {
        ItemMeta meta = getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        setItemMeta(meta);
        return this;
    }

    public ItemBuilderAPI removeAllFlags() {
        ItemMeta meta = getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            meta.removeItemFlags(flag);
        }
        setItemMeta(meta);
        return this;
    }

    public ItemBuilderAPI setDisplayName(String displayName) {
        return setDisplayName(displayName, false);
    }

    public ItemBuilderAPI setDisplayName(String name, boolean force) {
        if (isInvalidItem()) return this;

        if (force) {
            this.displayName = new TextAPI(name).colorRecognise().build();
        } else {
            this.displayName = new TextAPI(name).colorRecognise().stripColor().build();
            this.displayName = isRecombobulated ? new TextAPI(this.displayName).setColor(tier.getUpgrade().getColor()).build() : new TextAPI(this.displayName).setColor(tier.getColor()).build();
        }

        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("display name", this.displayName);
        });
        return this;
    }

    public String getDisplayName() {
        if (isInvalidItem()) return "";
        return NBT.modify(this.itemStack, nbt -> {
            return nbt.getOrDefault("display name", getMaterialName());
        });
    }

    public String getStripDisplayName() {
        return new TextAPI(getDisplayName()).stripColor().build();
    }

    public String getMaterialName() {
        if (isInvalidItem()) return "";
        return ChatColor.translateAlternateColorCodes('&', "&f"
                + Arrays.stream(getType().name().toLowerCase().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" ")));
    }

    public ItemBuilderAPI setInternalName(String internalName) {
        if (isInvalidItem()) return this;
        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("internalName", internalName);
            this.internalName = internalName;
        });
        return this;
    }

    public String getInternalName() {
        if (isInvalidItem()) return "";
        return NBT.modify(this.itemStack, nbt -> {
            return nbt.getOrDefault("internalName", "NONE");
        });
    }

    public ItemBuilderAPI setLore(int index, String lore) {
        List<String> lores = getLores();
        if (index > lores.size()) {
            lores.addAll(Collections.nCopies(index - lores.size(), "")); // Fill with empty strings
        }

        lores.set(index - 1, lore);
        return setLores(lores);
    }

    public ItemBuilderAPI setLores(List<String> lores) {
        if (isInvalidItem()) return this;

        // Stream API to apply transformations
        List<String> formattedLores = lores.stream()
                .map(lore -> new TextAPI(lore).colorRecognise().build())
                .collect(Collectors.toList());

        ItemMeta meta = getItemMeta();
        meta.setLore(formattedLores);
        setItemMeta(meta);
        return this;
    }

    public List<String> getLores() {
        ItemMeta meta = getItemMeta();

        if (!meta.hasLore()) return Collections.emptyList();

        return new ArrayList<>(Optional.ofNullable(meta.getLore()).orElse(new ArrayList<>()));
    }

    public ItemBuilderAPI resetLore() {
        return setLores(new ArrayList<>());
    }

    public ItemBuilderAPI removeLore(int index) {
        List<String> lores = getLores();

        // Ensure index is within valid range
        if (isInvalidIndex(index, lores.size())) return this;

        lores.remove(index - 1); // Adjust for 1-based index
        return setLores(lores); // Method chaining for simplicity
    }

    public ItemBuilderAPI moveLore(int oldIndex, int newIndex) {
        if (oldIndex == newIndex) return this; // No need to move if indices are the same

        List<String> lores = getLores();

        // Ensure both oldIndex and newIndex are valid
        if (isInvalidIndex(oldIndex, lores.size()) || isInvalidIndex(newIndex, lores.size())) return this;

        String lore = lores.remove(oldIndex - 1); // Remove from oldIndex (adjust for 1-based index)
        lores.add(newIndex - 1, lore); // Add at newIndex

        return setLores(lores); // Method chaining
    }

    public List<String> getDescriptionLores(String description, int maxLength) {
        if (isInvalidItem()) return new ArrayList<>();
        return Arrays.asList(ChatPaginator.wordWrap(new TextAPI(description).colorRecognise().build(), maxLength));
    }

    public ItemBuilderAPI setDescription(String description) {
        if (isInvalidItem()) return this;
        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("description", description);
        });
        return this;
    }

    public ItemBuilderAPI setTier(Tier tier) {
        if (isInvalidItem()) return this;
        try {
            TierAPI tierAPI = new TierAPI(this.itemStack);
            tierAPI.setTier(tier);
            this.tier = tier;
            this.nearestTiers = tierAPI.getNearTiersCircle();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to set tier of item!");
        }
        return this;
    }

    public Tier getTier() {
        if (isInvalidItem()) return null;
        return this.tier;
    }

    public List<Tier> getNearestTiers() {
        if (isInvalidItem()) return new ArrayList<>();
        return this.nearestTiers;
    }

    public ItemBuilderAPI setCategory(Category category) {
        if (isInvalidItem()) return this;
        try {
            CategoryAPI categoryAPI = new CategoryAPI(this.itemStack);
            categoryAPI.setCategory(category);
            this.category = category;
            this.defaultCategory = categoryAPI.getDefaultCategory();
            this.nearestCategories = categoryAPI.getNearCategoriesCircle();
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while setting category of the item!");
        }
        return this;
    }

    public Category getCategory() {
        if (isInvalidItem()) return null;
        return this.category;
    }

    public Category getDefaultCategory() {
        if (isInvalidItem()) return null;
        return this.defaultCategory;
    }

    public List<Category> getNearestCategories() {
        if (isInvalidItem()) return new ArrayList<>();
        return this.nearestCategories;
    }

    public ItemBuilderAPI setDungeonItem(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setDungeonItem(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while setting dungeon item!");
        }
        return this;
    }

    public boolean isDungeonItem() {
        if (isInvalidItem()) return false;
        return this.isDungeonItem;
    }

    public ItemBuilderAPI setLoreNumbered(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setLoreNumbered(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while setting lore numbered of item!");
        }
        return this;
    }

    public boolean isLoreNumbered() {
        if (isInvalidItem()) return false;
        return this.isLoreNumbered;
    }

    public ItemBuilderAPI setGlowing(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setGlowing(value);
            getItemMeta().setEnchantmentGlintOverride(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while sett item glowing!");
        }
        return this;
    }

    public boolean isGlowing() {
        if (isInvalidItem()) return false;
        return this.isGlowing;
    }

    public ItemBuilderAPI setUnique(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setUnique(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while setting unique of item!");
        }
        return this;
    }

    public boolean isUnique() {
        if (isInvalidItem()) return false;
        return this.isUnique;
    }

    public ItemBuilderAPI setRecombobulated(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setRecombobulated(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while recombobulating item!");
        }
        return this;
    }

    public boolean isRecombobulated() {
        if (isInvalidItem()) return false;
        return this.isRecombobulated;
    }

    public ItemBuilderAPI setTimestamp(boolean value) {
        if (isInvalidItem()) return this;
        try {
            new ItemUtilsAPI(this.itemStack).setTimestamp(value);
        } catch (Exception e) {
            Bukkit.getLogger().warning("There ware something wrong while setting timestamp of item!");
        }
        return this;
    }

    public long getTimestamp() {
        if (isInvalidItem()) return -1;
        return this.timestamp;
    }

    private boolean isInvalidItem() {
        return getType() == Material.AIR;
    }

    private boolean isInvalidIndex(int index, int size) {
        return index < 1 || index > size;
    }
}
