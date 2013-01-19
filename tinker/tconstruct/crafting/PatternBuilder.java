package tinker.tconstruct.crafting;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tinker.tconstruct.TConstructContent;
import tinker.tconstruct.items.Pattern;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PatternBuilder
{
	public static PatternBuilder instance = new PatternBuilder();
	//Map items to their parts with a hashmap
	List<ItemKey> materials = new ArrayList<ItemKey>();
	HashMap materialSets = new HashMap<String, MaterialSet>();
	
	//We could use IRecipe if it wasn't tied to InventoryCrafting
	List<PatternKey> toolPatterns = new ArrayList<PatternKey>();

	/* Register methods */
	public void registerMaterial (ItemStack material, int value, String key)
	{
		materials.add(new ItemKey(material.getItem(), material.getItemDamage(), value, key));
	}

	public void registerMaterialSet (String key, ItemStack shard, ItemStack rod, int materialID)
	{
		materialSets.put(key, new MaterialSet(shard, rod, materialID));
		materials.add(new ItemKey(shard.getItem(), shard.getItemDamage(), 1, key));
	}
	
	// 1 + 2 = EVERYTHING
	public void registerFullMaterial (ItemStack material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
	{
		materials.add(new ItemKey(material.getItem(), material.getItemDamage(), value, key));
		materials.add(new ItemKey(shard.getItem(), shard.getItemDamage(), 1, key));
		materialSets.put(key, new MaterialSet(shard, rod, materialID));
	}

	public void addToolPattern (ItemStack pattern, Item toolPart)
	{
		toolPatterns.add(new PatternKey(pattern.getItem(), pattern.getItemDamage(), toolPart));
	}

	/* Build tool parts from patterns */
	public ItemStack[] getToolPart (ItemStack material, ItemStack pattern, ItemStack otherPattern)
	{
		if (material != null && pattern != null)
		{
			ItemKey key = getItemKey(material);
			Item toolPart = getMatchingPattern(pattern);
			
			if (key != null && toolPart != null)
			{
				MaterialSet mat = (MaterialSet) materialSets.get(key.key);
				int patternValue = ((Pattern)pattern.getItem()).getPatternCost(pattern.getItemDamage());
				int totalMaterial = key.value * material.stackSize;
				
				if (totalMaterial < patternValue) // Not enough material
					return null;
				
				else if ( patternValue == key.value ) //Material only
					return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), null }; 
				
				else
				{
					if (patternValue % 2 == 1)
					{
						return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), mat.shard.copy() }; //Material + shard
					}
					else
						return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), null };
				}
				/*if ( patternValue < totalMaterial )
				{
					if (otherPattern != null)
					{
						int otherValue = ((Pattern)otherPattern.getItem()).getPatternCost(otherPattern.getItemDamage());
						if (patternValue + otherValue <= key.value)
						{
							Item otherPart = getMatchingPattern(otherPattern);
							return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), new ItemStack(otherPart, 1, mat.materialID) }; //Material + Material
						}
					}
					return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), mat.shard.copy() }; //Material + Shard, copy to avoid weirdness with the itemstack reference
				}
				
				else if ( patternValue == key.value )
					return new ItemStack[] { new ItemStack(toolPart, 1, mat.materialID), null }; //Material only
				
				else
					return null; //Not a valid match*/
			}			
		}
		return null;
	}
	
	public int getPartID (ItemStack material)
	{
		if (material != null)
		{
			ItemKey key = getItemKey(material);
			if (key != null)
			{
				MaterialSet set = (MaterialSet) materialSets.get(key.key);
				return set.materialID;
			}
		}
		return -1;
	}
	
	public int getPartValue (ItemStack material)
	{
		if (material != null)
		{
			ItemKey key = getItemKey(material);
			if (key != null)
				return key.value;
		}
		return 0;
	}
	
	public ItemKey getItemKey (ItemStack material)
	{
		Item mat = material.getItem();
		int damage = material.getItemDamage();
		for (ItemKey ik : materials)
		{
			if ( mat == ik.item && (ik.damage == -1 || damage == ik.damage))
				return ik;
		}
		return null;
	}
	
	public Item getMatchingPattern (ItemStack pattern)
	{
		Item pat = pattern.getItem();
		int damage = pattern.getItemDamage();
		for (PatternKey pk : toolPatterns)
		{
			if (pat == pk.item && (pk.damage == -1 || damage == pk.damage))
				return pk.output;
		}
		return null;
	}
	
	//Small data classes. I would prefer the struct from C#, but we do what we can.
	public class ItemKey
	{
		public final Item item;
		public final int damage;
		public final int value;
		public final String key;
		public ItemKey(Item i, int d, int v, String s)
		{
			item = i;
			damage = d;
			value = v;
			key = s;
		}
	}
	
	public class PatternKey
	{
		public final Item item;
		public final int damage;
		public final Item output;
		public PatternKey(Item i, int d, Item o)
		{
			item = i;
			damage = d;
			output = o;
		}
	}
	
	public class MaterialSet
	{
		public final ItemStack shard;
		public final ItemStack rod;
		public final int materialID;

		public MaterialSet(ItemStack s, ItemStack r, int id)
		{
			shard = s;
			rod = r;
			materialID = id;
		}
	}
	
	//Helper Methods
	public void registerMaterial (Block material, int value, String key) { registerMaterial(new ItemStack(material, 1, -1), value, key); }
	public void registerMaterial (Item material, int value, String key) { registerMaterial(new ItemStack(material, 1, -1), value, key); }
	
	public void registerFullMaterial (Block material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
	{ registerFullMaterial(new ItemStack(material, 1, -1), value, key, shard, rod, materialID); }
	public void registerFullMaterial (Item material, int value, String key, ItemStack shard, ItemStack rod, int materialID)
	{ registerFullMaterial(new ItemStack(material, 1, -1), value, key, shard, rod, materialID); }
	public void registerFullMaterial (Block material, int value, String key, int materialID)
	{ registerFullMaterial(new ItemStack(material, 1, -1), value, key, new ItemStack(TConstructContent.toolShard, 1, materialID), new ItemStack(TConstructContent.toolRod, 1, materialID), materialID); }
	public void registerFullMaterial (Item material, int value, String key, int materialID)
	{ registerFullMaterial(new ItemStack(material, 1, -1), value, key, new ItemStack(TConstructContent.toolShard, 1, materialID), new ItemStack(TConstructContent.toolRod, 1, materialID), materialID); }
}