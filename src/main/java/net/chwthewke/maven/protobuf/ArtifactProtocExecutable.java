package net.chwthewke.maven.protobuf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.Os;

public class ArtifactProtocExecutable implements ProtocExecutable {

    public ArtifactProtocExecutable( final ArtifactExtractor artifactExtractor, final DependencyResolver resolver,
            final Dependency dependency ) {
        this.artifactExtractor = artifactExtractor;
        this.resolver = resolver;
        this.dependency = withOsClassifier( checkNotNull( dependency ) );
    }

    @Override
    public void prepare( ) throws MojoExecutionException {
        artifactExtractor.extractArtifact( resolver.resolveDependency( dependency ), protocDirectory( ) );
    }

    @Override
    public Path getPath( ) {
        return protocDirectory( ).resolve( "protoc" );
    }

    private Path protocDirectory( ) {
        return ProtocPaths.PLUGIN_WORK.resolve( "protoc" );
    }

    private static Dependency withOsClassifier( final Dependency original ) {
        final Dependency dependency = new Dependency( );
        dependency.setGroupId( original.getGroupId( ) );
        dependency.setArtifactId( original.getArtifactId( ) );
        dependency.setVersion( original.getVersion( ) );
        dependency.setType( original.getType( ) );
        dependency.setClassifier( classifierByOs( ) );
        return dependency;
    }

    private static String classifierByOs( ) {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            return "win32";
        if ( Os.isFamily( Os.FAMILY_UNIX ) )
            return "linux_" + Os.OS_ARCH;
        throw new UnsupportedOperationException( "Unsupported OS " + Os.OS_NAME );
    }

    private final ArtifactExtractor artifactExtractor;
    private final DependencyResolver resolver;

    private final Dependency dependency;

}
