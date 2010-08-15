/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.logging;

import org.gradle.api.logging.LogLevel;

public interface LoggingSystem {
    Snapshot snapshot();

    /**
     * Enables logging for this logging system at the given level.
     *
     * @param level The new level.
     * @return the state of this logging system immediately before the changes are applied.
     */
    Snapshot on(LogLevel level);

    /**
     * Disables logging for this logging system
     *
     * @return the state of this logging system immediately before the changes are applied.
     */
    Snapshot off();

    void restore(Snapshot state);

    interface Snapshot {
    }
}
