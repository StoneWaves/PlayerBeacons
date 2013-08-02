package playerbeacons.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import playerbeacons.common.DamageBehead;
import playerbeacons.common.PlayerBeacons;

import java.util.List;


public class BeheaderItem extends ItemArmor {

	public BeheaderItem(int id) {
		super(id, EnumArmorMaterial.IRON, 2, 0);
		setCreativeTab(CreativeTabs.tabCombat);
		setUnlocalizedName("beheader");
		setMaxDamage(200);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer enemyPlayer = (EntityPlayer) entity;
			if (enemyPlayer.getCurrentArmor(0) == null) {
				enemyPlayer.setCurrentItemOrArmor(4, new ItemStack(PlayerBeacons.beheaderItem));
				player.setCurrentItemOrArmor(0, null);
			}
		}
		return true;
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
			switch (itemStack.getItemDamage()) {
				case 1: player.sendChatToPlayer(ChatMessageComponent.func_111066_d("You feel a strange device clamp around your head")); break;
				case 100:
					player.sendChatToPlayer(ChatMessageComponent.func_111066_d("The device tightens, as you hear it power up"));
					player.addPotionEffect(new PotionEffect(Potion.blindness.id, 200));
					player.addPotionEffect(new PotionEffect(Potion.confusion.id, 200));
					break;
				case 200:
					player.sendChatToPlayer(ChatMessageComponent.func_111066_d("With a quick slash, you suddenly find yourself without your head"));
					ItemStack newItemStack = new ItemStack(Item.skull, 1, 3);
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("SkullOwner", player.username);
					newItemStack.setTagCompound(tag);
					player.setCurrentItemOrArmor(4, null);
					player.setCurrentItemOrArmor(4, newItemStack);
					player.attackEntityFrom(new DamageBehead(), 100);
					itemStack.setItemDamage(itemStack.getMaxDamage());
					break;
			}
			itemStack.setItemDamage(itemStack.getItemDamage()+1);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (par1ItemStack.getItem() instanceof BeheaderItem) {
				if (par1ItemStack.getItemDamage() != 0) {
					par3EntityPlayer.setCurrentItemOrArmor(0, null);
					par3EntityPlayer.setCurrentItemOrArmor(0, new ItemStack(PlayerBeacons.beheaderItem));
					par3EntityPlayer.sendChatToPlayer(ChatMessageComponent.func_111066_d("The device clicks, it seems to have reset"));
					return par1ItemStack;
				}
			}
		}
		return par1ItemStack;
	}

	//What does par4 do? Metadata?
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
		super.addInformation(itemStack, entityPlayer, list, par4);

		list.add("Go on, try it on!");
		list.add("The spirit of Loki possesses this device");

	}

	//TODO Get nex to do something for this?
	@Override
	@SideOnly(Side.CLIENT)
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks, boolean hasScreen, int mouseX, int mouseY) {

	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}
}
