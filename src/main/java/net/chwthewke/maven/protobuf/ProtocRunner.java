package net.chwthewke.maven.protobuf;

import net.chwthewke.maven.protobuf.services.ServiceProvider;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

public class ProtocRunner {

    public void runProtocSubprocess( final Commandline commandline ) throws MojoExecutionException,
            MojoFailureException {
        try
        {
            serviceProvider.getLog( ).debug( "Running protoc with " + commandline );
            final int exitValue =
                    CommandLineUtils.executeCommandLine( commandline, infoStreamConsumer, errorStreamConsumer );

            if ( exitValue != 0 )
                throw new MojoFailureException( String.format( "protoc encountered an error (%d)", exitValue ) );

            serviceProvider.getLog( ).info( "protoc executed successfully." );
        }
        catch ( final CommandLineException e )
        {
            throw new MojoExecutionException( "Error creating protoc process", e );
        }
    }

    public ProtocRunner( final ServiceProvider serviceProvider ) {
        this.serviceProvider = serviceProvider;
    }

    private final StreamConsumer infoStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( final String line ) {
            serviceProvider.getLog( ).info( "[PROTOC]: " + line );
        }
    };

    private final StreamConsumer errorStreamConsumer = new StreamConsumer( ) {
        @Override
        public void consumeLine( final String line ) {
            serviceProvider.getLog( ).error( "[PROTOC]: " + line );
        }
    };
    private final ServiceProvider serviceProvider;

}
