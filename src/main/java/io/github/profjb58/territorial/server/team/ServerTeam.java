package io.github.profjb58.territorial.server.team;

import io.github.profjb58.territorial.team.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;

public class ServerTeam extends Team {

    protected Date lastLoginDate;
    protected int numChunkClaims;
    protected boolean isInactive;
    private Map<RegistryKey<World>, HashSet<BlockPos>> beaconPositions;

    public ServerTeam(UUID id, String name, Banner banner, Date lastLoginDate, int numChunkClaims, boolean isInactive, Members members, @Nullable Map<RegistryKey<World>, HashSet<BlockPos>> beaconPositions) {
        super(id, name, banner, members);

        this.lastLoginDate = lastLoginDate;
        this.numChunkClaims = numChunkClaims;
        this.isInactive = isInactive;
        this.beaconPositions = beaconPositions;
    }

    protected ServerTeam(String name, Banner banner, ServerPlayerEntity owner) {
        super(name, banner, owner);
    }

    public void setIdentifyingData(String name, Banner banner) {
        ServerTeamManager.BANNER_DYE_COLOR_USAGE[this.banner.baseColour().getId()]--;
        ServerTeamManager.BANNER_DYE_COLOR_USAGE[banner.baseColour().getId()]++;

        this.name = name;
        this.banner = banner;
    }

    public void updateLastLogin() { lastLoginDate = Date.from(Instant.EPOCH); }

    public void addBeaconPos(World world, BlockPos pos) {
        var worldKey = world.getRegistryKey();
        HashSet<BlockPos> beaconPosSet = new HashSet<>();

        if(beaconPositions.containsKey(worldKey)) {
            beaconPosSet = beaconPositions.get(worldKey);
            beaconPosSet.add(pos);
        } else {
            beaconPosSet.add(pos);
            beaconPositions.put(worldKey, beaconPosSet);
        }
    }

    public void removeBeaconPos(World world, BlockPos pos) {
        var worldKey = world.getRegistryKey();

        if(beaconPositions.containsKey(worldKey)) {
            HashSet<BlockPos> beaconPosSet = beaconPositions.get(worldKey);
            if(beaconPosSet.size() == 1)
                beaconPositions.remove(worldKey);
            else
                beaconPosSet.remove(pos);
        }
    }
}
