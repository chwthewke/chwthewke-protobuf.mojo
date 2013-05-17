package net.chwthewke.maven.protobuf.source;

import java.nio.file.Paths;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.model.Dependency;

public class ProtocolSourceFactory {

    public ProtocolSource sourceDirectory( final String directory ) {
        return new DirectoryProtocolSource( serviceProvider, Paths.get( directory ) );
    }

    public ProtocolSource includeDirectory( final String directory ) {
        return new DirectoryProtocolInclude( serviceProvider, Paths.get( directory ) );
    }

    public ProtocolSource protocolSourceDependency( final Dependency dependency ) {
        return new DependencyProtocolSource( serviceProvider, dependency, true );
    }

    public ProtocolSource protocolDependency( final Dependency dependency ) {
        return new DependencyProtocolSource( serviceProvider, dependency, false );
    }

    public ProtocolSourceFactory( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private final ServiceProvider serviceProvider;
}
