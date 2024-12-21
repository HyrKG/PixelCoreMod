package cn.hyrkg.fastforge_v2.pixelcore.fastgui.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class TickableHandler {

	private static Set<ITickable> tickables = new HashSet();

	public static void onPreInit() {
		MinecraftForge.EVENT_BUS.register(new TickableHandler());
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent clientTickEvent) {
		if (clientTickEvent.phase == Phase.START) {
			new ArrayList<>(tickables).forEach(j -> j.tick());
		}
	}

	public static void registerTickable(ITickable tickable) {
		tickables.add(tickable);
	}

	public static void unregisterTickable(ITickable tickable) {
		tickables.remove(tickable);
	}

}
