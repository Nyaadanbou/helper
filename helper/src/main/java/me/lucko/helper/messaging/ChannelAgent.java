/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.helper.messaging;

import me.lucko.helper.terminable.Terminable;

import java.util.Set;

/**
 * Represents an agent for interacting with a {@link Channel}s message streams.
 *
 * @param <T> the channel message type
 */
public interface ChannelAgent<T> extends Terminable {

    /**
     * Gets the channel this agent is acting for.
     *
     * @return the parent channel
     */
    Channel<T> getChannel();

    /**
     * Gets an immutable copy of the listeners currently held by this agent.
     *
     * @return the active listeners
     */
    Set<ChannelListener<T>> getListeners();

    /**
     * Gets if this agent has any active listeners.
     *
     * @return true if this agent has listeners
     */
    boolean hasListeners();

    /**
     * Adds a new listener to the channel;
     *
     * @param listener the listener to add
     * @return true if successful
     */
    boolean addListener(ChannelListener<T> listener);

    /**
     * Removes a listener from the channel.
     *
     * @param listener the listener to remove
     * @return true if successful
     */
    boolean removeListener(ChannelListener<T> listener);

}