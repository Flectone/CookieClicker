package net.flectone.cookieclicker;

import de.tr7zw.nbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ItemManager {
    private static HashMap<String, ItemStack> itemStackHashMap = new HashMap<>();
    public static void loadItems() {
        ItemStack enchRuby = new ItemStack(Material.RED_DYE);
        ItemMeta enchrubymeta = enchRuby.getItemMeta();
        enchrubymeta.setDisplayName(ChatColor.DARK_RED + "Прекрасный рубин");
        enchrubymeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enchRuby.setItemMeta(enchrubymeta);
        enchRuby.addUnsafeEnchantment(Enchantment.MENDING, 1);
        itemStackHashMap.put("ENCHANTED_RUBY", enchRuby);

        ItemStack enchCookie = new ItemStack(Material.COOKIE);
        ItemMeta enchCookieMeta = enchCookie.getItemMeta();
        enchCookieMeta.setDisplayName(ChatColor.YELLOW + "Зачарованное печенье");
        enchCookieMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enchCookie.setItemMeta(enchCookieMeta);
        enchCookie.addUnsafeEnchantment(Enchantment.MENDING, 1);
        itemStackHashMap.put("ENCHANTED_COOKIE", enchCookie);

        ItemStack compressedEnchCookie = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta enchCookie64Meta = compressedEnchCookie.getItemMeta();
        enchCookie64Meta.setDisplayName(ChatColor.DARK_PURPLE + "Зачарованное печенье x64");
        compressedEnchCookie.setItemMeta(enchCookie64Meta);
        NBTItem cookieHead = new NBTItem(compressedEnchCookie);
        NBTCompound tag = cookieHead.addCompound("SkullOwner");
        tag.setString("Name", "Solarum");
        int[] id = new int[]{1633761909, -163033562, -1621463632, 626493331};
        tag.setIntArray("Id", id);
        NBTListCompound texture = tag.addCompound("Properties").getCompoundList("textures").addCompound();
        texture.setString("Signature", "gD7OckQKNJGz+D7gR/GcceM7xq8oBTZ40wpCWjbHdJra0Q9vz7lR/spea0iBUvCMzUjMCbnSQXhwRoGq808hCvRfCA0eTIGUMR8XMjoA+Ya8s5XtieC6dEw+CWrbtJJxkVtfvmHNuwEiGCTSmSxqFGe1UDKTwXtP8qgB84SnOIjfqvsuYnlBjFrWLg6lZ5b0Feh3qOr6OArrGqrD+a2S6n0Md2HfXcPCSQqsqg9FZ66sXu3J+y/4h7ZdSCxzJoT19cR6O4xxUWKrx5snpo0WR/BvXNnXAGassx9+z6FjSvk0mHv55Q0HmS0vVQCDz7+xlXVYxjNqpwgnNlSSzjo2xRqi7zgftcr7uvhxxVA9KQeLgjhg0fao+wf8Ec4C+XCV6d+VFg6QlIv/5FJH4yUeddbqlyWj8olrDs0zj72mAjbyq0Axq9ct/n8Li+xG8OhCJE6Qw4Us7Uxtwlp78WSBj8+kpsv0/jxhzux0hnBVEEgZfalCMjJxMt2u8DBRukCInBhCN69vO1NVhwKP88FmrS2NnPP0wkaDPfb2c7Fp8Ze8EA6eQ1fAPR7N400yQqeXyxzyyDH6STiS7HDVR98JCJhpQoeTfT5cv+r4tfjRAAaiWMm1dTwX0ruNj4p/qyOmJJyC7w3nsBjVklsEc2qUJvVUal+0IC9DpPlbt0LqmGg=");
        texture.setString("Value", "ewogICJ0aW1lc3RhbXAiIDogMTY5MTAxMDYwMjcyMSwKICAicHJvZmlsZUlkIiA6ICI2MTYxM2E3NWY2NDg0ZTI2OWY1YTZkYjAyNTU3ODc5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTb2xhcnVtIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk5N2E0NTA3OTc0NTQ0NzZhNDQzMDRmNjQzNWU3MTk3MGVmMThkNjdhZmViYzA0Yzk1YTlhMGZjODJiYTc1ODEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==");
        compressedEnchCookie = cookieHead.getItem();
        itemStackHashMap.put("64ENCH_COOKIE", compressedEnchCookie);

        ItemStack melonBlock = new ItemStack(Material.MELON);
        ItemMeta melonMeta = melonBlock.getItemMeta();
        melonMeta.setDisplayName(ChatColor.GREEN + "Арбуз");
        melonBlock.setItemMeta(melonMeta);
        itemStackHashMap.put("MELON_BLOCK", melonBlock);

        ItemStack enchWheat = new ItemStack(Material.WHEAT);
        ItemMeta enchWheatMeta = enchWheat.getItemMeta();
        enchWheatMeta.setDisplayName(ChatColor.YELLOW + "Зачарованная пшеница");
        enchWheatMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enchWheat.setItemMeta(enchWheatMeta);
        enchWheat.addUnsafeEnchantment(Enchantment.MENDING, 1);
        itemStackHashMap.put("ENCHANTED_WHEAT", enchWheat);

        ItemStack hayBlock = new ItemStack(Material.HAY_BLOCK);
        ItemMeta hayBlockMeta = hayBlock.getItemMeta();
        hayBlockMeta.setDisplayName(ChatColor.YELLOW + "Сноп сена");
        ArrayList<String> list = new ArrayList<>();
        list.add(ChatColor.GRAY + " Нужен для крафта торта");
        hayBlockMeta.setLore(list);
        hayBlock.setItemMeta(hayBlockMeta);
        itemStackHashMap.put("HAY_BLOCK", hayBlock);

        ItemStack enchBeans = new ItemStack(Material.COCOA_BEANS);
        ItemMeta enchBeansMeta = enchBeans.getItemMeta();
        enchBeansMeta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#964b00") + "Зачарованные какао-бобы");
        enchBeansMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enchBeans.setItemMeta(enchBeansMeta);
        enchBeans.addUnsafeEnchantment(Enchantment.MENDING, 1);
        itemStackHashMap.put("ENCH_COCOA_BEANS", enchBeans);

        ItemStack rubyPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta diamondPick = rubyPickaxe.getItemMeta();
        diamondPick.setDisplayName(ChatColor.RED + "Рубиновая кирка");
        diamondPick.setUnbreakable(true);
        diamondPick.addItemFlags(ItemFlag.HIDE_DESTROYS);
        rubyPickaxe.setItemMeta(diamondPick);
        rubyPickaxe = ItemUtils.forgeCanDestroyItem(rubyPickaxe,"minecraft:red_stained_glass","minecraft:amethyst_block");
        itemStackHashMap.put("RUBY_PICKAXE", rubyPickaxe);

        ItemStack cake = new ItemStack(Material.CAKE);
        ItemMeta cakeMeta = cake.getItemMeta();
        cakeMeta.setDisplayName(ChatColor.GOLD + "Торт");
        ArrayList<String> cakeList = new ArrayList<>();
        cakeList.add(ChatColor.GRAY + " Ты крутой");
        cakeList.add(ChatColor.GOLD + "" + ChatColor.ITALIC + " Добавляет первую звезду к броне фермера");
        cakeMeta.setLore(cakeList);
        cake.setItemMeta(cakeMeta);
        itemStackHashMap.put("CAKE", cake);

        ItemStack ruby = new ItemStack(Material.RED_DYE);
        ItemMeta rubyMeta = ruby.getItemMeta();
        rubyMeta.setDisplayName(ChatColor.DARK_RED + "Грубый рубин");
        ruby.setItemMeta(rubyMeta);
        itemStackHashMap.put("RUBY", ruby);

        ItemStack cookie = new ItemStack(Material.COOKIE);
        ItemMeta cookieMeta = cookie.getItemMeta();
        cookieMeta.setDisplayName(ChatColor.YELLOW + "Печенье");
        cookie.setItemMeta(cookieMeta);
        itemStackHashMap.put("COOKIE", cookie);

        ItemStack amethyst = ItemUtils.createDefaultMeta(ChatColor.LIGHT_PURPLE + "Грубый аметист", Material.AMETHYST_SHARD);
        itemStackHashMap.put("AMETHYST", amethyst);

        ItemStack fineAmethyst = ItemUtils.createEnchantedMeta(ChatColor.LIGHT_PURPLE + "Прекрасный аметист", Material.AMETHYST_SHARD, Enchantment.MENDING);
        itemStackHashMap.put("FINE_AMETHYST", fineAmethyst);

        ItemStack perfectAmethyst = ItemUtils.createDefaultMeta(ChatColor.LIGHT_PURPLE + "Идеальный аметист", Material.BUDDING_AMETHYST);
        itemStackHashMap.put("PERFECT_AMETHYST", perfectAmethyst);
    }
    public static ItemStack get(String string) {
        return itemStackHashMap.get(string);
    }
    public static Material getType(String string) {
        return itemStackHashMap.get(string).getType();
    }
    public static Set<String> getKeys() {
        return itemStackHashMap.keySet();
    }
}
