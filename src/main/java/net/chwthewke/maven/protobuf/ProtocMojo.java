package net.chwthewke.maven.protobuf;

import static com.google.common.collect.Lists.newArrayList;
import static net.chwthewke.maven.protobuf.PathUtils.fixPath;
import static net.chwthewke.maven.protobuf.PathUtils.joinPaths;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.Commandline.Argument;
import org.codehaus.plexus.util.cli.StreamConsumer;

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

    private static final String PROTOCOL_SOURCES_JAR = "protocol-sources.jar";

    /**
     * The source directories of the protocol.
     * Default: <code>${basedir}/src/main/proto</code>
     * 
     * @parameter
     */
    private String[ ] sourceDirectories;

    /**
     * @parameter
     */
    private String[ ] protoPaths;

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
     * @parameter
     */
    private Dependency[ ] protocolSourceDependencies;

    /**
     * @parameter
     */
    private Dependency protocExecutable;

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
        prepareProtocolDependencies( );

        selectSourceDirectories( );

        selectSources( );

        selectProtocPlugins( );

        prepareOutputDirectories( );

        executeProtoc( );
    }

    private void selectSourceDirectories( ) {

        if ( sourceDirectories != null )
        {
            for ( final String sourceDirectory : sourceDirectories )
                sourceDirectoriesList.add( PathUtils.fixPath( sourceDirectory ) );
        }

        if ( protocolSourceDependencies != null )
        {
            for ( final Dependency protocolSourceDependency : protocolSourceDependencies )
            {
                sourceDirectoriesList.add( protocolDependencyPath( protocolSourceDependency.getArtifactId( ) ) );
            }
        }

        if ( sourceDirectoriesList.isEmpty( ) )
            sourceDirectoriesList.add( PathUtils.joinPaths( "src", "main", "proto" ) );

        getLog( ).debug( String.format( "Source directories: %s", sourceDirectoriesList ) );
    }

    private void selectSources( ) {
        final FileSetManager fileSetManager = new FileSetManager( getLog( ) );

        final FileSet fs = new FileSet( );
        fs.setDirectory( project.getBasedir( ).getPath( ) );
        for ( final String sourceDir : sourceDirectoriesList )
            fs.addInclude( PathUtils.joinPaths( sourceDir, "**" ) );

        sources = ImmutableList.copyOf( fileSetManager.getIncludedFiles( fs ) );

        getLog( ).debug( String.format( "Source files: %s", sources ) );
    }

    private void prepareProtocolDependencies( ) throws MojoExecutionException {
        prepareProtocolDependencies( protocolDependencies, false );
        prepareProtocolDependencies( protocolSourceDependencies, true );
    }

    private void prepareProtocolDependencies( final Dependency[ ] dependencies, final boolean source )
            throws MojoExecutionException {
        if ( dependencies == null )
            return;
        for ( final Dependency protocolDependency : dependencies )
            prepareProtocolDependency( protocolDependency, source );
    }

    private void prepareProtocolDependency( final Dependency dependency, final boolean source )
            throws MojoExecutionException {

        final Artifact artifact = resolveDependency( dependency );

        final String extractPath = unpackProtocolDependency( artifact );
        if ( source )
            sourceDirectoriesList.add( extractPath );
        else
            protoPathsList.add( extractPath );
    }

    private Artifact resolveDependency( final Dependency dependency ) throws MojoExecutionException {
        final Artifact artifact = artifactFactory.createDependencyArtifact(
            dependency.getGroupId( ),
            dependency.getArtifactId( ),
            VersionRange.createFromVersion( dependency.getVersion( ) ),
            dependency.getType( ),
            dependency.getClassifier( ),
            Artifact.SCOPE_COMPILE );

        return resolveArtifact( artifact );
    }

    private Artifact resolveArtifact( final Artifact artifact ) throws MojoExecutionException {
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
        return artifact;
    }

    private String unpackProtocolDependency( final Artifact artifact ) throws MojoExecutionException {
        final String dependencyName = artifact.getArtifactId( );
        final String extractPath = protocolDependencyPath( dependencyName );

        getLog( ).info( String.format( "Extract protocol dependency %s to %s.", artifact, extractPath ) );

        extractProtoSources( artifact, extractPath );

        return extractPath;
    }

    private String protocolDependencyPath( final String artifactId ) {
        final String extractPath = PathUtils.joinPaths( "target", "protobuf", "dependencies", artifactId );
        return extractPath;
    }

    private void extractProtoSources( final Artifact artifact, final String extractPath ) throws MojoExecutionException {
        final File artifactFile = artifact.getFile( );
        final File absoluteExtractPath = toAbsolutePath( extractPath );

        if ( artifactFile.isDirectory( ) )
        {
            getLog( ).info( "Artifact file is a directory, attempting to locate proto jar in project." );
            final File sourcesJar = new File( PathUtils.joinPaths(
                artifactFile.getAbsolutePath( ), "..", "protobuf", PROTOCOL_SOURCES_JAR ) );
            if ( sourcesJar.exists( ) )
            {
                getLog( ).info(
                    "Found " + sourcesJar.getAbsolutePath( ) + ", extracting to "
                            + absoluteExtractPath.getAbsolutePath( ) );
                artifact.setFile( sourcesJar );
            }
        }

        extractArtifact( artifact, absoluteExtractPath );
    }

    private File toAbsolutePath( final String extractPath ) {
        return new File( project.getBasedir( ), extractPath );
    }

    private void extractArtifact( final Artifact artifact, final File absoluteExtractPath )
            throws MojoExecutionException {
        absoluteExtractPath.mkdirs( );

        final String type = artifact.getType( );
        final File file = artifact.getFile( );

        extractFile( file, type, absoluteExtractPath );
    }

    private void extractFile( final File file, final String type, final File absoluteExtractPath )
            throws MojoExecutionException {
        try
        {
            final UnArchiver unarchiver = archiverManager.getUnArchiver( type );
            unarchiver.setSourceFile( file );
            unarchiver.setDestDirectory( absoluteExtractPath );
            unarchiver.setFileSelectors( new FileSelector[ ] { new FileSelector( ) {

                @Override
                public boolean isSelected( final FileInfo fileInfo ) throws IOException {
                    return !fileInfo.getName( ).startsWith( "META-INF" );
                }
            } } );

            //getLog( ).info( "Unarchiver: " + unarchiver );

            unarchiver.extract( );
        }
        catch ( final NoSuchArchiverException e )
        {
            throw new MojoExecutionException( String.format( "Cannot unpack file %s.", file ), e );
        }
        catch ( final ArchiverException e )
        {
            throw new MojoExecutionException( String.format( "Failed to unpack file %s.", file ), e );
        }
    }

    private void selectProtocPlugins( ) {

        if ( protocPlugins == null )
            protocPluginsList = ImmutableList.of( new ProtocPlugin( "java" ) );
        else
            protocPluginsList = ImmutableList.copyOf( protocPlugins );

        for ( final ProtocPlugin plugin : protocPluginsList )
            plugin.validate( );

        getLog( ).debug( String.format( "Requested plugins: %s", protocPluginsList ) );
    }

    private void prepareOutputDirectories( ) throws MojoExecutionException {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
        {
            final String outputDirectory = PathUtils.fixPath( protocPlugin.getOutputDirectory( ) );

            final File file = toAbsolutePath( outputDirectory );
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

    private void computeCommand( ) throws MojoExecutionException {

        if ( protocExecutable == null )
        {
            commandline.setExecutable( "protoc" );
            return;
        }

        final Artifact protocArtifact =
                resolveArtifact(
                artifactFactory.createArtifactWithClassifier(
                    protocExecutable.getGroupId( ),
                    protocExecutable.getArtifactId( ),
                    protocExecutable.getVersion( ),
                    protocExecutable.getType( ),
                    classifierByOs( ) ) );

        getLog( ).info( String.format( "Using protoc from artifact %s.", protocArtifact ) );

        final String protocDir = PathUtils.joinPaths( "target", "protobuf", "protoc" );
        extractArtifact( protocArtifact, toAbsolutePath( protocDir ) );

        commandline.setExecutable( PathUtils.joinPaths( protocDir, "protoc" ) );
        getLog( ).debug( String.format( "Set command to '%s'.", commandline.getExecutable( ) ) );
    }

    private String classifierByOs( ) {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            return "win32";
        if ( Os.isFamily( Os.FAMILY_UNIX ) )
            return "linux_" + Os.OS_ARCH;
        return null;
    }

    private String findExecutable( final File directory, final String basename ) throws MojoExecutionException {
        final Collection<String> extensions = executableExtentionsByOs( );

        for ( final String ext : extensions )
        {
            final String execFilename = basename + ext;
            final File potentialExecutable = new File( directory, execFilename );

            if ( potentialExecutable.isFile( ) )
                return potentialExecutable.getPath( );
        }
        throw new MojoExecutionException(
            String.format( "Could not find executable %s in dir %s", basename, directory ) );
    }

    private Collection<String> executableExtentionsByOs( ) {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            return WINDOWS_EXE;
        if ( Os.isFamily( Os.FAMILY_UNIX ) )
            return LINUX_EXE;
        return Collections.<String>emptyList( );
    }

    private void computeCommandLineArguments( ) throws MojoExecutionException {

        addPluginsToCommandLine( );

        addSourceDirsToCommandLine( );

        addSourcesToCommandLine( );

        getLog( ).info( "Command line arguments to protoc:" );
        for ( final String arg : commandline.getArguments( ) )
            getLog( ).info( arg );

    }

    private void addPluginsToCommandLine( ) throws MojoExecutionException {
        for ( final ProtocPlugin protocPlugin : protocPluginsList )
            addPluginToCommandLine( protocPlugin );
    }

    private void addPluginToCommandLine( final ProtocPlugin protocPlugin ) throws MojoExecutionException {
        // TODO Executable

        if ( protocPlugin.getExecutable( ) != null )
        {
            final Dependency dependency = protocPlugin.getDependency( );
            final File executableFile;
            if ( dependency != null )
            {
                final Artifact artifact = resolveDependency( dependency );
                final String pluginDir = PathUtils.joinPaths(
                    project.getBasedir( ).getPath( ),
                    "target", "protobuf", "plugin", dependency.getArtifactId( ) );

                final File extractPath = new File( pluginDir );
                final File artifactFile = artifact.getFile( );
                if ( artifactFile.isDirectory( ) )
                {
                    getLog( ).info( "Artifact file is a directory, attempting to locate plugin archive in project." );
                    final StringBuilder archiveNameBuilder = new StringBuilder( );

                    archiveNameBuilder
                        .append( artifact.getArtifactId( ) )
                        .append( "-" )
                        .append( artifact.getVersion( ) );
                    if ( !StringUtils.isEmpty( artifact.getClassifier( ) ) )
                        archiveNameBuilder
                            .append( "-" )
                            .append( artifact.getClassifier( ) );
                    archiveNameBuilder.append( "." )
                        .append( artifact.getType( ) );

                    final File pluginArchive = new File( PathUtils.joinPaths(
                        artifactFile.getAbsolutePath( ), "..", archiveNameBuilder.toString( ) ) );
                    if ( pluginArchive.exists( ) )
                    {
                        getLog( ).info( "Found " + artifactFile.getAbsolutePath( ) );
                        extractFile( pluginArchive, artifact.getType( ), extractPath );
                    }
                    else
                    {
                        throw new MojoExecutionException( "Cannot resolve plugin from directory" );
                    }
                }
                else
                {
                    extractArtifact( artifact, extractPath );
                }
                executableFile = new File( PathUtils.joinPaths(
                    pluginDir, protocPlugin.getExecutable( ) ) );
            }
            else
            {
                executableFile = new File(
                    joinPaths(
                        project.getBasedir( ).getPath( ),
                        fixPath( protocPlugin.getExecutable( ) ) ) );
            }
            final String actualExecutable = findExecutable( executableFile.getParentFile( ), executableFile.getName( ) );

            addArgument( String.format( "--plugin=protoc-gen-%s=%s", protocPlugin.getPlugin( ), actualExecutable ) );
        }

        addArgument( String.format( "--%s_out=%s",
            protocPlugin.getPlugin( ),
            protocPlugin.getOutputDirectory( ) ) );
    }

    private void addSourceDirsToCommandLine( ) {
        for ( final String sourceDir : sourceDirectoriesList )
            addSourceDirToCommandLine( sourceDir );
        if ( protoPaths != null )
            for ( final String protoPath : protoPaths )
                addSourceDirToCommandLine( PathUtils.fixPath( protoPath ) );
        for ( final String protoPath : protoPathsList )
            addSourceDirToCommandLine( protoPath );
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
        protoArchiveFile = new File( project.getBasedir( ), PathUtils.joinPaths(
            "target", "protobuf", PROTOCOL_SOURCES_JAR ) );
        getLog( ).debug( String.format( "Archiving protocol sources to %s.", protoArchiveFile ) );

        Exception archiveException = null;
        try
        {
            final Archiver archiver = archiverManager.getArchiver( "jar" );
            archiver.setDestFile( protoArchiveFile );

            for ( final String sourceDirectory : sourceDirectoriesList )
                archiver.addDirectory( toAbsolutePath( sourceDirectory ) );
            for ( final String sourceDirectory : protoPathsList )
                archiver.addDirectory( toAbsolutePath( sourceDirectory ) );

            for ( final String source : sources )
                archiver.addFile( toAbsolutePath( source ), "" );
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

    private final List<String> sourceDirectoriesList = newArrayList( );
    private final List<String> protoPathsList = newArrayList( );
    private List<String> sources;
    private List<ProtocPlugin> protocPluginsList;
    private Commandline commandline;
    private File protoArchiveFile;

    private final StreamConsumer infoStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( final String line ) {
            getLog( ).info( "[PROTOC]: " + line );
        }
    };

    private final StreamConsumer errorStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( final String line ) {
            getLog( ).error( "[PROTOC]: " + line );
        }
    };

    private static final String PROTOCOL_ARCHIVE_ERROR = "Unable to create protocol archive.";

    private static final ImmutableList<String> LINUX_EXE = ImmutableList.<String>of( "", ".sh" );

    private static final ImmutableList<String> WINDOWS_EXE = ImmutableList.<String>of( ".exe", ".bat", ".cmd" );

}
