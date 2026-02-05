package net.flectone.cookieclicker.entities.objects;

import net.flectone.cookieclicker.entities.playerdata.ServerCookiePlayer;

public interface Spawnable {

    void spawn(ServerCookiePlayer serverCookiePlayer);
    void remove(ServerCookiePlayer serverCookiePlayer);
    void setLocation(Double x, Double y, Double z);
}
