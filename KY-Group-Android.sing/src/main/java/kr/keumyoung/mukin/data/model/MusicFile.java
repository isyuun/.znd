package kr.keumyoung.mukin.data.model;

import java.io.File;

import kr.keumyoung.mukin.helper.AudioMixingHelper;

/**
 *  on 06/04/18.
 * Project: KyGroup
 */
// unused
public class MusicFile {
    private File file;
    private AudioMixingHelper.MusicFileDescriptor descriptor;

    public MusicFile(File file, AudioMixingHelper.MusicFileDescriptor descriptor) {
        this.file = file;
        this.descriptor = descriptor;
    }

    public File getFile() {
        return file;
    }

    public AudioMixingHelper.MusicFileDescriptor getDescriptor() {
        return descriptor;
    }
}
