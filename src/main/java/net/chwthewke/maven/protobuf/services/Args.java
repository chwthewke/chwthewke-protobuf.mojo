package net.chwthewke.maven.protobuf.services;

import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.Commandline;

public final class Args {

    public static Arg of( final String arg ) {
        final Commandline.Argument argument = new Commandline.Argument( );
        argument.setValue( arg );
        return argument;
    }

    private Args( ) {
        throw new UnsupportedOperationException( );
    }
}
