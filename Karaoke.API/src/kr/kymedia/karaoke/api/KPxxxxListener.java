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
 * project	:	Karaoke.KPOP.LIB
 * filename	:	KPnnnnListener.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.karaoke.kpop.data
 *    |_ KPnnnnListener.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.api;

import kr.kymedia.karaoke.api.KPnnnn.KPnnnnListener;

/**
 *
 * NOTE:<br>
 * 외부배포라이브러리 인터페이스
 *
 * @author isyoon
 * @since 2013. 3. 22.
 * @version 1.0
 * @see
 */
@Deprecated
public interface KPxxxxListener extends KPnnnnListener {

	/**
	 * 조회전체
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	public void onKPnnnnResult(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회전체
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	public interface onKPnnnnResult {
		public void onResult(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회시작
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	public void onKPnnnnStart(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회시작
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	public interface onKPnnnnStart {
		public void onKStart(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회성공
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	public void onKPnnnnSuccess(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회성공
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	public interface onKPnnnnSuccess {
		public void onSuccess(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회실패
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	public void onKPnnnnFail(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회실패
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	public interface onKPnnnnFail {
		public void onFail(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회오류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	public void onKPnnnnError(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회오류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	public interface onKPnnnnError {
		public void onError(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회취소 - 보류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	@Deprecated
	public void onKPnnnnCancel(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회취소 - 보류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Deprecated
	public interface onKPnnnnCancel {
		public void onCancel(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 조회종료 - 보류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Override
	@Deprecated
	public void onKPnnnnFinish(int what, String opcode, String r_code, String r_message,
			KPItem r_info);

	/**
	 * 조회종료 - 보류
	 * 
	 * @param what
	 * @param p_opcode
	 * @param r_code
	 * @param r_message
	 * @param r_info
	 */
	@Deprecated
	public interface onKPnnnnFinish {
		public void onFinish(int what, String opcode, String r_code, String r_message,
				KPItem r_info);
	}

	/**
	 * 전문진행확인
	 * 
	 * @param size
	 * @param total
	 */
	@Override
	public void onKPnnnnProgress(long size, long total);

	/**
	 * 전문진행확인
	 * 
	 * @param size
	 * @param total
	 */
	public interface onKPnnnnProgress {
		public void onProgress(long size, long total);
	}

}
