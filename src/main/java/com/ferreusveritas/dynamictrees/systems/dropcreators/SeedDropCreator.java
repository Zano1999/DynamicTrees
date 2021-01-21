package com.ferreusveritas.dynamictrees.systems.dropcreators;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.treedata.IDropCreator;
import com.ferreusveritas.dynamictrees.event.SeedVoluntaryDropEvent;
import com.ferreusveritas.dynamictrees.init.DTConfigs;
import com.ferreusveritas.dynamictrees.trees.Species;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class SeedDropCreator implements IDropCreator {
	
	protected final float rarity;
	protected ItemStack customSeed = ItemStack.EMPTY;
	
	public SeedDropCreator() {
		this(1.0f);
	}
	
	public SeedDropCreator(float rarity) {
		this.rarity = rarity;
	}
	
	//Set a custom seed if for some reason the tree should not drop its own seed
	//Example: Tree A drops seeds of tree B
	public SeedDropCreator setCustomSeedDrop (ItemStack fruitItem){
		this.customSeed = fruitItem;
		return this;
	}
	
	//Provided for customization via override
	protected float getHarvestRarity() {
		return rarity;
	}
	
	//Provided for customization via override
	protected float getVoluntaryRarity() {
		return rarity;
	}
	
	//Provided for customization via override
	protected float getLeavesRarity() {
		return rarity;
	}
	
	//Allows for overriding species seed drop if a custom seed is set.
	protected ItemStack getSeedStack(Species species){
		if (customSeed.isEmpty()){
			return species.getSeedStack(1);
		} else {
			return customSeed;
		}
	}
	
	@Override
	public ResourceLocation getName() {
		return new ResourceLocation(DynamicTrees.MODID, "seed");
	}
	
	@Override
	public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
		if((1 / 64f) * getHarvestRarity() > random.nextFloat()) {//1 in 64 chance to drop a seed on destruction..	
			dropList.add(getSeedStack(species));
		}
		return dropList;
	}
	
	@Override
	public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, Random random, List<ItemStack> dropList, int soilLife) {
		if(getVoluntaryRarity() * DTConfigs.seedDropRate.get() > random.nextFloat()) {
			dropList.add(getSeedStack(species));
			SeedVoluntaryDropEvent seedDropEvent = new SeedVoluntaryDropEvent(world, rootPos, species, dropList);
			MinecraftForge.EVENT_BUS.post(seedDropEvent);
			if(seedDropEvent.isCanceled()) {
				dropList.clear();
			}
		}
		return dropList;
	}
	
	@Override
	public List<ItemStack> getLeavesDrop(World access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
		int chance = 20; //See BlockLeaves#getSaplingDropChance(state);
		//Hokey fortune stuff here to match Vanilla logic.
		if (fortune > 0) {
			chance -= 2 << fortune;
			if (chance < 10) { 
				chance = 10;
			}
		}
		
		if(random.nextInt((int) (chance / getLeavesRarity())) == 0) {
			dropList.add(getSeedStack(species));
		}
		
		return dropList;
	}
	
	@Override
	public List<ItemStack> getLogsDrop(World world, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, float volume) {
		return dropList;
	}
	
	
	
}