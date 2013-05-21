package net.chwthewke.maven.protobuf.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

class DefaultArtifactExtractor implements ArtifactExtractor {

    public DefaultArtifactExtractor( final Mojo mojo, final MavenProject project,
            final ArchiverManager archiverManager,
            final IncrementalBuildHelper incrementalBuildHelper ) {
        this.mojo = mojo;
        this.project = project;
        this.archiverManager = archiverManager;
        this.incrementalBuildHelper = incrementalBuildHelper;
    }

    @Override
    public boolean extractArtifact( final Artifact artifact, final Path path ) throws MojoExecutionException {
        if ( !incrementalBuildHelper.hasDependencyArchiveChanged( artifact, path ) )
        {
            mojo.getLog( )
                .debug( String.format( "Not extracting %s to %s, directory is up-to-date.",
                    artifact.getFile( ).toPath( ), path ) );
            return false;
        }

        final Path extractPath = project.getBasedir( ).toPath( ).resolve( path );

        mojo.getLog( ).debug( String.format( "Extracting %s to %s.", artifact.getFile( ).toPath( ), path ) );

        try
        {
            Files.createDirectories( extractPath );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( "Unable to create directory to extract artifact", e );
        }

        final String type = artifact.getType( );
        final File file = artifact.getFile( );

        extractFile( file, type, extractPath );

        return true;
    }

    private void extractFile( final File file, final String type, final Path extractPath )
            throws MojoExecutionException {
        try
        {
            final UnArchiver unarchiver = archiverManager.getUnArchiver( type );
            unarchiver.setSourceFile( file );
            unarchiver.setDestDirectory( extractPath.toFile( ) );
            unarchiver.setFileSelectors( new FileSelector[ ] { new FileSelector( ) {

                @Override
                public boolean isSelected( final FileInfo fileInfo ) {
                    // TODO PluginConstants constant for dummy file
                    return !fileInfo.getName( ).startsWith( "META-INF" ) && !fileInfo.getName( ).equals( "0" );
                }
            } } );

            unarchiver.extract( );
        }
        catch ( final NoSuchArchiverException e )
        {
            throw new MojoExecutionException( String.format( "Cannot unpack file %s.", file ), e );
        }
        catch ( final ArchiverException e )
        {
            throw new MojoExecutionException( String.format( "Failed to unpack file %s.", file ), e );
        }
    }

    private final Mojo mojo;
    private final MavenProject project;
    private final IncrementalBuildHelper incrementalBuildHelper;

    private final ArchiverManager archiverManager;

}
