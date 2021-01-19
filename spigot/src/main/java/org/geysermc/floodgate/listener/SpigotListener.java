/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Floodgate
 */

package org.geysermc.floodgate.listener;

import com.google.inject.Inject;
import com.nukkitx.protocol.bedrock.packet.TextPacket;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.geysermc.floodgate.FloodgatePlayerImpl;
import org.geysermc.floodgate.api.SimpleFloodgateApi;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.skin.SkinHandler;
import org.geysermc.floodgate.util.LanguageManager;

public final class SpigotListener implements Listener {
    @Inject private SimpleFloodgateApi api;
    @Inject private SkinHandler skinHandler;
    @Inject private LanguageManager languageManager;
    @Inject private FloodgateLogger logger;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            api.removePlayer(event.getUniqueId(), true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            api.removePlayer(uniqueId);
            return;
        }

        // if there was another player with the same uuid online,
        // he would've been disconnected by now
        FloodgatePlayer player = api.getPlayer(uniqueId);
        if (player != null) {
            player.as(FloodgatePlayerImpl.class).setLogin(false);
            logger.translatedInfo(
                    "floodgate.ingame.login_name",
                    player.getCorrectUsername(), player.getCorrectUniqueId()
            );
            skinHandler.handleSkinUploadFor(player);
            languageManager.loadLocale(player.getLanguageCode());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (api.removePlayer(player.getUniqueId()) != null) {
            logger.translatedInfo("floodgate.ingame.disconnect_name", player.getName());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TextPacket t = new TextPacket();
        t.setXuid("");
        t.setNeedsTranslation(false);
        t.setSourceName("");
        t.setPlatformChatId("");
        t.setMessage("test message!!???");
        t.setType(TextPacket.Type.SYSTEM);

        api.getPlayer(event.getPlayer().getUniqueId()).sendPacket(t);
    }
}
