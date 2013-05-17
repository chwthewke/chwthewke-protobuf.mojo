package net.chwthewke.maven.protobuf.source;

import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Arg;

public interface ProtocolSource {

    void resolve( ) throws MojoExecutionException;

    Iterable<Path> getSourcePaths( );

    Iterable<Path> getIncludeOnlyPaths( );

    List<Arg> includeArgs( );

    List<Arg> sourcesArgs( );

}
