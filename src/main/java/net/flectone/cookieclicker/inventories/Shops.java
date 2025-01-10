package net.flectone.cookieclicker.inventories;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.CompactItems;
import net.flectone.cookieclicker.items.ShopManager;
import net.flectone.cookieclicker.utility.CCConversionUtils;
import net.flectone.cookieclicker.utility.CCobjects.NormalItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Singleton
public class Shops {
    private final ContainerManager containerManager;
    private final ShopManager shopManager;
    private final CCConversionUtils converter;
    private final CompactItems compactItems;

    private final Random random = new Random();

    @Inject
    public Shops(ContainerManager containerManager, ShopManager shopManager, CCConversionUtils converter,
                 CompactItems compactItems) {
        this.containerManager = containerManager;
        this.shopManager = shopManager;
        this.converter = converter;
        this.compactItems = compactItems;
    }

    public void openCookiesShop(User user, Player player) {

        ClickerContainer Trading = new ClickerContainer(random.nextInt(99, 1000),
                3, "trading_farm");

        NormalItem upperItem = new NormalItem(Material.BEDROCK,
                "<gradient:#ffffff:#cccccc><italic:false>Здесь вы можете купить",
                "none", 1);
        upperItem.addLore("<gradient:#ffffff:#cccccc><italic:false>различные вещи за печенье");

        Trading.setTitle("торговля жесть в шоке все");

        for (int i = 0; i < 9; i++)
            Trading.setItem(i, upperItem.toItemStack());

        int slot = 9;
        //идёт по каждому товару
        for (Map.Entry<ItemStack, ItemStack> shopItems : shopManager.getEntrySet()) {
            ItemStack entryItem = shopItems.getKey();
            ItemStack finalItem = entryItem.copy();
            //название предмета, за который можно купить товар
            net.minecraft.network.chat.Component customName = shopItems.getValue().get(DataComponents.CUSTOM_NAME);
            //стоимость, которая потом добавится в лор
            Component cost_forLore = MiniMessage.miniMessage().deserialize("<#C70039>Стоимость: "
                    + shopItems.getValue().getCount() + " "
                    + (customName != null ? customName.getString() : "видимо воздуха"));

            List<net.minecraft.network.chat.Component> lore = new ArrayList<>();
            ItemLore itemLore = entryItem.getComponents().get(DataComponents.LORE);
            if (itemLore != null) lore.addAll(itemLore.lines());
            //стоимость добавляется в лор, при покупке видно не будет
            lore.addFirst(converter.convertToNMSComponent(cost_forLore));

            finalItem.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.LORE, new ItemLore(lore))
                    .build()
            );
            finalItem.remove(DataComponents.ATTRIBUTE_MODIFIERS);

            Trading.setItem(slot, finalItem);
            slot++;
        }
        containerManager.openContainer(user, player, Trading);
    }

    public void buyItemFarmer(User user, Player player, WrapperPlayClientClickWindow packet) {
        int slot = packet.getSlot();
        user.sendMessage(String.valueOf(slot));
        ClickerContainer container = containerManager.getOpenedContainer(user);
        if (container.getContainerItems().size() - 1 < slot) return;

        //отмена клика, самое адекватное, что я смог сделать
        //короче тут сначала отправляется пакет через nms, потому что у меня используются ItemStack оттуда,
        //а потом
        containerManager.setContainerSlot(player, container, slot, container.getContainerItems().get(slot));
        containerManager.setContainerSlot(player, container, -1, new ItemStack(Items.AIR));

        for (int i = 0; i < 36; i++) {
            //эта штука сначала пройдётся по хотбару, а потом по инвентарю
            int actualSlot = i < 9 ? 8 - i : 35 - (i - 9);
            if (player.getInventory().items.get(actualSlot).getItem().equals(Items.AIR)) {
                containerManager.setPlayerSlot(player, actualSlot, new ItemStack(Items.AIR));
                break;
            }
        }

        if (slot >= 9) {
            if (shopManager.itemsLength() <= slot - 9) return;

            ItemStack priceItem = shopManager.getPrice(slot - 9);
            compactItems.compact(player.getInventory(), priceItem.copy(), shopManager.getItem(slot - 9), priceItem.getCount(), 1);
        }
    }

}
