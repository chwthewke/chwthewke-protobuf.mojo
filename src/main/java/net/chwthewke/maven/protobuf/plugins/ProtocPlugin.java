package net.chwthewke.maven.protobuf.plugins;

import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Arg;

public interface ProtocPlugin {

    void resolve( ) throws MojoExecutionException;

    List<Arg> args( ) throws MojoExecutionException;

    Path getOutputDirectory( );

    boolean addToSources( );

}
