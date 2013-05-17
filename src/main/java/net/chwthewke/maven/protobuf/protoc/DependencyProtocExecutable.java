package net.chwthewke.maven.protobuf.protoc;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;

import net.chwthewke.maven.protobuf.services.Dependencies;
import net.chwthewke.maven.protobuf.services.PluginConstants;
import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Os;

class DependencyProtocExecutable implements ProtocExecutable {

    public DependencyProtocExecutable( final ServiceProvider serviceProvider, final Dependency dependency ) {
        this.serviceProvider = serviceProvider;
        this.dependency = withOsClassifier( checkNotNull( dependency ) );
    }

    @Override
    public void resolve( ) throws MojoExecutionException {
        final Artifact artifact = serviceProvider.getDependencyResolver( ).resolveDependency( dependency );
        serviceProvider.getArtifactExtractor( ).extractArtifact( artifact, protocDirectory( ) );
    }

    @Override
    public Path getPath( ) {
        return protocDirectory( ).resolve( "protoc" );
    }

    @Override
    public String toString( ) {
        return String.format( "%s %s", getClass( ).getSimpleName( ), dependency );
    }

    private Path protocDirectory( ) {
        return PluginConstants.PLUGIN_WORK.resolve( "protoc" );
    }

    private static Dependency withOsClassifier( final Dependency original ) {
        return Dependencies.copyWithClassifier( original, classifierByOs( ) );
    }

    private static String classifierByOs( ) {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            return "win32";
        if ( Os.isFamily( Os.FAMILY_UNIX ) )
            return "linux_" + Os.OS_ARCH;
        throw new UnsupportedOperationException( "Unsupported OS " + Os.OS_NAME );
    }

    private final ServiceProvider serviceProvider;

    private final Dependency dependency;

}
