package org.jaudiotagger.tag.images;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.jaudiotagger.R;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * Image Handling to to use when running on Android
 */
public class AndroidImageHandler implements ImageHandler {
	private static AndroidImageHandler instance;

	public static AndroidImageHandler getInstanceOf() {
		if (instance == null) {
			instance = new AndroidImageHandler();
		}
		return instance;
	}

	private AndroidImageHandler() {

	}

	/**
	 * Resize the image until the total size require to store the image is less
	 * than maxsize
	 * 
	 * @param artwork
	 * @param maxSize
	 * @throws IOException
	 */
	public void reduceQuality(Artwork artwork, int maxSize) throws IOException {
		while (artwork.getBinaryData().length > maxSize) {
			Bitmap srcImage = (Bitmap) artwork.getImage();
			int w = srcImage.getWidth();
			int newSize = w / 2;
			makeSmaller(artwork, newSize);
		}

		// Image srcImage = (Image)artwork.getImage();
		// int w = srcImage.getWidth(null);
		// int newSize = w /2;
		// makeSmaller(artwork,newSize);
	}

	/**
	 * Resize image using Java 2D
	 * 
	 * @param artwork
	 * @param size
	 * @throws java.io.IOException
	 */
	public void makeSmaller(Artwork artwork, int size) throws IOException {
		Bitmap image = (Bitmap) artwork.getImage();

		int w = image.getWidth();
		int h = image.getHeight();

		// Set to the new scaled image
		image = Bitmap.createScaledBitmap(image, size, size, false);

		if (artwork.getMimeType() != null
				&& isMimeTypeWritable(artwork.getMimeType())) {
			artwork.setBinaryData(writeImage(image, artwork.getMimeType()));
		} else {
			artwork.setBinaryData(writeImageAsPng(image));
		}
	}

	public boolean isMimeTypeWritable(String mimeType) {
		Resources res = Resources.getSystem();
		String[] writer = res.getStringArray(R.array.imageTypes);

		for (int i = 0; i < writer.length; i++) {
			if (writer[i].compareToIgnoreCase(mimeType) == 0) {
				return true;
			}
		}

		return false;
		// Iterator<ImageWriter> writers =
		// ImageIO.getImageWritersByMIMEType(mimeType);
		// return writers.hasNext();
	}

	/**
	 * Show read formats this is only for debugging On Windows supports
	 * png/jpeg/bmp/gif
	 */
	public void showReadFormats() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Show write formats
	 * 
	 * On Windows supports png/jpeg/bmp
	 */
	public void showWriteFormats() {
		throw new UnsupportedOperationException();
	}

	public byte[] writeImage(Bitmap bi, String mimeType) throws IOException {
		Resources res = Resources.getSystem();
		String[] writer = res.getStringArray(R.array.imageTypes);

		for (int i = 0; i < writer.length; i++) {
			if (writer[i].compareToIgnoreCase(mimeType) == 0) {
				ByteArrayOutputStream stream;
				
				int size = bi.getRowBytes() * bi.getHeight();
				ByteBuffer b = ByteBuffer.allocate(size);

				bi.copyPixelsToBuffer(b);
				b.rewind();

				byte[] bytes = new byte[size];

				try {
					b.get(bytes, 0, bytes.length);
					return bytes;
				} catch (BufferUnderflowException e) {
					throw new IOException(e);
				}
			}
		}
		
		throw new IOException("Cannot write to this mimetype");
		
		// Iterator<ImageWriter> writers =
		// ImageIO.getImageWritersByMIMEType(mimeType);
		// if(writers.hasNext())
		// {
		// ImageWriter writer = writers.next();
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// writer.setOutput(ImageIO.createImageOutputStream(baos));
		// writer.write(bi);
		// return baos.toByteArray();
		// }
//		throw new IOException("Cannot write to this mimetype");
	}

	public byte[] writeImageAsPng(Bitmap bi) throws IOException {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bi.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
		
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// ImageIO.write(bi, ImageFormats.MIME_TYPE_PNG,baos);
		// return baos.toByteArray();
	}
}
