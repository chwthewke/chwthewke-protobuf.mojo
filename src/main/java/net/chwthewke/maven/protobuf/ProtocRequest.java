package net.chwthewke.maven.protobuf;

import java.nio.file.Path;

import org.codehaus.plexus.util.cli.Commandline;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public class ProtocRequest {

    public ProtocRequest( final ImmutableList<Path> sourceDirectories,
            final ImmutableList<Path> additionalSourceDirectories,
            final ImmutableList<ProtocPlugin> plugins ) {
        protocolSourceDirectories = sourceDirectoriesOf( sourceDirectories );
        this.additionalSourceDirectories = additionalSourceDirectories;
        this.plugins = plugins;
    }

    private static ImmutableList<ProtocolSourceDirectory> sourceDirectoriesOf(
            final ImmutableList<Path> sourceDirectories ) {
        return FluentIterable.from( sourceDirectories )
            .transform( new Function<Path, ProtocolSourceDirectory>( ) {
                @Override
                public ProtocolSourceDirectory apply( final Path path ) {
                    return new ProtocolSourceDirectory( path );
                }
            } )
            .toList( );
    }

    public Commandline execute( final ProtocExecutable executable ) {
        throw new UnsupportedOperationException( );
    }

    // Sources to compile
    private final ImmutableList<ProtocolSourceDirectory> protocolSourceDirectories;
    // Additional sources, not compiled
    private final ImmutableList<Path> additionalSourceDirectories;
    // Plugins to call protoc with
    private final ImmutableList<ProtocPlugin> plugins;
}
