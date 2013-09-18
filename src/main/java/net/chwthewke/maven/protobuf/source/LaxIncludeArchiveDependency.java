package net.chwthewke.maven.protobuf.source;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class LaxIncludeArchiveDependency extends AbstractProtocolSource {

    LaxIncludeArchiveDependency( ServiceProvider serviceProvider, Path archive ) {
        super( serviceProvider, false );
        this.archive = archive;
        dependencyPath = dependencyPath( );
    }

    @Override
    protected Optional<Path> getSourcePath( ) {
        return Optional.absent( );
    }

    @Override
    protected List<Path> getAdditionalIncludesPath( ) {
        return ImmutableList.of( dependencyPath( ) );
    }

    @Override
    public boolean collectChanges( ) throws MojoExecutionException {
        if ( !Files.exists( archive ) )
        {
            serviceProvider.getLog( ).info( "Optional archive " + archive + " missing, skipping." );
            return false;
        }

        try
        {
            Files.createDirectories( dependencyPath );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Unable to create directory " + dependencyPath, e );
        }

        return serviceProvider.getArchiveExtractor( )
            .extractArchive( archive, dependencyPath );
    }

    private Path projectPath( final Path sourceDirectory ) {
        return serviceProvider.getBasedir( ).resolve( sourceDirectory );
    }

    private Path dependencyPath( ) {
        return projectPath( PluginConstants.PLUGIN_WORK ).resolve( "dependencies" ).resolve( archive.getFileName( ) );
    }

    private final Path archive;
    private final Path dependencyPath;
}
