package io.github.lucfr1746.LLib.Item;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.lucfr1746.LLib.Item.Category.Category;
import io.github.lucfr1746.LLib.Item.Tier.Tier;
import io.github.lucfr1746.LLib.Text.TextAPI;
import io.github.lucfr1746.LLib.Utils.UtilsAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
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
    private boolean isRecombobulated;
    private boolean isLoreNumbered;
    private boolean isGlowing;
    private boolean isDungeonItem;
    private String isUnique;
    private String internalName;
    private String displayName;
    private Long timestamp;
    private Tier tier;
    private String tierString;
    private ChatColor tierColor;
    private Category category;
    private String categoryDescription;
    private String categoryNameHolder;

    public ItemBuilderAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilderAPI(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilderAPI() {
        this.itemStack = new ItemStack(Material.AIR);
    }

    public ItemBuilderAPI from(@NotNull Material material) {
        return new ItemBuilderAPI(material);
    }

    public @NotNull ItemStack build() {
        return this.itemStack;
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

    public ItemBuilderAPI setDisplayName(String name) {
        if (isInvalidItem()) return this;

        this.displayName = new TextAPI(name).colorRecognise().stripColor().build();

        NBT.modify(this.itemStack, nbt -> {
            if (isRecombobulated)
                nbt.setString("display name", new TextAPI(this.displayName).setColor(tier.getUpgrade().getColor()).build());
            else
                nbt.setString("display name", new TextAPI(this.displayName).setColor(tier.getColor()).build());
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

    private boolean isInvalidItem() {
        return getType() == Material.AIR;
    }

    private boolean isInvalidIndex(int index, int size) {
        return index < 1 || index > size;
    }
}
