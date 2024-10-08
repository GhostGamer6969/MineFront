package com.mime.minefront.graphics;

import java.util.Random;

import com.mime.minefront.Game;
import com.mime.minefront.input.Controller;

public class Render3D extends Render {

	public double[] zBuffer;
	private double renderDistane = 5000;
	private double forward, right, cosine, sine, up;

	public Render3D(int width, int height) {
		super(width, height);
		zBuffer = new double[width * height];
	}

	public void floor(Game game) {

		double floorPosition = 8;
		double ceilingPosition = 8;
		forward = game.controls.z;
		right = game.controls.x;
		up = game.controls.y;
		double walking = Math.sin(game.time / 6.0) * 0.5;
		if (Controller.crouchWalk) {
			walking = Math.sin(game.time / 6.0) * 0.25;
		}
		if (Controller.runWalk) {
			walking = Math.sin(game.time / 6.0) * 0.8;
		}

		double rotation = game.controls.rotation;// Math.sin(game.time%1000.0/80);
		cosine = Math.cos(rotation);
		sine = Math.sin(rotation);

		for (int y = 0; y < height; y++) {
			double ceiling = (y - height / 2.0) / height;

			double z = (floorPosition + up) / ceiling;
			if (Controller.walk) {
				z = (floorPosition + up + walking) / ceiling;
			}
			if (ceiling < 0) {
				z = (ceilingPosition - up) / -ceiling;
				if (Controller.walk) {
					z = (ceilingPosition - up - walking) / -ceiling;
				}
			}

			for (int x = 0; x < width; x++) {

				double depth = (x - width / 2.0) / height;
				depth *= z;
				double xx = depth * cosine + z * sine;
				double yy = z * cosine - depth * sine;
				int xPix = (int) (xx + right);
				int yPix = (int) (yy + forward);
				zBuffer[x + y * width] = z;
				pixels[x + y * width] = Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];

				if (z > 500) {
					pixels[x + y * width] = 0;
				}
			}
		}

	}

	public void renderWalls(double xLeft, double xRight, double zDistanceRight,double zDistanceLeft, double yHeight) {
		double xcLeft = ((xLeft) - right) * 2;
		double zcLeft = ((zDistanceLeft) - forward) * 2;

		double rotLeftSideX = (xcLeft * cosine - zcLeft * sine);
		double yCornerTL = ((-yHeight) - (-up*0.062)) * 2;
		double yCornerBL = ((+0.5 - yHeight) - (-up*0.062)) * 2;
		double rotLeftSideZ = (xcLeft * sine + zcLeft * cosine);

		double xcRight = ((xRight) - right) * 2;
		double zcRight = ((zDistanceRight) - forward) * 2;

		double rotRightSideX = (xcRight * cosine - zcRight * sine);
		double yCornerTR = ((-yHeight) - (-up*0.062)) * 2;
		double yCornerBR = ((+0.5 - yHeight) - (-up*0.062)) * 2;
		double rotRightSideZ = (xcRight * sine + zcRight * cosine);

		double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + width / 2);
		double xPixelRight = (rotRightSideX / rotRightSideZ * height + width / 2);

		if (xPixelLeft >= xPixelRight) {
			return;
		}

		int xPixelLeftInt = (int) xPixelLeft;
		int xPixelRightInt = (int) xPixelRight;

		if (xPixelLeftInt < 0) {
			xPixelLeftInt = 0;
		}
		if (xPixelRightInt >= width) {
			xPixelRightInt = width;
		}

		double yPixelLeftTop = (yCornerTL / rotLeftSideZ * height + height / 2.0);
		double yPixelLeftBottom = (yCornerBL / rotLeftSideZ * height + height / 2.0);
		double yPixelRightTop = (yCornerTR / rotRightSideZ * height + height / 2.0);
		double yPixelRightBottom = (yCornerBR / rotRightSideZ * height + height / 2.0);

		double tex1 = 1 / rotLeftSideZ;
		double tex2 = 1 / rotRightSideZ;
		double tex3 = 0 / rotLeftSideZ;
		double tex4 = 8 / rotRightSideZ - tex3;

		for (int x = xPixelLeftInt; x < xPixelRightInt; x++) {
			double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);

			int xTexture = (int) ((tex3 + tex4 * pixelRotation) / tex1 + (tex2 - tex1) * pixelRotation);

			double yPixelTop = (yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation);
			double yPixelBottom = (yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation);

			int yPixelTopInt = (int) yPixelTop;
			int yPixelBottomInt = (int) yPixelBottom;

			if (yPixelTopInt < 0) {
				yPixelTopInt = 0;
			}
			if (yPixelBottomInt >= height) {
				yPixelBottomInt = height;
			}

			for (int y = yPixelTopInt; y < yPixelBottomInt; y++) {
				double pixelRotationY = (y - yPixelTop) / (yPixelBottomInt - yPixelTop);
				int yTexture = (int) (8 * pixelRotationY);
				pixels[x + y * width] = xTexture * 100 + yTexture * 100 * 256;
				zBuffer[x + y * width] = 1/(tex1 + (tex2 - tex1) * pixelRotation)*8;
			}
		}
	}

	public void renderDistanceLimiter() {
		for (int i = 0; i < width * height; i++) {
			int colour = pixels[i];
			int brightness = (int) (renderDistane / (zBuffer[i]));

			if (brightness < 0) {
				brightness = 0;
			}

			if (brightness > 255) {
				brightness = 255;
			}
			int r = (colour >> 15) & 0xff;
			int g = (colour >> 8) & 0xff;
			int b = (colour) & 0xff;

			r = r * brightness / 255;
			g = g * brightness / 255;
			b = b * brightness / 255;

			pixels[i] = r << 16 | g << 8 | b;
		}
	}

}
