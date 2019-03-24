package lovdream.android.cit;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.os.SystemProperties;
public class TouchTest3 extends Activity {

	private StatusBarManager mSbManager;

	class TouchView extends View {

		private int mXorY = 0;
		private int mXCount = 0;
		private int mYCount = 0;
		private int mBCount = 0;
		private int[] mWidth = new int[5];
		private int[] mHeight = new int[8];
		private int[] mBlength = new int[8];
		private final static int XCOUNT = 5;
		private final static int YCOUNT = 5;
		private final static int BCOUNT = 2;
		private int mNeedToReSet = 0;
		private int mTestWidth =100;
		
		private void touch_move(float f, float f1) {
//			Log.d("CJ", "mWidth.length= "+mWidth.length);
//			Log.d("CJ", "screen_X= "+screen_X);
			
			if (mXorY == 1) {
				for (int i = 0; i < mWidth.length; i++) {
					if ((float)screen_X/(float)(mWidth.length-1)*(float)i-(float)56<=f 
							&& f<=(float)screen_X/(float)(mWidth.length-1)*(float)i+(float)56
							&& f1>=mTopCheck && f1<=mBottomCheck) {
						mWidth[i] = 1;
						break;
//						Log.d("CJ", "mWidth[i]="+mWidth[i]+"i ="+i);
					}
				}
				if (f1<=mTopCheck || f1>=mBottomCheck) {
					mNeedToReSet ++;
				}
			}else if (mXorY == 2) {
				for (int i = 0; i < mHeight.length; i++) {
					if ((float)screen_Y/(float)(mHeight.length-1)*(float)i-(float)56<=f1 
							&& f1<= (float)screen_X/((float)mWidth.length-1)*(float)i+(float)56
							&& f>=mLeftCheck && f<=mRightCheck) {
						mHeight[i] = 1;
						Log.d("HH", "------>"+i+"----"+mHeight[i]);
						break;
					}
				}
				if (f<=mLeftCheck || f>=mRightCheck) {
					mNeedToReSet ++;
				}
			}else if (mXorY == 3) {
				float rX = 0;
				float distance = 0;
				if(mBCount==0){
//				Log.d("SP", "------>------>>>rX"+rX);
				rX=screen_X*f1/screen_Y;
			    distance=Math.abs(rX-f);
//				Log.d("SP", "------>------>>>"+distance);
				for (int i = 0; i < mBlength.length; i++) {
					if (distance<40 && (float)screen_Y/(float)mBlength.length*(float)i<=f1
							&&(float)screen_Y/(float)mBlength.length*(float)(i+1)>f1){
						mBlength[i] = 1;
//						Log.d("GSP", "------>"+i+"----"+mBlength[i]);
						break;
					}
				}
				}else if(mBCount==1){
					rX=screen_X-screen_X*f1/screen_Y;
					distance=Math.abs(rX-f);
					for (int i = 0; i < mBlength.length; i++) {
						if (distance<60 && (float)screen_Y/(float)mBlength.length*(float)i<=f1
								&&(float)screen_Y/(float)mBlength.length*(float)(i+1)>f1){
							mBlength[i] = 1;
//							Log.d("GSP", "------>"+i+"----"+mBlength[i]);
							break;
						}
					}	
				}
				if(distance>60){
					mNeedToReSet ++;
				}
			
			}
			
			float f2 = mX;
			float f3 = Math.abs(f - f2);
			float f4 = mY;
			float f5 = Math.abs(f1 - f4);
			if (f3 >= 4F || f5 >= 4F) {
				Path path = mPath;
				float f6 = mX;
				float f7 = mY;
				float f8 = (mX + f) / 2F;
				float f9 = (mY + f1) / 2F;
				
				path.quadTo(f6, f7, f8, f9);
				mX = f;
				mY = f1;
			}
		}

		private void touch_start(float f, float f1) {
			mPath.reset();
			mPath.moveTo(f, f1);
			mX = f;
			mY = f1;
		}

		private void touch_up() {
			Path path = mPath;
			float f = mX;
			float f1 = mY;
			path.lineTo(f, f1);
			Canvas canvas = mCanvas;
			Path path1 = mPath;
			Paint paint = TouchTest3.mPaint;
			canvas.drawPath(path1, paint);
			mPath.reset();
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			
			Paint pt = new Paint();
			pt.setARGB(255,0,0,0);
			canvas.drawText(getResources().getString(R.string.touchname), 
					20,20, pt);
			
			Bitmap bitmap = mBitmap;
			Paint paint = mBitmapPaint;
			canvas.drawBitmap(bitmap, 0F, 0F, paint);
			Path path = mPath;
			Paint paint1 = TouchTest3.mPaint;
			canvas.drawPath(path, paint1);
		}

		protected void onSizeChanged(int i, int j, int k, int l) {
			super.onSizeChanged(i, j, k, l);
		}

		public boolean onTouchEvent(MotionEvent motionevent) {
			float f;
			float f1;
			f = motionevent.getX();
			f1 = motionevent.getY();
			Log.d("CJ", "x--f="+f);
			Log.d("CJ", "y--f1="+f1);
			int action = motionevent.getAction();
			switch (action) {
			// return true;
			case MotionEvent.ACTION_DOWN:
				touch_start(f, f1);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(f, f1);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();

//				Log.d("CJCJ", "mXorY="+mXorY);
				if (mXorY == 1) {
					boolean done = true;
					for (int i = 0; i < mWidth.length; i++) {
						if (mWidth[i] == 0) {
							done = false;
							break;
						}
					}
					if (!done || mNeedToReSet>0) {
						reDrawLine();
						break;
					}
					mXCount++;
				} else if (mXorY == 2) {
					boolean done = true;
					for (int i = 0; i < mHeight.length; i++) {
						if (mHeight[i] == 0) {
							done = false;
							break;
						}
					}
					if (!done || mNeedToReSet>0) {
						reDrawLine();
						break;
					}
					mYCount++;
				}else if (mXorY == 3) {
					boolean done = true;
					for (int i = 0; i < mBlength.length; i++) {
						if (mBlength[i] == 0) {
							done = false;
							Log.d("GSP", "false------>"+i+"----"+mBlength[i]);
							break;
						}
					}
					if (!done || mNeedToReSet>0) {
						reDrawLine();
						break;
					}
					if(done){
					mBCount++;
					}
				}
				
				drawNextLine();
				break;

			}
			return true;
		}

		private static final float TOUCH_TOLERANCE = 4F;
		private Bitmap mBitmap;
		private Paint mBitmapPaint;
		private Canvas mCanvas;
		private Path mPath;
		private Path mTestPath;
		private Paint mTestPaint;
		private float mX;
		private float mY;

		public TouchView(Context context) {
			super(context);
			int i = TouchTest3.screen_X;
			int j = TouchTest3.screen_Y;
			android.graphics.Bitmap.Config config = android.graphics.Bitmap.Config.ARGB_8888;
			Bitmap bitmap = Bitmap.createBitmap(i, j, config);
			mTestPath = new Path();
			mBitmap = bitmap;
			Canvas canvas = new Canvas(bitmap);
			mCanvas = canvas;
			Path path = new Path();
			mPath = path;
			Paint paint = new Paint(4);
			mBitmapPaint = paint;
			
			mTestPaint = new Paint();
			mTestPaint.setAntiAlias(true);
			mTestPaint.setDither(true);
			mTestPaint.setColor(Color.RED);
			Paint paint3 = mTestPaint;
			android.graphics.Paint.Style style = android.graphics.Paint.Style.STROKE;
			paint3.setStyle(style);
			Paint paint1 = mTestPaint;
			android.graphics.Paint.Join join = android.graphics.Paint.Join.ROUND;
			paint1.setStrokeJoin(join);
			Paint paint2 = mTestPaint;
			android.graphics.Paint.Cap cap = android.graphics.Paint.Cap.ROUND;
			paint2.setStrokeCap(cap);
			mTestPaint.setStrokeWidth(1F);
			
			drawNextLine();
		}
		
		private void drawNextLine() {
			if (mXCount < XCOUNT) {
				mXorY = 1;
				for (int i = 0; i < mWidth.length; i++) {
					mWidth[i] = 0;
				}
				drawXLine(mXCount, XCOUNT-1);
			}else if (mYCount < YCOUNT) {
				mXorY = 2;
				for (int i = 0; i < mHeight.length; i++) {
					mHeight[i] = 0;
				}
				drawYLine(mYCount, YCOUNT-1);
			}else if (mBCount < BCOUNT) {
				mXorY = 3;
				for (int i = 0; i < mBlength.length; i++) {
					mBlength[i] = 0;
				}
				drawBLine(mBCount, BCOUNT-mBCount);
			}
			else {
				Bundle bundle = new Bundle();
				Intent intent = new Intent();
				bundle.putInt("test_result", 1);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
		
		private void reDrawLine() {
			if (mXorY == 1) {
				for (int i = 0; i < mWidth.length; i++) {
					mWidth[i] = 0;
				}
				drawXLine(mXCount, XCOUNT-1);
			}else if (mXorY == 2) {
				for (int i = 0; i < mHeight.length; i++) {
					mHeight[i] = 0;
				}
				drawYLine(mYCount, YCOUNT-1);
			}else if (mXorY == 3) {
				for (int i = 0; i < mBlength.length; i++) {
					mBlength[i] = 0;
				}
				drawBLine(mBCount, BCOUNT-1);
			}
			mNeedToReSet = 0;
		}
		
		private float mLeftCheck;
		private float mRightCheck;
		private float mTopCheck;
		private float mBottomCheck;
		
		private void drawXLine(int index, int count) {
			mXorY = 1;
			mTestPath.reset();
			mTestPath.moveTo(0, (TouchTest3.screen_Y-mTestWidth)/count*index);
			mTestPath.lineTo(TouchTest3.screen_X, (TouchTest3.screen_Y-mTestWidth)/count*index);
			mTestPath.moveTo(0, (TouchTest3.screen_Y-mTestWidth)/count*index+mTestWidth);
			mTestPath.lineTo(TouchTest3.screen_X, (TouchTest3.screen_Y-mTestWidth)/count*index+mTestWidth);
			mTopCheck = (TouchTest3.screen_Y-mTestWidth)/count*index;
			mBottomCheck = (TouchTest3.screen_Y-mTestWidth)/count*index+mTestWidth;
			Canvas canvas = mCanvas;
			canvas.drawColor(Color.WHITE);
			Path path = mTestPath;
			Paint paint = mTestPaint;
			paint.setColor(Color.GREEN);
			canvas.drawPath(path, paint);
			invalidate();
		}
		
		private void drawYLine(int index, int count) {
			mXorY = 2;
			mTestPath.reset();
			mTestPath.moveTo((TouchTest3.screen_X-mTestWidth)/count*index, 0);
			mTestPath.lineTo((TouchTest3.screen_X-mTestWidth)/count*index, TouchTest3.screen_Y);
			mTestPath.moveTo((TouchTest3.screen_X-mTestWidth)/count*index+mTestWidth, 0);
			mTestPath.lineTo((TouchTest3.screen_X-mTestWidth)/count*index+mTestWidth, TouchTest3.screen_Y);
			mLeftCheck = (TouchTest3.screen_X-mTestWidth)/count*index;
			mRightCheck = (TouchTest3.screen_X-mTestWidth)/count*index+mTestWidth;
			Canvas canvas = mCanvas;
			canvas.drawColor(Color.WHITE);
			Path path = mTestPath;
			Paint paint = mTestPaint;
			paint.setColor(Color.GREEN);
			canvas.drawPath(path, paint);
			invalidate();
		}
		
		private void drawBLine(int index, int count) {
			mXorY = 3;
			mTestPath.reset();
			if(index==0){
			mTestPath.moveTo(0, mTestWidth);
			mTestPath.lineTo(TouchTest3.screen_X-mTestWidth, TouchTest3.screen_Y);
			mTestPath.moveTo(mTestWidth, 0);
			mTestPath.lineTo(TouchTest3.screen_X, TouchTest3.screen_Y-mTestWidth);
			mLeftCheck = (TouchTest3.screen_X-mTestWidth)/count*index;
			mRightCheck = (TouchTest3.screen_X-mTestWidth)/count*index+mTestWidth;
			Canvas canvas = mCanvas;
			canvas.drawColor(Color.WHITE);
			Path path = mTestPath;
			Paint paint = mTestPaint;
			paint.setColor(Color.GREEN);
			canvas.drawPath(path, paint);
			invalidate();
			}else if(index==1){
				mTestPath.moveTo(TouchTest3.screen_X-mTestWidth, 0);
				mTestPath.lineTo(0, TouchTest3.screen_Y-mTestWidth);
				mTestPath.moveTo(TouchTest3.screen_X,mTestWidth);
				mTestPath.lineTo(mTestWidth, TouchTest3.screen_Y);
				mLeftCheck = (TouchTest3.screen_X-mTestWidth)/count*index;
				mRightCheck = (TouchTest3.screen_X-mTestWidth)/count*index+mTestWidth;
				Canvas canvas = mCanvas;
				canvas.drawColor(Color.WHITE);
				Path path = mTestPath;
				Paint paint = mTestPaint;
				paint.setColor(Color.GREEN);
				canvas.drawPath(path, paint);
				invalidate();
				}
		}
		
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.d("CJ", "onBackPressed");
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putInt("test_result", 0);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	public TouchTest3() {
	}

	private void getScreenMetries() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screen_X = displaymetrics.widthPixels;
		screen_Y = displaymetrics.heightPixels;
		Log.d("CJ", "screen_X="+screen_X);
		Log.d("CJ", "screen_Y="+screen_Y);
	}

	private void setFullscreen() {
		requestWindowFeature(1);
		getWindow().setFlags(1024, 1024);
	}

	@Override
	public void onResume(){
		super.onResume();
		mSbManager.disable(StatusBarManager.DISABLE_EXPAND);
	}

	@Override
	public void onPause(){
		super.onPause();
		mSbManager.disable(StatusBarManager.DISABLE_NONE);
	}


	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setFullscreen();
		getScreenMetries();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xffff0000);
		Paint paint = mPaint;
		android.graphics.Paint.Style style = android.graphics.Paint.Style.STROKE;
		paint.setStyle(style);
		Paint paint1 = mPaint;
		android.graphics.Paint.Join join = android.graphics.Paint.Join.ROUND;
		paint1.setStrokeJoin(join);
		Paint paint2 = mPaint;
		android.graphics.Paint.Cap cap = android.graphics.Paint.Cap.ROUND;
		paint2.setStrokeCap(cap);
		mPaint.setStrokeWidth(5F);
		TouchView touchview = new TouchView(this);
		setContentView(touchview);
        
		mSbManager = (StatusBarManager)getSystemService(Context.STATUS_BAR_SERVICE);
        //touchview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED/*SYSTEM_UI_FLAG_IMMERSIVE_GESTURE_ISOLATED*/);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, 1, 0, R.string.lcdmenuquit);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem menuitem) {

		switch (menuitem.getItemId()) {
		case 1:
			finish();
		}

		return true;

	}

	private static final int LCD_MENU_QUIT = 1;
	private static Paint mPaint;
	private static int screen_X;
	private static int screen_Y;

}
