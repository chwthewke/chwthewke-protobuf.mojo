package net.chwthewke.maven.protobuf;

import java.io.File;

import com.google.common.base.Joiner;

class PathUtils {

    static String joinPaths( final String... elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

}
