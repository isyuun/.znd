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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.TV
 * filename	:	SongData.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.play
 *    |_ SongData.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.data;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import kr.keumyoung.karaoke.api._Const;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author isyoon
 * @since 2015. 8. 4.
 * @version 1.0
 */
class SongData implements _Const
{
	final static String AUTHOR = "작사";
	final static String LYRICSAUTHOR = "작곡";
	final static String SINGER = "가수";

	private CtrlTag m_cTag;
	private LyricsInfoTag m_lTag;
	private int m_nFileType;
	private int m_nSyncTime;
	private List<LyricsTag> m_listLyricsTag;
	private List<SyncTag> m_listSyncTag;
	private RandomAccessFile m_raf;
	private int m_filesize;

	public CtrlTag getControlTag() {
		return m_cTag;
	}

	public LyricsInfoTag getLyricsInfoTag() {
		return m_lTag;
	}

	public List<LyricsTag> getListLyricsTag() {
		return m_listLyricsTag;
	}

	public List<SyncTag> getListSyncTag() {
		return m_listSyncTag;
	}

	public SongData()
	{
		create();
	}

	public void create()
	{
		m_nSyncTime = 0;
		m_cTag = new CtrlTag();
		m_lTag = new LyricsInfoTag();
		m_listLyricsTag = new ArrayList<LyricsTag>();
		m_listSyncTag = new ArrayList<SyncTag>();
	}

	public void release()
	{
		m_listLyricsTag.clear();
		m_listSyncTag.clear();
	}

	public boolean load(String name)
	{
		boolean ret = true;

		if (name == null || name.equals(""))
			return false;

		m_nFileType = getFileType(name);
		if (m_nFileType == FILE_OPENERROR || m_nFileType == FILE_INVALID)
			return false;

		release();

		try
		{
			m_raf = new RandomAccessFile(name, "r");
			if (m_raf != null)
			{
				m_filesize = (int) m_raf.length();

				if (m_nFileType == FILE_SOK)
				{
					setLyricsTag();
				}
				else if (m_nFileType == FILE_MID)
				{
					setSyncTag();
				}
				else if (m_nFileType == FILE_ROK)
				{
					setControlTag();
					setLyricsTag();
					setSyncTag();
				}
				else if (m_nFileType == FILE_SKYM || m_nFileType == FILE_KYM)
				{
					setControlTag(m_nFileType);
					setLyricsTag(m_nFileType);
					setSyncTag(m_nFileType);
				}
				else
				{
					m_raf.close();
					return false;
				}
			}
		} catch (IOException e)
		{
			ret = false;
		} finally
		{
			try
			{
				if (m_raf != null)
					m_raf.close();
			} catch (IOException ee) {
			} finally {
			}
		}

		return ret;
	}

	private void setControlTag() throws IOException
	{
		m_raf.seek(0);

		m_raf.skipBytes(5);
		m_cTag.nSize = SongUtil.swap(m_raf.readUnsignedShort());

		byte[] buffer = new byte[8];
		m_raf.read(buffer);
		m_cTag.strSongId = SongUtil.byteToString(buffer);

		m_raf.skipBytes(2);
		m_cTag.nSoPos = SongUtil.swap(m_raf.readInt());

		m_raf.skipBytes(2);
		m_cTag.nGsPos = SongUtil.swap(m_raf.readInt());

		m_raf.skipBytes(2);
		m_cTag.nMpPos = SongUtil.swap(m_raf.readInt());

		m_raf.skipBytes(2);
		m_cTag.nMsCnt = SongUtil.swap(m_raf.readUnsignedShort());

		m_cTag.nMsPos = SongUtil.swap(m_raf.readInt());
	}

	private void setControlTag(int type) throws IOException
	{
		m_raf.seek(0);

		if (type == FILE_SKYM)
		{
			m_raf.skipBytes(17);
		}
		else if (type == FILE_KYM)
		{
			m_raf.skipBytes(m_filesize - 251);
		}

		m_cTag.nMsCnt = SongUtil.swap(m_raf.readInt());

		m_raf.skipBytes(4);
		m_cTag.nSoPos = SongUtil.swap(m_raf.readInt());

		m_cTag.nGsPos = SongUtil.swap(m_raf.readInt());

		m_cTag.nPiPos = SongUtil.swap(m_raf.readInt());

		if (type == FILE_SKYM)
		{
			m_cTag.nMpPos = SongUtil.swap(m_raf.readInt());
			m_cTag.nMsPos = m_filesize - m_cTag.nMpPos;
		}
		else if (type == FILE_KYM)
		{
			m_raf.seek(0);
			m_cTag.nMpPos = 0;
			m_raf.skipBytes(m_filesize - 234);
			m_cTag.nMsPos = SongUtil.swap(m_raf.readInt()) - 12;
		}
	}

	private void setLyricsTag() throws IOException
	{
		m_raf.seek(0);

		StringTokenizer tok = null;
		if (m_nFileType == FILE_ROK)
		{
			int size = m_cTag.nGsPos - m_cTag.nSoPos;
			byte[] buff = new byte[size];
			m_raf.skipBytes(m_cTag.nSoPos);
			m_raf.read(buff);
			tok = new StringTokenizer(buff.toString(), "\n");
		}
		else
		{
			byte[] buff = new byte[m_filesize];
			m_raf.read(buff);
			tok = new StringTokenizer(buff.toString(), "\n");
		}

		int limit = tok.countTokens();
		for (int i = 0; i < limit; i++)
		{
			String ptr = tok.nextToken();

			if (i == 0)
			{
				StringTokenizer line = new StringTokenizer(ptr, "\n");
				m_lTag.strYear = SongUtil.byteToString(line.nextToken().getBytes());
				m_lTag.strRythem = SongUtil.byteToString(line.nextToken().getBytes());
				m_lTag.strGenre = SongUtil.byteToString(line.nextToken().getBytes());
				StringTokenizer key = new StringTokenizer(line.nextToken(), "/");
				m_lTag.strSex = SongUtil.byteToString(key.nextToken().getBytes());
				m_lTag.strMale = SongUtil.byteToString(key.nextToken().getBytes());
				m_lTag.strFmale = SongUtil.byteToString(key.nextToken().getBytes());
			}
			else if (i == 1)
			{
				m_lTag.strTitle1 = SongUtil.byteToString(ptr.getBytes());
			}
			else if (i == 2)
			{
				m_lTag.strTitle2 = SongUtil.byteToString(ptr.getBytes());
			}
			else if (i == 3)
			{
				StringTokenizer ptr2 = new StringTokenizer(SongUtil.byteToString(ptr.getBytes()), LYRICSAUTHOR);
				String str = ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strLyricsAuthor = str.trim();
			}
			else if (i == 4)
			{
				StringTokenizer ptr2 = new StringTokenizer(SongUtil.byteToString(ptr.getBytes()), AUTHOR);
				String str = ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strAuthor = str.trim();
			}
			else if (i == 5)
			{
				StringTokenizer ptr2 = new StringTokenizer(SongUtil.byteToString(ptr.getBytes()), SINGER);
				String str = ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strAuthor = str.trim();
			}
			else
			{
				LyricsTag tag = new LyricsTag();
				tag.strLineLyrics = ptr.getBytes();
				if (!tag.strLineLyrics.equals(""))
					m_listLyricsTag.add(tag);
			}
		}
	}

	private void setLyricsTag(int type) throws IOException
	{
		m_raf.seek(0);

		if (type == FILE_SKYM)
		{
			m_raf.skipBytes(132);
		}
		else if (type == FILE_KYM)
		{
			m_raf.skipBytes(m_filesize - 508);
		}

		byte[] songid = new byte[6];
		m_raf.read(songid);
		m_cTag.strSongId = SongUtil.byteToString(songid);

		byte[] titl1 = new byte[40];
		m_raf.read(titl1);
		m_lTag.strTitle1 = SongUtil.byteToString(titl1);

		byte[] titl2 = new byte[40];
		m_raf.read(titl2);
		m_lTag.strTitle2 = SongUtil.byteToString(titl2);

		byte[] lauthor = new byte[20];
		m_raf.read(lauthor);
		m_lTag.strLyricsAuthor = SongUtil.byteToString(lauthor);

		byte[] author = new byte[20];
		m_raf.read(author);
		m_lTag.strAuthor = SongUtil.byteToString(author);

		byte[] singer = new byte[20];
		m_raf.read(singer);
		m_lTag.strSinger = SongUtil.byteToString(singer);

		m_raf.skipBytes(1);

		int year = SongUtil.swap(m_raf.readInt());
		m_lTag.strYear = "" + year;

		m_raf.skipBytes(8);

		byte[] sex = new byte[2];
		m_raf.read(sex);
		m_lTag.strSex = SongUtil.byteToString(sex);

		m_raf.seek(0);
		m_raf.skipBytes(m_cTag.nSoPos + 4);

		for (int i = 0; i < m_cTag.nMsCnt; i++)
		{
			byte[] lyrics = new byte[24];
			m_raf.read(lyrics);

			LyricsTag tag = new LyricsTag();
			tag.strLineLyrics = lyrics;
			m_listLyricsTag.add(tag);
		}
	}

	private void setSyncTag() throws IOException
	{
		m_raf.seek(0);

		int nGsSize = m_cTag.nMsPos - m_cTag.nGsPos;
		m_raf.skipBytes(m_cTag.nGsPos);

		int nGaCnt = nGsSize / 8;
		int nStartTime = 0, nEndTime = 0, nAttr = 0;
		byte nPosLyrics = 0;
		byte nPosLen = 0;
		int nPosOneLine = 0;
		int nLineDisplay = 0;
		int bNextDisplay = 0;

		byte ch = 0;
		int size = 0;

		size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;

		nStartTime = SongUtil.swap(m_raf.readInt());
		nAttr = SongUtil.swap(m_raf.readInt());
		nEndTime = SongUtil.swap(m_raf.readInt()) - 1;

		if (nAttr == 0)
		{
			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime + m_nSyncTime;
			sTag.lTimeSyncEnd = nEndTime + m_nSyncTime;
			sTag.nPosLyrics = 0;
			sTag.nPosLen = 0;
			sTag.nPosOneLine = 0;
			sTag.nLineDisplay = 0;
			sTag.nAttribute = 2;
			sTag.nNextDisplay = 0;

			if (sTag.lTimeSyncStart < 0)
				sTag.lTimeSyncStart = 0;

			if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_READY)
				sTag.lTimeSyncStart = sTag.lTimeSyncEnd - TIME_READY;

			m_listSyncTag.add(sTag);
		}
		else
		{
			m_raf.seek(0);
			m_raf.skipBytes(4);
		}

		for (int k = 1; k < nGaCnt - 1; k++)
		{
			nStartTime = SongUtil.swap(m_raf.readInt());
			nAttr = SongUtil.swap(m_raf.readInt());
			nEndTime = SongUtil.swap(m_raf.readInt()) - 1;

			nPosLen = 0;

			ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];

			if (ch == ' ')
			{
				nPosLyrics++;
				ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
			}

			if ((ch & 0x80) != 0)
			{
				nPosLen = 2;
			}
			else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))
			{
				int nAdd = nPosLyrics;

				for (; nAdd < size; nAdd++)
				{
					byte c = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nAdd];

					if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
						nPosLen++;
					else
						break;
				}
			}
			else
			{
				nPosLen = 1;
			}

			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime + m_nSyncTime;
			sTag.lTimeSyncEnd = nEndTime + m_nSyncTime;
			sTag.nPosLyrics = nPosLyrics;
			sTag.nPosLen = nPosLen;
			sTag.nLineDisplay = nLineDisplay;
			sTag.nPosOneLine = nPosOneLine;

			if (nAttr == 3)
				sTag.nAttribute = 7;
			else
				sTag.nAttribute = 0;

			if (nPosOneLine < nLineDisplay)
				nPosOneLine++;

			nPosLyrics += nPosLen;

			if (nPosLyrics >= size / 2 && bNextDisplay == 0)
			{
				sTag.nNextDisplay = 1;
				bNextDisplay = 1;
			}
			else
			{
				sTag.nNextDisplay = 0;
			}

			if (nPosLyrics >= size)
			{
				if (++nLineDisplay >= m_listLyricsTag.size())
					break;

				bNextDisplay = 0;
				nPosLyrics = 0;
				size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;

				if (size == 0)
					break;
			}

			if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_TEXT)
				sTag.lTimeSyncEnd = sTag.lTimeSyncStart + TIME_TEXT;

			m_listSyncTag.add(sTag);

			if (nAttr == 3)
			{
				SyncTag sTag2 = new SyncTag();
				nStartTime = SongUtil.swap(m_raf.readInt());
				nAttr = SongUtil.swap(m_raf.readInt());
				nEndTime = SongUtil.swap(m_raf.readInt()) - 1;

				sTag2.lTimeSyncStart = nStartTime + m_nSyncTime;
				sTag2.lTimeSyncEnd = nEndTime + m_nSyncTime;
				sTag2.nPosLyrics = 0;
				sTag2.nPosLen = 0;
				sTag2.nPosOneLine = nPosOneLine++;
				sTag2.nLineDisplay = nLineDisplay;
				sTag2.nAttribute = 2;
				sTag2.nNextDisplay = 0;

				if (sTag2.lTimeSyncEnd - sTag2.lTimeSyncStart > TIME_READY)
					sTag2.lTimeSyncStart = sTag2.lTimeSyncEnd - TIME_READY;

				m_listSyncTag.add(sTag2);
			}
		}

		SyncTag sTag = new SyncTag();
		sTag.lTimeSyncStart = nStartTime + m_nSyncTime;
		sTag.lTimeSyncEnd = nEndTime + m_nSyncTime;
		sTag.nPosLyrics = nPosLyrics;
		sTag.nPosLen = 1;
		sTag.nPosOneLine = nPosOneLine;
		sTag.nLineDisplay = nLineDisplay;
		sTag.nAttribute = 7;
		sTag.nNextDisplay = 0;
		m_listSyncTag.add(sTag);

		m_nSyncTime = 0;
	}

	private void setSyncTag(int type) throws IOException
	{
		m_raf.seek(0);

		m_raf.skipBytes(m_cTag.nGsPos + 4);

		int nCount = 0;
		int nPosition = m_cTag.nGsPos + 4;

		while (nPosition < m_cTag.nPiPos)
		{
			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = SongUtil.swap(m_raf.readInt());
			sTag.lTimeSyncEnd = SongUtil.swap(m_raf.readInt());
			sTag.nPosLyrics = m_raf.readByte();
			sTag.nPosLen = m_raf.readByte();
			sTag.nPosOneLine = SongUtil.swap(m_raf.readInt());
			sTag.nLineDisplay = SongUtil.swap(m_raf.readInt());
			sTag.nAttribute = m_raf.readByte();
			sTag.nNextDisplay = SongUtil.swap(m_raf.readInt());

			nPosition += 23;
			sTag.nPosOneLine = nCount;

			nCount = sTag.nLineDisplay;

			switch (sTag.nAttribute)
			{
			case SYNC_TEXT:
				if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > 1500)
					sTag.lTimeSyncEnd = sTag.lTimeSyncStart + 1500;
				break;

			case SYNC_READY:
				if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > 4000)
					sTag.lTimeSyncStart = sTag.lTimeSyncEnd - 4000;
				break;

			case SYNC_ENDDIVISION:
				sTag.lTimeSyncEnd = sTag.lTimeSyncStart + 1500;
				break;
			}

			m_listSyncTag.add(sTag);
		}

		SyncTag sTag = new SyncTag();
		sTag.lTimeSyncStart = sTag.lTimeSyncStart + 300;
		sTag.lTimeSyncEnd = sTag.lTimeSyncEnd + 300;
		sTag.nPosLyrics = sTag.nPosLyrics;
		sTag.nPosLen = sTag.nPosLen;
		sTag.nPosOneLine = sTag.nPosOneLine;
		sTag.nLineDisplay = sTag.nLineDisplay;
		sTag.nLineDisplay = SYNC_ENDDIVISION;
		sTag.nNextDisplay = sTag.nNextDisplay;

		m_listSyncTag.add(sTag);
	}

	private int getFileType(String name)
	{
		int size = name.length();
		int pos = name.lastIndexOf('.') + 1;
		String ext = name.substring(pos, size).toUpperCase();

		if (ext.equals("ROK"))
		{
			return FILE_ROK;
		}
		else if (ext.equals("SOK"))
		{
			return FILE_SOK;
		}
		else if (ext.equals("MID"))
		{
			return FILE_MID;
		}
		else if (ext.equals("SKYM"))
		{
			return FILE_SKYM;
		}
		else if (ext.equals("KYM"))
		{
			return FILE_KYM;
		}

		return FILE_INVALID;
	}

	protected class CtrlTag
	{
		public int nSize;
		public String strSongId;
		public int nSoPos;
		public int nGsPos;
		public int nMpPos;
		public int nMsCnt;
		public int nMsPos;
		public int nPiPos;
	}

	public class LyricsInfoTag
	{
		public String strYear;
		public String strRythem;
		public String strGenre;
		public String strSex;
		public String strMale;
		public String strFmale;
		public String strTitle1;
		public String strTitle2;
		public String strAuthor;
		public String strLyricsAuthor;
		public String strSinger;
	}

	public class LyricsTag
	{
		public byte[] strLineLyrics;

		public LyricsTag()
		{
			strLineLyrics = new byte[24];
		}
	}

	public class SyncTag
	{
		public long lTimeSyncStart;
		public long lTimeSyncEnd;
		public byte nPosLyrics;
		public byte nPosLen;
		public int nPosOneLine;
		public int nLineDisplay;
		public byte nAttribute;
		public int nNextDisplay;
	}
}
