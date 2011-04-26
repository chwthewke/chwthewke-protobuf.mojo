package net.chwthewke.maven.protobuf;

import java.io.File;

/**
 * @author Chewie
 */
public class ProtocPlugin {

    public ProtocPlugin( ) {
    }

    public ProtocPlugin( final String plugin, final File outputDirectory ) {
        this( );
        this.outputDirectory = outputDirectory;
        this.plugin = plugin;
    }

    public File getOutputDirectory( ) {
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
    private File outputDirectory;

}
