package net.chwthewke.maven.protobuf.plugins;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

public class ProtocPluginFactory {

    public ProtocPlugin createFor( final ProtocPluginDefinition definition ) {

        if ( definition.getExecutable( ) == null )
            return new DefaultProtocPlugin( serviceProvider, definition, testCompile );

        if ( definition.getDependency( ) == null )
            return new PathProtocPlugin( serviceProvider, definition, testCompile );

        return new DependencyProtocPlugin( serviceProvider, definition, testCompile );

    }

    public ProtocPluginFactory( final ServiceProvider serviceProvider, boolean testCompile ) {
        this.serviceProvider = serviceProvider;
        this.testCompile = testCompile;
    }

    private final ServiceProvider serviceProvider;
    private final boolean testCompile;
}
