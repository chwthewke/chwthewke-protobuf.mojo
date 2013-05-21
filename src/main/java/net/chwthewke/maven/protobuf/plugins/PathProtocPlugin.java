package net.chwthewke.maven.protobuf.plugins;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.plugin.MojoExecutionException;

import com.google.common.base.Optional;

class PathProtocPlugin extends AbstractProtocPlugin {

    @Override
    protected Optional<Path> locateExecutable( ) throws MojoExecutionException {
        final Path executable = Paths.get( pluginDefinition.getExecutable( ) );
        return Optional.of( findExecutableByOs( executable.getParent( ), executable.getFileName( ).toString( ) ) );
    }

    @Override
    protected boolean resolvePlugin( ) {
        return false;
    }

    PathProtocPlugin( final ServiceProvider serviceProvider, final ProtocPluginDefinition pluginDefinition ) {
        super( serviceProvider, pluginDefinition );
    }

}
