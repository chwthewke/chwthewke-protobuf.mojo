package net.chwthewke.maven.protobuf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

public class ArtifactExtractor {

    public ArtifactExtractor( final ArchiverManager archiverManager ) {
        this.archiverManager = archiverManager;
    }

    public void extractArtifact( final Artifact artifact, final Path path ) throws MojoExecutionException {
        try
        {
            Files.createDirectories( path );
        }
        catch ( final IOException e )
        {
            throw new MojoExecutionException( "Unable to create directory to extract artifact", e );
        }

        final String type = artifact.getType( );
        final File file = artifact.getFile( );

        extractFile( file, type, path );
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
                    return !fileInfo.getName( ).startsWith( "META-INF" );
                }
            } } );

            //getLog( ).info( "Unarchiver: " + unarchiver );

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

    private final ArchiverManager archiverManager;

}
