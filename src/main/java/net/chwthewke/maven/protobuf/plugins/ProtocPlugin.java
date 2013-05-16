package net.chwthewke.maven.protobuf.plugins;

import org.apache.maven.plugin.MojoExecutionException;

public interface ProtocPlugin {

    void resolve( ) throws MojoExecutionException;

}
