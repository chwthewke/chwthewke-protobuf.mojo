package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;

public interface IncrementalBuildHelper {

    boolean hasDirectoryChanged( final Path path );

    boolean hasDependencyArchiveChanged( final Artifact artifact, final Path extractPath );

}
