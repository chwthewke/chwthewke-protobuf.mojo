package net.chwthewke.maven.protobuf;

import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;

public interface ProtocExecutable {

    void prepare( ) throws MojoExecutionException;

    Path getPath( );

}
