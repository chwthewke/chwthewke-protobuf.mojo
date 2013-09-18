package net.chwthewke.maven.protobuf.services;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.nio.file.Path;

class DefaultServiceProvider implements ServiceProvider {

    @Override
    public Log getLog( ) {
        return mojo.getLog( );
    }

    @Override
    public Path getBasedir( ) {
        return project.getBasedir( ).toPath( );
    }

    @Override
    public MavenProject getProject( ) {
        return project;
    }

    @Override
    public MavenProjectHelper getProjectHelper( ) {
        return mavenProjectHelper;
    }

    @Override
    public ArtifactExtractor getArtifactExtractor( ) {
        return artifactExtractor;
    }

    @Override
    public ArchiveExtractor getArchiveExtractor( ) {
        return archiveExtractor;
    }

    @Override
    public DependencyResolver getDependencyResolver( ) {
        return dependencyResolver;
    }

    @Override
    public BuildContext getBuildContext( ) {
        return buildContext;
    }

    @Override
    public IncrementalBuildHelper getIncrementalBuildHelper( ) {
        return incrementalBuildHelper;
    }

    DefaultServiceProvider( final Mojo mojo, final MavenProject project,
            final MavenProjectHelper mavenProjectHelper,
            final BuildContext buildContext,
            final IncrementalBuildHelper incrementalBuildHelper,
            final ArtifactExtractor artifactExtractor,
            final ArchiveExtractor archiveExtractor,
            final DependencyResolver dependencyResolver ) {
        this.project = project;
        this.mojo = mojo;
        this.mavenProjectHelper = mavenProjectHelper;
        this.buildContext = buildContext;
        this.incrementalBuildHelper = incrementalBuildHelper;
        this.artifactExtractor = artifactExtractor;
        this.archiveExtractor = archiveExtractor;
        this.dependencyResolver = dependencyResolver;
    }

    private final MavenProject project;
    private final Mojo mojo;
    private final MavenProjectHelper mavenProjectHelper;
    private final BuildContext buildContext;
    private final IncrementalBuildHelper incrementalBuildHelper;
    private final ArtifactExtractor artifactExtractor;
    private final ArchiveExtractor archiveExtractor;
    private final DependencyResolver dependencyResolver;

}
