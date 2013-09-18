package net.chwthewke.maven.protobuf;

import com.google.common.collect.ImmutableList;
import net.chwthewke.maven.protobuf.MojoType.SourceArchive;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import net.chwthewke.maven.protobuf.source.ProtocolSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProtocolSourceArchiver {

    public void archiveProtocolSources( final List<ProtocolSource> sources ) throws MojoExecutionException {

        archiveAndAttachDirectories( sourceDirectories( sources ), archiverClassifiers.getSourceArchive( ) );
        archiveAndAttachDirectories(
            includeDirectories( sources ), archiverClassifiers.getDependenciesArchive( ) );

    }

    public ProtocolSourceArchiver( final ServiceProvider serviceProvider,
            final ArchiverManager archiverManager,
            final MojoType archiverClassifiers ) {
        this.serviceProvider = serviceProvider;
        this.archiverManager = archiverManager;
        this.archiverClassifiers = archiverClassifiers;
    }

    private void archiveAndAttachDirectories( final List<Path> directories,
            final SourceArchive sourceArchive ) throws MojoExecutionException {

        final Path projectArchivePath = projectPath( sourceArchive.getPath( ) );
        archiveDirectories( directories, projectArchivePath );

        serviceProvider.getProjectHelper( )
            .attachArtifact(
                serviceProvider.getProject( ), "jar", sourceArchive.getClassifier( ), projectArchivePath.toFile( ) );
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
            archiver.setDestFile( archive.toFile( ) );

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

    // TODO duplicated in ProtocolSourceFactory.java
    private Path projectPath( final Path sourceDirectory ) {
        return serviceProvider.getBasedir( ).resolve( sourceDirectory );
    }

    private final ServiceProvider serviceProvider;
    private final ArchiverManager archiverManager;
    private final MojoType archiverClassifiers;
}
