package net.chwthewke.maven.protobuf.source;

import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.model.Dependency;

import java.nio.file.Paths;

public class ProtocolSourceFactory {

    public ProtocolSource sourceDirectory( final String directory ) {
        return new DirectoryProtocolSource( serviceProvider, Paths.get( directory ) );
    }

    public ProtocolSource includeDirectory( final String directory ) {
        return new DirectoryProtocolInclude( serviceProvider, Paths.get( directory ) );
    }

    public ProtocolSource packagedSourceDependency( final Dependency dependency ) {
        return new PackagedDependencyProtocolSource( serviceProvider, dependency, true );
    }

    public ProtocolSource packagedIncludeDependency( final Dependency dependency ) {
        return new PackagedDependencyProtocolSource( serviceProvider, dependency, false );
    }

    public ProtocolSource sourceDependency( final Dependency dependency ) {
        return new DependencyProtocolSource( serviceProvider, dependency, true );
    }

    public ProtocolSource includeDependency( final Dependency dependency ) {
        return new DependencyProtocolSource( serviceProvider, dependency, false );
    }

    public ProtocolSourceFactory( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private final ServiceProvider serviceProvider;
}
