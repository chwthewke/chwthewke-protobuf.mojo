package net.chwthewke.maven.protobuf.services;

import org.apache.maven.plugin.MojoExecutionException;

import java.nio.file.Path;

public interface ArchiveExtractor {
    boolean extractArchive(Path archive, Path target) throws MojoExecutionException;
}
