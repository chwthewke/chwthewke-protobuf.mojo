package net.chwthewke.maven.protobuf;

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

public class DependencyResolver {

    public Artifact resolveDependency( final Dependency dependency ) throws MojoExecutionException {

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
        }
        catch ( ArtifactResolutionException | ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Unable to resolve dependency " + dependency, e );
        }

        return artifact;

    }

    private ArtifactResolver artifactResolver;
    private ArtifactFactory artifactFactory;

    private MavenProject project;
    private ArtifactRepository localRepository;

}
