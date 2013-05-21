package net.chwthewke.maven.protobuf.plugins;

import org.apache.maven.model.Dependency;

/**
 * @author Chewie
 */
public class ProtocPluginDefinition {

    /**
     * The name to use for the plugin in the command line.
     * Can be used to reference a default plugin (java, python, cpp),
     * in which case no other paremeter is required.
     * 
     * @parameter
     * @required
     */
    public String plugin;

    /**
     * Required when the plugin is not a default protobuf plugin, can be one of:<br/>
     * An executable on the system PATH<br/>
     * The path of an executable relative to the project root<br/>
     * The path of an executable inside the archive referenced by the <code>dependency</code> parameter.
     * 
     * @parameter
     * @optional
     */
    public String executable;

    /**
     * Indicates whether to add the generated sources to the project sources.
     * Defaults to false, except as a convenience if the plugin is the default java plugin.
     * 
     * @parameter
     * @optional
     */
    public Boolean addToSources;

    /**
     * Where to put the generated sources.
     * Defaults to <code>target/generated-sources/[<em>value of parameter &quot;plugin&quot;</em>]</code>
     * 
     * @parameter
     * @optional
     */
    public String outputDirectory;

    /**
     * The dependency containing the plugin executable
     * 
     * @parameter
     * @optional
     */
    public Dependency dependency;

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

}
