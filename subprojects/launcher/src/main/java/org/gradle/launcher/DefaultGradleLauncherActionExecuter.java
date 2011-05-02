/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.launcher;

import org.gradle.BuildExceptionReporter;
import org.gradle.BuildResult;
import org.gradle.GradleLauncher;
import org.gradle.StartParameter;
import org.gradle.api.internal.project.ServiceRegistry;
import org.gradle.initialization.GradleLauncherAction;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.logging.LoggingManagerInternal;
import org.gradle.logging.StyledTextOutputFactory;

public class DefaultGradleLauncherActionExecuter implements GradleLauncherActionExecuter<BuildActionParameters> {
    private final ServiceRegistry loggingServices;
    private final GradleLauncherFactory gradleLauncherFactory;

    public DefaultGradleLauncherActionExecuter(GradleLauncherFactory gradleLauncherFactory, ServiceRegistry loggingServices) {
        this.gradleLauncherFactory = gradleLauncherFactory;
        this.loggingServices = loggingServices;
    }

    public <T> T execute(GradleLauncherAction<T> action, BuildActionParameters parameters) {
        StartParameter startParameter = new StartParameter();
        if (action instanceof InitializationAware) {
            InitializationAware initializationAware = (InitializationAware) action;
            initializationAware.configureStartParameter(startParameter);
        }

        LoggingManagerInternal loggingManager = loggingServices.getFactory(LoggingManagerInternal.class).create();
        loggingManager.setLevel(startParameter.getLogLevel());
        loggingManager.start();
        try {
            GradleLauncher gradleLauncher = gradleLauncherFactory.newInstance(startParameter, parameters.getBuildRequestMetaData());
            BuildResult buildResult = action.run(gradleLauncher);
            Throwable failure = buildResult.getFailure();
            if (failure != null) {
                throw new ReportedException(failure);
            }
            return action.getResult();
        } catch (ReportedException e) {
            throw e;
        } catch (Throwable throwable) {
            BuildExceptionReporter exceptionReporter = new BuildExceptionReporter(loggingServices.get(StyledTextOutputFactory.class), new StartParameter(), parameters.getClientMetaData());
            exceptionReporter.reportException(throwable);
            throw new ReportedException(throwable);
        } finally {
            loggingManager.stop();
        }
    }
}
