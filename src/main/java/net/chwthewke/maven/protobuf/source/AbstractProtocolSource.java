package net.chwthewke.maven.protobuf.source;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import net.chwthewke.maven.protobuf.services.Args;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.util.cli.Arg;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

abstract class AbstractProtocolSource implements ProtocolSource {

    @Override
    public Iterable<Path> getSourcePaths( ) {
        return getSourcePath( ).asSet( );
    }

    @Override
    public Iterable<Path> getIncludeOnlyPaths( ) {
        return getAdditionalIncludesPath( );
    }

    @Override
    public List<Arg> includeArgs( ) {

        final ImmutableList.Builder<Arg> builder = ImmutableList.builder( );

        for ( final Path sourcePath : getSourcePath( ).asSet( ) )
            builder.add( includeArg( sourcePath ) );
        for ( final Path includePath : getAdditionalIncludesPath( ) )
            builder.add( includeArg( includePath ) );

        return builder.build( );
    }

    @Override
    public List<Arg> sourcesArgs( ) {
        if ( !getSourcePath( ).isPresent( ) )
            return ImmutableList.of( );

        final FileSetManager fileSetManager = new FileSetManager( serviceProvider.getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( serviceProvider.getBasedir( ).toString( ) );
        fs.addInclude( getSourcePath( ).get( ).toString( ) + File.separatorChar + "**" );

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

    @Override
    public String toString( ) {
        return String.format( "%s source=<%s> includes=<%s>",
            getClass( ).getSimpleName( ),
            getSourcePath( ), getAdditionalIncludesPath( ) );
    }

    protected abstract Optional<Path> getSourcePath( );

    protected abstract List<Path> getAdditionalIncludesPath( );

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
