package net.chwthewke.maven.protobuf;

import com.google.common.collect.ImmutableList;

import java.nio.file.Paths;

/**
 * Goal which executes the protoc compiler.
 * 
 * @requiresProject
 * @goal compile
 * @phase generate-sources
 */
@SuppressWarnings( "unused" )
public class ProtocCompileMojo extends AbstractProtocCompileMojo {

    @Override
    protected MojoType runType( ) {
        return MojoType.PRODUCTION;
    }

    protected ImmutableList<String> defaultSourceDirectory( ) {
        return ImmutableList.of( Paths.get( "src", "main", "proto" ).toString( ) );
    }

}
