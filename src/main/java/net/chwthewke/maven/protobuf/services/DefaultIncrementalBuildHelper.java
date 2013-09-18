package net.chwthewke.maven.protobuf.services;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.nio.file.Path;

class DefaultIncrementalBuildHelper implements IncrementalBuildHelper {

    @Override
    public boolean hasDirectoryChanged( final Path path ) {

        final Scanner scanner = buildContext.newScanner( projectPath( path ).toFile( ), true );
        scanner.scan( );
        final boolean hasChanged = scanner.getIncludedFiles( ).length > 0;

        mojo.getLog( ).debug( String.format( "[IBH] %s has changed : %s", path, hasChanged ) );

        return hasChanged;
    }

    @Override
    public boolean hasFileChanged( final Path path, final Path target ) {

        final boolean hasChanged = !buildContext.isIncremental( ) ||
                buildContext.isUptodate( projectPath( target ).toFile( ), projectPath( path ).toFile( ) );
        mojo.getLog( ).debug( String.format( "[IBH] %s is up-to-date for %s : %s",
            target, path, hasChanged ) );

        return hasChanged;
    }

    @Override
    public boolean hasDependencyArchiveChanged( final Artifact artifact, final Path extractPath ) {
        final boolean hasChanged = !buildContext.isIncremental( ) ||
                buildContext.isUptodate( projectPath( extractPath ).toFile( ), artifact.getFile( ) );
        mojo.getLog( ).debug( String.format( "[IBH] %s is up-to-date for %s : %s",
            extractPath, artifact.getFile( ).toPath( ), hasChanged ) );
        return hasChanged;
    }

    DefaultIncrementalBuildHelper( final Mojo mojo, final MavenProject project, final BuildContext buildContext ) {
        this.mojo = mojo;
        this.project = project;
        this.buildContext = buildContext;
    }

    private Path projectPath( Path path ) {
        return project.getBasedir( ).toPath( ).resolve( path );
    }

    private final Mojo mojo;
    private final MavenProject project;
    private final BuildContext buildContext;

}
