package net.flectone.cookieclicker.events;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.flectone.cookieclicker.playerdata.CookiePlayer;
import net.flectone.cookieclicker.playerdata.ServerCookiePlayer;
import net.flectone.cookieclicker.utility.Database;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class ConnectedPlayers {
    private final Database database;
    private final HashMap<UUID, CookiePlayer> playersOnServer = new HashMap<>();

    @Inject
    public ConnectedPlayers(Database database) {
        this.database = database;
    }

    public void processPlayerJoin(UUID uuid, String name) {
        try (Connection connection = database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT * FROM 'users' WHERE uuid LIKE ?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                playersOnServer.put(uuid, new CookiePlayer(uuid, resultSet.getInt("itemframe_clicks"), resultSet.getInt("remaining_xp"), resultSet.getInt("lvl")));
                return;
            }

            //если нет игрока такого в базе
            insertNewPlayer(uuid, name);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertNewPlayer(UUID uuid, String name) {
        try (Connection connection = database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO 'users' ('uuid', 'name') VALUES (?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.executeUpdate();

            playersOnServer.put(uuid, new CookiePlayer(uuid));
            MinecraftServer.getServer().sendSystemMessage(Component.literal(name + " добавлен в базу данных"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CookiePlayer getCookiePlayer(UUID uuid) {
        if (playersOnServer.isEmpty() || !playersOnServer.containsKey(uuid))
            return null;

        return playersOnServer.get(uuid);
    }

    public ServerCookiePlayer getServerCookiePlayer(UUID uuid) {
        CookiePlayer cookiePlayer = getCookiePlayer(uuid);

        return cookiePlayer != null ? new ServerCookiePlayer(cookiePlayer) : null;
    }

    private void update(CookiePlayer cookiePlayer) {
        try (Connection connection = database.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE 'users' SET 'itemframe_clicks' = ?, 'remaining_xp' = ?, 'lvl' = ? WHERE uuid = ?");
            statement.setInt(1, cookiePlayer.getIFrameClicks());
            statement.setInt(2, cookiePlayer.getRemainingXp());
            statement.setInt(3, cookiePlayer.getLvl());
            statement.setString(4, cookiePlayer.getUuid().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(CookiePlayer cookiePlayer, boolean localSave) {
        playersOnServer.put(cookiePlayer.getUuid(), cookiePlayer);
        if (!localSave) {
            update(cookiePlayer);
        }
    }

    public void saveAndDeleteLocal(UUID uuid) {
        CookiePlayer cookiePlayer = getServerCookiePlayer(uuid);
        if (cookiePlayer == null)
            return;

        update(cookiePlayer);
        playersOnServer.remove(uuid);
    }
}
