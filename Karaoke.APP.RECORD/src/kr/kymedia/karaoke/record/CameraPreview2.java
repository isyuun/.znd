package kr.kymedia.karaoke.record;

import is.yuun.net.pikanji.camerapreviewsample.CameraPreview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * CameraPreview class that is extended only for the purpose of testing CameraPreview class. This
 * class is added functionality to set arbitrary preview size, and removed automated retry function
 * to start preview on exception.
 */
public class CameraPreview2 extends CameraPreview {
	final static String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private final CameraInfo mCameraInfo = new CameraInfo();

	public CameraInfo getCameraInfo() {
		return mCameraInfo;
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		String text = String.format("%s() ", name);
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// text = String.format("line:%d - %s() ", line, name);
		return text;
	}

	public static String getQuality(int quality) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			switch (quality) {
			case CamcorderProfile.QUALITY_LOW:
				return "LOW";
			case CamcorderProfile.QUALITY_HIGH:
				return "HIGH";
			case CamcorderProfile.QUALITY_QCIF:
				return "QCIF";
			case CamcorderProfile.QUALITY_CIF:
				return "CIF";
			case CamcorderProfile.QUALITY_480P:
				return "480P";
			case CamcorderProfile.QUALITY_720P:
				return "720P";
			case CamcorderProfile.QUALITY_1080P:
				return "1080P";
			case CamcorderProfile.QUALITY_TIME_LAPSE_LOW:
				return "LAPSE_LOW";
			case CamcorderProfile.QUALITY_TIME_LAPSE_HIGH:
				return "LAPSE_HIGH";
			case CamcorderProfile.QUALITY_TIME_LAPSE_QCIF:
				return "LAPSE_QCIF";
			case CamcorderProfile.QUALITY_TIME_LAPSE_CIF:
				return "LAPSE_CIF";
			case CamcorderProfile.QUALITY_TIME_LAPSE_480P:
				return "LAPSE_480P";
			case CamcorderProfile.QUALITY_TIME_LAPSE_720P:
				return "LAPSE_720P";
			case CamcorderProfile.QUALITY_TIME_LAPSE_1080P:
				return "LAPSE_1080P";
			default:
				break;
			}
		} else {
			switch (quality) {
			case CamcorderProfile.QUALITY_LOW:
				return "LOW";
			case CamcorderProfile.QUALITY_HIGH:
				return "HIGH";
			default:
				break;
			}

		}

		return "unknown";
	}

	/**
	 * <pre>
	 * 	int	AAC	AAC Low Complexity (AAC-LC) audio codec
	 * 	int	AAC_ELD	Enhanced Low Delay AAC (AAC-ELD) audio codec
	 * 	int	AMR_NB	AMR (Narrowband) audio codec
	 * 	int	AMR_WB	AMR (Wideband) audio codec
	 * 	int	DEFAULT	
	 * 	int	HE_AAC	High Efficiency AAC (HE-AAC) audio codec
	 * </pre>
	 * 
	 * @param audioCodec
	 * @return
	 */
	public static String getAudioCodec(int audioCodec) {
		switch (audioCodec) {
		case AudioEncoder.AAC:
			return "AAC";
		case AudioEncoder.AAC_ELD:
			return "AAC_ELD";
		case AudioEncoder.AMR_NB:
			return "AMR_NB";
		case AudioEncoder.AMR_WB:
			return "AMR_WB";
		case AudioEncoder.DEFAULT:
			return "DEFAULT";
		case AudioEncoder.HE_AAC:
			return "HE_AAC";
		default:
			return "unknown";
		}
	}

	public static String getFileFormat(int fileFormat) {
		switch (fileFormat) {
		case OutputFormat.MPEG_4:
			return "MPEG_4";
		case OutputFormat.RAW_AMR:
			return "RAW_AMR";
		case OutputFormat.THREE_GPP:
			return "THREE_GPP";
		case OutputFormat.DEFAULT:
			return "DEFAULT";
		default:
			return "unknown";
		}
	}

	public static String getVideoCodec(int videoCodec) {
		switch (videoCodec) {
		case VideoEncoder.DEFAULT:
			return "DEFAULT";
		case VideoEncoder.H263:
			return "H263";
		case VideoEncoder.H264:
			return "H264";
		case VideoEncoder.MPEG_4_SP:
			return "MPEG_4_SP";
		default:
			return "unknown";
		}
	}

	@Override
	public List<Camera.Size> getSupportedPreivewSizes() {
		return mPreviewSizeList;
	}

	public CameraPreview2(Context context, int cameraId, LayoutMode mode) {
		super(context, cameraId, mode);

		Log.e(__CLASSNAME__, getMethodName());

		addCamcorderProfile(cameraId);

		Camera.getCameraInfo(cameraId, mCameraInfo);

	}

	protected ArrayList<CamcorderProfile> mCamcorderProfiles = new ArrayList<CamcorderProfile>();
	protected CamcorderProfile mCamcorderProfile;

	public void addCamcorderProfile(int cameraId) {
		Log.e(__CLASSNAME__, getMethodName());

		try {
			mCamcorderProfiles.clear();
			mPreviewSizeList.clear();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// 기본설정
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_QCIF);
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_CIF);
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_480P);
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_720P);
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_1080P);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_LOW);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_HIGH);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_QCIF);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_CIF);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_480P);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_720P);
				// addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_TIME_LAPSE_1080P);
			} else {
				// 기본설정
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_LOW);
				addCamcorderProfile(cameraId, CamcorderProfile.QUALITY_HIGH);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (mCamcorderProfiles != null && mCamcorderProfiles.size() > 0) {
			mCamcorderProfile = mCamcorderProfiles.get(0);
			mPreviewSize = mPreviewSizeList.get(0);
		}

		if (mCamcorderProfile != null) {
			String sprofile = getQuality(mCamcorderProfile.quality);
			sprofile += " - " + getFileFormat(mCamcorderProfile.fileFormat);
			sprofile += " - " + getVideoCodec(mCamcorderProfile.videoCodec);
			sprofile += " - " + getAudioCodec(mCamcorderProfile.audioCodec);
			sprofile += " - " + mCamcorderProfile.videoFrameWidth + ", " + mCamcorderProfile.videoFrameHeight;
			sprofile += " - " + mPreviewSize.width + ", " + mPreviewSize.height;
			Log.e(__CLASSNAME__, sprofile);
		}
	}

	private CamcorderProfile addCamcorderProfile(int cameraId, int quality) {
		// _Log.e(__CLASSNAME__, getMethodName() + cameraId + ", " + quality);

		CamcorderProfile profile = null;

		try {
			// if (CamcorderProfile.hasProfile(cameraId, quality)) {
			// }
			profile = CamcorderProfile.get(cameraId, quality);
			Camera.Size size = mCamera.new Size(profile.videoFrameWidth, profile.videoFrameHeight);

			String sprofile = getQuality(profile.quality);
			sprofile += " - " + getFileFormat(profile.fileFormat);
			sprofile += " - " + getVideoCodec(profile.videoCodec);
			sprofile += " - " + getAudioCodec(profile.audioCodec);
			sprofile += " - " + profile.videoFrameWidth + ", " + profile.videoFrameHeight;
			sprofile += " - " + size.width + ", " + size.height;
			Log.e(__CLASSNAME__, sprofile);

			mCamcorderProfiles.add(profile);
			mPreviewSizeList.add(size);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return profile;
	}

	public ArrayList<CamcorderProfile> getCamorderProfiles() {
		return mCamcorderProfiles;
	}

	public CamcorderProfile getCamorderProfile(int index) {
		if (mCamcorderProfiles != null && index < mCamcorderProfiles.size()) {
			return mCamcorderProfiles.get(index);
		} else {
			return null;
		}
	}

	public CamcorderProfile getCamorderProfile() {
		return mCamcorderProfile;
	}

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
		// _Log.e(__CLASSNAME__, getMethodName());

		// if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
		// // this device has a camera
		// return true;
		// } else {
		// // no camera on this device
		// return false;
		// }
		if (Camera.getNumberOfCameras() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static int getCameraId(int facing) {
		int cameraId = 0;
		boolean find = false;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			for (int id = 0; id < Camera.getNumberOfCameras(); id++) {
				Camera.getCameraInfo(id, cameraInfo);
				// if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				if (cameraInfo.facing == facing) {
					cameraId = id;
					find = true;
					break;
				}
			}
		}

		// _Log.e(__CLASSNAME__, getMethodName() + find + ":" + cameraId);
		return cameraId;
	}

	public Camera getCamera() {
		Log.e(__CLASSNAME__, getMethodName());

		return mCamera;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.e(__CLASSNAME__, getMethodName());


		super.surfaceChanged(holder, format, width, height);
	}

	public void setCameraSize(int index, int width, int height) {
		// _Log.e(__CLASSNAME__, getMethodName());

		CamcorderProfile profile = getCamorderProfile(index);

		Log.e(__CLASSNAME__, getMethodName() + index + ":" + getQuality(profile.quality) + " - w: "
				+ profile.videoFrameWidth + ", h: " + profile.videoFrameWidth);

		mCamcorderProfile = profile;


		// super.setPreviewSize(index, width, height);
		Log.e(__CLASSNAME__, getMethodName() + "[START]" + mSurfaceConfiguring);

		mCamera.stopPreview();

		Camera.Parameters cameraParams = mCamera.getParameters();
		boolean portrait = isPortrait();

		Camera.Size previewSize = mPreviewSizeList.get(index);
		Camera.Size pictureSize = determinePictureSize(previewSize);
		if (DEBUGGING) {
			Log.e(LOG_TAG, "Requested Preview Size - w: " + previewSize.width + ", h: " + previewSize.height);
		}
		mPreviewSize = previewSize;
		mPictureSize = pictureSize;
		// 레이아웃사이즈변경확인불필요. 왜? 꽉차니까.
		// boolean layoutChanged = adjustSurfaceLayoutSize(previewSize, portrait, width, height);
		// if (layoutChanged) {
		// _Log.e(__CLASSNAME__, getMethodName() + "[CHANGE]" + layoutChanged);
		// mSurfaceConfiguring = true;
		// return;
		// }
		adjustSurfaceLayoutSize(previewSize, portrait, width, height);

		configureCameraParameters(cameraParams, portrait);
		try {
			mCamera.startPreview();
		} catch (Exception e) {
			Toast.makeText(mContext, "Failed to satart preview: " + Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
		}

		Log.e(__CLASSNAME__, getMethodName() + "[END]" + mSurfaceConfiguring);

		mSurfaceConfiguring = false;

	}

	@Override
	protected Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
		Log.e(__CLASSNAME__, getMethodName() + "- w: " + reqWidth + ", h: " + reqHeight);

		Camera.Size ret = null;

		if (mCamcorderProfiles != null) {
			int index = mCamcorderProfiles.indexOf(mCamcorderProfile);
			Log.e(__CLASSNAME__, getMethodName() + index);
			if (index > -1) {
				ret = mPreviewSizeList.get(index);
			}
		}

		if (ret != null) {
			Log.e(__CLASSNAME__, "  w: " + ret.width + ", h: " + ret.height);
		}

		if (ret == null) {
			ret = super.determinePreviewSize(portrait, reqWidth, reqHeight);
		}

		int index = 0;

		try {
			Log.w(__CLASSNAME__, "Listing all supported video sizes");
			int i = 0;
			for (Camera.Size size : mPreviewSizeList) {
				Log.w(__CLASSNAME__, "  w: " + size.width + ", h: " + size.height);
				if (size.equals(ret)) {
					Log.e(__CLASSNAME__, "  w: " + ret.width + ", h: " + ret.height);
					index = i;
				}
				i++;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		CamcorderProfile profile = getCamorderProfile(index);

		Log.e(__CLASSNAME__, getMethodName() + index + ":" + getQuality(profile.quality) + " - w: "
				+ profile.videoFrameWidth + ", h: " + profile.videoFrameHeight);

		mCamcorderProfile = profile;

		Log.e(__CLASSNAME__, getMethodName() + "- w: " + ret.width + ", h: " + ret.height);

		return ret;
	}

	/**
	 * 
	 * <pre>
	 * TODO
	 * </pre>
	 * 
	 * 
	 * @see <a href="http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)">setDisplayOrientation</a>
	 */
	@Override
	protected void configureCameraParameters(Parameters cameraParams, boolean portrait) {
		Log.e(__CLASSNAME__, getMethodName());


		// super.configureCameraParameters(cameraParams, portrait);

		Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		String srotation = "ROTATION_UNKNOWN";
		int rotation = display.getRotation();
		int degrees = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			srotation = "ROTATION_0";
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			srotation = "ROTATION_90";
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			srotation = "ROTATION_180";
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			srotation = "ROTATION_270";
			break;
		}

		try {
			int orientation;
			if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				orientation = (mCameraInfo.orientation + degrees) % 360;
				orientation = (360 - orientation) % 360;  // compensate the mirror
			} else {  // back-facing
				orientation = (mCameraInfo.orientation - degrees + 360) % 360;
			}

			Log.e(__CLASSNAME__, "rotation: " + srotation + ", degree: " + degrees + ", orientation: " + orientation);
			mCamera.setDisplayOrientation(orientation);

			if (DEBUGGING) {
				Log.v(__CLASSNAME__, "Preview Actual Size - w: " + mPreviewSize.width + ", h: "
						+ mPreviewSize.height);
				Log.v(__CLASSNAME__, "Picture Actual Size - w: " + mPictureSize.width + ", h: "
						+ mPictureSize.height);
			}
			cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
			mCamera.setParameters(cameraParams);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@Override
	protected boolean adjustSurfaceLayoutSize(Size previewSize, boolean portrait, int availableWidth,
			int availableHeight) {
		Log.e(__CLASSNAME__, getMethodName());

		super.adjustSurfaceLayoutSize(previewSize, portrait, availableWidth, availableHeight);
		return false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(__CLASSNAME__, getMethodName());


		super.surfaceCreated(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(__CLASSNAME__, getMethodName());


		super.surfaceDestroyed(holder);
	}

	@Override
	public void stop() {
		Log.e(__CLASSNAME__, getMethodName());

		try {
			if (mCamera != null) {
				mCamera.setPreviewCallback(null);
				// mCamera.unlock();
			}
			super.stop();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
