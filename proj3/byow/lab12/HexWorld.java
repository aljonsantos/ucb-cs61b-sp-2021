package byow.lab12;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;

    public static void main(String[] args) {
        World world = new World(WIDTH, HEIGHT);
        Hexagon.addHexagonTesselation(4, 3, 1, 0, world);
        world.render();
    }
}
