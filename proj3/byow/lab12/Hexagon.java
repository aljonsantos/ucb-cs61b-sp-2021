package byow.lab12;

import byow.TileEngine.TETile;

public class Hexagon {

    public static void makeHexagon(int s, TETile tile, int x, int y, World world) {
        if (s < 2) return;

        int len = s;
        int rowX = x + (s - 1);
        int rowMirrorY = y + (s * 2 - 1);

        for (int r = 0; r < s; r++) {
            drawRow(tile, len, rowX - r, y + r, world);
            drawRow(tile, len, rowX - r, rowMirrorY - r, world);
            len += 2;
        }
    }

    public static void addHexagon(int s, int x, int y, World world) {
        makeHexagon(s, World.randomTile(), x, y, world);
    }

    public static void addHexagonColumn(int s, int n, int x, int y, World world) {
        for (int i = 0; i < n; i++) {
            addHexagon(s, x, y, world);
            y += s * 2;
        }
    }

    public static void addHexagonTesselation(int s, int n, int x, int y, World world) {
        int hexagonXOffset = s * 2 - 1;
        int centerColX = x + (n - 1) * hexagonXOffset;
        int colN = n * 2 - 1;

        addHexagonColumn(s, colN, centerColX, y, world);
        for (int c = 1; c < n; c++) {
            addHexagonColumn(s, colN - c, centerColX - hexagonXOffset * c, y + s * c, world);
            addHexagonColumn(s, colN - c, centerColX + hexagonXOffset * c, y + s * c, world);

        }
    }

    private static void drawRow(TETile tile, int len, int x, int y, World world) {
        for (int i = 0; i < len; i++) {
            world.addTile(tile, x + i, y);
        }
    }
}
