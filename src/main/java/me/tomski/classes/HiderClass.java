package me.tomski.classes;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class HiderClass {

	private ItemStack helmet;
	private ItemStack torso;
	private ItemStack legs;
	private ItemStack boots;
	private List<PotionEffect> peffects;
	private List<ItemStack> invent;
	
	
	public HiderClass(ItemStack helmet,ItemStack torso,ItemStack legs, ItemStack boots, List<PotionEffect> pfx, List<ItemStack> inv){
		this.helmet=helmet;
		this.torso=torso;
		this.legs = legs;
		this.boots = boots;
		this.peffects = pfx;
		this.invent = inv;
	}
	
	
	@SuppressWarnings("deprecation")
	public void givePlayer(Player p){
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(torso);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);
		p.addPotionEffects(peffects);
		for(ItemStack i : invent){
			p.getInventory().addItem(i);
		}
		p.updateInventory();
	}
	
}
