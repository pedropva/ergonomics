/**
 * This is a basic example to show how to use the virtual device.
 * This sample will create a new thread to generate dummy frame, and read by OpenNI API.
 *
 * http://viml.nchc.org.tw/home/
 */

// STL Header
#include <iostream>

// OpenNI Header
#include <OpenNI.h>

// XnLib in OpenNI Source Code, use for threading
#include ".\XnLib.h"

// Virtual Device Header
#include "..\..\VirtualDevice\VirtualDevice.h"

// NiTE Header
#include ".\NiTE.h"

#include ".\VirtualDeviceHelper.h"

// namespace
//using namespace cv;
using namespace std;
using namespace openni;
// global object
bool bRunning = true;

// The function to generate dummy frame
XN_THREAD_PROC GenerateDummyFrame( XN_THREAD_PARAM pThreadParam )
{
	VideoStream* pVStream = (VideoStream*)pThreadParam;

	while( bRunning )
	{
		if( pVStream->isValid() )
		{
			// get a frame form virtual video stream
			OniFrame* pFrame = NULL;
			if( pVStream->invoke( GET_VIRTUAL_STREAM_IMAGE, pFrame ) == openni::STATUS_OK )
			{
				// type casting
				DepthPixel* pVirData = reinterpret_cast<DepthPixel*>( pFrame->data );

				// Fill dummy data
				for( int y = 0; y < pFrame->height; ++ y )
				{
					for( int x = 0; x < pFrame->width; ++ x )
					{
						int idx = x + y * pFrame->width;
						pVirData[idx] = 100;
					}
				}

				// write data to form virtual video stream
				pVStream->invoke( SET_VIRTUAL_STREAM_IMAGE, pFrame );

			}
		}

		// sleep
		xnOSSleep(33);
	}

	XN_THREAD_PROC_RETURN(XN_STATUS_OK);
}

int main( int, char** )
{
	#pragma region OpenNI initialize
	// Initial OpenNI
	if( OpenNI::initialize() != STATUS_OK )
	{
		cerr << "OpenNI Initial Error: " << OpenNI::getExtendedError() << endl;
		return -1;
	}

	// Open Virtual Device
	Device	devVirDevice;
	if( devVirDevice.open( "\\OpenNI2\\VirtualDevice\\TEST" ) != STATUS_OK )
	{
		cerr << "Can't create virtual device: " << OpenNI::getExtendedError() << endl;
		return -1;
	}

	// create virtual color video stream
	VideoStream vsVirDepth;
	if( vsVirDepth.create( devVirDevice, SENSOR_DEPTH ) == STATUS_OK )
	{
		VideoMode mMode;
		mMode.setFps( 30 );
		mMode.setResolution( 320, 240 );
		mMode.setPixelFormat( PIXEL_FORMAT_DEPTH_1_MM );
		vsVirDepth.setVideoMode( mMode );
	}
	else
	{
		cerr << "Can't create depth stream on device: " << OpenNI::getExtendedError() << endl;
		return -1;
	}
	#pragma endregion

	#pragma region main loop
	// start data generate
	vsVirDepth.start();

	// create a new thread to generate dummy data
	XN_THREAD_HANDLE mThreadHandle;
	xnOSCreateThread( GenerateDummyFrame, &vsVirDepth, &mThreadHandle );

	// Initial NiTE
	if (nite::NiTE::initialize() != STATUS_OK)
	{
		cerr << "NiTE initial error" << endl;
		return -1;
	}

	// create user tracker
	nite::UserTracker mUserTracker;
	if (mUserTracker.create(&devVirDevice) != STATUS_OK)
	{
		cerr << "Can't create user tracker: " << mUserTracker.create(&devVirDevice) << endl;
		return -1;
	}

	// create OpenCV Window
	//cv::namedWindow( "User Image",  CV_WINDOW_AUTOSIZE );

	// start
	while (true)
	{
		// get user frame
		nite::UserTrackerFrameRef	mUserFrame;

		if (mUserTracker.readFrame(&mUserFrame) == nite::STATUS_OK)
		{
			// get depth data and convert to OpenCV format
			openni::VideoFrameRef vfDepthFrame = mUserFrame.getDepthFrame();
			//const cv::Mat mImageDepth( vfDepthFrame.getHeight(), vfDepthFrame.getWidth(), CV_16UC1, const_cast<void*>( vfDepthFrame.getData() ) );
			// re-map depth data [0,Max] to [0,255]
			//cv::Mat mScaledDepth;
			//mImageDepth.convertTo( mScaledDepth, CV_8U, 255.0 / 10000 );

			// convert gray-scale to color
			//cv::Mat mImageBGR;
			//cv::cvtColor( mScaledDepth, mImageBGR, CV_GRAY2BGR );
			// get users data
			const nite::Array<nite::UserData>& aUsers = mUserFrame.getUsers();

			for (int i = 0; i < aUsers.getSize(); ++i)
			{
				const nite::UserData& rUser = aUsers[i];
				// check user status
				if (rUser.isNew())
				{
					cout << "New User [" << rUser.getId() << "] found." << endl;
					// 5a. start tracking for new user
					mUserTracker.startSkeletonTracking(rUser.getId());
				}
				else if (rUser.isLost())
				{
					cout << "User [" << rUser.getId() << "] lost." << endl;
				}

				if (rUser.isVisible())
				{
					// get user skeleton
					const nite::Skeleton& rSkeleton = rUser.getSkeleton();
					if (rSkeleton.getState() == nite::SKELETON_TRACKED)
					{
						// build joints array
						nite::SkeletonJoint aJoints[15];
						aJoints[0] = rSkeleton.getJoint(nite::JOINT_HEAD);
						aJoints[1] = rSkeleton.getJoint(nite::JOINT_NECK);
						aJoints[2] = rSkeleton.getJoint(nite::JOINT_LEFT_SHOULDER);
						aJoints[3] = rSkeleton.getJoint(nite::JOINT_RIGHT_SHOULDER);
						aJoints[4] = rSkeleton.getJoint(nite::JOINT_LEFT_ELBOW);
						aJoints[5] = rSkeleton.getJoint(nite::JOINT_RIGHT_ELBOW);
						aJoints[6] = rSkeleton.getJoint(nite::JOINT_LEFT_HAND);
						aJoints[7] = rSkeleton.getJoint(nite::JOINT_RIGHT_HAND);
						aJoints[8] = rSkeleton.getJoint(nite::JOINT_TORSO);
						aJoints[9] = rSkeleton.getJoint(nite::JOINT_LEFT_HIP);
						aJoints[10] = rSkeleton.getJoint(nite::JOINT_RIGHT_HIP);
						aJoints[11] = rSkeleton.getJoint(nite::JOINT_LEFT_KNEE);
						aJoints[12] = rSkeleton.getJoint(nite::JOINT_RIGHT_KNEE);
						aJoints[13] = rSkeleton.getJoint(nite::JOINT_LEFT_FOOT);
						aJoints[14] = rSkeleton.getJoint(nite::JOINT_RIGHT_FOOT);
						//do whatever you want to do with skeleton
					}
				}
			}

			// show image
			//cv::imshow( "User Image", mImageBGR );

			mUserFrame.release();
		}
		else
		{
			cerr << "Can't get user frame" << endl;
		}

		// check keyboard
		//if( cv::waitKey( 1 ) == 'q' )
		//break;
	}

	// stop
	mUserTracker.destroy();
	nite::NiTE::shutdown();
	/*
		// use for-loop to read 100 frames
	for( int i = 0; i < 100; ++ i )
	{
		VideoFrameRef mFrame;
		if( vsVirDepth.readFrame( &mFrame ) == STATUS_OK )
		{
			const DepthPixel* pData = reinterpret_cast<const DepthPixel*>( mFrame.getData() );
			cout << pData[ mFrame.getWidth() / 2 + ( mFrame.getHeight() / 2 ) * mFrame.getWidth() ] << endl;
		}
	}
	*/
	

	// stop data generate
	bRunning = false;
	vsVirDepth.stop();

	// close device
	vsVirDepth.destroy();
	devVirDevice.close();

	// shutdown
	OpenNI::shutdown();

	#pragma endregion

	return 0;
}
