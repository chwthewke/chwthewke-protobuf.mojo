package net.chwthewke.maven.protobuf;

/*
 * Copyright 2011 Thomas Dufour.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.Commandline.Argument;
import org.codehaus.plexus.util.cli.StreamConsumer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal compile
 * @phase generate-sources
 */
public class ProtocMojo
        extends AbstractMojo
{

    /**
     * The source directories of the protocol.
     * Default: <code>${basedir}/src/main/proto</code>
     * 
     * @parameter
     */
    private String[ ] sourceDirectories;

    /**
     * The plugins to execute with their respective output directories.
     * 
     * @parameter
     */
    private ProtocPlugin[ ] protocPlugins;

    /**
     * @parameter
     */
    private Dependency[ ] protocolDependencies;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * The Maven project to analyze.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @readonly
     */
    private ArchiverManager archiverManager;

    /**
     * Maven ProjectHelper.
     * 
     * @component
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    /**
     * @component
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     * @readonly
     */
    private ArtifactResolver artifactResolver;

    @Override
    public void execute( ) throws MojoExecutionException
    {
        selectSourceDirectories( );

        selectSources( );

        prepareProtocolDependencies( );

        selectProtocPlugins( );

        prepareOutputDirectories( );

        executeProtoc( );
    }

    private void selectSourceDirectories( ) {
        sourceDirs = sourceDirectories == null ?
                ImmutableList.of( joinPaths( "src", "main", "proto" ) ) :
                ImmutableList.copyOf( sourceDirectories );

        getLog( ).debug( String.format( "Source directories: %s", sourceDirs ) );
    }

    private void selectSources( ) {
        // TODO sources from artifacts
        final FileSetManager fileSetManager = new FileSetManager( getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( project.getBasedir( ).getPath( ) );
        for ( final String sourceDir : sourceDirs )
            fs.addInclude( joinPaths( sourceDir, "**" ) );

        sources = ImmutableList.copyOf( fileSetManager.getIncludedFiles( fs ) );

        getLog( ).debug( String.format( "Source files: %s", sources ) );
    }

    private void prepareProtocolDependencies( ) throws MojoExecutionException {
        if ( protocolDependencies == null )
            return;
        for ( final Dependency protocolDependency : protocolDependencies )
            prepareProtocolDependency( protocolDependency );
    }

    private void prepareProtocolDependency( Dependency dependency ) throws MojoExecutionException {

        final Artifact artifact = artifactFactory.createDependencyArtifact(
            dependency.getGroupId( ),
            dependency.getArtifactId( ),
            VersionRange.createFromVersion( dependency.getVersion( ) ),
            dependency.getType( ),
            dependency.getClassifier( ),
            Artifact.SCOPE_COMPILE );

        try
        {
            artifactResolver.resolve( artifact,
                project.getRemoteArtifactRepositories( ),
                localRepository );
        }
        catch ( final ArtifactResolutionException e )
        {
            throw new MojoExecutionException( String.format( "Unable to resolve artifact: %s", artifact ), e );
        }
        catch ( final ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( String.format( "Unable to find artifact: %s", artifact ), e );
        }

        //UnArchiver unarchiver = archiverManager.getUnArchiver( protocolDependency.getType( ) );

        // TODO Auto-generated method stub

    }

    private void selectProtocPlugins( ) {

        if ( protocPlugins == null )
            protocPluginsList = ImmutableList.of(
                new ProtocPlugin( "java", joinPaths( "target", "protobuf", "generated-sources", "java" ), true ) );
        else
            protocPluginsList = ImmutableList.copyOf( protocPlugins );

        getLog( ).debug( String.format( "Requested plugins: %s", protocPluginsList ) );
    }

    private void prepareOutputDirectories( ) throws MojoExecutionException {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
        {
            final String outputDirectory = protocPlugin.getOutputDirectory( );

            final File file = new File( project.getBasedir( ), outputDirectory );
            if ( file.isDirectory( ) )
                continue;
            if ( !file.mkdirs( ) )
                throw new MojoExecutionException(
                    String.format( "Error creating output directory %s.", file.getAbsolutePath( ) ) );
        }
    }

    private void executeProtoc( ) throws MojoExecutionException {

        initCommandline( );

        computeCommand( );

        computeCommandLineArguments( );

        runProtocSubprocess( );

        addGeneratedSourcesToBuild( );

        archiveAndAttachProtocolSources( );
    }

    private void initCommandline( ) {
        commandline = new Commandline( );
        commandline.setWorkingDirectory( project.getBasedir( ) );
    }

    private void computeCommand( ) {
        // TODO protoc from dependency
        commandline.setExecutable( "protoc" );
    }

    private void computeCommandLineArguments( ) {

        addPluginsToCommandLine( );

        addSourceDirsToCommandLine( );

        addSourcesToCommandLine( );

        getLog( ).debug( "Command line arguments to protoc:" );
        for ( final String arg : commandline.getArguments( ) )
            getLog( ).debug( arg );

    }

    private void addPluginsToCommandLine( ) {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
            addPluginToCommandLine( protocPlugin );
    }

    private void addPluginToCommandLine( final ProtocPlugin protocPlugin ) {

        addArgument( String.format( "--%s_out=%s",
            protocPlugin.getPlugin( ),
            protocPlugin.getOutputDirectory( ) ) );
        // TODO Non standard plugins
    }

    private void addSourceDirsToCommandLine( ) {
        for ( final String sourceDir : sourceDirs )
            addSourceDirToCommandLine( sourceDir );
    }

    private void addSourceDirToCommandLine( final String sourceDir ) {
        addArgument( String.format( "-I%s", sourceDir ) );
    }

    private void addSourcesToCommandLine( ) {
        for ( final String source : sources )
            addArgument( source );
    }

    private void addArgument( final String value ) {
        final Arg argument = new Argument( );
        argument.setValue( value );
        commandline.addArg( argument );
    }

    private void runProtocSubprocess( ) throws MojoExecutionException {
        try
        {
            getLog( ).debug( "Running protoc" );
            final int exitValue =
                    CommandLineUtils.executeCommandLine( commandline, infoStreamConsumer, errorStreamConsumer );

            if ( exitValue != 0 )
                throw new MojoExecutionException( String.format( "protoc encountered an error (%d)", exitValue ) );

            getLog( ).info( "protoc executed successfully." );
        }
        catch ( final CommandLineException e )
        {
            throw new MojoExecutionException( "Error creating protoc process", e );
        }
    }

    private void archiveAndAttachProtocolSources( ) throws MojoExecutionException {
        archiveProtocolSources( );
        attachProtoSourcesArtifact( );
    }

    private void attachProtoSourcesArtifact( ) {
        projectHelper.attachArtifact( project, "jar", "proto", protoArchiveFile );
    }

    private void archiveProtocolSources( ) throws MojoExecutionException {
        // TODO make archive name unique
        protoArchiveFile = new File( project.getBasedir( ), joinPaths( "target", "protobuf", "protocol-sources.jar" ) );
        getLog( ).debug( String.format( "Archiving protocol sources to %s.", protoArchiveFile ) );

        Exception archiveException = null;
        try
        {
            final Archiver archiver = archiverManager.getArchiver( "jar" );
            archiver.setDestFile( protoArchiveFile );
            for ( final String source : sources )
                archiver.addFile( new File( project.getBasedir( ), source ), source );
            archiver.createArchive( );
        }
        catch ( final NoSuchArchiverException e )
        {
            archiveException = e;
        }
        catch ( final ArchiverException e )
        {
            archiveException = e;
        }
        catch ( final IOException e )
        {
            archiveException = e;
        }
        if ( archiveException != null )
        {
            getLog( ).error( archiveException.toString( ) );
            throw new MojoExecutionException( PROTOCOL_ARCHIVE_ERROR, archiveException );
        }
    }

    private void addGeneratedSourcesToBuild( ) {
        for ( final ProtocPlugin plugin : protocPluginsList )
        {
            if ( plugin.addToSources( ) )
                project.addCompileSourceRoot( plugin.getOutputDirectory( ) );
        }
    }

    private static String joinPaths( final String... elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

    private List<String> sourceDirs;
    private List<String> sources;
    private List<ProtocPlugin> protocPluginsList;
    private Commandline commandline;
    private File protoArchiveFile;

    private final StreamConsumer infoStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( String line ) {
            getLog( ).info( "[PROTOC]: " + line );
        }
    };

    private final StreamConsumer errorStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( String line ) {
            getLog( ).error( "[PROTOC]: " + line );
        }
    };

    private static final String PROTOCOL_ARCHIVE_ERROR = "Unable to create protocol archive.";

}
