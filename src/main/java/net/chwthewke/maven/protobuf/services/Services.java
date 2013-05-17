package net.chwthewke.maven.protobuf.services;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

public final class Services {

    public static ServiceProvider serviceProvider( final MavenProject project, final Mojo mojo,
            final ArchiverManager archiverManager, final ArtifactResolver artifactResolver,
            final ArtifactFactory artifactFactory, final ArtifactRepository localRepository ) {
        return new DefaultServiceProvider( project, mojo,
            new DefaultArtifactExtractor( mojo, archiverManager ),
            new DefaultDependencyResolver( mojo, artifactResolver, artifactFactory, project, localRepository ) );
    }

    private Services( ) {
        throw new UnsupportedOperationException( );
    }
}
