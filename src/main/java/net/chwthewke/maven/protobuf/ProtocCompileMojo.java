package net.chwthewke.maven.protobuf;

import java.nio.file.Paths;

import javax.annotation.Nullable;

import net.chwthewke.maven.protobuf.plugins.ProtocPlugin;
import net.chwthewke.maven.protobuf.plugins.ProtocPluginDefinition;
import net.chwthewke.maven.protobuf.plugins.ProtocPluginFactory;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutable;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutableFactory;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import net.chwthewke.maven.protobuf.services.Services;
import net.chwthewke.maven.protobuf.source.ProtocolSource;
import net.chwthewke.maven.protobuf.source.ProtocolSourceFactory;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal compile
 * @phase generate-sources
 */
public class ProtocCompileMojo extends AbstractMojo {

    /*
     * PARAMETERS
     */

    /**
     * The source directories of the protocol.
     * Default: <code>${basedir}/src/main/proto</code>
     * 
     * @parameter
     */
    private String[ ] sourceDirectories;

    /**
     * Additional directories of protocol sources to include
     * 
     * @parameter
     */
    private String[ ] protoPaths;

    /**
     * The plugins to execute with their respective output directories.
     * See <a href="apidocs/net/chwthewke/maven/protobuf/plugins/ProtocPluginDefinition.html">the javadoc</a> for
     * the parameters of ProtocPluginDefinition.
     * 
     * @parameter
     */
    private ProtocPluginDefinition[ ] protocPlugins;

    /**
     * Protocol whose sources are dependencies. The dependency must be a project built with this plugin.
     * 
     * @parameter
     */
    private Dependency[ ] protocolDependencies;

    /**
     * Protocols whose sources to compile (presumably with a different plugin than when first compiled).
     * The dependency must be a project built with this plugin. The dependencies of these protocols will
     * automatically be included.
     * 
     * @parameter
     */
    private Dependency[ ] protocolSourceDependencies;

    /**
     * The protocol executable dependency. To use the plugin you must make the dependency resolvable
     * to an archive with type tar.gz and classifier according to the required os ("windows", "linux_x86", or
     * "linux_amd64" are supported)
     * 
     * @parameter
     */
    private Dependency protocExecutable;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The Maven project to analyze.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /*
     * COMPONENTS
     */

    /**
     * @component
     * @readonly
     */
    private ArchiverManager archiverManager;

    /**
     * Maven ProjectHelper.
     * 
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * @component
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     * @readonly
     */
    private BuildContext buildContext;

    private ServiceProvider serviceProvider;

    /*
     * MOJO EXECUTION
     */

    @Override
    public void execute( ) throws MojoExecutionException, MojoFailureException {

        serviceProvider = Services.serviceProvider(
            project, this, projectHelper, buildContext, archiverManager,
            artifactResolver, artifactFactory, localRepository );

        final ProtocRequest request = createProtocolRequest( );

        if ( !request.collectChanges( ) )
        {
            getLog( ).info( "Incremental build and no changes, stopping." );
            return;
        }

        new ProtocRunner( serviceProvider ).runProtocSubprocess( request.execute( ) );

        new ProtocolSourceArchiver( serviceProvider, archiverManager )
            .archiveProtocolSources( request.getProtocolSources( ) );

        buildContext.refresh( serviceProvider.getBasedir( )
            .resolve( PluginConstants.GENERATED_SOURCES_BASE )
            .toFile( ) );
    }

    private ProtocRequest createProtocolRequest( ) {
        final ImmutableList<ProtocolSource> protocolSources = getProtocolSources( );
        getLog( ).debug( "Sources: " + protocolSources );

        final ImmutableList<ProtocPlugin> plugins = getPlugins( );
        getLog( ).debug( "Plugins: " + plugins );

        final ProtocExecutable executable = getProtocExecutable( );
        getLog( ).debug( "Executable: " + executable );

        return new ProtocRequest( serviceProvider, protocolSources, plugins, executable );
    }

    private ProtocExecutable getProtocExecutable( ) {
        return new ProtocExecutableFactory( serviceProvider ).createFor( Optional.fromNullable( protocExecutable ) );
    }

    private ImmutableList<ProtocolSource> getProtocolSources( ) {
        final ProtocolSourceFactory protocolSourceFactory = new ProtocolSourceFactory( serviceProvider );
        final ImmutableList.Builder<ProtocolSource> sourcesBuilder = ImmutableList.builder( );

        final ImmutableList<Dependency> protocolSourceDependenciesList = asList( protocolSourceDependencies );

        final ImmutableList<String> sourceDirectoriesList = asList( sourceDirectories );
        final ImmutableList<String> sourceDirectoriesListWithDefault =
                ( protocolSourceDependenciesList.isEmpty( ) && sourceDirectoriesList.isEmpty( ) ) ?
                        ImmutableList.of( Paths.get( "src", "main", "proto" ).toString( ) ) :
                        sourceDirectoriesList;

        for ( final String sourceDirectory : sourceDirectoriesListWithDefault )
            sourcesBuilder.add( protocolSourceFactory.sourceDirectory( sourceDirectory ) );

        for ( final String includeDirectory : asList( protoPaths ) )
            sourcesBuilder.add( protocolSourceFactory.includeDirectory( includeDirectory ) );

        for ( final Dependency protocolSourceDependency : protocolSourceDependenciesList )
            sourcesBuilder.add( protocolSourceFactory.protocolSourceDependency( protocolSourceDependency ) );
        for ( final Dependency protocolDependency : asList( protocolDependencies ) )
            sourcesBuilder.add( protocolSourceFactory.protocolDependency( protocolDependency ) );

        return sourcesBuilder.build( );
    }

    private ImmutableList<ProtocPlugin> getPlugins( ) {
        final ProtocPluginFactory pluginFactory = new ProtocPluginFactory( serviceProvider );
        final ImmutableList.Builder<ProtocPlugin> pluginsBuilder = ImmutableList.builder( );

        for ( final ProtocPluginDefinition pluginDefinition : pluginsWithDefault( ) )
            pluginsBuilder.add( pluginFactory.createFor( pluginDefinition ) );

        return pluginsBuilder.build( );

    }

    private ImmutableList<ProtocPluginDefinition> pluginsWithDefault( ) {
        final ImmutableList<ProtocPluginDefinition> pluginsList = asList( protocPlugins );
        if ( pluginsList.isEmpty( ) )
            return ImmutableList.of( new ProtocPluginDefinition( "java" ) );
        return pluginsList;
    }

    private static <E> ImmutableList<E> asList( @Nullable final E[ ] array ) {
        if ( array == null )
            return ImmutableList.of( );
        return ImmutableList.copyOf( array );
    }

}
