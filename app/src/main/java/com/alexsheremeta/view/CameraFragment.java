package com.alexsheremeta.view;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.alexsheremeta.encoder.MediaAudioEncoder;
import com.alexsheremeta.encoder.MediaEncoder;
import com.alexsheremeta.encoder.MediaMuxerWrapper;
import com.alexsheremeta.encoder.MediaVideoEncoder;
import com.alexsheremeta.tasks.OnImageTakenListener;

import static com.alexsheremeta.CameraModule.CAMERA_PHOTO;
import static com.alexsheremeta.CameraModule.CAMERA_VIDEO;

public class CameraFragment extends Fragment {

    private static final boolean DEBUG = false;    // TODO set false on release
    private static final String TAG = "CameraFragment";

    private int currentCameraMode = CAMERA_PHOTO;
    /**
     * for camera preview display
     */
    private CameraGLView mCameraView;
    /**
     * for scale mode display
     */
    /**
     * button for start/stop recording
     */
    private ImageButton mRecordButton;
    private ImageButton mFlashButton;

    private ImageButton mPhotoButton;
    private ImageButton mVideoButton;
    /**
     * muxer for audio/video recording
     */
    private MediaMuxerWrapper mMuxer;

    private String videoFileUrl;

    public CameraFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ImageButton switchCamera = (ImageButton) rootView.findViewById(R.id.butSwitch);
        mFlashButton = (ImageButton) rootView.findViewById(R.id.butFlash);
        mPhotoButton = (ImageButton) rootView.findViewById(R.id.butPhoto);
        mVideoButton = (ImageButton) rootView.findViewById(R.id.butVideo);

        mFlashButton = (ImageButton) rootView.findViewById(R.id.butFlash);
        mCameraView = (CameraGLView) rootView.findViewById(R.id.cameraView);
        mCameraView.setOnClickListener(mOnClickListener);
        mRecordButton = (ImageButton) rootView.findViewById(R.id.record_button);
        mRecordButton.setOnClickListener(mOnClickListener);
        switchCamera.setOnClickListener(mOnClickListener);
        mFlashButton.setOnClickListener(mOnClickListener);

        mPhotoButton.setOnClickListener(mOnClickListener);
        mVideoButton.setOnClickListener(mOnClickListener);

        //setCameraMoode(true);
        hideTopUI();
        return rootView;
    }

    private void hideTopUI() {
        mPhotoButton.setVisibility(View.GONE);
        mVideoButton.setVisibility(View.GONE);
    }

    private void setCameraMode(boolean isPhoto) {
        currentCameraMode = isPhoto ? CAMERA_PHOTO : CAMERA_VIDEO;
        mPhotoButton.setActivated(isPhoto);
        mVideoButton.setActivated(!isPhoto);
    }

    private void switchFlashMode() {
        int mode = mCameraView.switchFlashMode();
        switch (mode) {
            case CameraGLView.FLASH_AUTO:
                mFlashButton.setImageResource(R.drawable.flashlight_auto_but);
                break;
            case CameraGLView.FLASH_DISABLED:
                mFlashButton.setImageResource(R.drawable.flashlight_off_but);
                break;
            case CameraGLView.FLASH_ENABLED:
                mFlashButton.setImageResource(R.drawable.flashlight_on_but);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        stopRecording();
        mCameraView.onPause();
        super.onPause();
    }

    /**
     * method when touch record button
     */
    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.cameraView:
                    final int scale_mode = (mCameraView.getScaleMode() + 1) % 4;
                    mCameraView.setScaleMode(scale_mode);
                    break;
                case R.id.record_button:
                    if (currentCameraMode == CAMERA_VIDEO) {
                        if (mMuxer == null)
                            startRecording();
                        else
                            stopRecording();
                    } else {
                        takePhoto();
                    }
                    break;

                case R.id.butSwitch:
                    mCameraView.switchCamera();
                    break;

                case R.id.butFlash:
                    switchFlashMode();
                    break;

                case R.id.butPhoto:
                    setCameraMode(true);
                    break;

                case R.id.butVideo:
                    setCameraMode(false);
                    break;
            }
        }
    };

    public void setCurrentCameraMode(int currentCameraMode) {
        this.currentCameraMode = currentCameraMode;
    }

    private void takePhoto() {
        mCameraView.takePhoto(new OnImageTakenListener() {
            @Override
            public void onPhotoTaken(String url) {
                Bundle bundle = new Bundle();
                bundle.putString(CameraActivity.IMAGE_URL, url);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    /**
     * start resorcing
     * This is a sample project and call this on UI thread to avoid being complicated
     * but basically this should be called on private thread because prepareing
     * of encoder is heavy work
     */
    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        try {
            setRecordButtonState(true);
            mMuxer = new MediaMuxerWrapper(".mp4");

            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

            videoFileUrl = mMuxer.getOutputPath();

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            setRecordButtonState(false);
            Log.e(TAG, "startCapture:", e);
        }
    }

    private void setRecordButtonState(boolean active) {
        mRecordButton.setImageResource(active ? R.drawable.record_button_active : R.drawable.record_button);
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        setRecordButtonState(false);    // return to default color
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }

        setVideoResult();
    }

    private void setVideoResult() {
        if (videoFileUrl != null) {
            Bundle bundle = new Bundle();
            bundle.putString(CameraActivity.VIDEO_URL, videoFileUrl);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };
}
