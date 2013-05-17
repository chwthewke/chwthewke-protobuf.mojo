package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

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
    public MavenProject getProject( ) {
        return project;
    }

    @Override
    public MavenProjectHelper getProjectHelper( ) {
        return mavenProjectHelper;
    }

    @Override
    public ArtifactExtractor getArtifactExtractor( ) {
        return artifactExtractor;
    }

    @Override
    public DependencyResolver getDependencyResolver( ) {
        return dependencyResolver;
    }

    DefaultServiceProvider( final Mojo mojo, final MavenProject project,
            final MavenProjectHelper mavenProjectHelper,
            final ArtifactExtractor artifactExtractor,
            final DependencyResolver dependencyResolver ) {
        this.project = project;
        this.mojo = mojo;
        this.mavenProjectHelper = mavenProjectHelper;
        this.artifactExtractor = artifactExtractor;
        this.dependencyResolver = dependencyResolver;
    }

    private final MavenProject project;
    private final Mojo mojo;
    private final MavenProjectHelper mavenProjectHelper;
    private final ArtifactExtractor artifactExtractor;
    private final DependencyResolver dependencyResolver;

}
