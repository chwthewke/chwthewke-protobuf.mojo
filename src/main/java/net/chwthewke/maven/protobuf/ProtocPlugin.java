package net.chwthewke.maven.protobuf;

import java.io.File;

public class ProtocPlugin {

    public File getOutputDirectory( ) {
        return outputDirectory;
    }

    public String getPlugin( ) {
        return plugin;
    }

    private File outputDirectory;

    private String plugin;

}
