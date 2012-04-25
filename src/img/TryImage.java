package img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.nutz.lang.Files;

public class TryImage {

	public static void main(String[] args) throws Exception {
		// 背景 | 前景 | 目标 文件对象
		File bgImgFile = Files.findFile("img/bg.jpg");
		File feImgFile = Files.findFile("img/logo.png");
		File destImgFile = Files.createFileIfNoExists("~/tmp/myImage.png");

		// 创建背景图片
		BufferedImage bgImg = ImageIO.read(bgImgFile);
		// 创建前景图片 - 可以用半透明 png
		BufferedImage feImg = ImageIO.read(feImgFile);

		// 拼合两个图片 - 将前景拼合到背景
		Graphics g = bgImg.getGraphics();
		g.drawImage(feImg, 300, 300, null);
		// 并写一段红色的文字
		g.setColor(new Color(255, 0, 0));
		String txt = "这是叠加的图片";
		g.drawChars(txt.toCharArray(), 0, txt.length(), 350, 306);

		// 输出
		ImageIO.write(bgImg, "png", destImgFile);

		// 打印结果
		System.out.printf("Done:> %s(%d) \n", destImgFile, destImgFile.length());
	}

}
