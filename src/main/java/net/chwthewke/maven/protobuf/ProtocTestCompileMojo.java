package net.chwthewke.maven.protobuf;

import com.google.common.collect.ImmutableList;
import net.chwthewke.maven.protobuf.source.ProtocolSource;
import net.chwthewke.maven.protobuf.source.ProtocolSourceFactory;

import java.nio.file.Paths;

import static net.chwthewke.maven.protobuf.MojoType.TEST;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal test-compile
 * @phase generate-test-sources
 */
@SuppressWarnings( "unused" )
public class ProtocTestCompileMojo extends AbstractProtocCompileMojo {

    @Override
    protected MojoType runType( ) {
        return TEST;
    }

    @Override
    protected ImmutableList<ProtocolSource> getProtocolSources( final ProtocolSourceFactory protocolSourceFactory ) {

        return ImmutableList.<ProtocolSource>builder( )
            .addAll( super.getProtocolSources( protocolSourceFactory ) )
            .addAll( protocolSourceFactory.productionSourcesAsTestDependency( ) )
            .build( );
    }


    protected ImmutableList<String> defaultSourceDirectory( ) {
        return ImmutableList.of( Paths.get( "src", "test", "proto" ).toString( ) );
    }

}
