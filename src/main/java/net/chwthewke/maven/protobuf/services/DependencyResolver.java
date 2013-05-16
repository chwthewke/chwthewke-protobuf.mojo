package net.chwthewke.maven.protobuf.services;

import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

public interface DependencyResolver {

    /**
     * Resolves a dependency into an artifact
     * If the artifact's file is the project directory (i.e. artifact was resolved my m2e to a workspace project),
     * will throw a {@link MojoExecutionException}.
     * 
     * @param dependency
     *            The dependency to resolve
     * @return An artifact for the dependency, whose file is an archive of the dependency
     * @throws MojoExecutionException
     *             if the dependency cannot be resolved, or it is resolved to a project directory.
     */
    Artifact resolveDependency( final Dependency dependency )
            throws MojoExecutionException;

    /**
     * Resolves a dependency into an artifact
     * If the artifact's file is the project directory (i.e. artifact was resolved my m2e to a workspace project),
     * will attempt to locate the archive inside the project with the path <code>archiveInProject</code> and set that
     * as the artifact's file, or throw a {@link MojoExecutionException}.
     * 
     * @param dependency
     *            The dependency to resolve
     * @param archiveInProject
     *            The path where to search for the artifact archive under the project directory.
     * @return An artifact for the dependency, whose file is an archive of the dependency
     * @throws MojoExecutionException
     *             if the dependency cannot be resolved, or it is resolved to an unbuilt project directory.
     */
    Artifact resolveDependency( final Dependency dependency, final Path archiveInProject )
            throws MojoExecutionException;

}
