package net.chwthewke.maven.protobuf.source;

import java.nio.file.Path;
import java.util.List;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

class DirectoryProtocolSource extends AbstractProtocolSource {

    public DirectoryProtocolSource( final ServiceProvider serviceProvider, final Path path ) {
        super( serviceProvider, true );
        this.path = path;
    }

    @Override
    public boolean collectChanges( ) {
        serviceProvider.getLog( )
            .info( String.format( "Adding protocol sources from %s.", path ) );
        return serviceProvider.getIncrementalBuildHelper( ).hasDirectoryChanged( path );
    }

    @Override
    protected Optional<Path> getSourcePath( ) {
        return Optional.of( path );
    }

    @Override
    protected List<Path> getAdditionalIncludesPath( ) {
        return ImmutableList.of( );
    }

    private final Path path;

}
