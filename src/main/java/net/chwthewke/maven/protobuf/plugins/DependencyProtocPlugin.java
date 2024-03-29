package net.chwthewke.maven.protobuf.plugins;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.StringUtils;

import java.nio.file.Path;

class DependencyProtocPlugin extends AbstractProtocPlugin {

    @Override
    protected boolean resolvePlugin( ) throws MojoExecutionException {

        final Artifact artifact = serviceProvider.getDependencyResolver( )
            .resolveDependency( pluginDefinition.getDependency( ), PLUGIN_ARCHIVE_PATH_IN_PROJECT );

        return serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, executableDir( ) );
    }

    @Override
    protected Optional<Path> locateExecutable( ) throws MojoExecutionException {
        return Optional.of( findExecutableByOs( executableDir( ), pluginDefinition.getExecutable( ) ) );
    }

    DependencyProtocPlugin( final ServiceProvider serviceProvider, final ProtocPluginDefinition pluginDefinition,
            final boolean testCompile ) {
        super( serviceProvider, pluginDefinition, testCompile );
    }

    private Path executableDir( ) {
        return PluginConstants.PROTOC_PLUGINS.resolve( pluginDefinition.getPlugin( ) );
    }

    private static Function<Artifact, Path> PLUGIN_ARCHIVE_PATH_IN_PROJECT =
            new Function<Artifact, Path>( ) {
                @Override
                public Path apply( final Artifact artifact ) {
                    final StringBuilder archiveNameBuilder = new StringBuilder( );

                    archiveNameBuilder
                        .append( artifact.getArtifactId( ) )
                        .append( "-" )
                        .append( artifact.getVersion( ) );
                    if ( !StringUtils.isEmpty( artifact.getClassifier( ) ) )
                        archiveNameBuilder
                            .append( "-" )
                            .append( artifact.getClassifier( ) );
                    archiveNameBuilder.append( "." )
                        .append( artifact.getType( ) );
                    return PluginConstants.TARGET.resolve( archiveNameBuilder.toString( ) );
                }
            };

}
