package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

class DefaultServiceProvider implements ServiceProvider {

    @Override
    public Log getLog( ) {
        return mojo.getLog( );
    }

    @Override
    public Path getBasedir( ) {
        return project.getBasedir( ).toPath( );
    }

    @Override
    public ArtifactExtractor getArtifactExtractor( ) {
        return artifactExtractor;
    }

    @Override
    public DependencyResolver getDependencyResolver( ) {
        return dependencyResolver;
    }

    DefaultServiceProvider( final MavenProject project, final Mojo mojo, final ArtifactExtractor artifactExtractor,
            final DependencyResolver dependencyResolver ) {
        this.project = project;
        this.mojo = mojo;
        this.artifactExtractor = artifactExtractor;
        this.dependencyResolver = dependencyResolver;
    }

    private final MavenProject project;
    private final Mojo mojo;
    private final ArtifactExtractor artifactExtractor;
    private final DependencyResolver dependencyResolver;

}
