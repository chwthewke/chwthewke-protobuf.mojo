package net.chwthewke.maven.protobuf;

import net.chwthewke.maven.protobuf.plugins.ProtocPlugin;
import net.chwthewke.maven.protobuf.protoc.ProtocExecutable;
import net.chwthewke.maven.protobuf.services.ServiceProvider;
import net.chwthewke.maven.protobuf.source.ProtocolSource;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.Commandline;

import com.google.common.collect.ImmutableList;

public class ProtocRequest {

    public ProtocRequest( final ServiceProvider serviceProvider,
            final ImmutableList<ProtocolSource> protocolSources,
            final ImmutableList<ProtocPlugin> plugins,
            final ProtocExecutable protocExecutable ) {
        this.serviceProvider = serviceProvider;
        this.protocolSources = protocolSources;
        this.plugins = plugins;
        this.protocExecutable = protocExecutable;
    }

    public ImmutableList<ProtocolSource> getProtocolSources( ) {
        return protocolSources;
    }

    public ImmutableList<ProtocPlugin> getPlugins( ) {
        return plugins;
    }

    public void processRequirements( ) throws MojoExecutionException {
        for ( final ProtocolSource protocolSource : protocolSources )
            protocolSource.resolve( );
        for ( final ProtocPlugin protocPlugin : plugins )
            protocPlugin.resolve( );
        protocExecutable.resolve( );
    }

    public Commandline execute( ) throws MojoExecutionException {
        final Commandline commandline = new Commandline( );
        commandline.setWorkingDirectory( serviceProvider.getBasedir( ).toFile( ) );

        commandline.setExecutable( protocExecutable.getPath( ).toString( ) );

        for ( final ProtocPlugin plugin : plugins )
            for ( final Arg arg : plugin.args( ) )
                commandline.addArg( arg );

        for ( final ProtocolSource source : protocolSources )
            for ( final Arg includeArg : source.includeArgs( ) )
                commandline.addArg( includeArg );

        for ( final ProtocolSource source : protocolSources )
            for ( final Arg sourceArg : source.sourcesArgs( ) )
                commandline.addArg( sourceArg );

        return commandline;
    }

    private final ServiceProvider serviceProvider;

    // Protocol sources and source dependencies
    private final ImmutableList<ProtocolSource> protocolSources;
    // Plugins to call protoc with
    private final ImmutableList<ProtocPlugin> plugins;
    // 'protoc' executable
    private final ProtocExecutable protocExecutable;
}
