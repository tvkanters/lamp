import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Main {

	public static void main(String[] args) {
		CIELab lab = new CIELab();
		try {
			while (true) {
				Robot robot = new Robot();
				BufferedImage image = robot.createScreenCapture(new Rectangle(
						Toolkit.getDefaultToolkit().getScreenSize()));

				int height = image.getHeight();
				int width = image.getWidth();
				int pixels = width * height;

				int red = 0;
				int green = 0;
				int blue = 0;

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int rgb = image.getRGB(i, j);
						int[] rgbArr = getRGBArr(rgb);

						red += rgbArr[0];
						green += rgbArr[1];
						blue += rgbArr[2];
					}
				}

				System.out.println(red + " " + green + " " + blue);
				System.out.println(pixels);
				int[] colour = { red / pixels, green / pixels, blue / pixels };

				float[] colourFloat = { colour[0] / 255f, colour[1] / 255f,
						colour[2] / 255f };

				float[] hue = Color.RGBtoHSB(colour[0], colour[1], colour[2],
						new float[3]);

				System.out.println(colour[0] + " " + colour[1] + " "
						+ colour[2]);
				System.out.println(hue[0]);

				float[] cie = lab.toCIEXYZ(colourFloat);

				System.out.println(cie[0] + " " + cie[1] + " " + cie[2]);

				setLight(hue[0], hue[1], hue[2], cie);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	public static int[] getRGBArr(int pixel) {
		int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new int[] { red, green, blue };
	}

	public static void setLight(float hue, float saturation, float brightness,
			float[] cie) {
		try {
			System.out.println(hue);
			if (hue > 0.1 && hue < 0.3) {
				float tempHue = hue - 0.1f;
				hue += (tempHue % 0.2f) * 0.4;
			}
			if (hue > 0.2 && hue < 0.4) {
				float tempHue = hue - 0.2f;
				hue += (tempHue % 0.2f) * 0.3;
			}
			if (hue > 0.5 && hue < 0.7) {
				float tempHue = hue - 0.5f;
				hue += (tempHue % 0.2f) * 0.03;
			}
			System.out.println(hue);

			int briVal = (int) (brightness * 255);
			int satVal = (int) (saturation * 255);
			int hueVal = (int) (hue * 65535);

			hueVal = ((hueVal + 2500) % 65535);
			satVal = Math.min(255, satVal*2);
//			satVal = 255;

			String urlParameters = "{\"colormode\":\"hs\",\"hue\":" + hueVal
					+ ",\"sat\":" + satVal + ",\"bri\":" + briVal
					+ ",\"transitiontime\":10}";
			System.out.println(urlParameters);
			String request = "http://10.0.1.2/api/appeltaart/lights/3/state";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);

			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}

			in.close();

			connection.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
