package extrabiomes;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;
import extrabiomes.api.TerrainGenBlock;

public class WorldGenAutumnTree extends WorldGenerator {

	private static final int BASE_HEIGHT = 4;
	private static final int CANOPY_HEIGHT = 3;
	private static final int CANOPY_RADIUS_EXTRA_RADIUS = 0;
	private static final int MAX_VARIANCE_HEIGHT = 2;
	
	private static boolean isBlockSuitableForGrowing(final World world,
			final int x, final int y, final int z) {
		final int id = world.getBlockId(x, y, z);

		return id == Block.grass.blockID || id == Block.dirt.blockID;
	}
	
	private static boolean isRoomToGrow(final World world, final int x,
			final int y, final int z, final int height) {
		for (int i = y; i <= y + 1 + height; ++i) {

			if (i < 0 || i >= 256)
				return false;

			int radius = 1;

			if (i == y) {
				radius = 0;
			}

			if (i >= y + 1 + height - 2) {
				radius = 2;
			}

			for (int x1 = x - radius; x1 <= x + radius; ++x1) {
				for (int z1 = z - radius; z1 <= z + radius; ++z1) {
					final int id = world.getBlockId(x1, i, z1);

					if (id != 0 && !BlockControl.INSTANCE.isLeaves(id)
							&& id != Block.grass.blockID
							&& id != Block.dirt.blockID
							&& !BlockControl.INSTANCE.isWood(id)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private final MetaBlock leafBlock;
	private final MetaBlock woodBlock;

	public WorldGenAutumnTree(final boolean doBlockNotify,
			final TerrainGenBlock leaf, final TerrainGenBlock wood) {
		super(doBlockNotify);

		final BlockControl bc = BlockControl.INSTANCE;
		leafBlock = bc.getTerrainGenBlock(leaf);
		woodBlock = bc.getTerrainGenBlock(wood);
	}

	@Override
	public boolean generate(final World world, final Random rand, final int x,
			final int y, final int z) {
		final int height = rand.nextInt(MAX_VARIANCE_HEIGHT + 1) + BASE_HEIGHT;

		if (y < 1 || y + height + 1 > 256)
			return false;

		if (!isBlockSuitableForGrowing(world, x, y - 1, z))
			return false;

		if (!isRoomToGrow(world, x, y, z, height))
			return false;

		world.setBlock(x, y - 1, z, Block.dirt.blockID);
		growLeaves(world, rand, x, y, z, height);
		growTrunk(world, x, y, z, height);

		return true;
	}

	private void growLeaves(final World world, final Random rand, final int x,
			final int y, final int z, final int height) {
		for (int y1 = y - CANOPY_HEIGHT + height; y1 <= y + height; ++y1) {
			final int canopyRow = y1 - (y + height);
			final int radius = CANOPY_RADIUS_EXTRA_RADIUS + 1 - canopyRow / 2;

			for (int x1 = x - radius; x1 <= x + radius; ++x1) {
				final int xDistanceFromTrunk = x1 - x;

				for (int z1 = z - radius; z1 <= z + radius; ++z1) {
					int zDistanceFromTrunk = z1 - z;

					if ((Math.abs(xDistanceFromTrunk) != radius
							|| Math.abs(zDistanceFromTrunk) != radius || rand
							.nextInt(2) != 0 && canopyRow != 0)
							&& !Block.opaqueCubeLookup[world.getBlockId(x1, y1,
									z1)]) {
						this.setBlockAndMetadata(world, x1, y1, z1,
								leafBlock.blockId(), leafBlock.metadata());
					}
				}
			}
		}
	}

	private void growTrunk(final World world, final int x, final int y,
			final int z, final int height) {
		for (int y1 = 0; y1 < height; ++y1) {
			final int id = world.getBlockId(x, y + y1, z);

			if (id == 0 || BlockControl.INSTANCE.isLeaves(id))
				this.setBlockAndMetadata(world, x, y + y1, z,
						woodBlock.blockId(), woodBlock.metadata());
		}
	}
}
