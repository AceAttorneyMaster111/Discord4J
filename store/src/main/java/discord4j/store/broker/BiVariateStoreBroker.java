/*
 *  This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package discord4j.store.broker;

import discord4j.store.Store;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BiVariateStoreBroker<V1 extends Store<?, ?>, V2 extends Store<?, ?>> {

    Mono<Void> ensureStorePresent(long key1, long key2);

    Mono<V1> getPrimaryStore(long key);

    Mono<V2> getSecondaryStore(long key);

    Flux<V1> getAllPrimaryStores();

    Flux<V2> getAllSecondaryStores();

    Mono<Void> clearPrimaryStore(long key);

    Mono<Void> clearSecondaryStore(long key);

    Mono<Void> clearAllStores();
}
