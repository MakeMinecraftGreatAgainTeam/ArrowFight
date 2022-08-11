package org.mmga.mycraft.arrowfight.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.mmga.mycraft.arrowfight.events.EntityDamage;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.Material.*;
import static org.bukkit.potion.PotionType.*;

/**
 * Created On 2022/8/9 13:50
 *
 * @author wzp
 * @version 1.0.0
 */
public class VillagerUtils {

    private static final ItemStack ARROW_ITEM = new ItemStack(ARROW, 2);
    private static final MerchantRecipe IRON_ARROW = new MerchantRecipe(ARROW_ITEM, 99999);
    private static final List<MerchantRecipe> MERCHANT_RECIPES = new ArrayList<>();

    static {
        IRON_ARROW.addIngredient(new ItemStack(IRON_INGOT, 1));
        MERCHANT_RECIPES.add(IRON_ARROW);
        addItem(INVISIBILITY, 3, RED + "TNT箭矢", REDSTONE, 12, RED + "会在射到的地方生成一个TNT");
        addItem(INVISIBILITY, true, false, 1, RED + "TNT雨箭矢", REDSTONE, 16, RED + "会在射到的地方生成一场TNT雨");
        addItem(POISON, 1, GREEN + "苦力怕箭矢", EMERALD, 3, GREEN + "会在射到的地方生成亿些苦力怕");
        addItem(POISON, true, false, 1, GREEN + "僵尸箭矢", EMERALD, 4, GREEN + "会在射到的地方生成亿些僵尸");
        addItem(FIRE_RESISTANCE, true, false, 1, RED + "火焰箭矢", COAL_BLOCK, 1, RED + "会在射到的地方生成火焰");
        addItem(FIRE_RESISTANCE, 1, RED + "岩浆箭矢", LAVA_BUCKET, 1, RED + "会在射到的地方生成岩浆");
        addItem(LUCK, 3, RED + "世界吞噬者", COBBLESTONE, 10, RED + "所到之处，寸草不留");
        addItem(SLOW_FALLING, 1, "箭雨箭矢", IRON_BLOCK, 1, "会在射到的地方生成箭雨");
    }

    public static void initVillager(Villager villager) {
        villager.setProfession(Villager.Profession.FLETCHER);
        villager.setVillagerType(Villager.Type.PLAINS);
        villager.setVillagerLevel(5);
        villager.setVillagerExperience(999999);
        villager.setAI(true);
        villager.getScoreboardTags().add(EntityDamage.GAME_TAG);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999999, 1));
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999999, 255));
        villager.setRecipes(MERCHANT_RECIPES);
    }

    private static void addItem(PotionType type, boolean extended, boolean upgraded, int count, String name, Material need, int needCount, String... lores) {
        List<Component> lore = new ArrayList<>();
        for (String l : lores) {
            lore.add(Component.text(l));
        }
        ItemStack itemStack = new ItemStack(TIPPED_ARROW, count);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
        itemMeta.displayName(Component.text(name));
        itemMeta.lore(lore);
        itemMeta.setBasePotionData(new PotionData(type, extended, upgraded));
        itemStack.setItemMeta(itemMeta);
        MerchantRecipe merchantRecipe = new MerchantRecipe(itemStack, 99999);
        merchantRecipe.addIngredient(new ItemStack(need, needCount));
        MERCHANT_RECIPES.add(merchantRecipe);
    }

    private static void addItem(PotionType type, int count, String name, Material need, int needCount, String... lores) {
        addItem(type, false, false, count, name, need, needCount, lores);
    }
}
