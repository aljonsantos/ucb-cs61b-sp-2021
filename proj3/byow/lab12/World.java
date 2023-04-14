package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class World {
    private int width;
    private int height;
    private TETile[][] tiles;
    private TERenderer ter;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TETile[width][height];
        this.ter = new TERenderer();
        
        ter.initialize(width, height);
        this.initialize();
    }

    private void initialize() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void addTile(TETile tile, int x, int y) {
        tiles[x][y] = tile;
    }

    public static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.TREE;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.SAND;
            case 4: return Tileset.GRASS;
            default: return Tileset.NOTHING;
        }
    }

    public void render() {
        ter.renderFrame(tiles);
    }

}
