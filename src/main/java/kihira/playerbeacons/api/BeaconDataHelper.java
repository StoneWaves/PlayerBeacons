package kihira.playerbeacons.api;

import com.google.common.base.Stopwatch;
import kihira.foxlib.common.Loc4;
import kihira.playerbeacons.api.beacon.IBeacon;
import kihira.playerbeacons.common.Beacon;
import kihira.playerbeacons.common.PlayerBeacons;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BeaconDataHelper {

    //TODO move to better loc?
    public static HashMap<Loc4, Beacon> beaconMap = new HashMap<Loc4, Beacon>();

    public static float getPlayerCorruptionAmount(EntityPlayer player) {
        NBTTagCompound data = getBeaconDataTag(player);
        float corruptionAmount = 0;

        if (data.hasKey("Corruption")) {
            corruptionAmount = data.getFloat("Corruption");
        }

        return corruptionAmount;
    }

    public static void modifyCorruptionAmount(EntityPlayer player, float corrChange) {
        float corr = getPlayerCorruptionAmount(player);
        corr += corrChange;
        setPlayerCorruptionAmount(player, corr);
    }

    public static void setPlayerCorruptionAmount(EntityPlayer player, float corr) {
        NBTTagCompound data = getBeaconDataTag(player);

        data.setFloat("Corruption", Math.max(0, corr));
    }

    public static boolean doesPlayerHaveBeaconForDim(EntityPlayer player, int dimID) {
        NBTTagCompound beaconData = getBeaconDataTag(player);
        return beaconData.hasKey(String.valueOf(dimID));
    }

    public static void setBeaconForDim(EntityPlayer player, IBeacon playerBeacon, int dimID) {
        if (player != null) {
            String dimKey = String.valueOf(dimID);
            NBTTagCompound beaconData = getBeaconDataTag(player);
            NBTTagCompound worldBeaconData = beaconData.getCompoundTag(dimKey);
            if (playerBeacon != null) {
                worldBeaconData.setInteger("xPos", playerBeacon.getTileEntity().xCoord);
                worldBeaconData.setInteger("yPos", playerBeacon.getTileEntity().yCoord);
                worldBeaconData.setInteger("zPos", playerBeacon.getTileEntity().zCoord);

                //Set tag just incase it didn't alredy exist
                beaconData.setTag(dimKey, worldBeaconData);
            }
            else {
                int x = worldBeaconData.getInteger("xPos");
                int y = worldBeaconData.getInteger("yPos");
                int z = worldBeaconData.getInteger("zPos");
                Loc4 loc = new Loc4(dimID, x, y, z);

                if (beaconMap.containsKey(loc) && beaconMap.get(loc).dimID == dimID) beaconMap.remove(loc);
                beaconData.removeTag(dimKey);
            }
        }
    }

    public static void markBeaconDirty(final IBeacon theBeacon) {
        TileEntity tileEntity = theBeacon.getTileEntity();
        Loc4 loc = new Loc4(tileEntity.getWorldObj().provider.dimensionId, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        if (beaconMap.containsKey(loc)) {
            final Beacon beacon = beaconMap.get(loc);

            //TODO Test this a million times
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    boolean hasChanged = false;
                    //If checkStructure is false, something has changed
                    if (!beacon.checkStructure(theBeacon)) {
                        hasChanged = true;
                        beacon.invalidateStructure(theBeacon);
                        beacon.checkStructure(theBeacon); //Figure out the levels
                        beacon.formStructure(theBeacon);
                    }
                    stopwatch.stop();
                    PlayerBeacons.logger.debug("Multiblock loop took " + stopwatch.elapsed(TimeUnit.MICROSECONDS) +
                            " microseconds, beacon " + (hasChanged ? "changed" : "unchanged"));
                }
            };
            runnable.run();
        }
    }

    public static Beacon getBeaconForDim(EntityPlayer player, int dimID) {
        if (player != null) {
            NBTTagCompound beaconData = getBeaconDataTag(player);
            String dimKey = String.valueOf(dimID);

            if (beaconData.hasKey(dimKey)) {
                NBTTagCompound worldBeaconData = beaconData.getCompoundTag(dimKey);
                int x = worldBeaconData.getInteger("xPos");
                int y = worldBeaconData.getInteger("yPos");
                int z = worldBeaconData.getInteger("zPos");

                Loc4 loc = new Loc4(dimID, x, y, z);
                if (beaconMap.containsKey(loc)) return beaconMap.get(loc);

                World world = MinecraftServer.getServer().worldServerForDimension(dimID);
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof IBeacon) {
                    //TODO allow API to have custom Beacon instances?
                    Beacon beacon = new Beacon(dimID, x, y, z, player.getGameProfile());
                    beaconMap.put(loc, beacon);
                    markBeaconDirty((IBeacon) tileEntity);

                    return beacon;
                }
                else {
                    setBeaconForDim(player, null, dimID);
                }
            }
        }
        return null;
    }

    public static void unloadBeacon(Beacon beacon) {
        if (beacon != null) beaconMap.remove(new Loc4(beacon.dimID, beacon.posX, beacon.posY, beacon.posZ));
    }

    private static NBTTagCompound getBeaconDataTag(EntityPlayer player) {
        NBTTagCompound forgeData = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagCompound beaconData = forgeData.getCompoundTag("PlayerBeacons");

        //Creates/sets the tags if they don't exist
        if (!forgeData.hasKey("PlayerBeacons")) forgeData.setTag("PlayerBeacons", beaconData);
        if (!player.getEntityData().hasKey(EntityPlayer.PERSISTED_NBT_TAG)) player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, forgeData);

        return beaconData;
    }
}