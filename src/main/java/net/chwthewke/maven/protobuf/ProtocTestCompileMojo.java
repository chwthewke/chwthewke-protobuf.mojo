package net.chwthewke.maven.protobuf;

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
    protected ProtocolSourceArchiverClassifiers runType( ) {
        return ProtocolSourceArchiverClassifiers.TEST;
    }

}
