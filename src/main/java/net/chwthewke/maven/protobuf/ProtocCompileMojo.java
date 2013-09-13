package net.chwthewke.maven.protobuf;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
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

import javax.annotation.Nullable;
import java.nio.file.Paths;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal compile
 * @phase generate-sources
 */
@SuppressWarnings( "unused" )
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
    private String[ ] includeDirectories;

    /**
     * The content of these dependencies' archives are added to the sources to be compiled by protoc.
     * 
     * @parameter
     */
    private Dependency[ ] sourceArchives;

    /**
     * The content of these dependencies' archives are added to the sources to be included by protoc.
     * 
     * @parameter
     */
    private Dependency[ ] includeArchives;

    /**
     * This is a shortcut to depend on a protocol built with this plugin (presumably when you want to
     * compile the same .proto with a different plugin)
     * Do not specify a classifier for the dependency, its -proto-sources.jar will be added to sources,
     * and its -proto-deps.jar to includes.
     * 
     * @parameter
     */
    private Dependency[ ] packagedSourceDependencies;

    /**
     * Protocol whose sources are dependencies. The dependency must be a project built with this plugin.
     * 
     * @parameter
     */
    private Dependency[ ] packagedIncludeDependencies;

    /**
     * The plugins to execute with their respective output directories.
     * See <a href="apidocs/net/chwthewke/maven/protobuf/plugins/ProtocPluginDefinition.html">the javadoc</a> for
     * the parameters of ProtocPluginDefinition.
     * 
     * @parameter
     */
    private ProtocPluginDefinition[ ] protocPlugins;

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

        final ImmutableList<Dependency> protocolSourceDependenciesList = asList( packagedSourceDependencies );

        final ImmutableList<String> sourceDirectoriesList = asList( sourceDirectories );
        final ImmutableList<String> sourceDirectoriesListWithDefault =
                ( protocolSourceDependenciesList.isEmpty( ) && sourceDirectoriesList.isEmpty( ) ) ?
                        ImmutableList.of( Paths.get( "src", "main", "proto" ).toString( ) ) :
                        sourceDirectoriesList;

        for ( final String sourceDirectory : sourceDirectoriesListWithDefault )
            sourcesBuilder.add( protocolSourceFactory.sourceDirectory( sourceDirectory ) );

        for ( final String includeDirectory : asList( includeDirectories ) )
            sourcesBuilder.add( protocolSourceFactory.includeDirectory( includeDirectory ) );

        for ( final Dependency protocolSourceDependency : protocolSourceDependenciesList )
            sourcesBuilder.add( protocolSourceFactory.packagedSourceDependency( protocolSourceDependency ) );
        for ( final Dependency protocolDependency : asList( packagedIncludeDependencies ) )
            sourcesBuilder.add( protocolSourceFactory.packagedIncludeDependency( protocolDependency ) );

        for ( final Dependency protocolSourceDependency : asList( sourceArchives ) )
            sourcesBuilder.add( protocolSourceFactory.sourceDependency( protocolSourceDependency ) );
        for ( final Dependency protocolIncludeDependency : asList( includeArchives ) )
            sourcesBuilder.add( protocolSourceFactory.includeDependency( protocolIncludeDependency ) );

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
