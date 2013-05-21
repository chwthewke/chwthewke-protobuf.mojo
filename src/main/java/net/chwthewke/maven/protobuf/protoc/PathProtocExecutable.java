package net.chwthewke.maven.protobuf.protoc;

import java.nio.file.Path;
import java.nio.file.Paths;

class PathProtocExecutable implements ProtocExecutable {

    @Override
    public boolean collectChanges( ) {
        return false;
    }

    @Override
    public Path getPath( ) {
        return Paths.get( "protoc" );
    }

    @Override
    public String toString( ) {
        return getClass( ).getSimpleName( );
    }

}
