package net.chwthewke.maven.protobuf.source;

import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_CLASSIFIER;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_CLASSIFIER;

import java.nio.file.Path;

import net.chwthewke.maven.protobuf.services.Dependencies;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import com.google.common.base.Optional;

public class DependencyProtocolSource extends AbstractProtocolSource {

    @Override
    public void resolve( ) throws MojoExecutionException {
        if ( compileSources )
            extractDependency( PROTO_SOURCE_CLASSIFIER, getSourcePath( ), PROTO_SOURCE_ARCHIVE );
        extractDependency( PROTO_DEPENDENCIES_CLASSIFIER, includesPath( ), PROTO_DEPENDENCIES_ARCHIVE );
    }

    @Override
    public Path getSourcePath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "sources" ).resolve( dependency.getArtifactId( ) );
    }

    @Override
    protected Optional<Path> getIncludesPath( ) {
        return Optional.of( includesPath( ) );
    }

    public DependencyProtocolSource( final ServiceProvider serviceProvider, final Dependency dependency,
            final boolean compileSources ) {
        super( serviceProvider, compileSources );
        this.dependency = dependency;
    }

    private void extractDependency( final String classifier, final Path targetPath, final Path archivePathInProject )
            throws MojoExecutionException {

        final Dependency actualDependency = Dependencies.copyWithClassifier( dependency, classifier );

        final Artifact artifact = serviceProvider.getDependencyResolver( )
            .resolveDependency( actualDependency, archivePathInProject );

        serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, targetPath );
    }

    private Path includesPath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "dependencies" ).resolve( dependency.getArtifactId( ) );
    }

    private final Dependency dependency;

}
