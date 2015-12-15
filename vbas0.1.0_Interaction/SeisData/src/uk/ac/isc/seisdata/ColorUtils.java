package uk.ac.isc.seisdata;

import java.awt.Color;
import java.awt.Paint;

/**
 * Color API to generate a group of color sets
 *
 * @author hui
 */
public final class ColorUtils {

    //five colors picked from ColorBrewer
    public static Paint[] createColorBrewerFiveQualColors() {

        return new Paint[]{
            new Color(228, 26, 28),
            new Color(55, 126, 184),
            new Color(77, 175, 74),
            new Color(152, 78, 163),
            new Color(255, 127, 0)
        };

    }

    //ten colors picked from ColorBrewer
    public static Paint[] createColorBrewerTenQualColors() {

        return new Paint[]{
            new Color(166, 206, 227),
            new Color(31, 120, 180),
            new Color(178, 223, 138),
            new Color(51, 160, 44),
            new Color(251, 154, 153),
            new Color(227, 26, 28),
            new Color(253, 191, 111),
            new Color(255, 127, 0),
            new Color(202, 178, 214),
            new Color(106, 61, 154)
        };

    }

    //old color scheme for seismicty map
    public static Paint[] createOldSeismicityPaintArray() {

        return new Paint[]{
            new Color(255, 0, 0),
            new Color(255, 250, 0),
            new Color(139, 69, 19),
            new Color(154, 255, 154),
            new Color(0, 139, 69),
            new Color(135, 206, 255),
            new Color(0, 0, 255),
            new Color(190, 190, 190),
            new Color(255, 0, 255)
        };

    }

    //the initial color scheme set by Min
    public static Paint[] createInitialPaintArray() {

        return new Paint[]{
            new Color(255, 0, 0),
            new Color(255, 182, 1),
            new Color(1, 122, 255),
            new Color(192, 125, 64),
            new Color(182, 1, 255),
            new Color(213, 255, 1),
            new Color(0, 128, 0),
            new Color(255, 0, 255)
        };

    }

    // one optimised color scheme
    public static Paint[] createSeismicityPaintArray1() {

        return new Paint[]{
            new Color(251, 18, 4),
            new Color(255, 182, 1),
            new Color(24, 163, 255),
            new Color(104, 50, 1),
            new Color(98, 13, 118),
            new Color(214, 255, 6),
            new Color(0, 103, 0),
            new Color(240, 41, 228)
        };

    }

    // one optimised color scheme
    public static Paint[] createSeismicityPaintArray2() {

        return new Paint[]{
            new Color(250, 83, 14),
            new Color(208, 174, 0),
            new Color(26, 145, 219),
            new Color(124, 98, 46),
            new Color(55, 8, 183),
            new Color(32, 237, 4),
            new Color(1, 150, 72),
            new Color(237, 61, 231)
        };

    }

    // one optimised color scheme
    public static Paint[] createSeismicityPaintArray3() {

        return new Paint[]{
            new Color(219, 21, 6),
            new Color(251, 185, 15),
            new Color(95, 134, 185),
            new Color(149, 123, 53),
            new Color(118, 40, 173),
            new Color(126, 229, 7),
            new Color(13, 96, 16),
            new Color(238, 47, 254)
        };

    }

    //color scheme for depth map
    public static Paint[] createDepthViewPaintArray() {

        return new Paint[]{
            new Color(234, 202, 198),
            new Color(237, 233, 221),
            new Color(210, 219, 233),
            new Color(227, 209, 181),
            new Color(207, 184, 230),
            new Color(218, 233, 206),
            new Color(180, 230, 180),
            new Color(238, 216, 242),};

    }

}
