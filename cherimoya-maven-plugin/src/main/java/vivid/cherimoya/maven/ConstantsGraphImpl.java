/*
 * Copyright 2017 The Cherimoya Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package vivid.cherimoya.maven;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.SortedMap;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import org.codehaus.plexus.util.FileUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Manages {@code Constant}-related data in a graph database.
 */
class ConstantsGraphImpl
        implements ConstantsData
{

    private final GraphDatabaseService db;
    private final Path dbTempDirectory;
    private final HashMap<Object, Object> fieldValueStore;
    private final Mojo mojo;
    private final List<String> versionStrings;

    /**
     * Graph DB node identifiers.
     */
    private enum NodeLabels implements Label {
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
    private enum RelationshipTypes implements RelationshipType {
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
     * {@code CONSTANT_FIELD} node property: String representation of the fully-
     * qualified Java class field identifier.
     */
    private static final String FIELD_FULLY_QUALIFIED_NAME_PROPERTY = "fqn";

    /**
     * {@code CONSTANT_FIELD} node property: Class member field value ID correlating
     * to the field value store.
     */
    private static final String FIELD_VALUE_ID_PROPERTY = "valueID";

    /**
     * {@code VERSION} node property: String representation of the Maven artifact version.
     */
    private static final String VERSION_STRING_PROPERTY = "version";

    /**
     * {@code VERSION} node property: Order of appearance in the sorted list of all
     * versions under consideration.
     */
    private static final String VERSION_ORDINAL_PROPERTY = "ordinal";

    ConstantsGraphImpl(
            final Mojo mojo,
            final List<String> versionStrings
    ) throws IOException {
        this.mojo = mojo;
        this.versionStrings = versionStrings;

        this.dbTempDirectory = Files.createTempDirectory("cherimoya");
        this.db = new GraphDatabaseFactory()
                .newEmbeddedDatabase(dbTempDirectory.toFile());
        createConstraints();
        createArtifactVersionNodes(versionStrings);
        mojo.getLog().debug(
                "Instantiated a new database in " + this.dbTempDirectory
        );

        this.fieldValueStore = new HashMap<>();
    }

    /**
     * @return an ID that uniquely identifies this field
     */
    private String recordFieldValue(
            final Object fieldValue
    ) {
        final String fieldID = UUID.randomUUID().toString();
        fieldValueStore.put(fieldID, fieldValue);
        return fieldID;
    }

    public void close() {
        db.shutdown();

        try {
            FileUtils.deleteDirectory(dbTempDirectory.toFile());
        } catch (final IOException e) {
            mojo.getLog().info(
                    "Could not delete temporary DB directory: " + dbTempDirectory,
                    e
            );
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
        withinTransaction(() -> {
            final Function<Tuple2<String, Integer>, Node> createVersionNode =
                    versionDesc -> {
                        final Node node = db.createNode(NodeLabels.VERSION);
                        node.setProperty(VERSION_STRING_PROPERTY, versionDesc._1);
                        node.setProperty(VERSION_ORDINAL_PROPERTY, versionDesc._2);
                        return node;
                    };

            final BinaryOperator<Node> relateVersionNodes =
                    (cursor, next) -> {
                        cursor.createRelationshipTo(
                                next,
                                RelationshipTypes.NEXT_VERSION
                        );
                        return next;
                    };

            final Stream<Integer> versionOrder =
                    Stream.iterate(0, i -> i + 1);

            mojo.getLog().debug(
                    "Creating graph nodes for versions:  " +
                            Static.humanReadableVersionList(versions)
            );
            versions
                    .zip(versionOrder)
                    .map(createVersionNode)
                    // Until a better VAVR idiom is used to express the relating of
                    // consecutive versions, the following reduce() abuse will have to do:
                    .reduceLeft(relateVersionNodes);
        });
    }

    private void createConstraints() {
        mojo.getLog().debug("Creating graph database constraints");

        withinTransaction(() -> {
            db.schema()
                    .constraintFor(NodeLabels.CONSTANT_FIELD)
                    .assertPropertyIsUnique(FIELD_FULLY_QUALIFIED_NAME_PROPERTY)
                    .create();
            db.schema()
                    .constraintFor(NodeLabels.VERSION)
                    .assertPropertyIsUnique(VERSION_STRING_PROPERTY)
                    .create();
            db.schema()
                    .constraintFor(NodeLabels.VERSION)
                    .assertPropertyIsUnique(VERSION_ORDINAL_PROPERTY)
                    .create();
        });
    }

    private Node findOrCreateNode(
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
        return db.findNode(NodeLabels.VERSION, VERSION_STRING_PROPERTY, version);
    }

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
            // The field node represents a field bearing the @Constant
            // annotation. The existence of the field node indicates
            // that the field is present in one or more versions.
            final Node fieldNode = findOrCreateNode(
                    NodeLabels.CONSTANT_FIELD,
                    FIELD_FULLY_QUALIFIED_NAME_PROPERTY,
                    field._1
            );

            // The version in which the field exists. The versions are
            // expected to already be made.
            final Node versionNode = retrieveVersionNode(version);

            // Relate the constant field node to its version.
            // This vertex holds the field value as a property.
            final Relationship r = fieldNode.createRelationshipTo(
                    versionNode,
                    RelationshipTypes.FIELD_INSTANCE
            );
            final String fieldID = recordFieldValue(field._2);
            r.setProperty(FIELD_VALUE_ID_PROPERTY, fieldID);
        });
    }

    private void withinTransaction(
            final Runnable runnable
    ) {
        try (final Transaction tx = db.beginTx()) {
            runnable.run();
            tx.success();
        }
    }

    public int constantFieldsCount() {
        final ResourceIterator<Object> resultIterator =
                db.execute(
                        "MATCH (n:CONSTANT_FIELD) RETURN count(n) as count"
                )
                        .columnAs("count");
        final Object result = resultIterator.next();
        return Integer.parseInt(result.toString());
    }

    public List<ConstancyViolation> constancyViolationDescriptions() {
        java.util.List<ConstancyViolation> violations = new ArrayList<>();

        withinTransaction(() -> {
            final ResourceIterator<Node> fields = db.findNodes(NodeLabels.CONSTANT_FIELD);
            fields.forEachRemaining(field -> {
                final String fullyQualifiedFieldName = String.valueOf(field.getProperty(FIELD_FULLY_QUALIFIED_NAME_PROPERTY));
                final SortedMap<String, Option<Object>> version2value = this.versionStrings.toSortedMap(
                        v -> v,
                        v -> Option.none()
                );
                final Iterable<Relationship> fieldInstances =
                        field.getRelationships(Direction.OUTGOING, RelationshipTypes.FIELD_INSTANCE);

                final BiFunction<SortedMap<String, Option<Object>>, Relationship, SortedMap<String, Option<Object>>> noteFieldInstanceValue =
                        (v2v, r) -> v2v.put(
                                String.valueOf(r.getEndNode().getProperty(VERSION_STRING_PROPERTY)),
                                Option.of(
                                        fieldValueStore.get(r.getProperty(FIELD_VALUE_ID_PROPERTY))
                                )
                        );

                final Predicate<Tuple2<SimpleVersionRange, Option<Object>>> undefVal = e -> e._2.isEmpty();

                final SortedMap<SimpleVersionRange, Option<Object>> valueByVersion = Stream
                        .ofAll(fieldInstances)
                        .foldLeft(version2value, noteFieldInstanceValue)
                        .map((ver, val) -> new Tuple2<>(new SimpleVersionRange(ver, Option.none()), val));

                final BiFunction<SortedMap<SimpleVersionRange, Option<Object>>,
                        Tuple2<SimpleVersionRange, Option<Object>>,
                        SortedMap<SimpleVersionRange, Option<Object>>>
                        collapseAdjacentEntriesOfEqualValue =
                        (v2v, cur) -> {
                            final Tuple2<SimpleVersionRange, Option<Object>> prev = v2v.last();
                            if (prev._2.equals(cur._2)) {
                                return v2v.dropRight(1).put(
                                        new SimpleVersionRange(
                                                prev._1.start, Option.of(cur._1.start)),
                                        prev._2
                                );
                            } else {
                                return v2v.put(cur);
                            }
                        };

                final SortedMap<SimpleVersionRange, Option<Object>> roughHistory =
                        valueByVersion
                                .drop(1)
                                .foldLeft(valueByVersion.take(1), collapseAdjacentEntriesOfEqualValue);

                final UnaryOperator<SortedMap<SimpleVersionRange, Option<Object>>> trimUndefValEnds =
                        hist -> {
                            final SortedMap<SimpleVersionRange, Option<Object>> histHead = undefVal.test(roughHistory.take(1).get()) ? hist.drop(1) : hist;
                            return undefVal.test(histHead.takeRight(1).get()) ? histHead.dropRight(1) : histHead;
                        };

                final SortedMap<SimpleVersionRange, Option<Object>> fineHistory =
                        trimUndefValEnds.apply(roughHistory);

                if (fineHistory.isEmpty()) {
                    throw new SneakyMojoException(
                            CE1InternalError.asNewMojoExecutionException(
                                    mojo,
                                    String.format(
                                            "Field %s was recorded as being subject to @Constant constraints " +
                                                    "but was calculated as not being defined in any version",
                                            fullyQualifiedFieldName
                                    )
                            )
                    );
                }
                // When fineHistory.length() == 1, the field value is constant throughout its recorded range.
                else if (fineHistory.length() >= 2) {
                    final ConstancyViolation cv = new ConstancyViolation(fullyQualifiedFieldName, fineHistory.toList());
                    violations.add(cv);
                }

            });
        });

        return List.ofAll(violations);
    }

}
