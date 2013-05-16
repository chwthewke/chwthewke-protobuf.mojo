package net.chwthewke.maven.protobuf.source;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Arg;

public interface ProtocolSource {

    void resolve( ) throws MojoExecutionException;

    List<Arg> includeArgs( );

    List<Arg> sourcesArgs( );

}
