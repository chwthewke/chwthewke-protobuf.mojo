package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConstants {

    public static final Path TARGET = Paths.get( "target" );
    public static final Path PLUGIN_WORK = TARGET.resolve( "protobuf" );

    public static final Path GENERATED_SOURCES_BASE = TARGET.resolve( "generated-sources" ).resolve( "protobuf" );
    public static final Path PROTOC_PLUGINS = PLUGIN_WORK.resolve( "plugin" );

    // TODO make value objects
    public static final Path PROTO_DEPENDENCIES_ARCHIVE = PLUGIN_WORK.resolve( "proto-deps.jar" );
    public static final String PROTO_DEPENDENCIES_CLASSIFIER = "proto-deps";

    public static final Path PROTO_SOURCE_ARCHIVE = PLUGIN_WORK.resolve( "proto-sources.jar" );
    public static final String PROTO_SOURCE_CLASSIFIER = "proto-sources";

}
