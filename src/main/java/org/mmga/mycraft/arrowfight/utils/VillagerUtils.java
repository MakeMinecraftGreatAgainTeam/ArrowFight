package org.mmga.mycraft.arrowfight.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.mmga.mycraft.arrowfight.events.EntityDamage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;
import static org.bukkit.potion.PotionType.*;

public class VillagerUtils {

    private static final ItemStack ARROW_ITEM = new ItemStack(ARROW, 2);
    private static final MerchantRecipe IRON_ARROW = new MerchantRecipe(ARROW_ITEM, 99999);
    private static final List<MerchantRecipe> MERCHANT_RECIPES = new ArrayList<>();

    /*
        村民交易内容
        可选方法:addItem(),addNormalItem()
        参数解析(两个方法常用参数相同):出售物品（箭矢效果），数量，出售物品名称，所需物品，数量，出售物品描述
    */
    static {
        IRON_ARROW.addIngredient(new ItemStack(COBBLESTONE, 1));
        MERCHANT_RECIPES.add(IRON_ARROW);
        addNormalItem(OAK_PLANKS, 4, GREEN + "木板", IRON_INGOT, 2, RED + "没想到吧，木板这么贵。但是你就说你买不买吧");
        addNormalItem(GOLDEN_APPLE, 1 ,AQUA + "金苹果", GOLD_INGOT, 3, RED + "好吧，看来你在这里也能买到金苹果");
        addItem(INVISIBILITY, 3, RED + "TNT箭矢", REDSTONE, 12, RED + "会在射到的地方生成一个TNT");
        addItem(INVISIBILITY, true, false, 1, RED + "TNT雨箭矢", REDSTONE, 20, RED + "会在射到的地方生成一场TNT雨");
        addItem(POISON, 1, GREEN + "苦力怕箭矢", EMERALD, 3, GREEN + "会在射到的地方生成亿些苦力怕");
        addItem(POISON, true, false, 1, GREEN + "僵尸箭矢", EMERALD, 4, GREEN + "会在射到的地方生成亿些僵尸");
        addItem(FIRE_RESISTANCE, true, false, 1, RED + "火焰箭矢", COAL_BLOCK, 1, RED + "会在射到的地方生成火焰");
        addItem(FIRE_RESISTANCE, 1, RED + "岩浆箭矢", LAVA_BUCKET, 1, RED + "会在射到的地方生成岩浆");
        addItem(LUCK, 3, RED + "世界吞噬者", COBBLESTONE, 10, RED + "所到之处，寸草不留");
        addItem(SLOW_FALLING, 1, "箭雨箭矢", IRON_BLOCK, 1, "会在射到的地方生成箭雨");
        addItem(SLOWNESS, 1, "药箭", LAPIS_LAZULI, 4, "会在射到的地方生成滞留型药水（中毒）");
        addItem(SLOW_FALLING, true, false, 1, "⚡闪电⚡箭", GOLD_INGOT, 4, "会在射到的地方生成⚡闪电⚡");
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

    //添加常规物品
    private static void addNormalItem(Material item, int count, String name, Material need, int needCount, String... lores) {
        List<Component> lore = new ArrayList<>();
        for (String l : lores) {
            lore.add(Component.text(l));
        }
        ItemStack normalItem = new ItemStack(item, count);
        ItemMeta normalItemMeta = normalItem.getItemMeta();
        normalItemMeta.displayName(Component.text(name));
        normalItemMeta.lore(lore);
        normalItem.setItemMeta(normalItemMeta);
        MerchantRecipe merchantRecipe = new MerchantRecipe(normalItem, 9999999);
        merchantRecipe.addIngredient(new ItemStack(need, needCount));
        MERCHANT_RECIPES.add(merchantRecipe);

    }

    //添加箭矢初始化
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

    //添加箭矢
    private static void addItem(PotionType type, int count, String name, Material need, int needCount, String... lores) {
        addItem(type, false, false, count, name, need, needCount, lores);
    }
}
