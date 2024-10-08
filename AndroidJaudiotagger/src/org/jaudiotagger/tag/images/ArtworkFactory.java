package org.jaudiotagger.tag.images;

import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Get appropriate Artwork class
 */
public class ArtworkFactory
{
	// TODO 02-21 11:03:57.491: W/id3(1082): 03 One For the Road.mp3:No space to find another frame:
	// TODO 02-21 11:03:57.491: W/id3(1082): 03 One For the Road.mp3:Invalid Frame:03 One For the Road.mp3:No space to find another frame


    public static Artwork getNew()
    {
        //Android
        return new AndroidArtwork();
    }

    /**
     * Create Artwork instance from A Flac Metadata Block
     *
     * @param coverArt
     * @return
     */
    public static Artwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt)
    {
        return AndroidArtwork.createArtworkFromMetadataBlockDataPicture(coverArt);
    }

    /**
     * Create Artwork instance from an image file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Artwork createArtworkFromFile(File file) throws IOException
    {
        return AndroidArtwork.createArtworkFromFile(file);
    }

    /**
     * Create Artwork instance from an image file
     *
     * @param link
     * @return
     * @throws IOException
     */
    public static Artwork createLinkedArtworkFromURL(String link) throws IOException
    {
        return AndroidArtwork.createLinkedArtworkFromURL(link);
    }
}
