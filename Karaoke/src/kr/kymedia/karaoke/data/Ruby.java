package kr.kymedia.karaoke.data;

public class Ruby {
	public static String[] hanToRuby(String s, int pre, int curr, int type) {
		String[] result = new String[3];

		if (type == 0) { // 영어
			result = hanToRoma(s, pre, curr);
		} else if (type == 1) {	// 일어
			result = hanToKata(s, pre, curr);
		}

		return result;
	}

	public static String hanToRuby(String s, int type) {
		String result = s;

		if (type == 0) { // 영어
			result = hanToRoma(s);
		} else if (type == 1) {	// 일어
			result = hanToKata(s);
		}

		return result;
	}

	// ㄱ, ㄲ, ㄴ, ㄷ, ㄸ, ㄹ
	// ㅁ, ㅂ, ㅃ, ㅅ, ㅆ, ㅇ
	// ㅈ, ㅉ, ㅊ, ㅋ, ㅌ, ㅍ
	// ㅎ
	final static String[] ChoSung = { "g", "kk", "n", "d", "tt", "r",
			"m", "b", "pp", "s", "ss", "",
			"j", "jj", "ch", "k", "t", "p",
			"h" };

	// ㅏ, ㅐ, ㅑ, ㅒ, ㅓ, ㅔ
	// ㅕ, ㅖ, ㅗ, ㅘ, ㅙ, ㅚ
	// ㅛ, ㅜ, ㅝ, ㅞ, ㅟ, ㅠ
	// ㅡ, ㅢ, ㅣ
	final static String[] JwungSung = { "a", "ae", "ya", "yae", "eo", "e",
			"yeo", "ye", "o", "wa", "wae", "oe",
			"yo", "u", "wo", "we", "wi", "yu",
			"eu", "ui", "i" };

	// , ㄱ, ㄲ, ㄳ, ㄴ, ㄵ
	// ㄶ, ㄷ, ㄹ, ㄺ, ㄻ, ㄼ
	// ㄽ, ㄾ, ㄿ, ㅀ, ㅁ, ㅂ
	// ㅄ, ㅅ, ㅆ, ㅇ, ㅈ, ㅊ
	// ㅋ, ㅌ, ㅍ, ㅎ
	/*
	 * final static String[] JongSung = { "", "g", "kk", "k", "n", "n",
	 * "n", "t", "l", "lg", "lm", "lb",
	 * "ls", "lt", "lp", "lh", "m", "b",
	 * "bs", "s", "ss", "ng", "j", "ch",
	 * "k", "t", "p", "h" };
	 */

	// 모음 앞
	final static String[] JongSungMo = { "", "g", "kk", "gs", "n", "nj",
			"n", "d", "l", "lg", "lm", "lb",
			"l", "lt", "lp", "l", "m", "b",
			"ps", "s", "ss", "ng", "j", "ch",
			"k", "t", "p", "h" };

	// 자음 앞
	final static String[] JongSung = { "", "k", "k", "gs", "n", "nt",
			"nh", "t", "l", "lg", "m", "p",
			"l", "lt", "p", "lh", "m", "p",
			"p", "t", "t", "ng", "t", "t",
			"k", "t", "p", "h" };

	/*
	 * public static String hangulToRoma(String s) {
	 * 
	 * int a, b, c;
	 * String result = "";
	 * 
	 * for (int i = 0; i < s.length(); i++) {
	 * char ch = s.charAt(i);
	 * 
	 * if (ch >= 0xAC00 && ch <= 0xD7A3) {
	 * c = ch - 0xAC00;
	 * a = c / (21 * 28);
	 * c = c % (21 * 28);
	 * b = c / 28;
	 * c = c % 28;
	 * 
	 * result = result + ChoSung[a] + JwungSung[b];
	 * if (c != 0) result = result + JongSung[c];
	 * } else {
	 * result = result + ch;
	 * }
	 * }
	 * 
	 * return result;
	 * }
	 */

	public static String[] hanToRoma(String s, int pre, int curr) {
		int a, b, c;
		int d = -1;
		int space = 0;
		String[] result = new String[3];
		StringBuilder lyc = new StringBuilder();
		StringBuilder preLyc = new StringBuilder();
		StringBuilder currLyc = new StringBuilder();
		StringBuilder ret = new StringBuilder();

		if (pre == 0 && curr == 0) {
			result[0] = lyc.toString();
			result[1] = preLyc.toString();
			result[2] = currLyc.toString();

			return result;
		}

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			ret.setLength(0);
			space = 0;

			if (ch >= 0xAC00 && ch <= 0xD7A3) {
				c = ch - 0xAC00;
				a = c / (21 * 28);
				c = c % (21 * 28);
				b = c / 28;
				c = c % 28;

				if (d != -1) {
					if (a == 11)
						ret.append(JongSungMo[d]);
					else
						ret.append(JongSung[d]);
				}

				ret.append(ChoSung[a]).append(JwungSung[b]);

				d = c;
			} else {
				if (d != -1) {
					ret.append(JongSung[d]);
				}

				ret.append(ch);

				d = -1;

				if (i == pre - 1 && ch == ' ')
					space = 1;
			}

			lyc.append(ret);

			if (i < pre + space) {
				preLyc.append(ret);
			} else if (i < pre + curr) {
				currLyc.append(ret);
			}
		}

		if (d != -1) {
			lyc.append(JongSung[d]);
			currLyc.append(JongSung[d]);
		}

		result[0] = lyc.toString();
		result[1] = preLyc.toString();
		result[2] = currLyc.toString();

		return result;
	}

	public static String hanToRoma(String s) {
		int a, b, c;
		int d = -1;
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (ch >= 0xAC00 && ch <= 0xD7A3) {
				c = ch - 0xAC00;
				a = c / (21 * 28);
				c = c % (21 * 28);
				b = c / 28;
				c = c % 28;

				if (d != -1) {
					if (a == 11)
						result.append(JongSungMo[d]);
					else
						result.append(JongSung[d]);
				}

				result.append(ChoSung[a]).append(JwungSung[b]);
				d = c;
			} else {
				if (d != -1) {
					result.append(JongSung[d]);
				}

				result.append(ch);
				d = -1;
			}
		}

		if (d != -1) {
			result.append(JongSung[d]);
		}

		return result.toString();
	}

	final static String[] katakanaExt = {
			"가", "カ",
			"개", "ケ",
			"갸", "キャ",
			"걔", "ケ",
			"거", "コ",
			"게", "ケ",
			"겨", "キョ",
			"계", "ケ",
			"고", "コ",
			"과", "クァ",
			"괘", "ケ",
			"괴", "ケ",
			"교", "キョ",
			"구", "ク",
			"궈", "クォ",
			"궤", "クェ",
			"귀", "キ",
			"규", "キュ",
			"그", "ク",
			"긔", "クィ",
			"기", "キ",

			"까", "ッカ",
			"깨", "ッケ",
			"꺄", "ッキャ",
			"꺠", "ッケ",
			"꺼", "ッコ",
			"께", "ッケ",
			"껴", "ッキョ",
			"꼐", "ッケ",
			"꼬", "ッコ",
			"꽈", "ックァ",
			"꽤", "ッケ",
			"꾀", "ッケ",
			"꾜", "ッキョ",
			"꾸", "ック",
			"꿔", "ックォ",
			"꿰", "ックェ",
			"뀌", "ッキ",
			"뀨", "ッキュ",
			"끄", "ック",
			"끠", "ックィ",
			"끼", "ッキ",

			"다", "タ",
			"대", "テ",
			"댜", "テャ",
			"댸", "テ",
			"더", "ト",
			"데", "テ",
			"뎌", "テョ",
			"뎨", "テ",
			"도", "ト",
			"돠", "トァ",
			"돼", "デ",
			"되", "デ",
			"됴", "テョ",
			"두", "トゥ",
			"둬", "トォ",
			"뒈", "テ",
			"뒤", "ティ",
			"듀", "テュ",
			"드", "トゥ",
			"듸", "ティ",
			"디", "ティ",

			"따", "ッタ",
			"때", "ッテ",
			"땨", "ッテャ",
			"떄", "ッテ",
			"떠", "ット",
			"떼", "ッテ",
			"뗘", "ッテョ",
			"뗴", "ッテ",
			"또", "ット",
			"똬", "ットァ",
			"뙈", "ッテ",
			"뙤", "ッテ",
			"뚀", "ッテョ",
			"뚜", "ットゥ",
			"뚸", "ットォ",
			"뛔", "ッテ",
			"뛰", "ッティ",
			"뜌", "ッテュ",
			"뜨", "ットゥ",
			"띄", "ッティ",
			"띠", "ッティ",

			"바", "パ",
			"배", "ペ",
			"뱌", "ピャ",
			"뱨", "ペ",
			"버", "ポ",
			"베", "ペ",
			"벼", "ピョ",
			"볘", "ペ",
			"보", "ポ",
			"봐", "パ",
			"봬", "ペ",
			"뵈", "ペ",
			"뵤", "ピョ",
			"부", "プ",
			"붜", "ポ",
			"붸", "ペ",
			"뷔", "ピ",
			"뷰", "ピュ",
			"브", "プ",
			"븨", "ピ",
			"비", "ピ",

			"빠", "ッパ",
			"빼", "ッペ",
			"뺘", "ッピャ",
			"뺴", "ッペ",
			"뻐", "ッポ",
			"뻬", "ッペ",
			"뼈", "ッピョ",
			"뼤", "ッペ",
			"뽀", "ッポ",
			"뽜", "ッパ",
			"뽸", "ッペ",
			"뾔", "ッペ",
			"뾰", "ッピョ",
			"뿌", "ップ",
			"뿨", "ッポ",
			"쀄", "ッペ",
			"쀠", "ッピ",
			"쀼", "ッピュ",
			"쁘", "ップ",
			"쁴", "ッピ",
			"삐", "ッピ",

			"자", "チャ",
			"재", "チェ",
			"쟈", "チャ",
			"쟤", "チェ",
			"저", "チョ",
			"제", "チェ",
			"져", "チョ",
			"졔", "チェ",
			"조", "チョ",
			"좌", "チャ",
			"좨", "チェ",
			"죄", "チェ",
			"죠", "チョ",
			"주", "チュ",
			"줘", "チョ",
			"줴", "チェ",
			"쥐", "チ",
			"쥬", "チュ",
			"즈", "チュ",
			"즤", "チ",
			"지", "チ",

			"짜", "ッチャ",
			"째", "ッチェ",
			"쨔", "ッチャ",
			"쨰", "ッチェ",
			"쩌", "ッチョ",
			"쩨", "ッチェ",
			"쪄", "ッチョ",
			"쪠", "ッチェ",
			"쪼", "ッチョ",
			"쫘", "ッチャ",
			"쫴", "ッチェ",
			"쬐", "ッチェ",
			"쬬", "ッチョ",
			"쭈", "ッチュ",
			"쭤", "ッチョ",
			"쮀", "ッチェ",
			"쮜", "ッチ",
			"쮸", "ッチュ",
			"쯔", "ッチュ",
			"쯰", "ッチ",
			"찌", "ッチ",

			"차", "チャ",
			"채", "チェ",
			"챠", "チャ",
			"챼", "チェ",
			"처", "チョ",
			"체", "チェ",
			"쳐", "チョ",
			"쳬", "チェ",
			"초", "チョ",
			"촤", "チャ",
			"쵀", "チェ",
			"최", "チェ",
			"쵸", "チョ",
			"추", "チュ",
			"춰", "チョ",
			"췌", "チェ",
			"취", "チ",
			"츄", "チュ",
			"츠", "チュ",
			"츼", "チ",
			"치", "ッチ",

			"싸", "ッサ",
			"쌔", "ッセ",
			"쌰", "ッシャ",
			"썌", "ッシェ",
			"써", "ッソ",
			"쎄", "ッセ",
			"쎠", "ッショ",
			"쎼", "ッシェ",
			"쏘", "ッソ",
			"쏴", "ッスァ",
			"쐐", "ッセ",
			"쐬", "ッセ",
			"쑈", "ッショ",
			"쑤", "ッス",
			"쒀", "ッソ",
			"쒜", "ッシェ",
			"쒸", "ッシ",
			"쓔", "ッシュ",
			"쓰", "ッス",
			"씌", "ッスィ",
			"씨", "ッシ",

			"카", "カ",
			"캐", "ケ",
			"캬", "キャ",
			"컈", "ケ",
			"커", "コ",
			"케", "ッケ",
			"켜", "キョ",
			"켸", "ケ",
			"코", "ッコ",
			"콰", "クァ",
			"쾌", "ケ",
			"쾨", "ケ",
			"쿄", "キョ",
			"쿠", "ク",
			"쿼", "クォ",
			"퀘", "クォ",
			"퀘", "クェ",
			"퀴", "クィ",
			"퀴", "キ",
			"큐", "キュ",
			"크", "ク",
			"킈", "キュ",
			"킈", "クィ",
			"키", "キ",

			"타", "ッタ",
			"태", "テ",
			"탸", "テャ",
			"턔", "テ",
			"터", "ト",
			"테", "テ",
			"텨", "テョ",
			"톄", "テ",
			"토", "ト",
			"톼", "トァ",
			"퇘", "テ",
			"퇴", "テ",
			"툐", "テョ",
			"투", "トゥ",
			"퉈", "トォ",
			"퉤", "テ",
			"튀", "ティ",
			"튜", "テュ",
			"트", "トゥ",
			"틔", "ティ",
			"티", "ティ",

			"파", "ッパ",
			"패", "ッペ",
			"퍄", "ッピャ",
			"퍠", "ッペ",
			"퍼", "ッポ",
			"페", "ッペ",
			"펴", "ッピョ",
			"폐", "ッペ",
			"포", "ッポ",
			"퐈", "ッパ",
			"퐤", "ッペ",
			"푀", "ッペ",
			"표", "ッピョ",
			"푸", "ップ",
			"풔", "ッポ",
			"풰", "ッペ",
			"퓌", "ッピ",
			"퓨", "ッピュ",
			"프", "ップ",
			"픠", "ッピ",
			"피", "ッピ"
	};

	final static String[] katakana =
	{
			"가", "ガ",
			"개", "ゲ",
			"갸", "ギャ",
			"걔", "ゲ",
			"거", "ゴ",
			"게", "ゲ",
			"겨", "ギョ",
			"계", "ゲ",
			"고", "ゴ",
			"과", "グァ",
			"괘", "グェ",
			"괴", "ゲ",
			"교", "ギョ",
			"구", "グ",
			"궈", "グォ",
			"궤", "グェ",
			"귀", "ギ",
			"규", "ギュ",
			"그", "グ",
			"긔", "グィ",
			"기", "ギ",

			"까", "カ",
			"깨", "ケ",
			"꺄", "キャ",
			"꺠", "ケ",
			"꺼", "コ",
			"께", "ケ",
			"껴", "キョ",
			"꼐", "ケ",
			"꼬", "コ",
			"꽈", "クァ",
			"꽤", "ケ",
			"꾀", "ケ",
			"꾜", "キョ",
			"꾸", "ク",
			"꿔", "クォ",
			"꿰", "クェ",
			"뀌", "キ",
			"뀨", "キュ",
			"끄", "ク",
			"끠", "クィ",
			"끼", "キ",

			"나", "ナ",
			"내", "ネ",
			"냐", "ニャ",
			"냬", "ニェ",
			"너", "ノ",
			"네", "ネ",
			"녀", "ニョ",
			"녜", "ニェ",
			"노", "ノ",
			"놔", "ノァ",
			"놰", "ネ",
			"뇌", "ネ",
			"뇨", "ニョ",
			"누", "ヌ",
			"눠", "ノ",
			"눼", "ネ",
			"뉘", "ニ",
			"뉴", "ニュ",
			"느", "ヌ",
			"늬", "ニ",
			"니", "ニ",

			"다", "ダ",
			"대", "デ",
			"댜", "デャ",
			"댸", "デ",
			"더", "ド",
			"데", "デ",
			"뎌", "デョ",
			"뎨", "デ",
			"도", "ド",
			"돠", "ドァ",
			"돼", "デ",
			"되", "デ",
			"됴", "デョ",
			"두", "ドゥ",
			"둬", "ドォ",
			"뒈", "デ",
			"뒤", "ディ",
			"듀", "デュ",
			"드", "ドゥ",
			"듸", "ディ",
			"디", "ディ",

			"따", "タ",
			"때", "テ",
			"땨", "テャ",
			"떄", "テ",
			"떠", "ト",
			"떼", "テ",
			"뗘", "テョ",
			"뗴", "テ",
			"또", "ト",
			"똬", "トァ",
			"뙈", "テ",
			"뙤", "テ",
			"뚀", "テョ",
			"뚜", "トゥ",
			"뚸", "トォ",
			"뛔", "テ",
			"뛰", "ティ",
			"뜌", "テュ",
			"뜨", "トゥ",
			"띄", "ティ",
			"띠", "ティ",

			"라", "ラ",
			"래", "レ",
			"랴", "リャ",
			"럐", "リェ",
			"러", "ロ",
			"레", "レ",
			"려", "リョ",
			"례", "リェ",
			"로", "ロ",
			"롸", "ラ",
			"뢔", "レ",
			"뢰", "レ",
			"료", "リョ",
			"루", "ル",
			"뤄", "ロ",
			"뤠", "レ",
			"뤼", "リ",
			"류", "リュ",
			"르", "ル",
			"릐", "リ",
			"리", "リ",

			"마", "マ",
			"매", "メ",
			"먀", "ミャ",
			"먜", "メ",
			"머", "モ",
			"메", "メ",
			"며", "ミョ",
			"몌", "メ",
			"모", "モ",
			"뫄", "マ",
			"뫠", "メ",
			"뫼", "メ",
			"묘", "ミョ",
			"무", "ム",
			"뭐", "モ",
			"뭬", "メ",
			"뮈", "ミ",
			"뮤", "ミュ",
			"므", "ム",
			"믜", "ミ",
			"미", "ミ",

			"바", "バ",
			"배", "ベ",
			"뱌", "ビャ",
			"뱨", "ベ",
			"버", "ボ",
			"베", "ベ",
			"벼", "ビョ",
			"볘", "ベ",
			"보", "ボ",
			"봐", "バ",
			"봬", "ベ",
			"뵈", "ベ",
			"뵤", "ビョ",
			"부", "ブ",
			"붜", "ボ",
			"붸", "ベ",
			"뷔", "ビ",
			"뷰", "ビュ",
			"브", "ブ",
			"븨", "ビ",
			"비", "ビ",

			"빠", "パ",
			"빼", "ペ",
			"뺘", "ピャ",
			"뺴", "ペ",
			"뻐", "ポ",
			"뻬", "ペ",
			"뼈", "ピョ",
			"뼤", "ペ",
			"뽀", "ポ",
			"뽜", "パ",
			"뽸", "ペ",
			"뾔", "ペ",
			"뾰", "ピョ",
			"뿌", "プ",
			"뿨", "ポ",
			"쀄", "ペ",
			"쀠", "ピ",
			"쀼", "ピュ",
			"쁘", "プ",
			"쁴", "ピ",
			"삐", "ピ",

			"사", "サ",
			"새", "セ",
			"샤", "シャ",
			"섀", "シェ",
			"서", "ソ",
			"세", "セ",
			"셔", "ショ",
			"셰", "シェ",
			"소", "ソ",
			"솨", "スァ",
			"쇄", "セ",
			"쇠", "セ",
			"쇼", "ショ",
			"수", "ス",
			"숴", "ソ",
			"쉐", "シェ",
			"쉬", "シ",
			"슈", "シュ",
			"스", "ス",
			"싀", "スィ",
			"시", "シ",

			"싸", "サ",
			"쌔", "セ",
			"쌰", "シャ",
			"썌", "シェ",
			"써", "ソ",
			"쎄", "セ",
			"쎠", "ショ",
			"쎼", "シェ",
			"쏘", "ソ",
			"쏴", "スァ",
			"쐐", "セ",
			"쐬", "セ",
			"쑈", "ショ",
			"쑤", "ス",
			"쒀", "ソ",
			"쒜", "シェ",
			"쒸", "シ",
			"쓔", "シュ",
			"쓰", "ス",
			"씌", "スィ",
			"씨", "シ",

			"아", "ア",
			"애", "エ",
			"야", "ヤ",
			"얘", "イェ",
			"어", "オ",
			"에", "エ",
			"여", "ヨ",
			"예", "イェ",
			"오", "オ",
			"와", "ワ",
			"왜", "ウェ",
			"외", "ウェ",
			"요", "ヨ",
			"우", "ウ",
			"워", "ウォ",
			"웨", "ウェ",
			"위", "ウィ",
			"유", "ユ",
			"으", "ウ",

			"윽", "ク",
			"윾", "ク",
			"윿", "ク",
			"은", "ン",
			"읁", "ン",
			"읂", "ン",
			"읃", "ッ",
			"을", "ル",
			"읅", "ク",
			"읆", "ム",
			"읇", "プ",
			"읈", "ッォ",
			"읉", "ッ",
			"읊", "プ",
			"읋", "ム",
			"음", "ム",
			"읍", "プ",
			"읎", "プス",
			"읏", "ッ",
			"읐", "ッ",
			"응", "ン",
			"읒", "ッ",
			"읓", "ッ",
			"읔", "ク",
			"읕", "ッ",
			"읖", "プ",
			"읗", "ッ",

			"의", "ウィ",
			"이", "イ",

			"자", "ジャ",
			"재", "ジェ",
			"쟈", "ジャ",
			"쟤", "ジェ",
			"저", "ジョ",
			"제", "ジェ",
			"져", "ジョ",
			"졔", "ジェ",
			"조", "ジョ",
			"좌", "ジャ",
			"좨", "ジェ",
			"죄", "ジェ",
			"죠", "ジョ",
			"주", "ジュ",
			"줘", "ジョ",
			"줴", "ジェ",
			"쥐", "ジ",
			"쥬", "ジュ",
			"즈", "ジュ",
			"즤", "ジ",
			"지", "ジ",

			"짜", "チャ",
			"째", "チェ",
			"쨔", "チャ",
			"쨰", "チェ",
			"쩌", "チョ",
			"쩨", "チェ",
			"쪄", "チョ",
			"쪠", "チェ",
			"쪼", "チョ",
			"쫘", "チャ",
			"쫴", "チェ",
			"쬐", "チェ",
			"쬬", "チョ",
			"쭈", "チュ",
			"쭤", "チョ",
			"쮀", "チェ",
			"쮜", "チ",
			"쮸", "チュ",
			"쯔", "チュ",
			"쯰", "チ",
			"찌", "チ",

			"차", "チャ",
			"채", "チェ",
			"챠", "チャ",
			"챼", "チェ",
			"처", "チョ",
			"체", "チェ",
			"쳐", "チョ",
			"쳬", "チェ",
			"초", "チョ",
			"촤", "チャ",
			"쵀", "チェ",
			"최", "チェ",
			"쵸", "チョ",
			"추", "チュ",
			"춰", "チョ",
			"췌", "チェ",
			"취", "チ",
			"츄", "チュ",
			"츠", "チュ",
			"츼", "チ",
			"치", "チ",

			"카", "カ",
			"캐", "ケ",
			"캬", "キャ",
			"컈", "ケ",
			"커", "コ",
			"케", "ケ",
			"켜", "キョ",
			"켸", "ケ",
			"코", "コ",
			"콰", "コヮ",
			"콰", "クァ",
			"쾌", "ケ",
			"쾨", "ケ",
			"쿄", "キョ",
			"쿠", "ク",
			"쿼", "クォ",
			"퀘", "クォ",
			"퀘", "クェ",
			"퀴", "クィ",
			"퀴", "キ",
			"큐", "キュ",
			"크", "ク",
			"킈", "キュ",
			"킈", "クィ",
			"키", "キ",

			"타", "タ",
			"태", "テ",
			"탸", "テャ",
			"턔", "テ",
			"터", "ト",
			"테", "テ",
			"텨", "テョ",
			"톄", "テ",
			"토", "ト",
			"톼", "トァ",
			"퇘", "テ",
			"퇴", "テ",
			"툐", "テョ",
			"투", "トゥ",
			"퉈", "トォ",
			"퉤", "テ",
			"튀", "ティ",
			"튜", "テュ",
			"트", "トゥ",
			"틔", "ティ",
			"티", "ティ",

			"파", "パ",
			"패", "ペ",
			"퍄", "ピャ",
			"퍠", "ペ",
			"퍼", "ポ",
			"페", "ペ",
			"펴", "ピョ",
			"폐", "ペ",
			"포", "ポ",
			"퐈", "パ",
			"퐤", "ペ",
			"푀", "ペ",
			"표", "ピョ",
			"푸", "プ",
			"풔", "ポ",
			"풰", "ペ",
			"퓌", "ピ",
			"퓨", "ピュ",
			"프", "プ",
			"픠", "ピ",
			"피", "ピ",

			"하", "ハ",
			"해", "ヘ",
			"햐", "ヒャ",
			"햬", "ヘ",
			"허", "ホ",
			"헤", "ヘ",
			"혀", "ヒョ",
			"혜", "ヘ",
			"호", "ホ",
			"화", "ファ",
			"홰", "フェ",
			"회", "フェ",
			"효", "ヒョ",
			"후", "フ",
			"훠", "フォ",
			"훼", "フェ",
			"휘", "フィ",
			"휴", "ヒュ",
			"흐", "フ",
			"희", "ヒ",
			"히", "ヒ"
	};

	public static String matchKatakana(String s) {
		String result = "";
		int e = katakana.length / 2;

		for (int i = 0; i < e; i++) {
			if (s.equals(katakana[i * 2])) {
				result = katakana[i * 2 + 1];
				break;
			}
		}

		return result;
	}

	public static String matchKatakanaExt(String s) {
		String result = "";
		int e = katakanaExt.length / 2;

		for (int i = 0; i < e; i++) {
			if (s.equals(katakanaExt[i * 2])) {
				result = katakanaExt[i * 2 + 1];
				break;
			}
		}

		return result;
	}

	public static int[] ChosungTrans(int d, int n1, int n2, int n3) {
		int[] ret = new int[2];
		ret[0] = n1;
		ret[1] = 0;

		if (n1 == 11) {		// 모음
			switch (d) {
			case 1:	// ㄱ
				ret[0] = 0;
				break;
			case 2:	// ㄲ
				ret[1] = 1;
				ret[0] = 1;
				break;
			case 3:	// ㄳ
				ret[0] = 9;
				break;
			case 4:	// ㄴ
				ret[0] = 2;
				break;
			case 5:	// ㄵ
				ret[0] = 12;
				break;
			case 6:	// ㄶ
				ret[0] = 2;
				break;
			case 7:	// ㄷ
				if (n2 == 20)
					ret[0] = 12;
				else
					ret[0] = 3;
				break;
			case 8:	// ㄹ
				ret[0] = 5;
				break;
			case 9:	// ㄺ
				ret[0] = 0;
				break;
			case 10:	// ㄻ
				ret[0] = 6;
				break;
			case 11:	// ㄼ
				ret[0] = 7;
				break;
			case 12:	// ㄽ
				ret[0] = 5;
				break;
			case 13:	// ㄾ
				ret[0] = 16;
				break;
			case 14:	// ㄿ
				ret[0] = 17;
				break;
			case 15:	// ㅀ
				ret[0] = 5;
				break;
			case 16:	// ㅁ
				ret[0] = 6;
				break;
			case 17:	// ㅂ
				ret[0] = 7;
				break;
			case 18:	// ㅄ
				ret[0] = 9;
				break;
			case 19:	// ㅅ
				// 만약 뒷글자가 '없' 일 경우 11,4,18
				if (n1 == 11 && n2 == 4 && n3 == 18)
					ret[0] = 3;
				else
					ret[0] = 9;
				break;
			case 20:	// ㅆ
				ret[1] = 1;
				ret[0] = 10;
				break;
			case 22:	// ㅈ
				ret[0] = 12;
				break;
			case 23:	// ㅊ
				if (n1 == 11 && n2 == 20 && n3 == 26)	// 잎 꽃잎
					ret[0] = 2;
				else
					ret[0] = 14;
				break;
			case 24:	// ㅋ
				ret[0] = 0;	// 들녘에 -> 들녀게
				break;
			case 25:	// ㅌ
				if (n2 == 20)
					ret[0] = 14;
				else if (n1 == 11 && n2 == 4 && n3 == 18)	// 만약 뒷글자가 '없' 일 경우 11,4,18
					ret[0] = 3;
				else {
					ret[0] = 16;
					ret[1] = 1;
				}
				break;
			case 26:	// ㅍ
				ret[0] = 17;
				ret[1] = 1;
				break;
			case 27:	// ㅎ
				// n1 = 18; //그냥 생략
				break;
			}
		} else {		// 자음
			switch (d) {
			case 16:	// ㅌ
				ret[1] = 1;
				break;
			}
		}

		return ret;
	}

	public static String trans(int n1, int n2, int n3, int d) {
		StringBuilder result = new StringBuilder();
		StringBuilder w1 = new StringBuilder();
		int ext = 0;

		if (n1 != -1) {
			if (n1 == 11) {	// 시작 모음
				// 자음 종성일때 뒷 초성의 자음 확인 : 종성이 뒤 초성으로 가면서 삭제
				// ㄱ, ㄲ, ㅋ, ㄷ, ㅌ, ㅂ, ㅍ, ㅈ, ㅊ, ㅅ, ㅆ, ㅎ, ㄴ, ㅁ, ㅇ, ㄹ
				if (d == 1 ||		// ㄱ
						d == 2 ||		// ㄲ
						d == 4 ||		// ㄴ
						d == 6 ||		// ㄶ
						d == 7 ||		// ㄷ
						d == 8 ||		// ㄹ
						d == 15 ||		// ㅀ
						d == 16 ||		// ㅁ
						d == 17 ||		// ㅂ
						d == 19 ||		// ㅅ
						d == 20 ||		// ㅆ
						d == 22 ||		// ㅈ
						d == 23 ||		// ㅊ
						d == 24 ||		// ㅋ
						d == 25 ||		// ㅌ
						d == 26 ||		// ㅍ
						d == 27)		// ㅎ
				{
					d = 0;
					int[] val = ChosungTrans(d, n1, n2, n3);
					n1 = val[0];
					ext = val[1];
				} else if (d == 21) { 		// ㅇ
					w1.setLength(0);
					w1.append((char) (0xC73C + d));
					result.append(matchKatakana(w1.toString()));
				} else if (d == 3 ||		// ㄳ //복자음 종성이 하나는 남고 하나는 뒷 초성으로
						d == 5 ||		// ㄵ
						d == 9 ||		// ㄺ
						d == 10 ||		// ㄻ
						d == 11 ||		// ㄼ
						d == 12 ||  		// ㄽ
						d == 13 ||		// ㄾ
						d == 14 ||		// ㄿ
						d == 15 ||		// ㅀ
						d == 18)			// ㅄ
				{
					if (n1 == 11) {
						switch (d) {
						case 3: {
							int[] val = ChosungTrans(d, n1, n2, n3);
							n1 = val[0];
							ext = val[1];
						}
							break;
						case 5: {
							int[] val = ChosungTrans(d, n1, n2, n3);
							n1 = val[0];
							ext = val[1];
							d = 4;
						}
							break;
						case 9:
						case 10:
						case 11:
						case 12:
						case 13:
						case 14:
						case 15: {
							int[] val = ChosungTrans(d, n1, n2, n3);
							n1 = val[0];
							ext = val[1];
							d = 8;
						}
							break;
						case 18: {
							int[] val = ChosungTrans(d, n1, n2, n3);
							n1 = val[0];
							ext = val[1];
							d = 26;
						}
							break;
						}
					}

					if (d > 0) {
						w1.setLength(0);
						w1.append((char) (0xC73C + d));
						result.append(matchKatakana(w1.toString()));
					}
				}

				char c = (char) (0xAC00 + 28 * 21 * n1 + 28 * n2);
				w1.setLength(0);
				w1.append(c);

				if (ext == 1) {
					result.append(matchKatakanaExt(w1.toString()));
				} else {
					if (n1 == 0 || n1 == 3 || n1 == 7 || n1 == 12) {
						if (n1 == 7) {	// ㅂ
							if (n2 == 9 && n3 == 0)	// 봐
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 4 && n3 == 0)	// 버
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 4 && n3 == 4)	// 번
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 9 && n3 == 20)	// 봤
								result.append(matchKatakana(w1.toString()));
							else
								result.append(matchKatakanaExt(w1.toString()));
						} else if (n1 == 12) {
							if (n2 == 14 && n3 == 0)	// 줘
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 14 && n3 == 20) // 줬
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 0)	// 주
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 8)	// 줄
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 4)	// 준
								result.append(matchKatakana(w1.toString()));
							else
								result.append(matchKatakanaExt(w1.toString()));
						} else {
							result.append(matchKatakanaExt(w1.toString()));
						}
					} else {
						result.append(matchKatakana(w1.toString()));
					}
				}
			} else if (n1 >= 0 && n1 <= 19) {
				switch (d) {
				case 0:
					switch (n1)
					{
					case 1:	// ㄲ
					case 4: // ㄸ
					case 8:	// ㅃ
					case 11: 	// ㅆ
					case 13:	// ㅉ
						ext = 1;
						break;
					}
					break;
				case 1: // ㄱ
					switch (n1)
					{
					case 0:	// ㄱ
						n1 = 1;
						d = 0;
						ext = 1;
						break;
					case 1:		// ㄲ
						d = 0;
						break;
					case 2: 	// ㄴ
						d = 21;
						break;
					case 3: 	// ㄷ
						n1 = 4;
						break;
					case 4: 	// ㄸ
						break;
					case 5: 	// ㄹ
						n1 = 2;
						d = 21;
						break;
					case 6: 	// ㅁ
						d = 21;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 8: 	// ㅃ
						break;
					case 9: 	// ㅅ
						n1 = 10;
						break;
					case 10: 	// ㅆ
						break;
					case 11: 	// ㅇ
						break;
					case 12: 	// ㅈ
						n1 = 13;
						break;
					case 13: 	// ㅉ
						break;
					case 14: 	// ㅊ
						break;
					case 15: 	// ㅋ
						d = 0;
						break;
					case 16: 	// ㅌ
						break;
					case 17: 	// ㅍ
						break;
					case 18: 	// ㅎ
						n1 = 1;
						d = 0;
						ext = 1;
						break;
					}
					break;
				case 2: // ㄲ
					switch (n1)
					{
					case 0:	// ㄱ
						n1 = 1;
						d = 0;
						break;
					case 1:		// ㄲ
						d = 0;
						break;
					case 2: 	// ㄴ
						d = 21;
						break;
					case 3: 	// ㄷ
						n1 = 4;
						break;
					case 4: 	// ㄸ
						break;
					case 5: 	// ㄹ
						n1 = 2;
						d = 21;	// ng
						break;
					case 6: 	// ㅁ
						d = 21;	// ng
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 8: 	// ㅃ
						break;
					case 9: 	// ㅅ
						n1 = 10;
						break;
					case 10: 	// ㅆ
						break;
					case 11: 	// ㅇ
						break;
					case 12: 	// ㅈ
						n1 = 13;
						break;
					case 13: 	// ㅉ
						break;
					case 14: 	// ㅊ
						break;
					case 15: 	// ㅋ
						d = 0;
						break;
					case 16: 	// ㅌ
						break;
					case 17: 	// ㅍ
						break;
					case 18: 	// ㅎ
						n1 = 1;
						d = 0;
						ext = 1;
						break;
					}
					break;
				case 3: // ㄳ
					switch (n1)
					{
					case 0:	// ㄱ
						n1 = 1;
						d = 0;
						ext = 1;
						break;
					case 1:		// ㄲ
						d = 0;
						ext = 1;
						break;
					case 2: 	// ㄴ
						d = 21;
						break;
					case 3: 	// ㄷ
						n1 = 4;
						break;
					case 4: 	// ㄸ
						break;
					case 5: 	// ㄹ
						n1 = 2;
						d = 21;	// ng
						break;
					case 6: 	// ㅁ
						d = 21;	// ng
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 8: 	// ㅃ
						break;
					case 9: 	// ㅅ
						n1 = 10;
						break;
					case 10: 	// ㅆ
						break;
					case 11: 	// ㅇ
						break;
					case 12: 	// ㅈ
						n1 = 13;
						break;
					case 13: 	// ㅉ
						break;
					case 14: 	// ㅊ
						break;
					case 15: 	// ㅋ
						d = 0;
						ext = 1;
						break;
					case 16: 	// ㅌ
						break;
					case 17: 	// ㅍ
						break;
					case 18: 	// ㅎ
						n1 = 15;
						d = 0;
						break;
					}
					break;
				case 24:	// ㅋ
					switch (n1)
					{
					case 0:	// ㄱ
						n1 = 1;
						d = 0;
						break;
					case 1:		// ㄲ
						d = 0;
						break;
					case 2: 	// ㄴ
						d = 21;
						break;
					case 3: 	// ㄷ
						n1 = 4;
						break;
					case 4: 	// ㄸ
						break;
					case 5: 	// ㄹ
						n1 = 2;
						d = 21;	// ng
						break;
					case 6: 	// ㅁ
						d = 21;	// ng
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 8: 	// ㅃ
						break;
					case 9: 	// ㅅ
						n1 = 10;
						break;
					case 10: 	// ㅆ
						break;
					case 11: 	// ㅇ
						break;
					case 12: 	// ㅈ
						n1 = 13;
						break;
					case 13: 	// ㅉ
						break;
					case 14: 	// ㅊ
						break;
					case 15: 	// ㅋ
						d = 0;
						break;
					case 16: 	// ㅌ
						break;
					case 17: 	// ㅍ
						break;
					case 18: 	// ㅎ
						n1 = 15;
						d = 0;
						break;
					}
					break;
				case 7:	// ㄷ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;	// n
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 0;
						ext = 1;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 6:		// ㅁ
						d = 4;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 14;
						break;
					}
					break;
				case 25:	// ㅌ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;	// n
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 0;
						ext = 1;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 6:		// ㅁ
						d = 4;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 14;
						break;
					}
					break;
				case 17:	// ㅂ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 16;
						break;
					case 3:		// ㄷ
						n1 = 4;
						break;
					case 5:		// ㄹ
						n1 = 2;
						d = 16;
						break;
					case 6:		// ㅁ
						d = 16;
						break;
					case 7:		// ㅂ
						n1 = 8;
						d = 0;
						break;
					case 8:		// ㅃ
						d = 0;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 17:		// ㅍ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 17;
						d = 0;
						ext = 1;
						break;
					}
					break;
				case 26:	// ㅍ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 16;
						break;
					case 3:		// ㄷ
						n1 = 4;
						break;
					case 5:		// ㄹ
						n1 = 2;
						d = 16;
						break;
					case 6:		// ㅁ
						d = 16;
						break;
					case 7:		// ㅂ
						n1 = 8;
						d = 0;
						break;
					case 8:		// ㅃ
						d = 0;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 17:		// ㅍ
						d = 0;
						break;
					}
					break;
				case 22:	// ㅈ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 0;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 6:		// ㅁ
						d = 4;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 14;
						d = 0;
						break;
					}
					break;
				case 23:	// ㅊ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 0;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 6:		// ㅁ
						d = 4;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					}
					break;
				case 19:	// ㅅ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;
						break;
					case 3:		// ㄷ
						n1 = 4;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 6:		// ㅁ
						d = 4;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 16;
						d = 0;
						break;
					}
					break;
				case 20:	// ㅆ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 2:		// ㄴ
						d = 4;
						break;
					case 3:		// ㄷ
						n1 = 4;
						break;
					case 4:		// ㄸ
						n3 = 0;
						break;
					case 7:		// ㅂ
						n1 = 8;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					case 16:		// ㅌ
						d = 0;
						break;
					}
					break;
				case 27:	// ㅎ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 15;
						d = 0;
						ext = 1;
						break;
					case 1:		// ㄲ
						d = 0;
						break;
					case 2:		// ㄴ
						d = 4;	// n
						break;
					case 3:		// ㄷ
						n1 = 16;
						d = 0;
						ext = 1;
						break;
					case 4:		// ㄸ
						d = 0;
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 0;
						break;
					case 12:		// ㅈ
						n1 = 14;
						d = 0;
						ext = 1;
						break;
					case 15:		// ㅋ
						d = 0;
						break;
					case 16:		// ㅌ
						d = 0;
						break;

					}
					break;
				case 4:		// ㄴ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 5: 	// ㄹ
						n1 = 5;
						d = 8;	// ng
						break;
					case 9: 	// ㅅ
						n1 = 10;
						break;
					case 18: 	// ㅎ
						n1 = 2;
						d = 0;
						break;
					}
					break;
				case 16:	// ㅁ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:	// ㄱ
						if (n2 == 6 && n3 == 0) // 겨
							n1 = 0;
						else
							n1 = 1;
						break;
					case 5:		// ㄹ
						n1 = 2;
						break;
					case 12:		// ㅈ
						if (n2 == 0 || n2 == 4)// ㅏ, ㅓ
							n1 = 12;
						else
							n1 = 13;	// ㅉ
						break;
					case 18:		// ㅎ
						n1 = 6;
						d = 0;
						break;
					}
					break;
				case 21: 	// ㅇ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 5:		// ㄹ
						n1 = 2;
						break;
					}
					break;
				case 8:		// ㄹ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						if (n2 == 4 || n2 == 5)	// 거, 게
							n1 = 1;
						else
							n1 = 0;
						break;
					case 2:		// ㄴ
						n1 = 5;
						break;
					case 3:		// ㄷ
						break;
					case 5:
						n1 = 5;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 18:		// ㅎ
						n1 = 5;
						d = 0;
						break;
					}
					break;
				case 6:		// ㄶ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 15;
						d = 4;	// n
						break;
					case 2:		// ㄴ
						d = 4;	// n
						break;
					case 3:		// ㄷ
						n1 = 16;
						d = 4;	// n
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 4;	// n
						break;
					case 12:		// ㅈ
						n1 = 14;
						d = 4;	// n
						break;
					}
					break;
				case 5:		// ㄵ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						d = 4;	// n
						break;
					case 2:		// ㄴ
						d = 4;	// n
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 4;	// n
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 4;	// n
						break;
					case 12:		// ㅈ
						n1 = 13;
						d = 4;	// n
						break;
					case 18: 	// ㅎ
						n1 = 14;
						d = 4;
						break;
					}
					break;
				case 18:	// ㅄ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						d = 17;	// l
						break;
					case 2:		// ㄴ
						d = 16;
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 17;
						break;
					case 6:		// ㅁ
						d = 16;
						break;
					case 7:		// ㅂ
						n1 = 8;
						d = 0;
						break;
					case 8:		// ㅃ
						d = 0;
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 17;
						break;
					case 12:		// ㅈ
						n1 = 13;
						d = 17;
						break;
					case 17:		// ㅍ
						d = 0;
						break;
					case 18:		// ㅎ
						n1 = 17;
						break;
					}
					break;
				case 9:		// ㄺ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						d = 0;	// n
						break;
					case 2:		// ㄴ
						d = 21;	// ng
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 1;	// k
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 1;	// k
						break;
					case 12:		// ㅈ
						n1 = 13;
						d = 1;	// k
						break;
					case 18:		// ㅎ
						n1 = 15;
						d = 8;	// k
						break;
					}
					break;
				case 10:	// ㄻ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						break;
					case 3:		// ㄷ
						n1 = 4;
						break;
					case 9:		// ㅅ
						n1 = 10;
						break;
					case 12:		// ㅈ
						n1 = 13;
						break;
					}
					break;
				case 11:	// ㄼ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 1;
						d = 8;	// n
						break;
					case 2:		// ㄴ
						d = 6;	// m
						break;
					case 3:		// ㄷ
						n1 = 4;
						d = 8;	// ㄹ
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 8;	// ㄹ
						break;
					case 12:		// ㅈ
						n1 = 13;
						d = 8;	// ㄹ
						break;
					case 18:		// ㅎ
						n1 = 17;
						d = 8;	// ㄹ
						break;
					}
					break;
				case 15:	// ㅀ
					switch (n1)	// 뒷글자의 현재 초성 말한다
					{
					case 0:		// ㄱ
						n1 = 15;
						d = 8;	// ㄹ
						break;
					case 2:		// ㄴ
						d = 8;	// m
						break;
					case 3:		// ㄷ
						n1 = 16;
						d = 8;	// ㄹ
						break;
					case 9:		// ㅅ
						n1 = 10;
						d = 8;	// k
						break;
					case 12:		// ㅈ
						n1 = 14;
						d = 8;	// k
						break;
					}
					break;
				}

				if (d > 0) {
					w1.setLength(0);
					w1.append((char) (0xC73C + d));
					result.append(matchKatakana(w1.toString()));
				}

				char c = (char) (0xAC00 + 28 * 21 * n1 + 28 * n2);
				w1.setLength(0);
				w1.append(c);

				if (ext == 1) {
					result.append(matchKatakanaExt(w1.toString()));
				} else {
					if (n1 == 0 || n1 == 3 || n1 == 7 || n1 == 12) {
						if (n1 == 7) {	// ㅂ
							if (n2 == 9 && n3 == 0)	// 봐
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 4 && n3 == 0)	// 버
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 4 && n3 == 4)	// 번
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 9 && n3 == 20)	// 봤
								result.append(matchKatakana(w1.toString()));
							else
								result.append(matchKatakanaExt(w1.toString()));
						} else if (n1 == 12) {
							if (n2 == 14 && n3 == 0)	// 줘
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 14 && n3 == 20) // 줬
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 0)	// 주
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 8)	// 줄
								result.append(matchKatakana(w1.toString()));
							else if (n2 == 13 && n3 == 4)	// 준
								result.append(matchKatakana(w1.toString()));
							else
								result.append(matchKatakanaExt(w1.toString()));
						} else {
							result.append(matchKatakanaExt(w1.toString()));
						}
					} else {
						result.append(matchKatakana(w1.toString()));
					}
				}
			}
		} else {
			if (d > 0) {
				w1.setLength(0);
				w1.append((char) (0xC73C + d));
				result.append(matchKatakana(w1.toString()));
			}
		}

		return result.toString();
	}

	public static String[] hanToKata(String s, int pre, int curr) {
		int a, b, c;
		int d = -1;
		int space = 0;
		String[] result = new String[3];
		StringBuilder lyc = new StringBuilder();
		StringBuilder preLyc = new StringBuilder();
		StringBuilder currLyc = new StringBuilder();
		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			ret.setLength(0);
			space = 0;

			if (ch >= 0xAC00 && ch <= 0xD7A3) {
				c = ch - 0xAC00;
				a = c / (21 * 28);
				c = c % (21 * 28);
				b = c / 28;
				c = c % 28;

				ret.append(trans(a, b, c, d));

				d = c;
			} else {
				if (d != -1) {
					ret.append(trans(-1, -1, -1, d));
				}

				ret.append(ch);

				d = -1;

				if (i == pre - 1 && ch == ' ')
					space = 1;
			}

			lyc.append(ret);

			if (i < pre + space) {
				preLyc.append(ret);
			} else if (i < pre + curr) {
				currLyc.append(ret);
			}
		}

		if (d != -1) {
			String str = trans(-1, -1, -1, d);
			lyc.append(str);
			currLyc.append(str);
		}

		result[0] = lyc.toString();
		result[1] = preLyc.toString();
		result[2] = currLyc.toString();

		return result;
	}

	public static String hanToKata(String s) {
		int a, b, c;
		int d = -1;
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (ch >= 0xAC00 && ch <= 0xD7A3) {
				c = ch - 0xAC00;
				a = c / (21 * 28);
				c = c % (21 * 28);
				b = c / 28;
				c = c % 28;
				result.append(trans(a, b, c, d));

				d = c;
			} else {
				if (d != -1) {
					result.append(trans(-1, -1, -1, d));
				}

				result.append(ch);
				d = -1;
			}
		}

		if (d != -1) {
			result.append(trans(-1, -1, -1, d));
		}

		return result.toString();
	}
};
