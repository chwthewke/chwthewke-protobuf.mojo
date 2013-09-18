package net.chwthewke.maven.protobuf.source;

import com.google.common.base.Function;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class DependencyProtocolSource extends AbstractDependencyProtocolSource {

    public DependencyProtocolSource( ServiceProvider serviceProvider, Dependency dependency, boolean compileSources ) {
        super( serviceProvider, dependency, compileSources );
    }

    @Override
    public boolean collectChanges( ) throws MojoExecutionException {
        serviceProvider.getLog( )
            .info( String.format( "Resolving protocol dependency %s, compile sources: %s",
                dependency, compileSources ) );

        boolean hasChanged = false;

        if ( compileSources )
            hasChanged |= extractDependency( sourcePath( ) );
        hasChanged |= extractDependency( includesPath( ) );

        return hasChanged;
    }

    private boolean extractDependency( final Path targetPath )
            throws MojoExecutionException {

        final Artifact artifact = serviceProvider.getDependencyResolver( )
            .resolveDependency( dependency, new Function<Artifact, Path>( ) {
                @Override
                public Path apply( final Artifact input ) {
                    return input.getFile( ).toPath( ).resolve( "target" ).resolve( "classes" );
                }
            } );

        if ( Files.isDirectory( artifact.getFile( ).toPath( ) ) )
        {
            try
            {
                FileUtils.copyDirectory( artifact.getFile( ), targetPath.toFile( ), "**/*.proto", "" );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( String.format( "Could not copy build output for %s.", artifact ), e );
            }

        }

        return serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, targetPath );
    }

}
