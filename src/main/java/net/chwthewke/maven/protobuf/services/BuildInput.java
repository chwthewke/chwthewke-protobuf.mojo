package net.chwthewke.maven.protobuf.services;

import org.apache.maven.plugin.MojoExecutionException;

public interface BuildInput {
    boolean collectChanges( ) throws MojoExecutionException;
}
