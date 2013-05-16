package net.chwthewke.maven.protobuf.source;

import java.nio.file.Path;
import java.util.List;

import net.chwthewke.maven.protobuf.services.Args;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Arg;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

abstract class AbstractProtocolSource implements ProtocolSource {

    @Override
    public List<Arg> includeArgs( ) {

        final ImmutableList.Builder<Arg> builder = ImmutableList.builder( );

        if ( compileSources )
            builder.add( includeArg( getSourcePath( ) ) );

        for ( final Path includePath : getIncludesPath( ).asSet( ) )
        {
            builder.add( includeArg( includePath ) );
        }

        return builder.build( );
    }

    @Override
    public List<Arg> sourcesArgs( ) {
        if ( !compileSources )
            return ImmutableList.of( );

        final FileSetManager fileSetManager = new FileSetManager( serviceProvider.getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( serviceProvider.getBasedir( ).toString( ) );
        fs.addInclude( getSourcePath( ).resolve( "**" ).toString( ) );

        final ImmutableList<String> sources = ImmutableList.copyOf( fileSetManager.getIncludedFiles( fs ) );

        serviceProvider.getLog( ).debug( String.format( "Source files: %s", sources ) );

        return FluentIterable.from( sources )
            .transform( new Function<String, Arg>( ) {
                @Override
                public Arg apply( final String arg ) {
                    return Args.of( arg );
                }
            } )
            .toList( );
    }

    protected abstract Path getSourcePath( );

    protected abstract Optional<Path> getIncludesPath( );

    protected AbstractProtocolSource( final ServiceProvider serviceProvider, final boolean compileSources ) {
        this.serviceProvider = serviceProvider;
        this.compileSources = compileSources;
    }

    protected final ServiceProvider serviceProvider;
    protected final boolean compileSources;

    private Arg includeArg( final Path includePath ) {
        return Args.of( String.format( "-I%s", includePath ) );
    }

}
