package kihira.playerbeacons.common.buff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.playerbeacons.api.beacon.IBeacon;
import kihira.playerbeacons.api.buff.Buff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class ResistanceBuff extends Buff {

	public ResistanceBuff() {
		super("resistance");
	}

	@Override
	public float doBuff(EntityPlayer player, IBeacon theBeacon, int crystalCount) {
        if (player.worldObj.getTotalWorldTime() % 20 == 0) {
            player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, MathHelper.clamp_int(crystalCount, 0, theBeacon.getLevels() - 2) - 1, true));
        }
        return (MathHelper.clamp_int(crystalCount, 0, theBeacon.getLevels()) - 2) * 20;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public float[] getRGBA() {
        return new float[] {0.5F, 0F, 0F, 1F};
    }

    @Override
    public int[] getUV() {
        return new int[]{108, 216};
    }
}
