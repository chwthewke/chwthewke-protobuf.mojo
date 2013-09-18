package net.chwthewke.maven.protobuf.source;

import net.chwthewke.maven.protobuf.services.Dependencies;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import java.nio.file.Path;

import static net.chwthewke.maven.protobuf.services.PluginConstants.*;

class PackagedDependencyProtocolSource extends AbstractDependencyProtocolSource {

    @Override
    public boolean collectChanges( ) throws MojoExecutionException {
        serviceProvider.getLog( )
            .info( String.format( "Resolving protocol dependency %s, compile sources: %s",
                dependency, compileSources ) );

        boolean hasChanged = false;

        if ( compileSources )
            hasChanged |= extractDependency( PROTO_SOURCE_CLASSIFIER, sourcePath( ) );
        hasChanged |= extractDependency( PROTO_DEPENDENCIES_CLASSIFIER, includesPath( ) );
        hasChanged |= extractDependency( PROTO_SOURCE_CLASSIFIER, includesPath( ) );

        return hasChanged;
    }

    public PackagedDependencyProtocolSource( final ServiceProvider serviceProvider, final Dependency dependency,
            final boolean compileSources ) {
        super( serviceProvider, dependency, compileSources );
    }

    private boolean extractDependency( final String classifier, final Path targetPath )
            throws MojoExecutionException {

        final String fullClassifier = dependency.getClassifier( ) + classifier;

        final Dependency actualDependency =
                Dependencies.copyWithClassifier( dependency, fullClassifier );

        final Path archivePathInProject = PLUGIN_WORK.resolve( fullClassifier + ".jar" );

        final Artifact artifact = serviceProvider.getDependencyResolver( )
            .resolveDependency( actualDependency, archivePathInProject );

        return serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, targetPath );
    }

}
