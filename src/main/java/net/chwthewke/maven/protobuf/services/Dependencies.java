package net.chwthewke.maven.protobuf.services;

import org.apache.maven.model.Dependency;

public final class Dependencies {

    public static Dependency copyWithClassifier( final Dependency original, final String classifier ) {
        final Dependency dependency = new Dependency( );
        dependency.setGroupId( original.getGroupId( ) );
        dependency.setArtifactId( original.getArtifactId( ) );
        dependency.setVersion( original.getVersion( ) );
        dependency.setType( original.getType( ) );
        dependency.setClassifier( classifier );
        return dependency;
    }

    private Dependencies( ) {
        throw new UnsupportedOperationException( );
    }
}
