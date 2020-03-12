/*
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

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Manages {@code Constant}-related data in a graph database.
 */
class ConstantsGraphImpl
        implements ConstantsData
{

    private final GraphDatabaseService db;
    private ExecutionContext executionContext;

    /**
     * Graph DB node identifiers.
     */
    private enum MyLabels implements Label {
        /**
         * A specific Maven artifact version.
         */
        VERSION,

        /**
         * Represents a class field annotated with @Constant, in at least one of
         * the versions under consideration.
         */
        CONSTANT_FIELD
    }

    /**
     * Graph DB relation type identifiers.
     */
    enum RelationshipTypes implements RelationshipType {
        /**
         * A specific instance of a Java class field in a specific Maven artifact version.
         */
        FIELD_INSTANCE,

        /**
         * Relates a version to its immediate successor.
         */
        NEXT_VERSION
    }

    /**
     * {@code VERSION} node property: String representation of the Maven artifact version.
     */
    static final String VERSION_STRING_PROPERTY = "versionString";

    /**
     * {@code CONSTANT_FIELD} node property: String representation of the fully-
     * qualified Java class field identifier.
     */
    static final String FQ_FIELD_NAME_PROPERTY = "fullyQualifiedFieldName";

    /**
     * {@code CONSTANT_FIELD} node property: Class member field object instance.
     */
    static final String FIELD_VALUE_PROPERTY = "fieldValue";

    ConstantsGraphImpl(
            final ExecutionContext executionContext,
            final List<String> versionStrings
    ) {
        this.executionContext = executionContext;

        executionContext.log.debug("Instantiating a new Neo4j in-memory graph database");
        this.db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        createConstraints();
        createArtifactVersionNodes(versionStrings);
    }

    @Override
    public void close() {
        if (db != null) {
            System.out.println("\n-- Neo4j DUMP --");
            withinTransaction(() -> Stream.ofAll(db.getAllNodes())
                    .forEach((n) -> {
                        System.out.println(String.format("Node: %s %s", n.getLabels(), n.getAllProperties()));
                        n.getRelationships().forEach(
                                (r) -> System.out.println(
                                        String.format("  -> %s : %s",
                                                r.getEndNode().getLabels(),
                                                r.getAllProperties())
                                )
                        );
                    }));
            db.shutdown();
        }
    }

    /**
     * Creates a chain of Maven artifact versions, each related to the next
     * in order of appearance using {@code RelationshipTypes.NEXT_VERSION}.
     *
     * @param versions an ordered set of Maven artifact versions as strings
     */
    private void createArtifactVersionNodes(
            final List<String> versions
    ) {
        final Function<String, Node> createVersionNode =
                versionString -> {
                    final Node node = db.createNode(MyLabels.VERSION);
                    node.setProperty(VERSION_STRING_PROPERTY, versionString);
                    return node;
                };

        final BinaryOperator<Node> relateVersionNodes =
                (versionNode, nextVersionNode) -> {
                    versionNode.createRelationshipTo(
                            nextVersionNode,
                            RelationshipTypes.NEXT_VERSION
                    );
                    return nextVersionNode;
                };

        executionContext.log.debug("Creating graph nodes for versions:  " + Static.listOfVersions(versions));
        withinTransaction(() ->
            versions
                    .map(createVersionNode)
                    // Until a better VAVR idiom is used to express the relating of
                    // consecutive versions, the following reduce() abuse will have to do:
                    .reduceLeft(relateVersionNodes)
        );
    }

    private void createConstraints() {
        executionContext.log.debug("Creating graph database constraints");

        withinTransaction(() -> db.schema()
                .constraintFor(MyLabels.VERSION)
                .assertPropertyIsUnique(VERSION_STRING_PROPERTY)
                .create());
    }

    private Node findOrCreate(
            final Label label,
            final String key,
            final Object value
    ) {
        final Node existingNode = db.findNode(label, key, value);
        if (existingNode != null) {
            return existingNode;
        }

        final Node newNode = db.createNode(label);
        newNode.setProperty(key, value);
        return newNode;
    }

    /**
     * Expects to be run within a transaction.
     * Throws an exception if the request version node couldn't be found.
     */
    private Node retrieveVersionNode(
            final String version
    ) {
        final String v = version.toString();
        final Node n = db.findNode(MyLabels.VERSION, VERSION_STRING_PROPERTY, v);

        if (n == null) {
//            throw new MojoExecutionException(
//                    iiiiiiI18n.getText(
//                            "vivid.cherimoya.error.ce-1-internal-error",
//                            "Unexpectedly could not find the artifact version node for " + artifactVersion
//                    )
//            );
        }

        return n;
    }

    @Override
    public void recordConstantFields(
            final String version,
            final List<Tuple2<String, Object>> fields
    ) {
        fields.forEach(
                f -> recordConstantField(version, f)
        );
    }

    private void recordConstantField(
            final String version,
            Tuple2<String, Object> field
    ) {
        withinTransaction(() -> {
            // The field node represents a field bearing the @Constant annotation.
            // The existence of the field node indicates that the field
            // is present in one or more versions.
            final Node fieldNode = findOrCreate(
                    MyLabels.CONSTANT_FIELD,
                    FQ_FIELD_NAME_PROPERTY,
                    field._1
            );

            // The version in which the field exists. The versions are expected to already be made.
            final Node versionNode = retrieveVersionNode(version);

            // Relate the constant field node to its version.
            // This vertex holds the field value as a property.
            final Relationship r = fieldNode.createRelationshipTo(
                    versionNode,
                    RelationshipTypes.FIELD_INSTANCE
            );
            r.setProperty(FIELD_VALUE_PROPERTY, field._2);
        });
    }

    private void withinTransaction(
            final Runnable runnable
    ) {
        try (final Transaction tx = db.beginTx()) {
            runnable.run();
            tx.success();
        } // TODO catch { tx.failure() }
    }

}
