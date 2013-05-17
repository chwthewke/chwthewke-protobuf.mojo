package net.chwthewke.maven.protobuf;

import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_CLASSIFIER;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_CLASSIFIER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import net.chwthewke.maven.protobuf.source.ProtocolSource;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import com.google.common.collect.ImmutableList;

public class ProtocolSourceArchiver {

    public void archiveProtocolSources( final List<ProtocolSource> sources ) throws MojoExecutionException {

        archiveAndAttachDirectories( sourceDirectories( sources ), PROTO_SOURCE_ARCHIVE, PROTO_SOURCE_CLASSIFIER );
        archiveAndAttachDirectories(
            includeDirectories( sources ), PROTO_DEPENDENCIES_ARCHIVE, PROTO_DEPENDENCIES_CLASSIFIER );

    }

    public ProtocolSourceArchiver( final ServiceProvider serviceProvider, final ArchiverManager archiverManager ) {
        this.serviceProvider = serviceProvider;
        this.archiverManager = archiverManager;
    }

    private void archiveAndAttachDirectories( final List<Path> directories, final Path archive,
            final String artifactClassifier ) throws MojoExecutionException {

        archiveDirectories( directories, archive );

        serviceProvider.getProjectHelper( )
            .attachArtifact( serviceProvider.getProject( ), "jar", artifactClassifier, archive.toFile( ) );
    }

    private ImmutableList<Path> sourceDirectories( final List<ProtocolSource> sources ) {
        final ImmutableList.Builder<Path> builder = ImmutableList.builder( );

        for ( final ProtocolSource protocolSource : sources )
            builder.addAll( protocolSource.getSourcePaths( ) );

        return builder.build( );
    }

    private ImmutableList<Path> includeDirectories( final List<ProtocolSource> sources ) {
        final ImmutableList.Builder<Path> builder = ImmutableList.builder( );

        for ( final ProtocolSource protocolSource : sources )
            builder.addAll( protocolSource.getIncludeOnlyPaths( ) );

        return builder.build( );
    }

    private void archiveDirectories( final List<Path> directories, final Path archive ) throws MojoExecutionException {

        try
        {
            final Archiver archiver = archiverManager.getArchiver( "jar" );
            archiver.setIncludeEmptyDirs( true );
            archiver.setDestFile( projectPath( archive ).toFile( ) );

            for ( final Path sourceDirectory : directories )
                archiver.addDirectory( projectPath( sourceDirectory ).toFile( ) );

            if ( directories.isEmpty( ) )
            {
                final Path dummyDependenciesDir = projectPath( PluginConstants.PLUGIN_WORK.resolve( "dummy-dependencies" ) );
                final Path dummyDependenciesFile = dummyDependenciesDir.resolve( "0" );
                if ( !Files.exists( dummyDependenciesFile ) )
                {
                    Files.createDirectories( dummyDependenciesDir );
                    Files.createFile( dummyDependenciesFile );
                }
                archiver.addDirectory( dummyDependenciesDir.toFile( ) );
            }

            archiver.createArchive( );

        }
        catch ( NoSuchArchiverException | ArchiverException | IOException e )
        {
            throw new MojoExecutionException( "Failed to archive protocol directories " + directories, e );
        }
    }

    private Path projectPath( final Path sourceDirectory ) {
        return serviceProvider.getBasedir( ).resolve( sourceDirectory );
    }

    private final ServiceProvider serviceProvider;
    private final ArchiverManager archiverManager;
}
