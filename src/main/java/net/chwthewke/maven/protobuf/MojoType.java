package net.chwthewke.maven.protobuf;

import net.chwthewke.maven.protobuf.services.PluginConstants;

import java.nio.file.Path;

import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_DEPENDENCIES_CLASSIFIER;
import static net.chwthewke.maven.protobuf.services.PluginConstants.PROTO_SOURCE_CLASSIFIER;

public enum MojoType {
    PRODUCTION( "proto" ),
    TEST( "test-proto" );

    private MojoType( final String baseClassifier ) {
        this.sourceArchive = new SourceArchive( baseClassifier + PROTO_SOURCE_CLASSIFIER );
        this.dependenciesArchive = new SourceArchive( baseClassifier + PROTO_DEPENDENCIES_CLASSIFIER );
    }

    public boolean isTest( ) {
        return this == TEST;
    }

    public SourceArchive getSourceArchive( ) {
        return sourceArchive;
    }

    public SourceArchive getDependenciesArchive( ) {
        return dependenciesArchive;
    }

    private final SourceArchive sourceArchive;
    private final SourceArchive dependenciesArchive;

    public static class SourceArchive {
        public String getClassifier( ) {
            return classifier;
        }

        public Path getPath( ) {
            return PluginConstants.PLUGIN_WORK.resolve( classifier + ".jar" );
        }

        public SourceArchive( final String classifier ) {
            this.classifier = classifier;
        }

        public String toString( ) {
            return String.format( "%s proto archive %s", getClassifier( ), getPath( ) );
        }

        private final String classifier;
    }

}
