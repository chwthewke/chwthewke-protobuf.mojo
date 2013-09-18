package net.chwthewke.maven.protobuf.plugins;

import com.google.common.base.Optional;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import java.nio.file.Path;

class DefaultProtocPlugin extends AbstractProtocPlugin {

    @Override
    protected Optional<Path> locateExecutable( ) {
        return Optional.absent( );
    }

    @Override
    protected boolean resolvePlugin( ) {
        return false;
    }

    DefaultProtocPlugin( final ServiceProvider serviceProvider, final ProtocPluginDefinition pluginDefinition,
                         final boolean testCompile ) {
        super( serviceProvider, pluginDefinition, testCompile);
    }

}
