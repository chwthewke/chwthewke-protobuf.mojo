package net.chwthewke.maven.protobuf.protoc;

import java.nio.file.Path;

import net.chwthewke.maven.protobuf.services.BuildInput;

import org.apache.maven.plugin.MojoExecutionException;

public interface ProtocExecutable extends BuildInput {

    @Override
    boolean collectChanges( ) throws MojoExecutionException;

    Path getPath( );

}
