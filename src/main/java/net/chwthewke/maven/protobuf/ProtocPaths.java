package net.chwthewke.maven.protobuf;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ProtocPaths {

    public static final Path TARGET = Paths.get( "target" );
    public static final Path PLUGIN_WORK = TARGET.resolve( "protobuf" );

}
