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

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

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
     * The Maven project to analyze.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private List<String> sourceDirs;

    private List<String> sources;

    private List<ProtocPlugin> protocPluginsList;

    private List<String> commandLineArguments;

    public void execute( )
            throws MojoExecutionException
    {

        selectSourceDirectories( );

        selectSources( );

        selectProtocPlugins( );

        prepareOutputDirectories( );

        executeProtoc( );
    }

    private void executeProtoc( ) {

        computeCommandLineArguments( );

    }

    private void computeCommandLineArguments( ) {

        commandLineArguments = newArrayList( );

        addPluginsToCommandLine( );

        addSourceDirsToCommandLine( );

        addSourcesToCommandLine( );

        getLog( ).debug( "Command line arguments to protoc:" );
        for ( final String arg : commandLineArguments )
            getLog( ).debug( arg );
    }

    private void addSourcesToCommandLine( ) {
        for ( final String source : sources )
            commandLineArguments.add( source );
    }

    private void addSourceDirsToCommandLine( ) {
        for ( final String sourceDir : sourceDirs )
            addSourceDirToCommandLine( sourceDir );
    }

    private void addPluginsToCommandLine( ) {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
            addPluginToCommandLine( protocPlugin );
    }

    private void addSourceDirToCommandLine( final String sourceDir ) {
        commandLineArguments.add(
            String.format( "-I%s", sourceDir ) );
    }

    private void addPluginToCommandLine( final ProtocPlugin protocPlugin ) {

        commandLineArguments.add(
            String.format( "--%s_out=%s",
                protocPlugin.getPlugin( ),
                protocPlugin.getOutputDirectory( ) ) );

        // TODO Non standard plugins
    }

    private void prepareOutputDirectories( ) throws MojoExecutionException {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
        {
            final File file = new File( protocPlugin.getOutputDirectory( ) );
            try
            {
                Files.createParentDirs( file );
            }
            catch ( final IOException e )
            {
                throw new MojoExecutionException( "Error creating output directory", e );
            }
        }
    }

    private void selectProtocPlugins( ) {

        if ( protocPlugins == null )
            protocPluginsList = ImmutableList.of(
                new ProtocPlugin( "java", joinPaths( "target", "generated-sources", "java" ) ) );
        else
            protocPluginsList = ImmutableList.copyOf( protocPlugins );

        getLog( ).debug( String.format( "Requested plugins: %s", protocPluginsList ) );
    }

    private void selectSources( ) {
        final FileSetManager fileSetManager = new FileSetManager( getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( project.getBasedir( ).getPath( ) );
        for ( final String sourceDir : sourceDirs )
            fs.addInclude( joinPaths( sourceDir, "**" ) );

        sources = ImmutableList.copyOf( fileSetManager.getIncludedFiles( fs ) );

        getLog( ).debug( String.format( "Source files: %s", sources ) );
    }

    private void selectSourceDirectories( ) {
        sourceDirs = sourceDirectories == null ?
                ImmutableList.of( joinPaths( "src", "main", "proto" ) ) :
                ImmutableList.copyOf( sourceDirectories );

        getLog( ).debug( String.format( "Source directories: %s", sourceDirs ) );
    }

    private static String joinPaths( final String... elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

}
