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
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;

class DefaultDependencyResolver implements DependencyResolver {

    @Override
    public Artifact resolveDependency( final Dependency dependency )
            throws MojoExecutionException {

        return resolveDependencyToArtifact( dependency, Optional.<Function<? super Artifact, Path>>absent( ) );

    }

    @Override
    public Artifact resolveDependency( final Dependency dependency, final Path archiveInProject )
            throws MojoExecutionException {

        return resolveDependencyToArtifact(
            dependency, Optional.<Function<? super Artifact, Path>>of( Functions.constant( archiveInProject ) ) );

    }

    @Override
    public Artifact resolveDependency( final Dependency dependency, final Function<Artifact, Path> archiveInProject )
            throws MojoExecutionException {
        return resolveDependencyToArtifact(
            dependency, Optional.<Function<? super Artifact, Path>>of( archiveInProject ) );
    }

    DefaultDependencyResolver( final Mojo mojo,
            final ArtifactResolver artifactResolver,
            final ArtifactFactory artifactFactory,
            final MavenProject project,
            final ArtifactRepository localRepository ) {
        this.mojo = mojo;
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
        this.project = project;
        this.localRepository = localRepository;
    }

    private Artifact resolveDependencyToArtifact( final Dependency dependency,
            final Optional<Function<? super Artifact, Path>> archiveInProject ) throws MojoExecutionException {
        final Artifact artifact = artifactFactory.createDependencyArtifact(
            dependency.getGroupId( ),
            dependency.getArtifactId( ),
            VersionRange.createFromVersion( dependency.getVersion( ) ),
            dependency.getType( ),
            dependency.getClassifier( ),
            Artifact.SCOPE_COMPILE );

        mojo.getLog( ).debug( String.format( "Resolving dependency %s", dependency ) );

        try
        {
            artifactResolver.resolve( artifact, project.getRemoteArtifactRepositories( ), localRepository );

            if ( artifact.getFile( ).isDirectory( ) )
            {
                resolveArtifactToPathIfProvided( dependency, archiveInProject, artifact );
            }

            mojo.getLog( ).info(
                String.format( "Resolved dependency %s to %s.", dependency, artifact.getFile( ).toPath( ) ) );
        }
        catch ( ArtifactResolutionException | ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to resolve dependency " + dependency, e );
        }

        return artifact;
    }

    private void resolveArtifactToPathIfProvided( final Dependency dependency,
            final Optional<Function<? super Artifact, Path>> archiveInProject,
            final Artifact artifact ) throws MojoExecutionException {

        if ( !archiveInProject.isPresent( ) )
            throw new MojoExecutionException( String.format(
                "Dependency %s resolved to directory %s and no archive path given.",
                dependency, artifact.getFile( ) ) );

        mojo.getLog( ).debug( String.format(
            "Artifact for %s was resolved by maven to %s, attempting to locate archive with %s.",
            dependency, artifact.getFile( ).toPath( ), archiveInProject.get( ) ) );

        resolveArtifactToPath( artifact, archiveInProject.get( ).apply( artifact ) );
    }

    private void resolveArtifactToPath( final Artifact artifact, final Path archiveInProject )
            throws MojoExecutionException {

        final Path projectDirectory = artifact.getFile( ).toPath( );
        final Path archivePath = projectDirectory.resolve( ".." ).resolve( ".." ).resolve( archiveInProject );

        if ( Files.isRegularFile( archivePath ) )
        {
            artifact.setFile( archivePath.toFile( ) );
        }
        else
            throw new MojoExecutionException(
                String.format( "Looking for archive %s for artifact %s in project directory %s : not found.",
                    archiveInProject, artifact, artifact.getFile( ) ) );
    }

    private final Mojo mojo;

    private final ArtifactResolver artifactResolver;
    private final ArtifactFactory artifactFactory;

    private final MavenProject project;
    private final ArtifactRepository localRepository;

}
