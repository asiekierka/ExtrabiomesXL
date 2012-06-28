package extrabiomes;

import java.util.Random;

import extrabiomes.api.MetaBlock;
import extrabiomes.api.TerrainGenBlock;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

public class WorldGenFirTree2 extends WorldGenerator {

	private final MetaBlock leafBlock;
	private final MetaBlock woodBlock;

	public WorldGenFirTree2(boolean doNotify) {
		super(doNotify);
		BlockControl bc = BlockControl.INSTANCE;
		leafBlock = bc.getTerrainGenBlock(TerrainGenBlock.FIR_LEAVES);
		woodBlock = bc.getTerrainGenBlock(TerrainGenBlock.FIR_WOOD);
	}

	private static void setBlockandMetadataIfChunkExists(World world, int x, int y, int z, int blockId, int metadata){
		if (world.getChunkProvider().chunkExists(x >> 4, z >> 4))
			world.setBlockAndMetadata(x, y, z, blockId, metadata);
	}
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int height = rand.nextInt(16) + 32;
		int j = 1 + rand.nextInt(12);
		int k = height - j;
		int l = 2 + rand.nextInt(9);

		if (y < 1 || y + height + 1 > 256) {
			return false;
		}

		for (int y1 = y; y1 <= y + 1 + height; y1++) {

			if (y1 < 0 && y1 >= 256)
				return false;

			int k1 = 1;

			if (y1 - y < j) {
				k1 = 0;
			} else {
				k1 = l;
			}

			for (int x1 = x - k1; x1 <= x + k1; x1++) {
				for (int z1 = z - k1; z1 <= z + k1; z1++) {

					if (!world.getChunkProvider().chunkExists(x1 >> 4, z1 >> 4))
						return false;

					int id = world.getBlockId(x1, y1, z1);

					if (id != 0 && !BlockControl.INSTANCE.isLeaves(id)) {
						return false;
					}
				}
			}
		}

		int j1 = world.getBlockId(x, y - 1, z);

		if (j1 != Block.grass.blockID && j1 != Block.dirt.blockID
				|| y >= 256 - height - 1) {
			return false;
		}

		world.setBlock(x, y - 1, z, Block.dirt.blockID);
		world.setBlock(x - 1, y - 1, z, Block.dirt.blockID);
		world.setBlock(x, y - 1, z - 1, Block.dirt.blockID);
		world.setBlock(x - 1, y - 1, z - 1, Block.dirt.blockID);
		int l1 = rand.nextInt(2);
		int j2 = 1;
		boolean flag1 = false;

		for (int i3 = 0; i3 <= k; i3++) {
			int k3 = (y + height) - i3;

			for (int i4 = x - l1; i4 <= x + l1; i4++) {
				int k4 = i4 - x;

				for (int l4 = z - l1; l4 <= z + l1; l4++) {
					int i5 = l4 - z;

					if ((Math.abs(k4) != l1 || Math.abs(i5) != l1 || l1 <= 0)
							&& !Block.opaqueCubeLookup[world.getBlockId(i4, k3,
									l4)]) {
						setBlockandMetadataIfChunkExists(world, i4, k3, l4, leafBlock.blockId(),
								leafBlock.metadata());
						setBlockandMetadataIfChunkExists(world, i4 - 1, k3, l4,
								leafBlock.blockId(), leafBlock.metadata());
						setBlockandMetadataIfChunkExists(world, i4, k3, l4 - 1,
								leafBlock.blockId(), leafBlock.metadata());
						setBlockandMetadataIfChunkExists(world, i4 - 1, k3, l4 - 1,
								leafBlock.blockId(), leafBlock.metadata());
					}
				}
			}

			if (l1 >= j2) {
				l1 = ((flag1) ? 1 : 0);
				flag1 = true;

				if (++j2 > l) {
					j2 = l;
				}
			} else {
				l1++;
			}
		}

		int j3 = rand.nextInt(3);

		for (int l3 = 0; l3 < height - j3; l3++) {
			int j4 = world.getBlockId(x, y + l3, z);

			if (j4 == 0 || BlockControl.INSTANCE.isLeaves(j4)) {
				setBlockAndMetadata(world, x, y + l3, z, woodBlock.blockId(), woodBlock.metadata());
				setBlockAndMetadata(world, x - 1, y + l3, z,
						woodBlock.blockId(), woodBlock.metadata());
				setBlockAndMetadata(world, x, y + l3, z - 1,
						woodBlock.blockId(), woodBlock.metadata());
				setBlockAndMetadata(world, x - 1, y + l3, z - 1,
						woodBlock.blockId(), woodBlock.metadata());
			}
		}

		return true;
	}
}
