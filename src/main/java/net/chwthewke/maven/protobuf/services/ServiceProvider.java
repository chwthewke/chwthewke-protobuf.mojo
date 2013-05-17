package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

public interface ServiceProvider {

    Log getLog( );

    ArtifactExtractor getArtifactExtractor( );

    DependencyResolver getDependencyResolver( );

    Path getBasedir( );

    MavenProject getProject( );

    MavenProjectHelper getProjectHelper( );

}
