package net.chwthewke.maven.protobuf.services;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;

import java.nio.file.Path;

class DefaultArchiveExtractor implements ArchiveExtractor {

    @Override
    public boolean extractArchive( final Path archive, final Path target ) throws MojoExecutionException {
        if ( !incrementalBuildHelper.hasFileChanged( archive, target ) )
            return false;

        try
        {
            // TODO duplication with DefaultArtifactExtractor
            final UnArchiver unarchiver = archiverManager.getUnArchiver( archive.toFile( ) );
            unarchiver.setSourceFile( archive.toFile( ) );
            unarchiver.setDestDirectory( target.toFile( ) );
            unarchiver.setFileSelectors( new FileSelector[ ] { new FileSelector( ) {

                @Override
                public boolean isSelected( final FileInfo fileInfo ) {
                    // TODO PluginConstants constant for dummy file
                    return !fileInfo.getName( ).startsWith( "META-INF" ) && !fileInfo.getName( ).equals( "0" );
                }
            } } );

            mojo.getLog( ).info( String.format( "Extracting archive %s to %s", archive, target ) );

            unarchiver.extract( );

            return true;

        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Unable to extract archive", e );
        }

    }

    DefaultArchiveExtractor( final Mojo mojo,
            final IncrementalBuildHelper incrementalBuildHelper,
            final ArchiverManager archiverManager ) {
        this.mojo = mojo;
        this.incrementalBuildHelper = incrementalBuildHelper;
        this.archiverManager = archiverManager;
    }

    private final Mojo mojo;
    private final IncrementalBuildHelper incrementalBuildHelper;
    private final ArchiverManager archiverManager;
}
