package net.chwthewke.maven.protobuf.plugins;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import net.chwthewke.maven.protobuf.services.Args;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Arg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.chwthewke.maven.protobuf.services.PluginConstants.GENERATED_SOURCES_BASE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.GENERATED_TEST_SOURCES_BASE;

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
    public final boolean collectChanges( ) throws MojoExecutionException {
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

        addGeneratedSourcesToBuildIfRequired( );

        return resolvePlugin( );
    }

    @Override
    public Path getOutputDirectory( ) {
        if ( pluginDefinition.getOutputDirectory( ) != null )
            return Paths.get( pluginDefinition.getOutputDirectory( ) );
        final Path baseDir = testCompile ? GENERATED_TEST_SOURCES_BASE : GENERATED_SOURCES_BASE;
        return baseDir.resolve( getPlugin( ) );
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

    protected abstract boolean resolvePlugin( ) throws MojoExecutionException;

    protected abstract Optional<Path> locateExecutable( ) throws MojoExecutionException;

    protected AbstractProtocPlugin( final ServiceProvider serviceProvider,
            final ProtocPluginDefinition pluginDefinition, boolean testCompile ) {
        this.serviceProvider = serviceProvider;
        this.pluginDefinition = pluginDefinition;
        this.testCompile = testCompile;

        checkArgument(
            checkNotNull( pluginDefinition.getPlugin( ), "protocPlugin 'plugin' cannot be null." ).length( ) > 0,
            "protocPlugin 'plugin' cannot be empty." );
    }

    protected final ProtocPluginDefinition pluginDefinition;
    protected final ServiceProvider serviceProvider;

    private void createOutputDirectory( ) throws IOException {
        serviceProvider.getLog( ).debug(
            String.format( "Creating output directory %s.", getOutputDirectory( ).toAbsolutePath( ) ) );
        Files.createDirectories( serviceProvider.getBasedir( ).resolve( getOutputDirectory( ) ) );
    }

    private boolean addToSources( ) {
        final Boolean addToSources = pluginDefinition.addToSources( );
        return addToSources != null ? addToSources : "java".equals( getPlugin( ) );
    }

    private void addGeneratedSourcesToBuildIfRequired( ) {
        if ( addToSources( ) )
        {
            if ( testCompile )
                serviceProvider.getProject( ).addTestCompileSourceRoot( getOutputDirectory( ).toString( ) );
            else
                serviceProvider.getProject( ).addCompileSourceRoot( getOutputDirectory( ).toString( ) );
        }
    }

    private Path findExecutable( final Path directory, final String basename ) throws MojoExecutionException {
        final Collection<String> extensions = executableExtentionsByOs( );

        final Path directoryInProject = serviceProvider.getBasedir( ).resolve( directory );

        for ( final String ext : extensions )
        {
            final String execFilename = basename + ext;
            final Path potentialExecutable = directoryInProject.resolve( execFilename );

            if ( Files.isRegularFile( potentialExecutable ) )
            {
                if ( Files.isExecutable( potentialExecutable ) )
                    return serviceProvider.getBasedir( ).relativize( potentialExecutable );

                serviceProvider.getLog( ).warn( String.format( "Found non-executable file %s in dir %s.",
                    potentialExecutable, directory ) );
            }
        }
        throw new MojoExecutionException(
            String.format( "Could not find executable %s in dir %s", basename, directory ) );
    }

    private Optional<Path> executable;
    private final boolean testCompile;

    private static List<String> executableExtentionsByOs( ) {
        return EXTENSIONS_BY_OS_FAMILY.get( Os.OS_FAMILY );
    }

    private static final ImmutableListMultimap<String, String> EXTENSIONS_BY_OS_FAMILY =
            ImmutableListMultimap.<String, String>builder( )
                .putAll( Os.FAMILY_WINDOWS, ".exe", ".bat", ".cmd" )
                .putAll( Os.FAMILY_UNIX, "", ".sh" )
                .build( );

}
