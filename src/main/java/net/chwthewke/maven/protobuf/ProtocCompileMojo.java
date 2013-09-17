package net.chwthewke.maven.protobuf;

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
    protected ProtocolSourceArchiverClassifiers runType( ) {
        return ProtocolSourceArchiverClassifiers.PRODUCTION;
    }

}
