package net.chwthewke.maven.protobuf.protoc;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathProtocExecutable implements ProtocExecutable {

    @Override
    public void prepare( ) {
    }

    @Override
    public Path getPath( ) {
        return Paths.get( "protoc" );
    }

}
