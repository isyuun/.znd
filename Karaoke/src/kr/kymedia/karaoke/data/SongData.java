package kr.kymedia.karaoke.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.text.TextUtils;

/*import kr.kymedia.karaoke.kpop.play2.midilib.MidiFile;
 import kr.kymedia.karaoke.kpop.play2.midilib.MidiTrack;
 import kr.kymedia.karaoke.kpop.play2.midilib.event.MidiEvent;
 import kr.kymedia.karaoke.kpop.play2.midilib.event.ProgramChange;
 import kr.kymedia.karaoke.kpop.play2.midilib.event.meta.Tempo;*/

public class SongData {
	public final static String AUTHOR = "작곡";
	public final static String LYRICSAUTHOR = "작사";
	public final static String SINGER = "노래";
	public final static String READY = "(간주중)";

	public final static String AUTHOR_JP = "作曲";
	public final static String LYRICSAUTHOR_JP = "作詞";
	public final static String SINGER_JP = "歌手";
	public final static String READY_JP = "(間奏中)";

	public final static String AUTHOR_EN = "Composer";
	public final static String LYRICSAUTHOR_EN = "Writer";
	public final static String SINGER_EN = "Singer";
	public final static String READY_EN = "(Interlude)";

	private final int SYNC_TEXT = 0;
	private final int SYNC_READY = 2;
	private final int SYNC_ENDDIVISION = 7;
	private final int FILE_KYM = 0;
	private final int FILE_SKYM = 1;
	private final int FILE_INVALID = 5;
	private final int FILE_OPENERROR = 6;
	private final int FILE_ROK = 7;
	private final int FILE_SOK = 9;
	private final int FILE_MID = 10;
	private final int TIME_TEXT = 1500;
	private final int TIME_READY = 5000;

	private CtrlTag m_cTag;
	private LyricsInfoTag m_lTag;
	private int m_nFileType;
	private int m_nSyncTime;
	private List<String> m_listRuby1Tag;
	private List<String> m_listRuby2Tag;
	private List<LyricsTag> m_listLyricsTag;
	private List<SyncTag> m_listSyncTag;
	private RandomAccessFile m_raf;
	private int m_filesize;
	private boolean m_load;

	private String m_title;
	private String[] m_titles;
	private String m_singer;
	private String m_composer;
	private String m_writer;

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

	public List<String> getListRuby1Tag() {
		return m_listRuby1Tag;
	}

	public List<String> getListRuby2Tag() {
		return m_listRuby2Tag;
	}

	public SongData() {
		create();
	}

	public boolean isLoad() {
		return m_load;
	}

	public void create() {
		m_nSyncTime = 0;
		m_cTag = new CtrlTag();
		m_lTag = new LyricsInfoTag();
		m_listLyricsTag = new ArrayList<LyricsTag>();
		m_listRuby1Tag = new ArrayList<String>();
		m_listRuby2Tag = new ArrayList<String>();
		m_listSyncTag = new ArrayList<SyncTag>();
		m_load = false;
	}

	public void release() {
		m_listLyricsTag.clear();
		m_listRuby1Tag.clear();
		m_listRuby2Tag.clear();
		m_listSyncTag.clear();
		m_load = false;
	}

	public boolean load(String name) {
		boolean ret = true;

		if (name == null || name.equals(""))
			return false;

		m_nFileType = getFileType(name);
		if (m_nFileType == FILE_OPENERROR || m_nFileType == FILE_INVALID)
			return false;

		release();

		try {
			m_raf = new RandomAccessFile(name, "r");
			if (m_raf != null && m_raf.length() > 0) {
				m_filesize = (int) m_raf.length();

				if (m_nFileType == FILE_SOK) {
					setLyricsTag();
				} else if (m_nFileType == FILE_MID) {
					setSyncTag();
				} else if (m_nFileType == FILE_ROK) {
					setControlTag();
					setLyricsTag();
					setSyncTag();
				} else if (m_nFileType == FILE_SKYM || m_nFileType == FILE_KYM) {
					setControlTag(m_nFileType);
					setLyricsTag(m_nFileType);
					setSyncTag(m_nFileType);
				} else {
					m_raf.close();
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			try {
				if (m_raf != null)
					m_raf.close();
			} catch (IOException ee) {
			} finally {
			}
		}

		m_load = ret;

		return ret;
	}

	public boolean load(String sok, String mid) {
		boolean ret = true;

		if (sok == null || sok.equals(""))
			return false;

		if (mid == null || mid.equals(""))
			return false;

		release();

		try {
			setLyricsSokTag(sok);
			setSyncMidTag(mid);
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}

		m_load = ret;

		return ret;
	}

	public boolean load(InputStream is) {
		boolean ret = false;

		release();

		try {
			DataInputStream dis = new DataInputStream(is);

			byte[] type = new byte[3];
			dis.read(type);
			String fileType = SongUtil.byteToString(type);

			if (fileType.equals("ROK") == true) {
				ret = setControlRokTag(dis);
				if (ret == false)
					return ret;

				ret = setLyricsRokTag(dis);
				if (ret == false)
					return ret;

				ret = setSyncRokTag(dis);
				if (ret == false)
					return ret;
			} else if (fileType.equals("LYR") == true) {
				ret = setControlTag(dis);
				if (ret == false)
					return ret;

				ret = setLyricsInfoTag(dis);
				if (ret == false)
					return ret;

				ret = setLyricsTag(dis);
				if (ret == false)
					return ret;

				ret = setSyncTag(dis);
				if (ret == false)
					return ret;
			}
		} catch (IOException e) {
			ret = false;
		}

		m_load = ret;

		return ret;
	}

	public boolean load(InputStream sok, InputStream mid, int sokLen) {
		boolean ret = false;

		release();

		try {
			DataInputStream dataSok = new DataInputStream(sok);

			ret = setLyricsSokTag(dataSok, sokLen);
			if (ret == false)
				return ret;

			ret = setSyncMidTag(mid);
			if (ret == false)
				return ret;
		} catch (IOException e) {
			ret = false;
		}

		m_load = ret;

		return ret;
	}

	public int getLastSyncTime() {
		return m_nSyncTime;
	}

	public long DeltaTempoToTime(long delta, int tempo, int resolution) {
		double tick = 60;
		tick *= tempo;
		tick /= 60000000;
		tick /= resolution;
		tick *= delta;

		long time = (long) (tick * 1000);

		return time;
	}

	/*
	 * public int MidiToTime(long delta, int resolution, ArrayList<Tempo> tempo) {
	 * long ret = 0;
	 * long prev = 0;
	 * long curr = 0;
	 * 
	 * long currTime = 0;
	 * int currTempo = 0;
	 * int prevTempo = 0;
	 * 
	 * for( int i = 0; i < tempo.size(); i++ ) {
	 * currTime = tempo.get(i).getTick();
	 * currTempo = tempo.get(i).getMpqn();
	 * 
	 * if ( currTime > delta )
	 * break;
	 * 
	 * prev = curr;
	 * curr = currTime;
	 * ret += DeltaTempoToTime(curr - prev, prevTempo, resolution);
	 * prevTempo = currTempo;
	 * }
	 * 
	 * if ( currTime > delta )
	 * ret += DeltaTempoToTime(delta - curr, prevTempo, resolution);
	 * else
	 * ret += DeltaTempoToTime(delta - curr, currTempo, resolution);
	 * 
	 * return (int)ret;
	 * }
	 */

	private boolean setLyricsSokTag(DataInputStream dis, int size) throws IOException {
		byte[] buffer = new byte[size];
		dis.read(buffer);

		StringTokenizer tok = new StringTokenizer(SongUtil.byteToString(buffer, "EUC-JP"), "\n");
		m_lTag.strLang = "EUC-JP";

		int limit = tok.countTokens();
		for (int i = 0; i < limit; i++) {
			String ptr = tok.nextToken().trim();

			if (i == 0) {
				try {
					StringTokenizer line = new StringTokenizer(ptr, "-");
					m_lTag.strYear = line.nextToken();
					m_lTag.strRythem = line.nextToken();
					m_lTag.strGenre = line.nextToken();
					StringTokenizer key = new StringTokenizer((String) line.nextToken(), "/");
					m_lTag.strSex = key.nextToken();
					m_lTag.strMale = key.nextToken();
					m_lTag.strFmale = key.nextToken();
				} catch (NoSuchElementException e) {
					return false;
				}
			} else if (i == 1) {
				m_lTag.strTitle1 = ptr;
			} else if (i == 2) {
				m_lTag.strTitle2 = ptr;
			} else if (i == 3) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, LYRICSAUTHOR_JP);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strLyricsAuthor = str.trim();
			} else if (i == 4) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, AUTHOR_JP);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strAuthor = str.trim();
			} else if (i == 5) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, SINGER_JP);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strSinger = str.trim();
				// } else if ( i%3 == 0 ) {
				// m_listRuby2Tag.add(ptr);
			} else if (i % 3 == 1) {
				m_listRuby1Tag.add(ptr);
			} else if (i % 3 == 2) {
				LyricsTag tag = new LyricsTag();
				tag.strLineLyrics = ptr.getBytes("EUC-JP");
				if (!tag.strLineLyrics.equals(""))
					m_listLyricsTag.add(tag);
			}
		}

		StringTokenizer token = new StringTokenizer(SongUtil.byteToString(buffer, "EUC-KR"), "\n");
		for (int i = 0; i < limit; i++) {
			String ptr = token.nextToken().trim();

			if (i > 5 && i % 3 == 0) {
				m_listRuby2Tag.add(ptr);
			}
		}

		return true;
	}

	private boolean setSyncMidTag(InputStream dis) throws IOException {
		/*
		 * MidiFile midi = null;
		 * try {
		 * midi = new MidiFile(dis);
		 * } catch(IOException e) {
		 * return false;
		 * }
		 * 
		 * ArrayList<Tempo> tempoList = new ArrayList<Tempo>();
		 * 
		 * MidiTrack T0 = midi.getTracks().get(0);
		 * Iterator<MidiEvent> it = T0.getEvents().iterator();
		 * while(it.hasNext()) {
		 * MidiEvent E = it.next();
		 * 
		 * if ( E.getClass().equals(Tempo.class) ) {
		 * Tempo tempo = (Tempo)E;
		 * tempoList.add(tempo);
		 * }
		 * }
		 * 
		 * ArrayList<Integer> syncList = new ArrayList<Integer>();
		 * ArrayList<Integer> syncProgramList = new ArrayList<Integer>();
		 * 
		 * MidiTrack T1 = midi.getTracks().get(1);
		 * it = T1.getEvents().iterator();
		 * while(it.hasNext()) {
		 * MidiEvent E = it.next();
		 * 
		 * if ( E.getClass().equals(ProgramChange.class) ) {
		 * ProgramChange program = (ProgramChange)E;
		 * 
		 * int time = MidiToTime(program.getTick(), midi.getResolution(), tempoList);
		 * 
		 * syncList.add(time);
		 * syncProgramList.add(program.getProgramNumber());
		 * }
		 * }
		 * 
		 * int nStartTime = 0, nEndTime = 0, nAttr = 0;
		 * byte nPosLyrics = 0;
		 * byte nPosLen = 0;
		 * int nPosOneLine = 0;
		 * int nLineDisplay = 0;
		 * int bNextDisplay = 0;
		 * 
		 * byte ch = 0;
		 * int size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;
		 * 
		 * nStartTime = syncList.get(0);
		 * nAttr = syncProgramList.get(0);
		 * nEndTime = syncList.get(1) - 1;
		 * 
		 * int w = 0;
		 * 
		 * if ( nAttr == 0 ) {
		 * SyncTag sTag = new SyncTag();
		 * sTag.lTimeSyncStart = nStartTime;
		 * sTag.lTimeSyncEnd = nEndTime;
		 * sTag.nPosLyrics = 0;
		 * sTag.nPosLen = 0;
		 * sTag.nPosOneLine = 0;
		 * sTag.nLineDisplay = 0;
		 * sTag.nAttribute = 2;
		 * sTag.nNextDisplay = 0;
		 * 
		 * if (sTag.lTimeSyncStart < 0)
		 * sTag.lTimeSyncStart = 0;
		 * 
		 * if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_READY)
		 * sTag.lTimeSyncStart = sTag.lTimeSyncEnd - TIME_READY;
		 * 
		 * m_listSyncTag.add(sTag);
		 * 
		 * w = 1;
		 * }
		 * 
		 * for (int k = w; k < syncList.size(); k++) {
		 * nStartTime = syncList.get(k);
		 * nAttr = syncProgramList.get(k);
		 * 
		 * if ( k == syncList.size() - 1 ) {
		 * nEndTime = syncList.get(k) + TIME_TEXT;
		 * } else {
		 * nEndTime = syncList.get(k+1) - 1;
		 * }
		 * 
		 * nPosLen = 0;
		 * 
		 * ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
		 * 
		 * if (ch == ' ') {
		 * nPosLyrics++;
		 * ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
		 * }
		 * 
		 * if ((ch & 0x80) != 0) {
		 * nPosLen = 2;
		 * } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
		 * int nAdd = nPosLyrics;
		 * 
		 * for (; nAdd < size; nAdd++) {
		 * byte c = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nAdd];
		 * 
		 * if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
		 * nPosLen++;
		 * } else {
		 * break;
		 * }
		 * }
		 * } else {
		 * nPosLen = 1;
		 * }
		 * 
		 * SyncTag sTag = new SyncTag();
		 * sTag.lTimeSyncStart = nStartTime;
		 * sTag.lTimeSyncEnd = nEndTime;
		 * sTag.nPosLyrics = nPosLyrics;
		 * sTag.nPosLen = nPosLen;
		 * sTag.nLineDisplay = nLineDisplay;
		 * sTag.nPosOneLine = nPosOneLine;
		 * 
		 * if (nAttr == 3)
		 * sTag.nAttribute = 7;
		 * else
		 * sTag.nAttribute = 0;
		 * 
		 * if (nLineDisplay != nPosOneLine)
		 * nPosOneLine++;
		 * 
		 * nPosLyrics += nPosLen;
		 * 
		 * if (nPosLyrics >= size / 2 && bNextDisplay == 0) {
		 * sTag.nNextDisplay = 1;
		 * bNextDisplay = 1;
		 * } else {
		 * sTag.nNextDisplay = 0;
		 * }
		 * 
		 * if (nPosLyrics >= size) {
		 * if (++nLineDisplay >= m_listLyricsTag.size()) {
		 * break;
		 * }
		 * 
		 * bNextDisplay = 0;
		 * nPosLyrics = 0;
		 * size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;
		 * 
		 * if (size == 0) {
		 * break;
		 * }
		 * }
		 * 
		 * if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_TEXT)
		 * sTag.lTimeSyncEnd = sTag.lTimeSyncStart + TIME_TEXT;
		 * 
		 * m_listSyncTag.add(sTag);
		 * 
		 * if ( nAttr == 3 ) {
		 * k++;
		 * nStartTime = syncList.get(k);
		 * nAttr = syncProgramList.get(k);
		 * nEndTime = syncList.get(k+1) - 1;
		 * 
		 * SyncTag sTag2 = new SyncTag();
		 * sTag2.lTimeSyncStart = nStartTime;
		 * sTag2.lTimeSyncEnd = nEndTime;
		 * sTag2.nPosLyrics = 0;
		 * sTag2.nPosLen = 0;
		 * sTag2.nLineDisplay = nLineDisplay;
		 * sTag2.nPosOneLine = nPosOneLine++;
		 * sTag2.nAttribute = 2;
		 * sTag2.nNextDisplay = 0;
		 * 
		 * if (sTag2.lTimeSyncEnd - sTag2.lTimeSyncStart > TIME_READY)
		 * sTag2.lTimeSyncStart = sTag2.lTimeSyncEnd - TIME_READY;
		 * 
		 * m_listSyncTag.add(sTag2);
		 * }
		 * }
		 */

		return true;
	}

	private boolean setSyncMidTag(String path) throws IOException {
		/*
		 * MidiFile midi = null;
		 * File file = null;
		 * 
		 * try {
		 * file = new File(path);
		 * midi = new MidiFile(file);
		 * } catch(IOException e) {
		 * return false;
		 * }
		 * 
		 * ArrayList<Tempo> tempoList = new ArrayList<Tempo>();
		 * 
		 * MidiTrack T0 = midi.getTracks().get(0);
		 * Iterator<MidiEvent> it = T0.getEvents().iterator();
		 * while(it.hasNext()) {
		 * MidiEvent E = it.next();
		 * 
		 * if ( E.getClass().equals(Tempo.class) ) {
		 * Tempo tempo = (Tempo)E;
		 * tempoList.add(tempo);
		 * }
		 * }
		 * 
		 * ArrayList<Integer> syncList = new ArrayList<Integer>();
		 * ArrayList<Integer> syncProgramList = new ArrayList<Integer>();
		 * 
		 * MidiTrack T1 = midi.getTracks().get(1);
		 * it = T1.getEvents().iterator();
		 * while(it.hasNext()) {
		 * MidiEvent E = it.next();
		 * 
		 * if ( E.getClass().equals(ProgramChange.class) ) {
		 * ProgramChange program = (ProgramChange)E;
		 * 
		 * int time = MidiToTime(program.getTick(), midi.getResolution(), tempoList);
		 * syncProgramList.add(program.getProgramNumber());
		 * 
		 * syncList.add(time);
		 * }
		 * }
		 * 
		 * int nStartTime = 0, nEndTime = 0, nAttr = 0;
		 * byte nPosLyrics = 0;
		 * byte nPosLen = 0;
		 * int nPosOneLine = 0;
		 * int nLineDisplay = 0;
		 * int bNextDisplay = 0;
		 * 
		 * byte ch = 0;
		 * int size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;
		 * 
		 * nStartTime = syncList.get(0);
		 * nAttr = syncProgramList.get(0);
		 * nEndTime = syncList.get(1) - 1;
		 * 
		 * int w = 0;
		 * 
		 * if ( nAttr == 0 ) {
		 * SyncTag sTag = new SyncTag();
		 * sTag.lTimeSyncStart = nStartTime;
		 * sTag.lTimeSyncEnd = nEndTime;
		 * sTag.nPosLyrics = 0;
		 * sTag.nPosLen = 0;
		 * sTag.nPosOneLine = 0;
		 * sTag.nLineDisplay = 0;
		 * sTag.nAttribute = 2;
		 * sTag.nNextDisplay = 0;
		 * 
		 * if (sTag.lTimeSyncStart < 0)
		 * sTag.lTimeSyncStart = 0;
		 * 
		 * if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_READY)
		 * sTag.lTimeSyncStart = sTag.lTimeSyncEnd - TIME_READY;
		 * 
		 * m_listSyncTag.add(sTag);
		 * 
		 * w = 1;
		 * }
		 * 
		 * for (int k = w; k < syncList.size(); k++) {
		 * nStartTime = syncList.get(k);
		 * nAttr = syncProgramList.get(k);
		 * 
		 * if ( k == syncList.size() - 1 ) {
		 * nEndTime = syncList.get(k) + TIME_TEXT;
		 * } else {
		 * nEndTime = syncList.get(k+1) - 1;
		 * }
		 * 
		 * nPosLen = 0;
		 * 
		 * ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
		 * 
		 * if (ch == ' ') {
		 * nPosLyrics++;
		 * ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
		 * }
		 * 
		 * if ((ch & 0x80) != 0) {
		 * nPosLen = 2;
		 * } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
		 * int nAdd = nPosLyrics;
		 * 
		 * for (; nAdd < size; nAdd++) {
		 * byte c = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nAdd];
		 * 
		 * if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
		 * nPosLen++;
		 * else
		 * break;
		 * }
		 * } else {
		 * nPosLen = 1;
		 * }
		 * 
		 * SyncTag sTag = new SyncTag();
		 * sTag.lTimeSyncStart = nStartTime;
		 * sTag.lTimeSyncEnd = nEndTime;
		 * sTag.nPosLyrics = nPosLyrics;
		 * sTag.nPosLen = nPosLen;
		 * sTag.nLineDisplay = nLineDisplay;
		 * sTag.nPosOneLine = nPosOneLine;
		 * 
		 * if (nAttr == 3)
		 * sTag.nAttribute = 7;
		 * else
		 * sTag.nAttribute = 0;
		 * 
		 * if (nLineDisplay != nPosOneLine)
		 * nPosOneLine++;
		 * 
		 * nPosLyrics += nPosLen;
		 * 
		 * if (nPosLyrics >= size / 2 && bNextDisplay == 0) {
		 * sTag.nNextDisplay = 1;
		 * bNextDisplay = 1;
		 * } else {
		 * sTag.nNextDisplay = 0;
		 * }
		 * 
		 * if (nPosLyrics >= size) {
		 * if (++nLineDisplay >= m_listLyricsTag.size())
		 * break;
		 * 
		 * bNextDisplay = 0;
		 * nPosLyrics = 0;
		 * size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;
		 * 
		 * if (size == 0)
		 * break;
		 * }
		 * 
		 * if (sTag.lTimeSyncEnd - sTag.lTimeSyncStart > TIME_TEXT)
		 * sTag.lTimeSyncEnd = sTag.lTimeSyncStart + TIME_TEXT;
		 * 
		 * m_listSyncTag.add(sTag);
		 * 
		 * if ( nAttr == 3 ) {
		 * k++;
		 * nStartTime = syncList.get(k);
		 * nAttr = syncProgramList.get(k);
		 * nEndTime = syncList.get(k+1) - 1;
		 * 
		 * SyncTag sTag2 = new SyncTag();
		 * sTag2.lTimeSyncStart = nStartTime;
		 * sTag2.lTimeSyncEnd = nEndTime;
		 * sTag2.nPosLyrics = 0;
		 * sTag2.nPosLen = 0;
		 * sTag2.nLineDisplay = nLineDisplay;
		 * sTag2.nPosOneLine = nPosOneLine++;
		 * sTag2.nAttribute = 2;
		 * sTag2.nNextDisplay = 0;
		 * 
		 * if (sTag2.lTimeSyncEnd - sTag2.lTimeSyncStart > TIME_READY)
		 * sTag2.lTimeSyncEnd = sTag2.lTimeSyncStart + TIME_READY;
		 * 
		 * m_listSyncTag.add(sTag);
		 * }
		 * }
		 */

		return true;
	}

	private boolean setControlRokTag(DataInputStream dis) throws IOException {
		int skip = dis.skipBytes(4);
		if (skip == 0)
			return false;

		byte[] songid = new byte[8];
		dis.read(songid);
		m_cTag.strSongId = SongUtil.byteToString(songid);

		// sok position
		dis.skipBytes(2);
		m_cTag.nSoPos = SongUtil.swap(dis.readInt());

		// $gs position
		dis.skipBytes(2);
		m_cTag.nGsPos = SongUtil.swap(dis.readInt());

		// mp3 position
		dis.skipBytes(2);
		m_cTag.nMpPos = SongUtil.swap(dis.readInt());

		// melody count
		dis.skipBytes(2);
		m_cTag.nMsCnt = SongUtil.swap(dis.readUnsignedShort());

		// melody position
		m_cTag.nMsPos = SongUtil.swap(dis.readInt());

		return true;
	}

	private boolean setLyricsRokTag(DataInputStream dis) throws IOException {
		int size = m_cTag.nGsPos - m_cTag.nSoPos;
		byte[] buffer = new byte[size];
		dis.read(buffer);

		StringTokenizer tok = new StringTokenizer(SongUtil.byteToString(buffer), "\n");
		m_lTag.strLang = "KSC5601";

		int limit = tok.countTokens();
		for (int i = 0; i < limit; i++) {
			String ptr = tok.nextToken().trim();

			if (i == 0) {
				try {
					StringTokenizer line = new StringTokenizer(ptr, "-");
					m_lTag.strYear = line.nextToken();
					m_lTag.strRythem = line.nextToken();
					m_lTag.strGenre = line.nextToken();
					StringTokenizer key = new StringTokenizer((String) line.nextToken(), "/");
					m_lTag.strSex = key.nextToken();
					m_lTag.strMale = key.nextToken();
					m_lTag.strFmale = key.nextToken();
				} catch (NoSuchElementException e) {
					return false;
				}
			} else if (i == 1) {
				m_lTag.strTitle1 = ptr;
			} else if (i == 2) {
				m_lTag.strTitle2 = ptr;
			} else if (i == 3) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, LYRICSAUTHOR);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strLyricsAuthor = str.trim();
			} else if (i == 4) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, AUTHOR);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strAuthor = str.trim();
			} else if (i == 5) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, SINGER);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strSinger = str.trim();
			} else {
				LyricsTag tag = new LyricsTag();
				tag.strLineLyrics = ptr.getBytes("KSC5601");
				if (!tag.strLineLyrics.equals(""))
					m_listLyricsTag.add(tag);
			}
		}

		return true;
	}

	private boolean setSyncRokTag(DataInputStream dis) throws IOException {
		int position = 0;

		// GS
		int nGsSize = m_cTag.nMsPos - m_cTag.nGsPos;
		int nGaCnt = nGsSize / 8;
		byte[] buffer = new byte[nGsSize];
		dis.read(buffer);

		int nStartTime = 0, nEndTime = 0, nAttr = 0;
		byte nPosLyrics = 0;
		byte nPosLen = 0;
		int nPosOneLine = 0;
		int nLineDisplay = 0;
		int bNextDisplay = 0;

		byte ch = 0;
		int size = m_listLyricsTag.get(nLineDisplay).strLineLyrics.length;

		nStartTime = SongUtil.Byte2Int(buffer, position);
		position += 4;
		nAttr = SongUtil.Byte2Int(buffer, position);
		position += 4;
		nEndTime = SongUtil.Byte2Int(buffer, position) - 1;

		if (nAttr == 0) {
			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime;
			sTag.lTimeSyncEnd = nEndTime;
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
		} else {
			position -= 8;
		}

		for (int k = 1; k < nGaCnt - 1; k++) {
			nStartTime = SongUtil.Byte2Int(buffer, position);
			position += 4;
			nAttr = SongUtil.Byte2Int(buffer, position);
			position += 4;

			if (k == nGaCnt - 2 && nAttr == 3)
				nEndTime = nStartTime + 1000;
			else {
				nEndTime = SongUtil.Byte2Int(buffer, position) - 1;
			}

			nPosLen = 0;

			ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];

			if (ch == ' ') {
				nPosLyrics++;
				ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
			}

			if ((ch & 0x80) != 0) {
				nPosLen = 2;
			} else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
				int nAdd = nPosLyrics;

				for (; nAdd < size; nAdd++) {
					byte c = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nAdd];

					if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
						nPosLen++;
					else
						break;
				}
			} else {
				nPosLen = 1;
			}

			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime;
			sTag.lTimeSyncEnd = nEndTime;
			sTag.nPosLyrics = nPosLyrics;
			sTag.nPosLen = nPosLen;
			sTag.nLineDisplay = nLineDisplay;
			sTag.nPosOneLine = nPosOneLine;

			if (nAttr == 3)
				sTag.nAttribute = 7;
			else
				sTag.nAttribute = 0;

			if (nLineDisplay != nPosOneLine)
				nPosOneLine++;

			nPosLyrics += nPosLen;

			if (nPosLyrics >= size / 2 && bNextDisplay == 0) {
				sTag.nNextDisplay = 1;
				bNextDisplay = 1;
			} else {
				sTag.nNextDisplay = 0;
			}

			if (nPosLyrics >= size) {
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

			if (nAttr == 3) {
				SyncTag sTag2 = new SyncTag();
				nStartTime = SongUtil.Byte2Int(buffer, position);
				position += 4;
				nAttr = SongUtil.Byte2Int(buffer, position);
				position += 4;
				nEndTime = SongUtil.Byte2Int(buffer, position) - 1;

				sTag2.lTimeSyncStart = nStartTime;
				sTag2.lTimeSyncEnd = nEndTime;
				sTag2.nPosLyrics = 0;
				sTag2.nPosLen = 0;
				sTag2.nPosOneLine = nPosOneLine;
				sTag2.nLineDisplay = nLineDisplay++;
				sTag2.nAttribute = 2;
				sTag2.nNextDisplay = 0;

				if (sTag2.lTimeSyncEnd - sTag2.lTimeSyncStart > TIME_READY)
					sTag2.lTimeSyncStart = sTag2.lTimeSyncEnd - TIME_READY;

				m_listSyncTag.add(sTag2);
			}
		}

		SyncTag sTag = new SyncTag();
		sTag.lTimeSyncStart = nStartTime;
		sTag.lTimeSyncEnd = nEndTime + 1500;
		sTag.nPosLyrics = nPosLyrics;
		sTag.nPosLen = 1;
		sTag.nPosOneLine = nPosOneLine;
		sTag.nLineDisplay = nLineDisplay;
		sTag.nAttribute = 7;
		sTag.nNextDisplay = 0;
		m_listSyncTag.add(sTag);

		m_nSyncTime = (int) sTag.lTimeSyncEnd;

		return true;
	}

	private boolean setControlTag(DataInputStream dis) throws IOException {
		int skip = dis.skipBytes(14);
		if (skip == 0)
			return false;

		// total line
		m_cTag.nMsCnt = SongUtil.swap(dis.readInt());

		// sok position
		dis.skipBytes(4);
		m_cTag.nSoPos = SongUtil.swap(dis.readInt());

		// $gs position
		m_cTag.nGsPos = SongUtil.swap(dis.readInt());

		// image position
		m_cTag.nPiPos = SongUtil.swap(dis.readInt());

		// mp3 position
		m_cTag.nMpPos = SongUtil.swap(dis.readInt());

		return true;
	}

	private boolean setLyricsInfoTag(DataInputStream dis) throws IOException {
		int skip = dis.skipBytes(91);
		if (skip == 0)
			return false;

		m_lTag.strLang = "KSC5601";

		byte[] songid = new byte[6];
		dis.read(songid);
		m_cTag.strSongId = SongUtil.byteToString(songid);

		byte[] titl1 = new byte[40];
		dis.read(titl1);
		m_lTag.strTitle1 = SongUtil.byteToString(titl1);

		byte[] titl2 = new byte[40];
		dis.read(titl2);
		m_lTag.strTitle2 = SongUtil.byteToString(titl2);

		byte[] lauthor = new byte[20];
		dis.read(lauthor);
		m_lTag.strLyricsAuthor = SongUtil.byteToString(lauthor);

		byte[] author = new byte[20];
		dis.read(author);
		m_lTag.strAuthor = SongUtil.byteToString(author);

		byte[] singer = new byte[20];
		dis.read(singer);
		m_lTag.strSinger = SongUtil.byteToString(singer);

		dis.skipBytes(1);

		int year = SongUtil.swap(dis.readInt());
		m_lTag.strYear = "" + year;

		dis.skipBytes(8);

		byte[] sex = new byte[2];
		dis.read(sex);
		m_lTag.strSex = SongUtil.byteToString(sex);

		return true;
	}

	private boolean setLyricsTag(DataInputStream dis) throws IOException {
		int skip = dis.skipBytes(95);
		if (skip == 0)
			return false;

		for (int i = 0; i < m_cTag.nMsCnt; i++) {
			byte[] lyrics = new byte[24];
			dis.read(lyrics);

			LyricsTag tag = new LyricsTag();
			tag.strLineLyrics = lyrics;
			m_listLyricsTag.add(tag);
		}

		return true;
	}

	private boolean setSyncTag(DataInputStream dis) throws IOException {
		int skip = dis.skipBytes(4);
		if (skip == 0)
			return false;

		int nCount = 0;
		int nPosition = m_cTag.nGsPos + 4;

		while (nPosition < m_cTag.nPiPos) {
			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = SongUtil.swap(dis.readInt());
			sTag.lTimeSyncEnd = SongUtil.swap(dis.readInt());
			sTag.nPosLyrics = dis.readByte();
			sTag.nPosLen = dis.readByte();
			sTag.nPosOneLine = SongUtil.swap(dis.readInt());
			sTag.nLineDisplay = SongUtil.swap(dis.readInt());
			sTag.nAttribute = dis.readByte();
			sTag.nNextDisplay = SongUtil.swap(dis.readInt());

			nPosition += 23;
			sTag.nPosOneLine = nCount;

			nCount = sTag.nLineDisplay;

			switch (sTag.nAttribute) {
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

			m_nSyncTime = (int) sTag.lTimeSyncEnd;
		}

		SyncTag sTag = new SyncTag();
		sTag.lTimeSyncStart = sTag.lTimeSyncStart + 300;
		sTag.lTimeSyncEnd = sTag.lTimeSyncEnd + 300;
		sTag.nPosLyrics = sTag.nPosLyrics;
		sTag.nPosLen = sTag.nPosLen;
		sTag.nPosOneLine = sTag.nPosOneLine;
		sTag.nLineDisplay = SYNC_ENDDIVISION;
		sTag.nNextDisplay = sTag.nNextDisplay;

		m_listSyncTag.add(sTag);

		return true;
	}

	private void setControlTag() throws IOException {
		m_raf.seek(0);

		m_raf.skipBytes(5);
		m_cTag.nSize = SongUtil.swap(m_raf.readUnsignedShort());

		byte[] buffer = new byte[8];
		m_raf.read(buffer);
		m_cTag.strSongId = SongUtil.byteToString(buffer);

		// sok position
		m_raf.skipBytes(2);
		m_cTag.nSoPos = SongUtil.swap(m_raf.readInt());

		// $gs position
		m_raf.skipBytes(2);
		m_cTag.nGsPos = SongUtil.swap(m_raf.readInt());

		// mp3 position
		m_raf.skipBytes(2);
		m_cTag.nMpPos = SongUtil.swap(m_raf.readInt());

		// melody count
		m_raf.skipBytes(2);
		m_cTag.nMsCnt = SongUtil.swap(m_raf.readUnsignedShort());

		// melody position
		m_cTag.nMsPos = SongUtil.swap(m_raf.readInt());
	}

	private void setControlTag(int type) throws IOException {
		m_raf.seek(0);

		if (type == FILE_SKYM) {
			m_raf.skipBytes(17);
		} else if (type == FILE_KYM) {
			m_raf.skipBytes(m_filesize - 251);
		}

		// total line
		m_cTag.nMsCnt = SongUtil.swap(m_raf.readInt());

		// sok position
		m_raf.skipBytes(4);
		m_cTag.nSoPos = SongUtil.swap(m_raf.readInt());

		// $gs position
		m_cTag.nGsPos = SongUtil.swap(m_raf.readInt());

		// image position
		m_cTag.nPiPos = SongUtil.swap(m_raf.readInt());

		if (type == FILE_SKYM) {
			// mp3 position
			m_cTag.nMpPos = SongUtil.swap(m_raf.readInt());
			m_cTag.nMsPos = m_filesize - m_cTag.nMpPos;
		} else if (type == FILE_KYM) {
			// mp3 position
			m_raf.seek(0);
			m_cTag.nMpPos = 0;
			m_raf.skipBytes(m_filesize - 234);
			m_cTag.nMsPos = SongUtil.swap(m_raf.readInt()) - 12;
		}
	}

	private void setLyricsSokTag(String path) throws IOException {
		try {
			m_raf = new RandomAccessFile(path, "r");
			if (m_raf != null && m_raf.length() > 0) {
				m_raf.seek(0);

				StringTokenizer tok = null;
				byte[] buff = new byte[m_filesize];
				m_raf.read(buff);
				tok = new StringTokenizer(SongUtil.byteToString(buff), "\n");

				m_lTag.strLang = "EUC-JP";

				int limit = tok.countTokens();
				for (int i = 0; i < limit; i++) {
					String ptr = (String) tok.nextToken();

					if (i == 0) {
						try {
							StringTokenizer line = new StringTokenizer(ptr, "-");
							m_lTag.strYear = line.nextToken();
							m_lTag.strRythem = line.nextToken();
							m_lTag.strGenre = line.nextToken();
							StringTokenizer key = new StringTokenizer((String) line.nextToken(), "/");
							m_lTag.strSex = key.nextToken();
							m_lTag.strMale = key.nextToken();
							m_lTag.strFmale = key.nextToken();
						} catch (NoSuchElementException e) {
							return;
						}
					} else if (i == 1) {
						m_lTag.strTitle1 = ptr;
					} else if (i == 2) {
						m_lTag.strTitle2 = ptr;
					} else if (i == 3) {
						StringTokenizer ptr2 = new StringTokenizer(ptr, LYRICSAUTHOR);
						String str = (String) ptr2.nextToken();
						str.replace(" ", "");
						str.replace("\t", "");
						m_lTag.strLyricsAuthor = str.trim();
					} else if (i == 4) {
						StringTokenizer ptr2 = new StringTokenizer(ptr, AUTHOR);
						String str = (String) ptr2.nextToken();
						str.replace(" ", "");
						str.replace("\t", "");
						m_lTag.strAuthor = str.trim();
					} else if (i == 5) {
						StringTokenizer ptr2 = new StringTokenizer(ptr, SINGER);
						String str = (String) ptr2.nextToken();
						str.replace(" ", "");
						str.replace("\t", "");
						m_lTag.strSinger = str.trim();
					} else if (i % 3 == 2) {
						LyricsTag tag = new LyricsTag();
						tag.strLineLyrics = ptr.getBytes("EUC-JP");
						if (!tag.strLineLyrics.equals(""))
							m_listLyricsTag.add(tag);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (m_raf != null)
					m_raf.close();
			} catch (IOException ee) {
			} finally {
			}
		}
	}

	private void setLyricsTag() throws IOException {
		m_raf.seek(0);

		StringTokenizer tok = null;
		int size = m_cTag.nGsPos - m_cTag.nSoPos;
		byte[] buff = new byte[size];
		m_raf.skipBytes(m_cTag.nSoPos);
		m_raf.read(buff);
		tok = new StringTokenizer(SongUtil.byteToString(buff), "\n");

		m_lTag.strLang = "KSC5601";

		int limit = tok.countTokens();
		for (int i = 0; i < limit; i++) {
			String ptr = (String) tok.nextToken();

			if (i == 0) {
				try {
					StringTokenizer line = new StringTokenizer(ptr, "-");
					m_lTag.strYear = line.nextToken();
					m_lTag.strRythem = line.nextToken();
					m_lTag.strGenre = line.nextToken();
					StringTokenizer key = new StringTokenizer((String) line.nextToken(), "/");
					m_lTag.strSex = key.nextToken();
					m_lTag.strMale = key.nextToken();
					m_lTag.strFmale = key.nextToken();
				} catch (NoSuchElementException e) {
					return;
				}
			} else if (i == 1) {
				m_lTag.strTitle1 = ptr;
			} else if (i == 2) {
				m_lTag.strTitle2 = ptr;
			} else if (i == 3) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, LYRICSAUTHOR);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strLyricsAuthor = str.trim();
			} else if (i == 4) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, AUTHOR);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strAuthor = str.trim();
			} else if (i == 5) {
				StringTokenizer ptr2 = new StringTokenizer(ptr, SINGER);
				String str = (String) ptr2.nextToken();
				str.replace(" ", "");
				str.replace("\t", "");
				m_lTag.strSinger = str.trim();
			} else {
				LyricsTag tag = new LyricsTag();
				tag.strLineLyrics = ptr.getBytes("KSC5601");
				if (!tag.strLineLyrics.equals(""))
					m_listLyricsTag.add(tag);
			}
		}
	}

	private void setLyricsTag(int type) throws IOException {
		m_raf.seek(0);

		if (type == FILE_SKYM) {
			m_raf.skipBytes(132);
		} else if (type == FILE_KYM) {
			m_raf.skipBytes(m_filesize - 508);
		}

		m_lTag.strLang = "KSC5601";

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

		for (int i = 0; i < m_cTag.nMsCnt; i++) {
			byte[] lyrics = new byte[24];
			m_raf.read(lyrics);

			LyricsTag tag = new LyricsTag();
			tag.strLineLyrics = lyrics;
			m_listLyricsTag.add(tag);
		}
	}

	private void setSyncTag() throws IOException {
		m_raf.seek(0);

		int position = 0;

		// GS
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
		position += 4;
		nAttr = SongUtil.swap(m_raf.readInt());
		position += 4;
		nEndTime = SongUtil.swap(m_raf.readInt()) - 1;

		if (nAttr == 0) {
			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime;
			sTag.lTimeSyncEnd = nEndTime;
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
		} else {
			m_raf.seek(0);
			m_raf.skipBytes(m_cTag.nGsPos);
		}

		for (int k = 1; k < nGaCnt - 1; k++) {
			nStartTime = SongUtil.swap(m_raf.readInt());
			nAttr = SongUtil.swap(m_raf.readInt());
			nEndTime = SongUtil.swap(m_raf.readInt()) - 1;
			position += 8;

			m_raf.seek(0);
			m_raf.skipBytes(m_cTag.nGsPos + position);

			nPosLen = 0;

			ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];

			if (ch == ' ') {
				nPosLyrics++;
				ch = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nPosLyrics];
			}

			if ((ch & 0x80) != 0) {
				nPosLen = 2;
			} else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
				int nAdd = nPosLyrics;

				for (; nAdd < size; nAdd++) {
					byte c = m_listLyricsTag.get(nLineDisplay).strLineLyrics[nAdd];

					if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
						nPosLen++;
					else
						break;
				}
			} else {
				nPosLen = 1;
			}

			SyncTag sTag = new SyncTag();
			sTag.lTimeSyncStart = nStartTime;
			sTag.lTimeSyncEnd = nEndTime;
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

			if (nPosLyrics >= size / 2 && bNextDisplay == 0) {
				sTag.nNextDisplay = 1;
				bNextDisplay = 1;
			} else {
				sTag.nNextDisplay = 0;
			}

			if (nPosLyrics >= size) {
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

			if (nAttr == 3) {
				SyncTag sTag2 = new SyncTag();
				nStartTime = SongUtil.swap(m_raf.readInt());
				nAttr = SongUtil.swap(m_raf.readInt());
				nEndTime = SongUtil.swap(m_raf.readInt()) - 1;
				position += 8;

				m_raf.seek(0);
				m_raf.skipBytes(m_cTag.nGsPos + position);

				sTag2.lTimeSyncStart = nStartTime;
				sTag2.lTimeSyncEnd = nEndTime;
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
		sTag.lTimeSyncStart = nStartTime;
		sTag.lTimeSyncEnd = nEndTime;
		sTag.nPosLyrics = nPosLyrics;
		sTag.nPosLen = 1;
		sTag.nPosOneLine = nPosOneLine;
		sTag.nLineDisplay = nLineDisplay;
		sTag.nAttribute = 7;
		sTag.nNextDisplay = 0;
		m_listSyncTag.add(sTag);

		m_nSyncTime = (int) sTag.lTimeSyncEnd;
	}

	private void setSyncTag(int type) throws IOException {
		m_raf.seek(0);

		m_raf.skipBytes(m_cTag.nGsPos + 4);

		int nCount = 0;
		int nPosition = m_cTag.nGsPos + 4;

		while (nPosition < m_cTag.nPiPos) {
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

			switch (sTag.nAttribute) {
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

			m_nSyncTime = (int) sTag.lTimeSyncEnd;
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

	private int getFileType(String name) {
		int size = name.length();
		int pos = name.lastIndexOf('.') + 1;
		String ext = name.substring(pos, size).toUpperCase(Locale.getDefault());

		if (ext.equals("ROK")) {
			return FILE_ROK;
		} else if (ext.equals("SOK")) {
			return FILE_SOK;
		} else if (ext.equals("MID")) {
			return FILE_MID;
		} else if (ext.equals("SKYM")) {
			return FILE_SKYM;
		} else if (ext.equals("KYM")) {
			return FILE_KYM;
		}

		return FILE_INVALID;
	}

	public class CtrlTag {
		public int nSize;
		public String strSongId;
		public int nSoPos;
		public int nGsPos;
		public int nMpPos;
		public int nMsCnt;
		public int nMsPos;
		public int nPiPos;
	}

	public class LyricsInfoTag {
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
		public String strLang;
	}

	public class LyricsTag {
		public byte[] strLineLyrics;

		public LyricsTag() {
			strLineLyrics = new byte[24];
		}
	}

	public class SyncTag {
		public long lTimeSyncStart;
		public long lTimeSyncEnd;
		public byte nPosLyrics;
		public byte nPosLen;
		public int nPosOneLine;
		public int nLineDisplay;
		public byte nAttribute;
		public int nNextDisplay;
	}

	/**
	 * 곡제목 - 다국어처리
	 * 
	 * @param title
	 */
	public void setTitle(String title) {

		if (TextUtils.isEmpty(title)) {
			return;
		}

		this.m_title = title;
		m_titles = title.split("\n");
	}

	/**
	 * 곡제목 - 다국어데이터처리
	 */
	public String getTitle() {
		return m_title;
	}

	/**
	 * 곡제목 - 다국어데이터처리
	 */
	public String[] getTitles() {
		return m_titles;
	}

	/**
	 * 가수명 - 다국어데이터처리
	 * 
	 * @param singer
	 */
	public void setSinger(String singer) {

		if (TextUtils.isEmpty(singer)) {
			return;
		}

		this.m_singer = singer;
	}

	/**
	 * 가수명 - 다국어데이터처리
	 */
	public String getSinger() {
		return m_singer;
	}

	/**
	 * 작곡가 - 다국어데이터처리
	 * 
	 * @param composer
	 */
	public void setComposer(String composer) {

		if (TextUtils.isEmpty(composer)) {
			return;
		}

		this.m_composer = composer;
	}

	/**
	 * 작곡가 - 다국어데이터처리
	 */
	public String getComposer() {
		return m_composer;
	}

	/**
	 * 작사가 - 다국어데이터처리
	 * 
	 * @param writer
	 */
	public void setWriter(String writer) {

		if (TextUtils.isEmpty(writer)) {
			return;
		}

		this.m_writer = writer;
	}

	/**
	 * 작사가 - 다국어데이터처리
	 */
	public String getWriter() {
		return m_writer;
	}
}
