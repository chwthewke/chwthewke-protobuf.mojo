package net.chwthewke.maven.protobuf.plugins;

import org.apache.maven.model.Dependency;

/**
 * @author Chewie
 */
public class ProtocPluginDefinition {

    public ProtocPluginDefinition( ) {
    }

    public ProtocPluginDefinition( final String plugin ) {
        this( );
        this.plugin = plugin;
    }

    public String getOutputDirectory( ) {
        return outputDirectory;
    }

    public String getPlugin( ) {
        return plugin;
    }

    public Boolean addToSources( ) {
        return addToSources;
    }

    public String getExecutable( ) {
        return executable;
    }

    public Dependency getDependency( ) {
        return dependency;
    }

    @Override
    public String toString( ) {
        return "[plugin=" + plugin + ", executable=" + executable + ", addToSources="
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
     * @optional
     */
    private String outputDirectory;

    /**
     * @parameter
     * @optional
     */
    private Dependency dependency;

}
