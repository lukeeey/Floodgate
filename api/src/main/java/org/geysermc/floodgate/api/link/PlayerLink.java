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

package org.geysermc.floodgate.api.link;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.geysermc.floodgate.util.LinkedPlayer;

/**
 * The base class of the PlayerLink database implementation. The implementation is responsible for
 * making a connection with the database and keeping that connection alive so that Floodgate (or a
 * third party plugin) can check for example if a given player is linked.
 */
public interface PlayerLink {
    /**
     * Called by Floodgate after the initialization of the class. In this method the implementation
     * should start the connection with the database and create the collections if they don't exist
     * already.
     */
    void load();

    /**
     * Get a linked player by the bedrock uuid
     *
     * @param bedrockId the uuid of the bedrock player
     * @return a completable future with the {@link LinkedPlayer}. The future will have a null value
     * if that Bedrock player isn't linked
     */
    CompletableFuture<LinkedPlayer> getLinkedPlayer(UUID bedrockId);

    /**
     * Tells if the given player is a linked player
     *
     * @param bedrockId the bedrock uuid of the linked player
     * @return true if the player is a linked player
     */
    CompletableFuture<Boolean> isLinkedPlayer(UUID bedrockId);

    /**
     * Links a Java account to a Bedrock account.
     *
     * @param bedrockId the uuid of the Bedrock player
     * @param javaId    the uuid of the Java player
     * @param username  the username of the Java player
     * @return a future holding void on success or completed exceptionally when failed
     */
    CompletableFuture<Void> linkPlayer(UUID bedrockId, UUID javaId, String username);

    /**
     * Unlinks a Java account from a Bedrock account.
     *
     * @param javaId the uuid of the Java player
     * @return a future holding void on success or completed exceptionally when failed
     */
    CompletableFuture<Void> unlinkPlayer(UUID javaId);

    /**
     * Return if account linking is enabled. The difference between enabled and allowed is that
     * 'enabled' still allows already linked people to join with their linked account while 'allow
     * linking' allows people to link accounts using the commands.
     */
    boolean isEnabled();

    /**
     * Returns the duration (in seconds) before a {@link LinkRequest} timeouts
     */
    long getVerifyLinkTimeout();

    /**
     * Return if account linking is allowed. The difference between enabled and allowed is that
     * 'enabled' still allows already linked people to join with their linked account while 'allow
     * linking' allows people to link accounts using the commands.
     */
    boolean isAllowLinking();

    default boolean isEnabledAndAllowed() {
        return isEnabled() && isAllowLinking();
    }

    /**
     * Called when the Floodgate plugin is going to shutdown
     */
    void stop();
}
