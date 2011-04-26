package net.chwthewke.maven.protobuf;


/**
 * @author Chewie
 */
public class ProtocPlugin {

    public ProtocPlugin( ) {
    }

    public ProtocPlugin( final String plugin, final String outputDirectory ) {
        this( );
        this.outputDirectory = outputDirectory;
        this.plugin = plugin;
    }

    public String getOutputDirectory( ) {
        return outputDirectory;
    }

    public String getPlugin( ) {
        return plugin;
    }

    @Override
    public String toString( ) {
        return "ProtocPlugin [plugin=" + plugin + ", outputDirectory=" + outputDirectory + "]";
    }

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
