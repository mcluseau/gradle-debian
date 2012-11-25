/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.tasks.diagnostics.internal.graph.nodes;

import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.artifacts.result.ResolvedModuleVersionResult;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * by Szczepan Faber, created at: 8/10/12
 */
public class RenderableModuleResult extends AbstractRenderableModuleResult implements RenderableDependency {

    public RenderableModuleResult(ResolvedModuleVersionResult module) {
        super(module);
    }

    public Set<RenderableDependency> getChildren() {
        Set<RenderableDependency> out = new LinkedHashSet<RenderableDependency>();
        for (DependencyResult d : module.getDependencies()) {
            //TODO SF revisit when implementing the 'unresolved dependencies' story
            if (d instanceof ResolvedDependencyResult) {
                out.add(new RenderableDependencyResult((ResolvedDependencyResult) d));
            }
        }
        return out;
    }
}