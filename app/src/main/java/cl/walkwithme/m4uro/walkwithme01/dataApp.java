package cl.walkwithme.m4uro.walkwithme01;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.message.LedColor;

/**
 * Created by m4uro on 02-11-17.
 */

public abstract class dataApp {

    public static String macDevice = "";

    public static String url_server = "http://165.227.92.135/walkwithme/";

    public static LedColor blue = LedColor.create(0, 0, 255);
    public static LedColor red = LedColor.create(255, 0, 0);
    public static LedColor green = LedColor.create(0,255,0);
    public static LedColor off = LedColor.create(0,0,0);
}
