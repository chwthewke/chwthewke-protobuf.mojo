package net.chwthewke.maven.protobuf;

import net.chwthewke.maven.protobuf.plugins.ProtocPlugin;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutable;
import net.chwthewke.maven.protobuf.source.ProtocolSource;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Commandline;

import com.google.common.collect.ImmutableList;

public class ProtocRequest {

    public ProtocRequest( final ImmutableList<ProtocolSource> protocolSources,
            final ImmutableList<ProtocPlugin> plugins,
            final ProtocExecutable protocExecutable ) {
        this.protocolSources = protocolSources;
        this.plugins = plugins;
        this.protocExecutable = protocExecutable;
    }

    public void processRequirements( ) throws MojoExecutionException {
        for ( final ProtocolSource protocolSource : protocolSources )
            protocolSource.resolve( );
    }

    public Commandline execute( final ProtocExecutable executable ) {
        throw new UnsupportedOperationException( );
    }

    // Protocol sources and source dependencies
    private final ImmutableList<ProtocolSource> protocolSources;
    // Plugins to call protoc with
    private final ImmutableList<ProtocPlugin> plugins;
    // 'protoc' executable
    private final ProtocExecutable protocExecutable;
}
