package net.chwthewke.maven.protobuf.plugins;

import net.chwthewke.maven.protobuf.PathUtils;

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Chewie
 */
public class ProtocPluginDefinition {

    public ProtocPluginDefinition( ) {
    }

    @Deprecated
    public ProtocPluginDefinition( final String plugin ) {
        this( plugin, null, null );
    }

    @Deprecated
    public ProtocPluginDefinition( final String plugin, final String outputDirectory, final Boolean addToSources ) {
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

    @Deprecated
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
        return "ProtocPluginDefinition [plugin=" + plugin + ", executable=" + executable + ", addToSources="
                + addToSources + ", outputDirectory=" + outputDirectory + ", dependency=" + dependency + "]";
    }

    /**
     * @parameter
     * @required
     */
    private String plugin;

    /**
     * @parameter
     * @optional
     */
    private String executable;

    /**
     * @parameter
     * @optional
     */
    private Boolean addToSources;

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

}
