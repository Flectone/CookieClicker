package net.flectone.cookieclicker.gameplay.cookiepart;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.items.attributes.StatType;
import net.flectone.cookieclicker.utility.StatsUtils;
import net.flectone.cookieclicker.utility.hoes.EpicHoeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class StatisticDisplay {

    private final EpicHoeUtils epicHoeUtils;
    private final StatsUtils statsUtils;

    @Inject
    public StatisticDisplay(EpicHoeUtils epicHoeUtils, StatsUtils statsUtils) {
        this.epicHoeUtils = epicHoeUtils;
        this.statsUtils = statsUtils;
    }

    public void displayActionBar(ServerCookiePlayer serverCookiePlayer, Integer maxAmount, Integer droppedAmount) {
        // Показ статистики
        Component line = Component.text("| ").style(Style.style(TextDecoration.BOLD));
        // Первое число - общее количество удачи
        // Второе число - число выпавших предметов
        Component fortune = Component.text("+" + droppedAmount).color(TextColor.color(15426836))
                .append(Component.text(" (" + maxAmount + "★) ")).color(TextColor.color(14985236));

        // В скобках первое - уровень заряда, который увеличивает количество выпавших предметов (не удачу)
        // В скобках второе - заряд от эпической мотыги
        Component epicStat = Component.text(String.format("[x%d (%d%%)] ", epicHoeUtils.getTier(serverCookiePlayer.getUuid()), epicHoeUtils.getCharge(serverCookiePlayer.getUuid())))
                .color(TextColor.color(11539691));

        // После скобок первое - кол-во предметов на земле
        Component droppedItems = Component.text(String.format("%d/10 ", serverCookiePlayer.getItems().size()))
                .color(TextColor.color(13299385));

        // Временные штуки, пока не используются
        Component miningStats = Component.text("0⛏").color(TextColor.color(11800078));

        Component actionBar = Component.empty()
                .append(droppedItems)
                .append(line)
                .append(fortune)
                .append(epicStat)
                .append(miningStats);

        serverCookiePlayer.sendPEpacket(new WrapperPlayServerActionBar(actionBar));
    }

    public List<String> getStatsAsList(ServerCookiePlayer serverCookiePlayer) {
        List<String> stats = new ArrayList<>();

        stats.add(String.format("<#eee2d2><italic:false>   Уровень: <#f28423>%d", serverCookiePlayer.getLvl()));
        stats.add(String.format("<#e7f0ef><italic:false>(До %d-го: <#f7bb86>%d<#e7f0ef>)", serverCookiePlayer.getLvl() + 1, serverCookiePlayer.getRemainingXp()));
        stats.add("<#e7f0ef><italic:false>Удача фермера: <#ffc40a>" + statsUtils.extractStat(serverCookiePlayer.getPlayer(), StatType.FARMING_FORTUNE).toString());
        stats.add("<#e7f0ef><italic:false>Шанс на бонус: <#ffb652>наверное 3%");
        stats.add("<#e7f0ef><italic:false>Уровень заряда: <#7524f1>" + epicHoeUtils.getCharge(serverCookiePlayer.getUuid()));
        stats.add(String.format("<#e7f0ef><italic:false>Множитель от заряда: <#9631e1>x%.1f", 1f + (0.5f * epicHoeUtils.getTier(serverCookiePlayer.getUuid()))));
        stats.add(String.format("<#e7f0ef><italic:false>Кликов по рамке: <#bd702d>%d", serverCookiePlayer.getIFrameClicks()));

        return stats;
    }
}
