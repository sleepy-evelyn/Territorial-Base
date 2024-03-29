package io.github.profjb58.territorial.block.entity;

import com.mojang.datafixers.util.Pair;
import io.github.profjb58.territorial.block.BoundaryBeaconBlock;
import io.github.profjb58.territorial.screen.BoundaryBeaconScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoundaryBeaconBlockEntity extends BaseBeaconBlockEntity {

    @Nullable
    private List<Pair<BannerPattern, DyeColor>> bannerPatterns;

    @Nullable
    private NbtList bannerPatternNbt;

    // TODO - Put team container here

    public BoundaryBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new BoundaryBeaconScreenHandler(i, playerInventory, propertyDelegate, ScreenHandlerContext.create(this.world, this.getPos()));
    }

    public static void tick(World world, BlockPos pos, BlockState state, BaseBeaconBlockEntity blockEntity) {
        // TODO - Rendering stuff
        BaseBeaconBlockEntity.tick(world, pos, state, blockEntity);
    }

    public void setBanner(ItemStack bannerStack) {
        bannerPatternNbt = BannerBlockEntity.getPatternListNbt(bannerStack);
        bannerPatterns = null;
    }

    @Nullable
    public List<Pair<BannerPattern, DyeColor>> getBannerPatterns() {
        if (bannerPatterns == null)
            this.bannerPatterns = BannerBlockEntity.getPatternsFromNbt(getCachedState().get(BoundaryBeaconBlock.DYE_COLOUR), bannerPatternNbt);
        return this.bannerPatterns;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (bannerPatternNbt != null) {
            nbt.put("Patterns", bannerPatternNbt);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        bannerPatternNbt = nbt.getList("Patterns", 10);
        bannerPatterns = null;
    }
}
