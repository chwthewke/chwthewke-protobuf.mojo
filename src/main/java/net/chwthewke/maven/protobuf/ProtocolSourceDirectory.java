package net.chwthewke.maven.protobuf;

import java.nio.file.Path;

import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.Commandline.Argument;

public class ProtocolSourceDirectory {

    public ProtocolSourceDirectory( final Path path ) {
        this.path = path;
    }

    public Path getPath( ) {
        return path;
    }

    public Arg arg( ) {
        final Argument argument = new Commandline.Argument( );
        argument.setValue( String.format( "-I%s", path.resolve( "**" ) ) );
        return argument;
    }

    private final Path path;

}
