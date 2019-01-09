package com.liyiyue;

import java.awt.image.BufferedImage;

public class ImageUtil {
	// const
	public static int Dot_Len = 6;
	public static int Dot_Space = 6;

	public static void DrawCrossDotLine(BufferedImage image) {
		int x = image.getWidth() / 2 + image.getMinX();
		int y = image.getHeight() / 2 + image.getMinY();

		int len = 0;
		int space = 0;
		for (int i = image.getMinX(); i < image.getWidth() - 1; i++) {
			if (len < Dot_Len) {
				image.setRGB(i, y, 0x000000);
				len++;
			} else if (space < Dot_Space) {
				space++;
			} else {
				len = 0;
				space = 0;
			}
		}
		for (int j = image.getMinY(); j < image.getHeight() - 1; j++) {
			if (len < Dot_Len) {
				image.setRGB(x, j, 0x000000);
				len++;
			} else if (space < Dot_Space) {
				space++;
			} else {
				len = 0;
				space = 0;
			}
		}
	}
}
