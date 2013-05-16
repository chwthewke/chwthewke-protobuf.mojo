package net.chwthewke.maven.protobuf.services;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Optional;

class DefaultDependencyResolver implements DependencyResolver {

    @Override
    public Artifact resolveDependency( final Dependency dependency )
            throws MojoExecutionException {

        return resolveDependencyToArtifact( dependency, Optional.<Path>absent( ) );

    }

    @Override
    public Artifact resolveDependency( final Dependency dependency, final Path archiveInProject )
            throws MojoExecutionException {

        return resolveDependencyToArtifact( dependency, Optional.of( archiveInProject ) );

    }

    private Artifact resolveDependencyToArtifact( final Dependency dependency,
            final Optional<Path> archiveInProject ) throws MojoExecutionException {
        final Artifact artifact = artifactFactory.createDependencyArtifact(
            dependency.getGroupId( ),
            dependency.getArtifactId( ),
            VersionRange.createFromVersion( dependency.getVersion( ) ),
            dependency.getType( ),
            dependency.getClassifier( ),
            Artifact.SCOPE_COMPILE );

        try
        {
            artifactResolver.resolve( artifact, project.getRemoteArtifactRepositories( ), localRepository );

            if ( artifact.getFile( ).isDirectory( ) )
            {
                resolveArtifactToPathIfProvided( dependency, archiveInProject, artifact );
            }
        }
        catch ( ArtifactResolutionException | ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to resolve dependency " + dependency, e );
        }

        return artifact;
    }

    private void resolveArtifactToPathIfProvided( final Dependency dependency, final Optional<Path> archiveInProject,
            final Artifact artifact ) throws MojoExecutionException {

        if ( !archiveInProject.isPresent( ) )
            throw new MojoExecutionException( String.format(
                "Dependency %s resolved to directory %s and no archive path given.",
                dependency, artifact.getFile( ) ) );

        resolveArtifactToPath( artifact, archiveInProject.get( ) );
    }

    private void resolveArtifactToPath( final Artifact artifact, final Path archiveInProject )
            throws MojoExecutionException {

        final Path projectDirectory = artifact.getFile( ).toPath( );
        final Path archivePath = projectDirectory.resolve( archiveInProject );

        if ( Files.isRegularFile( archivePath ) )
            artifact.setFile( archivePath.toFile( ) );
        throw new MojoExecutionException(
            String.format( "Looking for archive %s for artifact %s in project directoty %s : not found.",
                archiveInProject, artifact, artifact.getFile( ) ) );
    }

    private ArtifactResolver artifactResolver;
    private ArtifactFactory artifactFactory;

    private MavenProject project;
    private ArtifactRepository localRepository;

}
