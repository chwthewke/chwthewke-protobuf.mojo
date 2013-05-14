package net.chwthewke.maven.protobuf;

import java.text.MessageFormat;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Chewie
 */
public class SimpleProtocPlugin implements ProtocPlugin {

    public SimpleProtocPlugin( ) {
    }

    public SimpleProtocPlugin( final String plugin ) {
        this( plugin, null, null );
    }

    public SimpleProtocPlugin( final String plugin, final String outputDirectory, final Boolean addToSources ) {
        this( );
        this.outputDirectory = outputDirectory;
        this.plugin = plugin;
        this.addToSources = addToSources;
    }

    public String getOutputDirectory( ) {
        return outputDirectory;
    }

    public String getPlugin( ) {
        return plugin;
    }

    public boolean addToSources( ) {
        return addToSources;
    }

    public String getExecutable( ) {
        return executable;
    }

    public Dependency getDependency( ) {
        return dependency;
    }

    public void validate( ) {
        if ( StringUtils.isEmpty( plugin ) )
            throw new IllegalArgumentException( "ProtocPlugin cannot have empty 'plugin'." );
        if ( StringUtils.isEmpty( outputDirectory ) )
            outputDirectory = PathUtils.joinPaths( "target", "generated-sources", "protobuf", plugin );
        if ( addToSources == null )
            addToSources = "java".equals( plugin );
    }

    @Override
    public String toString( ) {
        return MessageFormat.format( "ProtocPlugin [plugin={0}, outputDirectory={1}, addToSources={2}]",
            plugin, outputDirectory, addToSources );
    }

    /**
     * @parameter
     * @optional
     */
    private Boolean addToSources;

    /**
     * @parameter
     * @required
     */
    private String plugin;

    /**
     * @parameter
     * @required
     */
    private String outputDirectory;

    /**
     * @parameter
     * @optional
     */
    private Dependency dependency;

    /**
     * @parameter
     * @optional
     */
    private String executable;

}
