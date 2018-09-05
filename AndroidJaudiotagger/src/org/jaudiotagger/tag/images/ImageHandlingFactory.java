package org.jaudiotagger.tag.images;


/**
 * Provides a class for all Image handling, this is required because the image
 * classes provided by standard java are different to those provided by Android
 */
public class ImageHandlingFactory {

	public static ImageHandler getInstance() {

		return AndroidImageHandler.getInstanceOf();

	}
}
