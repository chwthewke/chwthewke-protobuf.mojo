package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;

public interface ServiceProvider {

    Log getLog( );

    ArtifactExtractor getArtifactExtractor( );

    DependencyResolver getDependencyResolver( );

    Path getBasedir( );

}
