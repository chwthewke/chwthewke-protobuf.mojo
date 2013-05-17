package net.chwthewke.maven.protobuf.protoc;

import java.nio.file.Path;

import org.apache.maven.plugin.MojoExecutionException;

public interface ProtocExecutable {

    void resolve( ) throws MojoExecutionException;

    Path getPath( );

}
