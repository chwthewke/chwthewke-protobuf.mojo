package net.chwthewke.maven.protobuf;

import java.lang.reflect.Array;
import java.nio.file.Paths;

import javax.annotation.Nullable;

import net.chwthewke.maven.protobuf.plugins.ProtocPlugin;
import net.chwthewke.maven.protobuf.plugins.ProtocPluginDefinition;
import net.chwthewke.maven.protobuf.plugins.ProtocPluginFactory;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutable;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutableFactory;
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
     * @parameter
     */
    private String[ ] protoPaths;

    /**
     * The plugins to execute with their respective output directories.
     * 
     * @parameter
     */
    private ProtocPluginDefinition[ ] protocPlugins;

    /**
     * @parameter
     */
    private Dependency[ ] protocolDependencies;

    /**
     * @parameter
     */
    private Dependency[ ] protocolSourceDependencies;

    /**
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

    private ServiceProvider serviceProvider;

    /*
     * MOJO EXECUTION
     */

    @Override
    public void execute( ) throws MojoExecutionException, MojoFailureException {

        serviceProvider = Services.serviceProvider(
            project, this, archiverManager, artifactResolver, artifactFactory, localRepository );

        final ProtocRequest request = createProtocolRequest( );

        request.processRequirements( );
    }

    private ProtocRequest createProtocolRequest( ) {
        final ImmutableList<ProtocolSource> protocolSources = getProtocolSources( );
        getLog( ).debug( "[PROTOC] Sources: " + protocolSources );

        final ImmutableList<ProtocPlugin> plugins = getPlugins( );
        getLog( ).debug( "[PROTOC] Plugins: " + plugins );

        final ProtocExecutable executable = getProtocExecutable( );
        getLog( ).debug( "[PROTOC] Executable: " + executable );

        return new ProtocRequest( protocolSources, plugins, executable );
    }

    private ProtocExecutable getProtocExecutable( ) {
        return new ProtocExecutableFactory( serviceProvider ).createFor( Optional.fromNullable( protocExecutable ) );
    }

    private ImmutableList<ProtocolSource> getProtocolSources( ) {
        final ProtocolSourceFactory protocolSourceFactory = new ProtocolSourceFactory( serviceProvider );
        final ImmutableList.Builder<ProtocolSource> sourcesBuilder = ImmutableList.builder( );

        for ( final String sourceDirectory : sourceDirectoriesWithDefault( ) )
            sourcesBuilder.add( protocolSourceFactory.sourceDirectory( sourceDirectory ) );
        for ( final String includeDirectory : emptyIfNull( protoPaths, String.class ) )
            sourcesBuilder.add( protocolSourceFactory.includeDirectory( includeDirectory ) );
        for ( final Dependency protocolSourceDependency : emptyIfNull( protocolSourceDependencies, Dependency.class ) )
            sourcesBuilder.add( protocolSourceFactory.protocolSourceDependency( protocolSourceDependency ) );
        for ( final Dependency protocolDependency : emptyIfNull( protocolDependencies, Dependency.class ) )
            sourcesBuilder.add( protocolSourceFactory.protocolDependency( protocolDependency ) );

        return sourcesBuilder.build( );
    }

    private String[ ] sourceDirectoriesWithDefault( ) {
        return withDefault( emptyIfNull( sourceDirectories, String.class ),
            Paths.get( "src", "main", "proto" ).toString( ) );
    }

    private ImmutableList<ProtocPlugin> getPlugins( ) {
        final ProtocPluginFactory pluginFactory = new ProtocPluginFactory( serviceProvider );
        final ImmutableList.Builder<ProtocPlugin> pluginsBuilder = ImmutableList.builder( );

        for ( final ProtocPluginDefinition pluginDefinition : pluginsWithDefault( ) )
            pluginsBuilder.add( pluginFactory.createFor( pluginDefinition ) );

        return pluginsBuilder.build( );

    }

    private ProtocPluginDefinition[ ] pluginsWithDefault( ) {
        return withDefault( emptyIfNull( protocPlugins, ProtocPluginDefinition.class ),
            new ProtocPluginDefinition( "java" ) );
    }

    @SuppressWarnings( "unchecked" )
    private static <E> E[ ] emptyIfNull( @Nullable final E[ ] array, final Class<E> componentType ) {
        return array == null ? (E[ ]) Array.newInstance( componentType, 0 ) : array;
    }

    private static <E> E[ ] withDefault( final E[ ] array, final E defaultValue ) {
        if ( array.length > 0 )
            return array;
        @SuppressWarnings( "unchecked" )
        final E[ ] defaultArray = (E[ ]) Array.newInstance( defaultValue.getClass( ), 1 );
        defaultArray[ 0 ] = defaultValue;
        return defaultArray;
    }
}
