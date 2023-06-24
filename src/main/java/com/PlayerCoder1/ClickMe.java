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
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

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
	private Rectangle scanRegion = new Rectangle(468, 341, 511, 339);
	private Color targetColor = new Color(255, 173, 0);

	private void displayScanRegions(Rectangle... regions) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setAlwaysOnTop(false);

		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.ORANGE);
				g2d.setStroke(new BasicStroke(3));

				for (Rectangle region : regions) {
					g2d.drawRect(region.x, region.y, region.width, region.height);
				}
			}
		};

		frame.setContentPane(panel);
		frame.setVisible(true);
	}

	@Override
	protected void startUp() throws Exception {
		robot = new Robot();
		startClickThread();
		displayScanRegions(scanRegion);
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
					Point screenPoint = new Point(scanRegion.x + canvasPoint.x, scanRegion.y + canvasPoint.y);
					robot.mouseMove(screenPoint.x, screenPoint.y);
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

	private boolean isColorMatch(Color c1, Color c2) {
		int tolerance = 5;

		int r1 = c1.getRed();
		int g1 = c1.getGreen();
		int b1 = c1.getBlue();

		int r2 = c2.getRed();
		int g2 = c2.getGreen();
		int b2 = c2.getBlue();

		return Math.abs(r1 - r2) <= tolerance && Math.abs(g1 - g2) <= tolerance && Math.abs(b1 - b2) <= tolerance;
	}

	private Point findTargetPoint(BufferedImage image) {
		int startX = scanRegion.x;
		int startY = scanRegion.y;
		int endX = scanRegion.x + scanRegion.width;
		int endY = scanRegion.y + scanRegion.height;

		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				Color color = new Color(image.getRGB(x, y));
				if (isColorMatch(color, targetColor)) {
					return new Point(x, y);
				}
			}
		}

		return null;
	}

	private Point findOrangeSquare() {
		BufferedImage screenCapture = robot.createScreenCapture(scanRegion);
		return findTargetPoint(screenCapture);
	}
}