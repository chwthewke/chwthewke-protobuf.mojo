package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

class DefaultServiceProvider implements ServiceProvider {

    @Override
    public Log getLog( ) {
        return log;
    }

    @Override
    public Path getBasedir( ) {
        return project.getBasedir( ).toPath( );
    }

    @Override
    public ArtifactExtractor getArtifactExtractor( ) {
        return _artifactExtractor;
    }

    @Override
    public DependencyResolver getDependencyResolver( ) {
        return _dependencyResolver;
    }

    private MavenProject project;
    private Log log;
    private ArtifactExtractor _artifactExtractor;
    private DependencyResolver _dependencyResolver;

}
