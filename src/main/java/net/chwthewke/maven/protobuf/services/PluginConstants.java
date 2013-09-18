package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConstants {

    public static final Path TARGET = Paths.get( "target" );
    public static final Path PLUGIN_WORK = TARGET.resolve( "protobuf" );

    public static final Path GENERATED_SOURCES_BASE = TARGET.resolve( "generated-sources" ).resolve( "protobuf" );
    public static final Path GENERATED_TEST_SOURCES_BASE =
            TARGET.resolve( "generated-test-sources" ).resolve( "protobuf" );

    public static final Path PROTOC_PLUGINS = PLUGIN_WORK.resolve( "plugin" );

    public static final String PROTO_DEPENDENCIES_CLASSIFIER = "-deps";
    public static final String PROTO_SOURCE_CLASSIFIER = "-sources";

}
