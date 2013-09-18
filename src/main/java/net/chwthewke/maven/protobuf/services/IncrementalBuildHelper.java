package net.chwthewke.maven.protobuf.services;

import org.apache.maven.artifact.Artifact;

import java.nio.file.Path;

public interface IncrementalBuildHelper {

    boolean hasDirectoryChanged( Path path );

    boolean hasFileChanged( Path path, Path target );

    boolean hasDependencyArchiveChanged( Artifact artifact, Path extractPath );

}
