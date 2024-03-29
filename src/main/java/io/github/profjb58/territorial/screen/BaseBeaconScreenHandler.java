package io.github.profjb58.territorial.screen;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.tag.ItemTags;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BaseBeaconScreenHandler extends ScreenHandler {
    private final Inventory payment;
    private final BaseBeaconScreenHandler.PaymentSlot paymentSlot;
    private final ScreenHandlerContext context;
    private final PropertyDelegate propertyDelegate;
    private boolean useAlternateEffects;

    public BaseBeaconScreenHandler(int syncId, Inventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new ArrayPropertyDelegate(3), ScreenHandlerContext.EMPTY);
        useAlternateEffects = buf.readBoolean();
    }

    public BaseBeaconScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(TerritorialRegistry.BASE_BEACON_SCREEN_HANDLER_TYPE, syncId);
        this.payment = new SimpleInventory(1) {
            public boolean isValid(int slot, ItemStack stack) {
                return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
            }

            public int getMaxCountPerStack() {
                return 1;
            }
        };
        checkDataCount(propertyDelegate, 3);
        this.propertyDelegate = propertyDelegate;
        this.context = context;
        this.paymentSlot = new BaseBeaconScreenHandler.PaymentSlot(this.payment, 0, 136 + 80, 110);
        useAlternateEffects = false;
        this.addSlot(this.paymentSlot);
        this.addProperties(propertyDelegate);

        enableSyncing();

        int k;
        for(k = 0; k < 3; ++k) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + k * 9 + 9, (36 + 80) + l * 18, 137 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, (36 + 80) + k * 18, 195));
        }

    }

    public void close(PlayerEntity player) {
        super.close(player);
        if (!player.world.isClient) {
            ItemStack itemStack = this.paymentSlot.takeStack(this.paymentSlot.getMaxItemCount());
            if (!itemStack.isEmpty()) {
                player.dropItem(itemStack, false);
            }

        }
    }

    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0) {
                if (!this.insertItem(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (!this.paymentSlot.hasStack() && this.paymentSlot.canInsert(itemStack2) && itemStack2.getCount() == 1) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index < 28) {
                if (!this.insertItem(itemStack2, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 28 && index < 37) {
                if (!this.insertItem(itemStack2, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    public int getProperties() {
        return this.propertyDelegate.get(0);
    }

    @Nullable
    public StatusEffect getPrimaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(1));
    }

    @Nullable
    public StatusEffect getSecondaryEffect() {
        return StatusEffect.byRawId(this.propertyDelegate.get(2));
    }

    public void setEffects(int primaryEffectId, int secondaryEffectId) {
        if (this.paymentSlot.hasStack()) {
            this.propertyDelegate.set(1, primaryEffectId);
            this.propertyDelegate.set(2, secondaryEffectId);
            this.paymentSlot.takeStack(1);
            this.context.run(World::markDirty);
        }
    }

    public boolean hasPayment() {
        return !this.payment.getStack(0).isEmpty();
    }

    public boolean useAlternateEffects() { return this.useAlternateEffects; }

    private class PaymentSlot extends Slot {
        public PaymentSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public boolean canInsert(ItemStack stack) {
            return stack.isIn(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        public int getMaxItemCount() {
            return 1;
        }
    }
}

