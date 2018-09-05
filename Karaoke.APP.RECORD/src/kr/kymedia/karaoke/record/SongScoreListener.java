/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.KPOP
 * filename	:	SongScoreListener.java
 * author	:	hownam
 *
 * <pre>
 * kr.kymedia.karaoke.record
 *    |_ SongScoreListener.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.record;

/**
 * 점수관련 리스너
 * 
 * @author hownam
 *
 */
public interface SongScoreListener {
	/**
	 * 점수 처리 완료
	 */
	public abstract void onCompletion(int score);

	/**
	 * 점수 처리중 에러
	 */
	public abstract void onError();

}
