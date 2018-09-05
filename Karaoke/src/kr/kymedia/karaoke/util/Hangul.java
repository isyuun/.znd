package kr.kymedia.karaoke.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 유니코드(Unicode) 기반으로 만든 한글 관련 클래스입니다.
 * </p>
 * 
 * 
 * <h2>유니코드</h2>
 * <p>
 * 유니코드(<a href="http://www.unicode.org/">Unicode</a>)는 전세계의 모든 문자를 컴퓨터에서 일관되게 표현하고 다룰 수 있도록 설계한 표준입니다.
 * </p>
 * 
 * <h2>Unicode Korean specific</h2>
 * <ul>
 * <li>0x1100-0x11F9 : <a href="http://www.unicode.org/charts/PDF/U1100.pdf">한글 자모(Hangul Jamo) 영역</a> - 조합형 영역
 * <li>0x3130-0x318E : <a href="http://www.unicode.org/charts/PDF/U3130.pdf">한글 호환 자모(Hangul Compatibility Jamo) 영역</a>
 * <li>0xAC00-0xD7A3 : <a href="http://www.unicode.org/charts/PDF/UAC00.pdf">한글 (Hangul Syllables) 영역</a> - 완성형 영역
 * <li>0xFF00-0xFFEF : <a href="http://www.unicode.org/charts/PDF/UFF00.pdf">Halfwidth Jamo</a>
 * </ul>
 * 
 * <h2>유니코드에서의 한글</h2>
 * <p>
 * 유니코드 CharacterSet에서 한글이 차지하는 영역은 세 개로 나눌 수 있습니다. 첫번째는 0x1100-0x11F9 까지의 한글 자모 영역. 한글에서 사용되는 자모들을 초성 자음/중성 모음/종성 자음으로 각각 나누어 한글자씩 대응시킵니다. 이 영역에 있는 글자들을 이용하면 한글 고어까지 표현이 가능해집니다. 캐릭터셋 변환이 어렵다는 단점이 있습니다. 두번째는 0x3130-0x318E 까지의 한글 호환 자모 영역. 이
 * 부분은 초/중/종성을 구분하지 않고 그냥 사용되는 모든 자모들이 한데 묶여 들어 있습니다. 세번째는 0xAC00-0xD7A3 까지의 한글 영역. 이 부분에는 현대 한글 자모로 표현 가능한 모든 한글 문자들(11172자=19*21*28))이 들어 있습니다. 간단한 계산을 통해 자모를 추출하는 것이 가능합니다. 그 외 한자들은 다른 영역에 포함되어 있습니다.
 * </p>
 * 
 * <h2>자모 유형</h2>
 * <ul>
 * <li>0x3130-0x314E : 현대자음(40개)
 * <li>0x314F-0x3163 : 현대모음(21개)
 * <li>0x3164 : 채움코드
 * <li>0x3165-0x318E : 옛글자모
 * </ul>
 * 
 * <h2>초/중/종성 추출하기</h2>
 * <ul>
 * <li>한글 : 0xAC00 + (초성_I * 21 * 28) + (중성_I * 28) + 종성_I
 * <li>초성_I : (한글 - 0xAC00) / (21 * 28)
 * <li>중성_I : ((한글 - 0xAC00) % (21 * 28)) / 28
 * <li>종성_I : ((한글 - 0xAC00) % (21 * 28)) % 28
 * </ul>
 * 
 * <h2>참고 자료</h2>
 * <ul>
 * <li><a href="http://www.kristalinfo.com/K-Lab/unicode/Unicode_intro-kr.html">유니코드(Unicode)와 유니코드 인코딩</a>
 * </ul>
 * 
 * 
 * @author <a href="mailto:kangwoo@jarusoft.com">kangwoo</a>
 * @version 1.0
 * @since 1.0
 */
public class Hangul {

	private static final char HANGUL_SYLLABLES_BEGIN = 0xAC00;
	private static final char HANGUL_SYLLABLES_END = 0xD7A3;

	private static final char HANGUL_COMPATIBILITY_JAMO_BEGIN = 0x3130;
	private static final char HANGUL_COMPATIBILITY_JAMO_END = 0x318E;

	private static final char HANGUL_COMPATIBILITY_JA_BEGIN = 0x3130;
	private static final char HANGUL_COMPATIBILITY_JA_END = 0x314E;

	private static final char HANGUL_COMPATIBILITY_MO_BEGIN = 0x314F;
	private static final char HANGUL_COMPATIBILITY_MO_END = 0x3163;

	private static final char HANGUL_JAMO_BEGIN = 0x1100;
	private static final char HANGUL_JAMO_END = 0x11F9;

	/**
	 * 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	 */
	private static final char[] HANGUL_CHOSEONG = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
	/**
	 * 'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
	 */
	private static final char[] HANGUL_JUNGSEONG = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
	/**
	 * ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	 */
	private static final char[] HANGUL_JONGSEONG = { 0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148,
			0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

	public static final int HANGUL_CHOSEONG_SIZE = HANGUL_CHOSEONG.length;	// 19
	public static final int HANGUL_JUNGSEONG_SIZE = HANGUL_JUNGSEONG.length;	// 21
	public static final int HANGUL_JONGSEONG_SIZE = HANGUL_JONGSEONG.length;	// 28

	private static final Map<Character, Integer> HANGUL_CHOSEONG_CODE_TABLE = new HashMap<Character, Integer>();
	private static final Map<Character, Integer> HANGUL_JUNGSEONG_CODE_TABLE = new HashMap<Character, Integer>();
	private static final Map<Character, Integer> HANGUL_JONGSEONG_CODE_TABLE = new HashMap<Character, Integer>();

	static {
		for (int i = 0; i < HANGUL_CHOSEONG.length; i++) {
			HANGUL_CHOSEONG_CODE_TABLE.put(HANGUL_CHOSEONG[i], i);
		}
		for (int i = 0; i < HANGUL_JUNGSEONG.length; i++) {
			HANGUL_JUNGSEONG_CODE_TABLE.put(HANGUL_JUNGSEONG[i], i);
		}
		for (int i = 0; i < HANGUL_JONGSEONG.length; i++) {
			HANGUL_JONGSEONG_CODE_TABLE.put(HANGUL_JONGSEONG[i], i);
		}
	}

	/**
	 * <p>
	 * 입력한 문자가 한글인지 판단한다.
	 * </p>
	 * <p>
	 * (코드값이 한글 영역(0xAC00-0xD7A3)인지 판단)
	 * </p>
	 * 
	 * @param c
	 * @return 한글이면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean isHangulSyllables(char ch) {
		return ch >= HANGUL_SYLLABLES_BEGIN && ch <= HANGUL_SYLLABLES_END;
	}

	/**
	 * <p>
	 * 입력한 문자가 한글 호환 자모인지 판단한다.
	 * </p>
	 * <p>
	 * (코드값이 한글 호환 자모 영역(0x1100-0x318E)인지 판단)
	 * </p>
	 * 
	 * @param ch
	 * @return 한글 호환 자모이면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean isHangulCompatibilityJamo(char ch) {
		return ch >= HANGUL_COMPATIBILITY_JAMO_BEGIN && ch <= HANGUL_COMPATIBILITY_JAMO_END;
	}

	/**
	 * <p>
	 * 입력한 문자가 한글 호환 영역 중 현대 자음인지 판단한다.
	 * </p>
	 * <p>
	 * (코드값이 한글 호환 자모 영역중 현대 자음(0x3130-0x314E)인지 판단)
	 * </p>
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isHangulCompatibilityJa(char ch) {
		return ch >= HANGUL_COMPATIBILITY_JA_BEGIN && ch <= HANGUL_COMPATIBILITY_JA_END;
	}

	/**
	 * <p>
	 * 입력한 문자가 한글 호환 영역중 현대 모음인지 판단한다.
	 * </p>
	 * <p>
	 * (코드값이 한글 호환 자모 영역중 현대 모음(0x314F-0x3163)인지 판단)
	 * </p>
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isHangulCompatibilityMo(char ch) {
		return ch >= HANGUL_COMPATIBILITY_MO_BEGIN && ch <= HANGUL_COMPATIBILITY_MO_END;
	}

	/**
	 * <p>
	 * 입력한 문자가 한글 자모인지 판단한다.
	 * </p>
	 * <p>
	 * (코드값이 한글 자모 영역(0x3130-0x11F9)인지 판단)
	 * </p>
	 * 
	 * @param ch
	 * @return 한글 자모이면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean isHangulJamo(char ch) {
		return ch >= HANGUL_JAMO_BEGIN && ch <= HANGUL_JAMO_END;
	}

	/**
	 * <p>
	 * 초성을 추출한다.
	 * </p>
	 * 
	 * @param ch
	 * @return
	 */
	public static char getChoseong(char ch) {
		if (isHangulSyllables(ch) == false) {
			throw new IllegalArgumentException("입력값이 잘못되었습니다. (" + ch + ")");
		}
		int hCode = (ch - HANGUL_SYLLABLES_BEGIN) / (HANGUL_JUNGSEONG_SIZE * HANGUL_JONGSEONG_SIZE);
		return HANGUL_CHOSEONG[hCode];
	}

	/**
	 * <p>
	 * 중성을 추출한다.
	 * </p>
	 * 
	 * @param ch
	 * @return
	 */
	public static char getJungseong(char ch) {
		if (isHangulSyllables(ch) == false) {
			throw new IllegalArgumentException("입력값이 잘못되었습니다. (" + ch + ")");
		}
		int hCode = ((ch - HANGUL_SYLLABLES_BEGIN) % (HANGUL_JUNGSEONG_SIZE * HANGUL_JONGSEONG_SIZE)) / HANGUL_JONGSEONG_SIZE;
		return HANGUL_JUNGSEONG[hCode];
	}

	/**
	 * <p>
	 * 종성을 추출한다.
	 * </p>
	 * 
	 * @param ch
	 * @return
	 */
	public static char getJongseong(char ch) {
		if (isHangulSyllables(ch) == false) {
			throw new IllegalArgumentException("입력값이 잘못되었습니다. (" + ch + ")");
		}
		int hCode = ((ch - HANGUL_SYLLABLES_BEGIN) % (HANGUL_JUNGSEONG_SIZE * HANGUL_JONGSEONG_SIZE)) % HANGUL_JONGSEONG_SIZE;
		return HANGUL_JONGSEONG[hCode];
	}

	/**
	 * <p>
	 * 초/중/종성을 추출한다.
	 * </p>
	 * 
	 * @param ch
	 * @return [초성, 중성, 종성] or [초성, 중성]
	 */
	public static char[] getJamo(char ch) {
		char[] jamo = new char[3];
		jamo[0] = getChoseong(ch);
		jamo[1] = getJungseong(ch);
		jamo[2] = getJongseong(ch);
		if (jamo[2] == HANGUL_JONGSEONG[0]) {
			char[] temp = new char[2];
			temp[0] = jamo[0];
			temp[1] = jamo[1];
			jamo = temp;
		}
		return jamo;
	}

	/**
	 * <p>
	 * 종성이 존재하는지 여부를 판단한다.
	 * </p>
	 * 
	 * @param ch
	 * @return 종성이 존재하면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean hasJongseong(char ch) {
		return getJongseong(ch) != HANGUL_JONGSEONG[0];
	}

	/**
	 * <p>
	 * 초성, 중성을 겹합하여 한글자로 만든다.
	 * </p>
	 * 
	 * @param choseong
	 *          초성
	 * @param jungseong
	 *          중성
	 * @return
	 */
	public static char toHangul(char choseong, char jungseong) {
		return toHangul(choseong, jungseong, HANGUL_JONGSEONG[0]);
	}

	/**
	 * <p>
	 * 초성, 중성, 종성을 결합하여 한글자로 만든다.
	 * </p>
	 * 
	 * @param choseong
	 *          초성
	 * @param jungseong
	 *          중성
	 * @param jongseong
	 *          종성
	 * @return
	 */
	public static char toHangul(char choseong, char jungseong, char jongseong) {
		Integer choseongIndex = HANGUL_CHOSEONG_CODE_TABLE.get(choseong);
		if (choseongIndex == null) {
			throw new IllegalArgumentException("초성이 잘못되었습니다. (" + choseong + ")");
		}
		Integer jungseongIndex = HANGUL_JUNGSEONG_CODE_TABLE.get(jungseong);
		if (jungseongIndex == null) {
			throw new IllegalArgumentException("중성이 잘못되었습니다. (" + jungseong + ")");
		}
		if (jongseong == ' ') {
			jongseong = HANGUL_JONGSEONG[0];
		}
		Integer jongseongIndex = HANGUL_JONGSEONG_CODE_TABLE.get(jongseong);
		if (jongseongIndex == null) {
			throw new IllegalArgumentException("종성이 잘못되었습니다. (" + jongseong + ")");
		}
		return (char) (HANGUL_SYLLABLES_BEGIN + (choseongIndex * (HANGUL_JUNGSEONG_SIZE * HANGUL_JONGSEONG_SIZE)) + (jungseongIndex * HANGUL_JONGSEONG_SIZE) + jongseongIndex);
	}

	/**
	 * <p>
	 * 글자에 알맞는 조사를 선택한다.
	 * </p>
	 * 
	 * <pre>
	 * Hangul.getJosa('한', '은', '는') = '은'
	 * Hangul.getJosa('지', '이', '가') = '가'
	 * </pre>
	 * 
	 * @param ch
	 * @param aux1
	 *          종성이 있을때 붙일 조사
	 * @param aux2
	 *          종성이 없을때 붙일 조사
	 * @return
	 */
	public static char getJosa(char ch, char aux1, char aux2) {
		return hasJongseong(ch) ? aux1 : aux2;
	}

	/**
	 * <p>
	 * 글자에 알맞는 조사를 선택한다.
	 * </p>
	 * 
	 * <pre>
	 * Hangul.getJosa('한', "은", "는") = "은"
	 * Hangul.getJosa('지', "이", "가") = "가"
	 * </pre>
	 * 
	 * @param ch
	 * @param aux1
	 *          종성이 있을때 붙일 조사
	 * @param aux2
	 *          종성이 없을때 붙일 조사
	 * @return
	 */
	public static String getJosa(char ch, String aux1, String aux2) {
		return hasJongseong(ch) ? aux1 : aux2;
	}
}
