package chneau.autotool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Main implements ClientModInitializer {
	private final double wee = 1e-4;
	private String old;

	@Override
	public void onInitializeClient() {
		System.out.println("Hello Fabric world!");
		ClientTickCallback.EVENT.register(client -> {
			HitResult hitResult = client.hitResult;
			Entity cameraEntity = client.cameraEntity;
			ClientPlayerEntity player = client.player;
			if (hitResult == null || cameraEntity == null || player == null || player.inventory == null) {
				old = null;
				return;
			}
			PlayerInventory inventory = player.inventory;
			Type type = hitResult.getType();
			if (type == Type.MISS) {
				old = null;
				return;
			}
			if (!(inventory.main.get(inventory.selectedSlot).getItem() instanceof ToolItem)) {
				return;
			}
			if (type == Type.ENTITY) {
				EntityHitResult eHitRes = (EntityHitResult) hitResult;
				Entity entity = eHitRes.getEntity();
				String entityName = entity.getName().asString();
				if (entityName.equals(old)) {
					return;
				}
				selectFirstSword(inventory);
				System.out.println("Entity: " + entityName);
				old = entityName;
				return;
			}
			BlockState bState = getTargetBS(client, hitResult, cameraEntity);
			Block block = bState.getBlock();
			Item targetItem = block.asItem();
			String itemName = targetItem.getName().asString();
			if (itemName.equals(old)) {
				return;
			}
			selectFirstTool(inventory, bState, targetItem);
			System.out.println("Item: " + itemName);
			old = itemName;
		});
	}

	/**
	 * Select the first sword on your inventory
	 *
	 * @param inventory
	 */
	private void selectFirstSword(PlayerInventory inventory) {
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory.main.get(i);
			Item item = itemStack.getItem();
			if (item instanceof SwordItem) {
				inventory.selectedSlot = i;
				break;
			}
		}
	}

	/**
	 * Select the first right tool for the selected block.
	 *
	 * @param inventory  the player inventory so that we can select the best item
	 * @param bState     the selected block (usually what you are looking at)
	 * @param targetItem the item of the selected block
	 */
	private void selectFirstTool(PlayerInventory inventory, BlockState bState, Item targetItem) {
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inventory.main.get(i);
			Item item = itemStack.getItem();
			if (item instanceof ToolItem) {
				float miningSpeed = item.getMiningSpeed(new ItemStack(targetItem), bState);
				if (miningSpeed > 1) {
					inventory.selectedSlot = i;
					return;
				}
			}
		}
	}

	/**
	 * @param client
	 * @param hitResult
	 * @param cameraEntity
	 * @return the targeted (the block you are looking at) block state.
	 */
	private BlockState getTargetBS(MinecraftClient client, HitResult hitResult, Entity cameraEntity) {
		Vec3d camera = cameraEntity.getCameraPosVec(1);
		Vec3d pos = hitResult.getPos();
		double x = (pos.x - camera.x > 0) ? wee : -wee;
		double y = (pos.y - camera.y > 0) ? wee : -wee;
		double z = (pos.z - camera.z > 0) ? wee : -wee;
		pos = pos.add(x, y, z);
		BlockPos bPos = new BlockPos(pos);
		BlockState bState = client.world.getBlockState(bPos);
		return bState;
	}
}
