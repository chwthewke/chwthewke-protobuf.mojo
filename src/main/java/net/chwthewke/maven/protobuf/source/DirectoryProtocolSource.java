package net.chwthewke.maven.protobuf.source;

import java.nio.file.Path;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import com.google.common.base.Optional;

public class DirectoryProtocolSource extends AbstractProtocolSource {

    public DirectoryProtocolSource( final ServiceProvider serviceProvider, final Path path ) {
        super( serviceProvider, true );
        this.path = path;
    }

    @Override
    public void resolve( ) {
    }

    @Override
    protected Path getSourcePath( ) {
        return path;
    }

    @Override
    protected Optional<Path> getIncludesPath( ) {
        return Optional.absent( );
    }

    private final Path path;

}
