package kr.kymedia.karaoke.play3;

/**
 * 재생뷰 리스너
 * 
 * @author hownam
 *
 */
public interface PlayViewListener {
	/**
	 * 화면 종료시 처리
	 */
	public abstract void onDestroy();

	/**
	 * 음원 다운로드 에러
	 */
	public abstract void onError();

	/**
	 * 전주/간주 카운트
	 */
	public abstract void onReady(int count);
}
