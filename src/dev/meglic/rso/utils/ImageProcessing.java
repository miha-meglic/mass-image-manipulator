package dev.meglic.rso.utils;

import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

public final class ImageProcessing {
	
	/*
	Resize image long edge wise.
	 */
	public static BufferedImage resizeLongedge (BufferedImage image, int longedge) {
		if (longedge <= 0)
			return null;
		
		BufferedImage processed;
		
		if (image.getWidth() >= image.getHeight()) {
			processed = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, longedge);
		} else {
			processed = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, longedge);
		}
		
		return processed;
	}
	
	/*
	Resize image width wise.
	 */
	public static BufferedImage resizeWidth (BufferedImage image, int width) {
		if (width <= 0)
			return null;
		return Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, width);
	}
	
	/*
	Scale image by a factor.
	 */
	public static BufferedImage scale (BufferedImage image, double scale) {
		if (scale <= 0 || scale > 5)
			return null;
		return Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, (int)(image.getWidth()*scale));
	}
	
	/*
	Transform image to grayscale
	 */
	public static BufferedImage grayscale (BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = image.getRGB(x, y);
				
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb) & 0xFF;
				
				int p = (int)((0.3*r)+(0.59*g)+(0.11*b));
				
				image.setRGB(x, y, ((0xFF<<24) | (p<<16) | (p<<8) | p));
			}
		}
		return image;
	}
}
