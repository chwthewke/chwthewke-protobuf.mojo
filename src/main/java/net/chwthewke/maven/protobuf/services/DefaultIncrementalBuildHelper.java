package net.chwthewke.maven.protobuf.services;

import java.io.File;
import java.nio.file.Path;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

class DefaultIncrementalBuildHelper implements IncrementalBuildHelper {

    @Override
    public boolean hasDirectoryChanged( final Path path ) {

        final Scanner scanner = buildContext.newScanner( projectFile( path ), true );
        scanner.scan( );
        final boolean hasChanged = scanner.getIncludedFiles( ).length > 0;

        mojo.getLog( ).debug( String.format( "[IBH] %s has changed : %s", path, hasChanged ) );

        return hasChanged;
    }

    @Override
    public boolean hasDependencyArchiveChanged( final Artifact artifact, final Path extractPath ) {
        final boolean hasChanged = !buildContext.isIncremental( ) ||
                buildContext.isUptodate( projectFile( extractPath ), artifact.getFile( ) );
        mojo.getLog( ).debug( String.format( "[IBH] %s is up-to-date for %s : %s",
            extractPath, artifact.getFile( ).toPath( ), hasChanged ) );
        return hasChanged;
    }

    DefaultIncrementalBuildHelper( final Mojo mojo, final MavenProject project, final BuildContext buildContext ) {
        this.mojo = mojo;
        this.project = project;
        this.buildContext = buildContext;
    }

    private File projectFile( final Path path ) {
        return project.getBasedir( ).toPath( ).resolve( path ).toFile( );
    }

    private final Mojo mojo;
    private final MavenProject project;
    private final BuildContext buildContext;

}
