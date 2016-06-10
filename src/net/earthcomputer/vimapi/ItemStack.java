package net.earthcomputer.vimapi;

import net.earthcomputer.vimapi.nbt.NBTCompound;

public class ItemStack {

	private String itemName;
	private int damage;
	private int stackSize;
	private NBTCompound tagCompound;

	public static final int WILDCARD_DAMAGE = Short.MAX_VALUE;

	public ItemStack(String itemName) {
		this(itemName, 0);
	}

	public ItemStack(String itemName, int damage) {
		this(itemName, damage, 1);
	}

	public ItemStack(String itemName, int damage, int stackSize) {
		this(itemName, damage, stackSize, null);
	}

	public ItemStack(String itemName, int damage, int stackSize, NBTCompound tagCompound) {
		this.itemName = itemName;
		this.damage = damage;
		this.stackSize = stackSize;
		this.tagCompound = tagCompound;
	}

	public String getItemName() {
		return itemName;
	}

	public int getDamage() {
		return damage;
	}

	public int getStackSize() {
		return stackSize;
	}

	public NBTCompound getTagCompound() {
		return tagCompound;
	}

	public boolean hasTagCompound() {
		return tagCompound != null;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public void setTagCompound(NBTCompound tagCompound) {
		this.tagCompound = tagCompound;
	}

}
