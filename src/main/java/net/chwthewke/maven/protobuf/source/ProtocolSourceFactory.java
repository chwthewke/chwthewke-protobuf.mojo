package net.chwthewke.maven.protobuf.source;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.chwthewke.maven.protobuf.MojoType.SourceArchive;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.model.Dependency;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.chwthewke.maven.protobuf.MojoType.PRODUCTION;

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

    public Iterable<ProtocolSource> productionSourcesAsTestDependency( ) {
        return Lists.transform(
            ImmutableList.of( PRODUCTION.getSourceArchive( ), PRODUCTION.getDependenciesArchive( ) ),
            new Function<SourceArchive, ProtocolSource>( ) {
                @Override
                public ProtocolSource apply( SourceArchive input ) {
                    return new LaxIncludeArchiveDependency( serviceProvider,
                        projectPath( input.getPath( ) ) );
                }
            } );
    }

    public ProtocolSourceFactory( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private Path projectPath( final Path sourceDirectory ) {
        return serviceProvider.getBasedir( ).resolve( sourceDirectory );
    }

    private final ServiceProvider serviceProvider;
}
