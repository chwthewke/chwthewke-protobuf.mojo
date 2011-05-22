package net.chwthewke.maven.protobuf;

import java.io.File;

import com.google.common.base.Joiner;

class PathUtils {

    static String joinPaths( final String... elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

    static String joinPaths( final Iterable<String> elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

    static String fixPath( final String path ) {
        return path.replace( '\\', File.separatorChar ).replace( '/', File.separatorChar );
    }

}
