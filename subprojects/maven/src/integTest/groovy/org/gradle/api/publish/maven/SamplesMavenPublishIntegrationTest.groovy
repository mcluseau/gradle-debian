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


package org.gradle.api.publish.maven
import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.Sample
import org.junit.Rule

public class SamplesMavenPublishIntegrationTest extends AbstractIntegrationSpec {

    @Rule Sample sample = new Sample("maven/publish-new")

    def sample() {
        given:
        executer.inDirectory(sample.dir)

        and:
        def fileRepo = maven(sample.dir.file("build/repo"))
        def module = fileRepo.module("org.gradle.sample", "publish-new", "1.0")

        when:
        succeeds "publish"

        then:
        def pom = module.parsedPom
        module.assertPublishedAsJavaModule()
        pom.description == "A demonstration of maven pom customisation"
        pom.scopes.runtime.assertDependsOn("commons-collections", "commons-collections", "3.0")
    }
}
