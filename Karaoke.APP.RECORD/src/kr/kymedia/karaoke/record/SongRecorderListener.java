/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	SongRecorderListener.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.record
 *    |_ SongRecorderListener.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.record;

import java.nio.ByteBuffer;

/**
 *
 * NOTE:<br>
 *
 * @author isyoon
 * @since 2013. 4. 5.
 * @version 1.0
 * @see SongRecorderListener.java
 */
public interface SongRecorderListener {
	int read(byte[] audioData, int offsetInBytes, int sizeInBytes);

	int read(ByteBuffer audioBuffer, int sizeInBytes);

	int read(short[] audioData, int offsetInShorts, int sizeInShorts);
}
