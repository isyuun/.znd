package kr.kymedia.karaoke.play3;

/**
 * 재생관련 리스너
 * 
 * @author hownam
 *
 */
public interface SongPlayListener {
	/**
	 * 재생준비 완료
	 */
	public abstract void onPrepared();

	/**
	 * 재생시간
	 *
	 * @param t
	 */
	public abstract void onTime(int t);

	/**
	 * 재생 완료
	 */
	public abstract void onCompletion();

	/**
	 * 재생중 에러
	 */
	public abstract void onError();

	/**
	 * 버퍼링 사이즈
	 */
	public abstract void onBufferingUpdate(int percent);

	/**
	 * release
	 */
	public abstract void onRelease();

	/**
	 * seek 완료
	 */
	public abstract void onSeekComplete();

	/**
	 * 전주/간주 카운트
	 */
	public abstract void onReady(int count);
}
