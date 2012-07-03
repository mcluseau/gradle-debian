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

package org.gradle.integtests.tooling

import org.gradle.integtests.fixtures.AutoTestedSamplesUtil
import org.gradle.internal.jvm.Jvm
import org.gradle.util.TemporaryFolder
import org.junit.Rule
import spock.lang.IgnoreIf
import spock.lang.Specification
import org.gradle.util.ClasspathUtil
import org.gradle.tooling.model.Element

/**
 * by Szczepan Faber, created at: 1/5/12
 */
@IgnoreIf({!Jvm.current().java6Compatible})
public class AutoTestedSamplesToolingApiTest extends Specification {

    @Rule public final TemporaryFolder temp = new TemporaryFolder()

    void runSamples() {
        expect:

        def util = new AutoTestedSamplesUtil()
        util.findSamples("subprojects/tooling-api/src/main") { file, sample ->
            println "Found sample: ${sample.split("\n")[0]} (...) in $file"
            def javaSource = """
//some typical imports
import org.gradle.tooling.*;
import org.gradle.tooling.model.*;
import org.gradle.tooling.model.build.*;
import java.io.*;

public class Sample {
  public static void main(String ... args) {
    $sample
  }
}
"""
            tryCompile(javaSource)
        }
    }

    /**
     * The implementation should never assume we're running against jdk6.
     * Hence the impl is quite awkward and does all interaction with java6 compiler api reflectively.
     *
     * @param source
     */
    void tryCompile(String source) {
        //TODO SF generalize and move the test out of integ tests, add unit tests
        source = normalize(source)
        def sourceFile = temp.dir.file("Sample.java")
        sourceFile.text = source

        def compiler = ("javax.tools.ToolProvider" as Class).getSystemJavaCompiler()
        def fileManager = compiler.getStandardFileManager(null, null, null);

        def location = ("javax.tools.StandardLocation" as Class).CLASS_OUTPUT
        fileManager.setLocation(location, [temp.dir]);

        location = ("javax.tools.StandardLocation" as Class).CLASS_PATH
        fileManager.setLocation(location, [ClasspathUtil.getClasspathForClass(Element)]);

        def checkDiagnostic = { diagnostic ->
            if (diagnostic.kind.name() == 'ERROR') {
                String[] lines = source.split("\n")
                int lineNo = diagnostic.lineNumber - 1

                def message = "Compilation error in sample in line: \n" + lines[lineNo] + "\n" + diagnostic + "\n"
                message = message - sourceFile.absolutePath
                throw new AssertionError(message)
            }
        }

        def diagnosticListener = checkDiagnostic.asType("javax.tools.DiagnosticListener" as Class)

        def input = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))
        compiler.getTask(null,
            fileManager,
            diagnosticListener,
            null,
            null,
            input).call();

        fileManager.close();
    }

    String normalize(String input) {
        String out = input.replaceAll("&gt;", ">")
        out = out.replaceAll("&lt;", "<")
        out
    }
}