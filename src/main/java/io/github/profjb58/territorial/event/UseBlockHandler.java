package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.item.KeyItem;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.util.debug.ActionLogger;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static io.github.profjb58.territorial.TerritorialClient.lockableHud;

public class UseBlockHandler implements UseBlockCallback {

    public static void init() {
        UseBlockCallback.EVENT.register(new UseBlockHandler());
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!player.isCreative() || !player.isHolding(TerritorialRegistry.MASTER_KEY)) {
            LockableBlockEntity lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
            if (lbe.exists()) {
                if(world.isClient) {
                    lockableHud.ignoreCycle();
                }
                else {
                    LockableBlock lb = lbe.getBlock();
                    Item heldItem = player.getStackInHand(player.getActiveHand()).getItem();

                    // Check if the interaction should be ignored and instead caught by the corresponding item class
                    if(player.isSneaking()) {
                        if(heldItem instanceof PadlockItem || heldItem instanceof KeyItem) {
                            return ActionResult.PASS;
                        }
                    }
                    ItemStack keyItemStack = lb.findMatchingKey((ServerPlayerEntity) player, true);
                    if(keyItemStack == null) {
                        if (!lb.getLockOwnerUuid().equals(player.getUuid())) { // No matching key found
                            player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                        }
                        else { // Owns the lock but no matching key was found
                            player.sendMessage(new TranslatableText("message.territorial.lock_no_key"), true);
                        }
                        lb.playSound(LockableBlock.LockSound.DENIED_ENTRY, world);
                        return ActionResult.FAIL;
                    }
                    else {
                        KeyItem keyItem = (KeyItem) keyItemStack.getItem();
                        if(keyItem.isMasterKey()) {
                            // Check if the master key should vanish after a single use
                            if(Territorial.getConfig().masterKeyVanish()) {
                                keyItemStack.decrement(1);
                                player.sendMessage(new TranslatableText("message.territorial.master_key_vanished"), false);
                                TerritorialServer.actionLogger.write(ActionLogger.LogType.INFO, ActionLogger.LogModule.LOCKS,
                                        "Player " + player.getName().getString() + " used a master key at location " + lb.getBlockPos());
                            }
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}