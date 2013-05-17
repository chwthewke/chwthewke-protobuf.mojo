package net.chwthewke.maven.protobuf.plugins;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

public class ProtocPluginFactory {

    public ProtocPlugin createFor( final ProtocPluginDefinition definition ) {

        if ( definition.getExecutable( ) == null )
            return new DefaultProtocPlugin( serviceProvider, definition );

        if ( definition.getDependency( ) == null )
            return new PathProtocPlugin( serviceProvider, definition );

        return new DependencyProtocPlugin( serviceProvider, definition );

    }

    public ProtocPluginFactory( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private final ServiceProvider serviceProvider;
}
