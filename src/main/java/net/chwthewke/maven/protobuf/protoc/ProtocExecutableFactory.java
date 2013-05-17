package net.chwthewke.maven.protobuf.protoc;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.model.Dependency;

import com.google.common.base.Optional;

public class ProtocExecutableFactory {

    public ProtocExecutable createFor( final Optional<Dependency> dependencyOption ) {
        if ( dependencyOption.isPresent( ) )
            return new DependencyProtocExecutable( serviceProvider, dependencyOption.get( ) );
        return new PathProtocExecutable( );
    }

    public ProtocExecutableFactory( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private final ServiceProvider serviceProvider;
}
