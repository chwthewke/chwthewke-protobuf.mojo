package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginConstants {

    public static final Path TARGET = Paths.get( "target" );
    public static final Path PLUGIN_WORK = TARGET.resolve( "protobuf" );

    // TODO make value objects
    public static final Path PROTO_DEPENDENCIES_ARCHIVE = PLUGIN_WORK.resolve( "proto-deps.jar" );
    public static final String PROTO_DEPENDENCIES_CLASSIFIER = "proto";

    public static final Path PROTO_SOURCE_ARCHIVE = PLUGIN_WORK.resolve( "proto-sources.jar" );
    public static final String PROTO_SOURCE_CLASSIFIER = "proto-sources";

}
