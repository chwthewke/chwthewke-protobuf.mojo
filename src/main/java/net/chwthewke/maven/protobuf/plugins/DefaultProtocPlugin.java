package net.chwthewke.maven.protobuf.plugins;

import java.nio.file.Path;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import com.google.common.base.Optional;

class DefaultProtocPlugin extends AbstractProtocPlugin {

    @Override
    protected Optional<Path> locateExecutable( ) {
        return Optional.absent( );
    }

    DefaultProtocPlugin( final ServiceProvider serviceProvider, final ProtocPluginDefinition pluginDefinition ) {
        super( serviceProvider, pluginDefinition );
    }

}
