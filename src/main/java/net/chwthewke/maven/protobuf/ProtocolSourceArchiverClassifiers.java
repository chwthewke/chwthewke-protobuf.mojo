package net.chwthewke.maven.protobuf;

import net.chwthewke.maven.protobuf.services.PluginConstants;

import java.nio.file.Path;

public enum ProtocolSourceArchiverClassifiers {
    PRODUCTION( "proto-sources", "proto-deps" ),
    TEST( "test-proto-sources", "test-proto-deps" );

    private ProtocolSourceArchiverClassifiers( final String sourceClassifier, final String dependenciesClassifier ) {
        this.sourceArchive = new SourceArchive( sourceClassifier );
        this.dependenciesArchive = new SourceArchive( dependenciesClassifier );
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

        private final String classifier;
    }

}
