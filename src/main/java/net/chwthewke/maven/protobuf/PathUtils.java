package net.chwthewke.maven.protobuf;

import java.io.File;

import com.google.common.base.Joiner;

@Deprecated
public class PathUtils {

    public static String joinPaths( final String... elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

    public static String joinPaths( final Iterable<String> elements ) {
        return Joiner.on( File.separator ).join( elements );
    }

    public static String fixPath( final String path ) {
        return path.replace( '\\', File.separatorChar ).replace( '/', File.separatorChar );
    }

}
