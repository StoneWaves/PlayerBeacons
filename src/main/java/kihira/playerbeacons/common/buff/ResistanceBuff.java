package kihira.playerbeacons.common.buff;

import kihira.playerbeacons.api.IBeacon;
import kihira.playerbeacons.api.buff.Buff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

public class ResistanceBuff extends Buff {

	public ResistanceBuff() {
		super("resistance", 20, 2, 3);
	}

	@Override
	public void doBuff(EntityPlayer player, IBeacon theBeacon, int crystalCount) {
		if (crystalCount < theBeacon.getLevels() - 2) player.addPotionEffect(new PotionEffect(Potion.resistance.id, 300, theBeacon.getLevels() - crystalCount - 3, true));
	}

	@Override
	public float getCorruption(EntityPlayer player, IBeacon theBeacon, int crystalCount) {
		return (MathHelper.clamp_int(crystalCount, 0, theBeacon.getLevels()) - 2) * this.corruptionGenerated;
	}

    @Override
    public float[] getRGBA() {
        return new float[] {0.5F, 0F, 0F, 1F};
    }

	@Override
	public String getName() {
		return "Resistance";
	}
}