package me.earth.phobos.util;

import me.earth.phobos.manager.CapeManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public
class CapeUtil {

    public static
    List < String > readURL ( ) {
        List < String > s = new ArrayList <> ( );
        try {
            final URL url = new URL ( CapeManager.capeURL );
            BufferedReader bufferedReader = new BufferedReader ( new InputStreamReader ( url.openStream ( ) ) );
            String cape;
            while ( ( cape = bufferedReader.readLine ( ) ) != null ) {
                s.add ( cape );
            }
        } catch ( Exception ignored ) {

        }
        return s;
    }
}