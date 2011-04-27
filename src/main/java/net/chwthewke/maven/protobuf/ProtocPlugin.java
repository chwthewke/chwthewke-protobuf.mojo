package net.chwthewke.maven.protobuf;

/**
 * @author Chewie
 */
public class ProtocPlugin {

    public ProtocPlugin( ) {
    }

    public ProtocPlugin( final String plugin, final String outputDirectory, final boolean addToSources ) {
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

    @Override
    public String toString( ) {
        return "ProtocPlugin [plugin=" + plugin + ", outputDirectory=" + outputDirectory + "]";
    }

    /**
     * @parameter default-value="false"
     * @optional
     */
    private boolean addToSources;

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

}
