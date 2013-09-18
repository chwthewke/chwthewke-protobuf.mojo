package net.chwthewke.maven.protobuf.services;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.nio.file.Path;

public interface ServiceProvider {

    Log getLog( );

    ArtifactExtractor getArtifactExtractor( );

    ArchiveExtractor getArchiveExtractor( );

    DependencyResolver getDependencyResolver( );

    Path getBasedir( );

    MavenProject getProject( );

    MavenProjectHelper getProjectHelper( );

    BuildContext getBuildContext( );

    IncrementalBuildHelper getIncrementalBuildHelper( );

}
