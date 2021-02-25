package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.init.DTConfigs;
import com.ferreusveritas.dynamictrees.init.DTRegistries;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.dropcreators.FruitDropCreator;
import com.ferreusveritas.dynamictrees.systems.genfeatures.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OakTree extends VanillaTreeFamily {
	
	public static class OakSpecies extends Species {
		
		OakSpecies(TreeFamily treeFamily) {
			super(treeFamily.getRegistryName(), treeFamily);
			
			//Oak trees are about as average as you can get
			setBasicGrowingParameters(0.3f, 12.0f, upProbability, lowestBranchHeight, 0.8f);
			
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.50f);
			envFactor(Type.DRY, 0.50f);
			envFactor(Type.FOREST, 1.05f);
			
			if(DTConfigs.worldGen.get() && !DTConfigs.enableAppleTrees.get()) {//If we've disabled apple trees we still need some way to get apples.
				addDropCreator(new FruitDropCreator());
			}

			setupStandardSeedDropping();
			setupStandardStickDropping();

			this.setPrimitiveSapling(Blocks.OAK_SAPLING);

			this.addGenFeature(GenFeatures.BEE_NEST);
		}
		
		@Override
		public boolean isBiomePerfect(RegistryKey<Biome> biome) {
			return BiomeDictionary.hasType(biome, Type.FOREST) && BiomeDictionary.hasType(biome, Type.OVERWORLD);
		}

		// TODO: Rot types.

		@Override
		public boolean rot(IWorld world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
			if(super.rot(world, pos, neighborCount, radius, random, rapid)) {
				if(radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(LightType.SKY, pos) < 4) {
					world.setBlockState(pos, random.nextInt(3) == 0 ? DTRegistries.blockStates.RED_MUSHROOM : DTRegistries.blockStates.BROWN_MUSHROOM, 3);//Change branch to a mushroom
					world.setBlockState(pos.down(), DTRegistries.blockStates.PODZOL, 3);//Change rooty dirt to Podzol
				}
				return true;
			}
			
			return false;
		}
		
	}
	
	/**
	 * Swamp Oaks are just Oaks with slight growth differences that can generate in water
	 * and with vines hanging from their leaves.
	 */
	public class SwampOakSpecies extends Species {
				
		SwampOakSpecies(TreeFamily treeFamily) {
			super(new ResourceLocation(treeFamily.getRegistryName().getNamespace(), "swamp_" + treeFamily.getRegistryName().getPath()), treeFamily);
			
			setBasicGrowingParameters(0.3f, 12.0f, upProbability, lowestBranchHeight, 0.8f);
			
			envFactor(Type.COLD, 0.50f);
			envFactor(Type.DRY, 0.50f);
			
			setupStandardSeedDropping();

			this.shouldSpawnPredicate = (world, trunkPos) -> BiomeDictionary.hasType(getBiomeKey(world.getBiome(trunkPos)), Type.SWAMP);
			
			//Add species features
			this.addGenFeature(GenFeatures.VINES.with(VinesGenFeature.MAX_LENGTH, 7)
					.with(VinesGenFeature.VERTICAL_SPREAD, 30f).with(VinesGenFeature.RAY_DISTANCE, 6f)
					.with(VinesGenFeature.QUANTITY, 24)); // Generate Vines
		}
		
		@Override
		public boolean isBiomePerfect(RegistryKey<Biome> biome) {
			return BiomeDictionary.hasType(biome, Type.SWAMP);
		}
		
		@Override
		public boolean isAcceptableSoilForWorldgen(IWorld world, BlockPos pos, BlockState soilBlockState) {
			
			if(DTConfigs.enableSwampOaksInWater.get() && soilBlockState.getBlock() == Blocks.WATER) {
				Biome biome = world.getBiome(pos);
				if(BiomeDictionary.hasType(getBiomeKey(biome), Type.SWAMP)) {
					BlockPos down = pos.down();
					if(isAcceptableSoil(world, down, world.getBlockState(down))) {
						return true;
					}
				}
			}
			
			return super.isAcceptableSoilForWorldgen(world, pos, soilBlockState);
		}
		
		//Swamp Oaks are just oaks in a swamp..  So they have the same seeds
		@Override
		public ItemStack getSeedStack(int qty) {
			return getCommonSpecies().getSeedStack(qty);
		}
		
		//Swamp Oaks are just oaks in a swamp..  So they have the same seeds
		@Override
		public Optional<Seed> getSeed() {
			return getCommonSpecies().getSeed();
		}
		
		@Override
		public boolean rot(IWorld world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
			if(super.rot(world, pos, neighborCount, radius, random, rapid)) {
				if(radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(LightType.SKY, pos) < 4) {
					world.setBlockState(pos, random.nextInt(3) == 0 ? DTRegistries.blockStates.RED_MUSHROOM : DTRegistries.blockStates.BROWN_MUSHROOM, 3);//Change branch to a mushroom
					world.setBlockState(pos.down(), DTRegistries.blockStates.PODZOL, 3);//Change rooty dirt to Podzol
				}
				return true;
			}
			
			return false;
		}
		
	}
	
	/**
	 * This species drops no seeds at all.  One must craft the seed from an apple.
	 */
	public static class AppleOakSpecies extends Species {
		
		private static final String speciesName = "apple";
		
		public AppleOakSpecies(TreeFamily treeFamily) {
			super(new ResourceLocation(treeFamily.getRegistryName().getNamespace(), speciesName), treeFamily);
			
			setRequiresTileEntity(true);
			
			//A bit stockier, smaller and slower than your basic oak
			setBasicGrowingParameters(0.4f, 10.0f, 1, 4, 0.7f);

			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.DRY, 0.25f);
			
			generateSeed();
			generateSapling();

			DTRegistries.appleBlock.setSpecies(this);
			this.addGenFeature(GenFeatures.FRUIT.with(FruitGenFeature.RAY_DISTANCE, 4f));
		}
		
		@Override
		public boolean isBiomePerfect(RegistryKey<Biome> biome) {
			return biome.equals(Biomes.PLAINS);
		}
		
	}

	Species swampSpecies;
	Species appleSpecies;
	
	public OakTree() {
		super(DynamicTrees.VanillaWoodTypes.oak);
		hasConiferVariants = true;
		addConnectableVanillaLeaves((state) -> state.getBlock() == Blocks.OAK_LEAVES);
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new OakSpecies(this));
		swampSpecies = new SwampOakSpecies(this);
		appleSpecies = new AppleOakSpecies(this);
	}
	
	@Override
	public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
		super.registerSpecies(speciesRegistry);
		speciesRegistry.register(swampSpecies);
		speciesRegistry.register(appleSpecies);
	}

	@Override
	public List<Block> getRegisterableBlocks(List<Block> blockList) {
		appleSpecies.getSapling().ifPresent(blockList::add);
		return super.getRegisterableBlocks(blockList);
	}

	@Override
	public List<Item> getRegisterableItems(List<Item> itemList) {
		appleSpecies.getSeed().ifPresent(itemList::add);//Since we generated the apple species internally we need to let the seed out to be registered.
		return super.getRegisterableItems(itemList);
	}

	@Override
	public boolean isThick() {
		return false;
	}

}
