package com.PlayerCoder1;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

@PluginDescriptor(
		name = "Runelite Click Test",
		description = "Trying to see if I can simulate clicks on Runelite"
)
public class ClickMe extends Plugin {

	@Inject
	private Client client;

	private Robot robot;
	private Thread clickThread;
	private int targetNPCId = 8503;

	@Override
	protected void startUp() throws Exception {
		robot = new Robot();
		startClickThread();
	}

	@Override
	protected void shutDown() throws Exception {
		stopClickThread();
	}

	private void startClickThread() {
		clickThread = new Thread(this::performNPCClicks);
		clickThread.start();
	}

	private void stopClickThread() {
		if (clickThread != null && clickThread.isAlive()) {
			clickThread.interrupt();
			clickThread = null;
		}
	}

	private void performNPCClicks() {
		try {
			while (!Thread.interrupted()) {
				NPC targetNPC = findTargetNPC();
				if (targetNPC != null) {
					NPCComposition npcComposition = targetNPC.getComposition();
					if (npcComposition != null) {
						debugPrint("Simulating click on NPC: " + npcComposition.getName());
						performMouseClick(targetNPC);
					}
				}

				int delay = getRandomDelay();
				debugPrint("Next click in " + delay + " ms");
				Thread.sleep(delay);
			}
		} catch (InterruptedException e) {

		}
	}

	private NPC findTargetNPC() {
		for (NPC npc : client.getNpcs()) {
			if (npc.getId() == targetNPCId && npc.getInteracting() == null) {
				return npc;
			}
		}
		return null;
	}

	private int getRandomDelay() {
		return new Random().nextInt(4000) + 1000;
	}

	private void performMouseClick(NPC npc) {
		LocalPoint localPoint = npc.getLocalLocation();

		if (localPoint != null) {
			Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);

			if (poly != null) {
				Point canvasPoint = getRandomPointInsidePolygon(poly);

				if (canvasPoint != null) {
					robot.mouseMove(canvasPoint.x, canvasPoint.y);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				}
			}
		}
	}

	private Point getRandomPointInsidePolygon(Polygon polygon) {
		Rectangle bounds = polygon.getBounds();

		if (!bounds.isEmpty()) {
			int x = bounds.x + new Random().nextInt(bounds.width);
			int y = bounds.y + new Random().nextInt(bounds.height);
			return new Point(x, y);
		}

		return null;
	}

	private void debugPrint(String message) {
		System.out.println("[DEBUG] " + message);
	}
}
