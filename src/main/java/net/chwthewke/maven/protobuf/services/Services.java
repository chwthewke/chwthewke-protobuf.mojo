package net.chwthewke.maven.protobuf.services;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.sonatype.plexus.build.incremental.BuildContext;

public final class Services {

    public static ServiceProvider serviceProvider( final MavenProject project,
            final Mojo mojo,
            final MavenProjectHelper mavenProjectHelper,
            final BuildContext buildContext,
            final ArchiverManager archiverManager,
            final ArtifactResolver artifactResolver,
            final ArtifactFactory artifactFactory,
            final ArtifactRepository localRepository ) {
        final IncrementalBuildHelper incrementalBuildHelper =
                new DefaultIncrementalBuildHelper( mojo, project, buildContext );

        return new DefaultServiceProvider( mojo, project, mavenProjectHelper, buildContext, incrementalBuildHelper,
            new DefaultArtifactExtractor( mojo, project, archiverManager, incrementalBuildHelper ),
            new DefaultArchiveExtractor( mojo, incrementalBuildHelper, archiverManager ),
            new DefaultDependencyResolver( mojo, artifactResolver, artifactFactory, project, localRepository ) );
    }

    private Services( ) {
        throw new UnsupportedOperationException( );
    }
}
