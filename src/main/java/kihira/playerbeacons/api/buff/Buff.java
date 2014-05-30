package kihira.playerbeacons.api.buff;

import kihira.playerbeacons.api.IBeacon;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * Base class for any Buff's. Extend this class and call a new instance of your Buff
 * and the buff will be automatically registered. The buff will automatically be added
 * to the PlayerBeacons config file, you don't need to offer options to edit values
 */
public abstract class Buff {

	public static HashMap<String, Buff> buffs = new HashMap<String, Buff>();

	protected float corruptionGenerated;
	protected int maxBuffLevel;
	protected int minBeaconLevel;

	public Buff(String simpleName, float corruptionGenerated, int maxBuffLevel, int minBeaconLevel) {
		if (!buffs.containsKey(simpleName)) {
			this.corruptionGenerated = corruptionGenerated;
			this.maxBuffLevel = maxBuffLevel;
			this.minBeaconLevel = minBeaconLevel;
			buffs.put(simpleName, this);
		}
		else throw new IllegalArgumentException("Buff " + simpleName + " is already registered");
	}

	/**
	 * This method is called when the requirements to activate the buff are reached.
	 * @param player The owner of the beacon
	 * @param theBeacon The beacon itself
	 * @param crystalCount The amount of crystals detected that throttle the buff
	 */
	public abstract void doBuff(EntityPlayer player, IBeacon theBeacon, int crystalCount);

	/**
	 * How much the buff will generated in corruption for the number of beacon levels. This is the
	 * amount generated regardless of any throttles or crystals that might be present.
	 * @param beaconLevel How many levels on the beacon
	 * @return How much corruption is generated
	 */
	public abstract float getCorruption(EntityPlayer player, IBeacon theBeacon, int crystalCount);

    public abstract float[] getRGBA();
}