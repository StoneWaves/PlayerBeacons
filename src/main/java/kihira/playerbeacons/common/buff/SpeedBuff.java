package kihira.playerbeacons.common.buff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.playerbeacons.api.beacon.IBeacon;
import kihira.playerbeacons.api.buff.Buff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class SpeedBuff extends Buff {

	public SpeedBuff() {
		super("speed");
	}

	@Override
	public float doBuff(EntityPlayer player, IBeacon theBeacon, int crystalCount) {
        if (player.worldObj.getTotalWorldTime() % 20 == 0) {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 300, MathHelper.clamp_int(crystalCount, 0, theBeacon.getLevels()) - 1, true));
        }
        return MathHelper.clamp_int(crystalCount, 0, theBeacon.getLevels()) * 10;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public float[] getRGBA() {
        return new float[] {0.5F, 0.5F, 1F, 1F};
    }

    @Override
    public int[] getUV() {
        return new int[]{0, 198};
    }
}
