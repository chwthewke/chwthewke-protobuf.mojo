package net.chwthewke.maven.protobuf.source;

import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_CLASSIFIER;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_ARCHIVE;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_CLASSIFIER;

import java.nio.file.Path;
import java.util.List;

import net.chwthewke.maven.protobuf.services.Dependencies;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

class DependencyProtocolSource extends AbstractProtocolSource {

    private final boolean compileSources;

    @Override
    public boolean collectChanges( ) throws MojoExecutionException {
        serviceProvider.getLog( )
            .info( String.format( "Resolving protocol dependency %s, compile sources: %s",
                dependency, compileSources ) );

        boolean hasChanged = false;

        if ( compileSources )
            hasChanged |= extractDependency( PROTO_SOURCE_CLASSIFIER, sourcePath( ), PROTO_SOURCE_ARCHIVE );
        hasChanged |= extractDependency( PROTO_DEPENDENCIES_CLASSIFIER, includesPath( ), PROTO_DEPENDENCIES_ARCHIVE );
        hasChanged |= extractDependency( PROTO_SOURCE_CLASSIFIER, includesPath( ), PROTO_SOURCE_ARCHIVE );

        return hasChanged;
    }

    @Override
    protected Optional<Path> getSourcePath( ) {
        if ( !compileSources )
            return Optional.absent( );

        return Optional.of( sourcePath( ) );
    }

    @Override
    protected List<Path> getAdditionalIncludesPath( ) {
        return ImmutableList.of( includesPath( ) );
    }

    public DependencyProtocolSource( final ServiceProvider serviceProvider, final Dependency dependency,
            final boolean compileSources ) {
        super( serviceProvider, compileSources );
        this.dependency = dependency;
        this.compileSources = compileSources;
    }

    private boolean extractDependency( final String classifier, final Path targetPath, final Path archivePathInProject )
            throws MojoExecutionException {

        final Dependency actualDependency = Dependencies.copyWithClassifier( dependency, classifier );

        final Artifact artifact = serviceProvider.getDependencyResolver( )
            .resolveDependency( actualDependency, archivePathInProject );

        return serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, targetPath );
    }

    private Path sourcePath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "sources" ).resolve( dependency.getArtifactId( ) );
    }

    private Path includesPath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "dependencies" ).resolve( dependency.getArtifactId( ) );
    }

    private final Dependency dependency;

}
