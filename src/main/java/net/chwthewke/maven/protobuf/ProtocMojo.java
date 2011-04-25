package net.chwthewke.maven.protobuf;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal execute
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
     * The Maven project to analyze.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute( )
            throws MojoExecutionException
    {

        final List<String> sourceDirs = getSourceDirs( );

        getLog( ).debug( String.format( "Source directories: %s", sourceDirs ) );

        final List<String> sources = gatherSources( sourceDirs );

        getLog( ).debug( String.format( "Source files: %s", sources ) );

        final List<ProtocPlugin> plugins = getProtocPlugins( );

        for ( final ProtocPlugin protocPlugin : plugins )
        {
            createOutputDirectory( protocPlugin.getOutputDirectory( ) );
        }

        executeProtoc( sourceDirs, sources, plugins );
    }

    private List<ProtocPlugin> getProtocPlugins( ) {
        // TODO Auto-generated method stub
        return Collections.<ProtocPlugin>emptyList( );
    }

    private List<String> getSourceDirs( ) {

        return sourceDirectories == null ?
                newArrayList( "src" + File.separator + "main" + File.separator + "proto" ) :
                newArrayList( sourceDirectories );

    }

    private List<String> gatherSources( final List<String> sourceDirs ) {

        final FileSetManager fileSetManager = new FileSetManager( getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( project.getBasedir( ).getAbsolutePath( ) );
        for ( final String sourceDir : sourceDirs )
        {
            fs.addInclude( sourceDir + File.separator + "**" );
        }

        return newArrayList( fileSetManager.getIncludedFiles( fs ) );
    }

    private void createOutputDirectory( final File file ) {
        // TODO Auto-generated method stub

    }

    private void executeProtoc( final List<String> sourceDirs, final List<String> sources,
            final List<ProtocPlugin> plugins ) {
        // TODO Auto-generated method stub

    }

}
