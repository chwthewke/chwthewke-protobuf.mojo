package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

public interface ArtifactExtractor {

    /**
     * Extracts the specified resolved artifact to a given directory
     * 
     * @param artifact
     *            The artifact to extract
     * @param path
     *            The path to extract to
     * @throws MojoExecutionException
     */
    void extractArtifact( final Artifact artifact, final Path path ) throws MojoExecutionException;

}
