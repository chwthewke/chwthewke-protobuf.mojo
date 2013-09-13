package net.chwthewke.maven.protobuf.source;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import org.apache.maven.model.Dependency;

import java.nio.file.Path;
import java.util.List;

abstract class AbstractDependencyProtocolSource extends AbstractProtocolSource {

    @Override
    protected Optional<Path> getSourcePath( ) {
        if ( !compileSources )
            return Optional.absent( );

        return Optional.of( sourcePath( ) );
    }

    @Override
    protected List<Path> getAdditionalIncludesPath( ) {
        return ImmutableList.of( includesPath( ) );
    }

    protected final Path sourcePath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "sources" ).resolve( dependency.getArtifactId( ) );
    }

    protected final Path includesPath( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "dependencies" ).resolve( dependency.getArtifactId( ) );
    }

    protected AbstractDependencyProtocolSource( ServiceProvider serviceProvider, Dependency dependency,
            boolean compileSources ) {
        super( serviceProvider, compileSources );
        this.dependency = dependency;
    }

    protected final Dependency dependency;

}
