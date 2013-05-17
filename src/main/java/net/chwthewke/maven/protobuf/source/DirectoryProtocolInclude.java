package net.chwthewke.maven.protobuf.source;

import java.nio.file.Path;
import java.util.List;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class DirectoryProtocolInclude extends AbstractProtocolSource {

    private final Path path;

    @Override
    public void resolve( ) {
        serviceProvider.getLog( )
            .info( String.format( "Adding protocol includes from %s.", path ) );
    }

    @Override
    protected Optional<Path> getSourcePath( ) {
        return Optional.absent( );
    }

    @Override
    protected List<Path> getIncludesPath( ) {
        return ImmutableList.of( path );
    }

    public DirectoryProtocolInclude( final ServiceProvider serviceProvider, final Path path ) {
        super( serviceProvider, false );
        this.path = path;
    }

}
