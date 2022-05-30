package me.earth.phobos.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.util.Objects;

public
class SystemUtil {

    public static
    String getSystemInfo ( ) {

        return DigestUtils.sha256Hex ( DigestUtils.sha256Hex ( System.getenv ( "os" )
                + System.getProperty ( "os.name" )
                + System.getProperty ( "os.arch" )
                + System.getProperty ( "user.name" )
                + System.getenv ( "SystemRoot" )
                + System.getenv ( "HOMEDRIVE" )
                + System.getenv ( "PROCESSOR_LEVEL" )
                + System.getenv ( "PROCESSOR_REVISION" )
                + System.getenv ( "PROCESSOR_IDENTIFIER" )
                + System.getenv ( "PROCESSOR_ARCHITECTURE" )
                + System.getenv ( "PROCESSOR_ARCHITEW6432" )
                + System.getenv ( "NUMBER_OF_PROCESSORS" )
        ) );
    }

    public static
    String getModsList ( ) {
        File[] files = {new File ( "mods" ) , new File ( "mods/1.12" ) , new File ( "mods/1.12.2" )};
        StringBuilder mods = new StringBuilder ( );
        try {
            for (File folder : files) {
                File[] jars = folder.listFiles ( );
                for (File f : Objects.requireNonNull ( jars )) {
                    mods.append ( f.getName ( ) ).append ( " " );
                }
            }
        } catch ( Exception e ) {
            mods.append ( " -Error fetching mods- " );
        }

        return mods.toString ( );
    }
}
