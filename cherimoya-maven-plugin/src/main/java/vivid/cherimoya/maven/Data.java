/**
 * Copyright 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vivid.cherimoya.maven;

import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.Set;
import java.util.TreeSet;

/**
 * @since 1.0
 */
class Data {

    private enum MyLabels implements Label { ARTIFACT_VERSION, CONSTANT_FIELD }

    private static final String ARTIFACT_VERSION_PROPERTY = "artifactVersion";

    private static final String CONSTANT_FULLY_QUALIFIED_FIELD_NAME_PROPERTY = "fullyQualifiedFieldName";

    private final GraphDatabaseService graphDatabaseService;

    Data() {
        this.graphDatabaseService = new TestGraphDatabaseFactory().newImpermanentDatabase();
        createConstraints();
    }

    private void createConstraints() {
        try (final Transaction tx = graphDatabaseService.beginTx()) {
            graphDatabaseService.schema()
                    .constraintFor(MyLabels.ARTIFACT_VERSION)
                    .assertPropertyIsUnique(ARTIFACT_VERSION_PROPERTY)
                    .create();
            tx.success();
        }
    }


    //
    // Graph DB nodes and relations
    //

    private Node createArtifactVersionNode(
            final String artifactVersion
    ) {
        try (final Transaction tx = graphDatabaseService.beginTx()) {
            final Node node = graphDatabaseService.createNode(MyLabels.ARTIFACT_VERSION);
            node.setProperty(ARTIFACT_VERSION_PROPERTY, artifactVersion);

            tx.success();

            return node;
        }
    }

    private Node createConstantFieldNode(
            final String clazzName,
            final String fieldName
    ) {
        try (final Transaction tx = graphDatabaseService.beginTx()) {
            final Node node = graphDatabaseService.createNode(MyLabels.CONSTANT_FIELD);
            node.setProperty(
                    CONSTANT_FULLY_QUALIFIED_FIELD_NAME_PROPERTY,
                    Static.fullyQualifiedFieldName(clazzName, fieldName)
            );

            tx.success();

            return node;
        }
    }


    //
    // Data Access
    //

    Set<String> getAllVersions() {
        try (final Transaction tx = graphDatabaseService.beginTx()) {
            final ResourceIterator<Node> nodes = graphDatabaseService.findNodes(MyLabels.ARTIFACT_VERSION);

            final Set<String> versions = new TreeSet<>();
            while (nodes.hasNext()) {
                final Node node = nodes.next();
                final String version = node.getProperty(ARTIFACT_VERSION_PROPERTY).toString();
                versions.add(version);
            }
            tx.success();
            return versions;
        }
    }

    void recordArtifactVersion(
            final String artifactVersion
    ) {
        createArtifactVersionNode(artifactVersion);
    }

    void recordConstantField(
            final String artifactVersion,
            final String clazzName,
            final String fieldName
    ) {
        createConstantFieldNode(clazzName, fieldName);
        // TODO Relate the constant field -> field type and value <- artifact version
    }

}
