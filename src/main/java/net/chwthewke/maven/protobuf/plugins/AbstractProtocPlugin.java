package net.chwthewke.maven.protobuf.plugins;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.chwthewke.maven.protobuf.services.Args;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Arg;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

abstract class AbstractProtocPlugin implements ProtocPlugin {

    @Override
    public List<Arg> args( ) throws MojoExecutionException {
        final ImmutableList.Builder<Arg> args = ImmutableList.builder( );

        final Optional<Path> executableOption = getExecutable( );
        if ( executableOption.isPresent( ) )
            args.add( Args.of( String.format( "--plugin=protoc-gen-%s=%s", getPlugin( ), executableOption.get( ) ) ) );

        args.add( Args.of( String.format( "--%s_out=%s",
            getPlugin( ), getOutputDirectory( ) ) ) );

        return args.build( );
    }

    @Override
    public void resolve( ) throws MojoExecutionException {
        try
        {
            createOutputDirectory( );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException(
                String.format( "Could not create output directory %s.",
                    getOutputDirectory( ).toAbsolutePath( ) ) );
        }
    }

    @Override
    public Path getOutputDirectory( ) {
        if ( pluginDefinition.getOutputDirectory( ) != null )
            return Paths.get( pluginDefinition.getOutputDirectory( ) );
        return PluginConstants.GENERATED_SOURCES_BASE.resolve( getPlugin( ) );
    }

    @Override
    public boolean addToSources( ) {
        final Boolean addToSources = pluginDefinition.addToSources( );
        return addToSources != null ? addToSources : "java".equals( getPlugin( ) );
    }

    @Override
    public String toString( ) {
        return String.format( "%s %s", getClass( ).getSimpleName( ), pluginDefinition );
    }

    protected String getPlugin( ) {
        return pluginDefinition.getPlugin( );
    }

    protected Path findExecutableByOs( final Path dir, final String baseName ) throws MojoExecutionException {
        return findExecutable( dir, baseName );
    }

    protected Optional<Path> getExecutable( ) throws MojoExecutionException {
        if ( executable == null )
            executable = locateExecutable( );
        return executable;
    }

    protected abstract Optional<Path> locateExecutable( ) throws MojoExecutionException;

    protected AbstractProtocPlugin( final ServiceProvider serviceProvider, final ProtocPluginDefinition pluginDefinition ) {
        this.serviceProvider = serviceProvider;
        this.pluginDefinition = pluginDefinition;

        checkArgument(
            checkNotNull( pluginDefinition.getPlugin( ), "protocPlugin 'plugin' cannot be null." ).length( ) > 0,
            "protocPlugin 'plugin' cannot be empty." );
    }

    protected final ProtocPluginDefinition pluginDefinition;
    protected final ServiceProvider serviceProvider;

    private void createOutputDirectory( ) throws IOException {
        Files.createDirectories( getOutputDirectory( ) );
    }

    private Path findExecutable( final Path directory, final String basename ) throws MojoExecutionException {
        final Collection<String> extensions = executableExtentionsByOs( );

        for ( final String ext : extensions )
        {
            final String execFilename = basename + ext;
            final Path potentialExecutable = directory.resolve( execFilename );

            if ( Files.isRegularFile( potentialExecutable ) )
            {
                if ( Files.isExecutable( potentialExecutable ) )
                    return potentialExecutable;

                serviceProvider.getLog( ).warn( String.format( "Found non-executable file %s in dir %s.",
                    potentialExecutable, directory ) );
            }
        }
        throw new MojoExecutionException(
            String.format( "Could not find executable %s in dir %s", basename, directory ) );
    }

    private Optional<Path> executable;

    private static List<String> executableExtentionsByOs( ) {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            return WINDOWS_EXE;
        if ( Os.isFamily( Os.FAMILY_UNIX ) )
            return LINUX_EXE;
        return Collections.<String>emptyList( );
    }

    private static final ImmutableList<String> LINUX_EXE = ImmutableList.<String>of( "", ".sh" );

    private static final ImmutableList<String> WINDOWS_EXE = ImmutableList.<String>of( ".exe", ".bat", ".cmd" );

}
