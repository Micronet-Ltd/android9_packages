package lovdream.android.cit;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;

public class TouchTest extends Activity {
	class TouchView extends View {

		private void touch_move(float f, float f1) {
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
			Paint paint = TouchTest.mPaint;
			canvas.drawPath(path1, paint);
			mPath.reset();
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawColor(-1);
			
			Paint pt = new Paint();
			pt.setARGB(255,0,0,0);
			canvas.drawText(getResources().getString(R.string.touchname), 
					20,20, pt);
			
			Bitmap bitmap = mBitmap;
			Paint paint = mBitmapPaint;
			canvas.drawBitmap(bitmap, 0F, 0F, paint);
			Path path = mPath;
			Paint paint1 = TouchTest.mPaint;
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
				break;

			}
			return true;
		}

		private static final float TOUCH_TOLERANCE = 4F;
		private Bitmap mBitmap;
		private Paint mBitmapPaint;
		private Canvas mCanvas;
		private Path mPath;
		private float mX;
		private float mY;

		public TouchView(Context context) {
			super(context);
			int i = TouchTest.screen_X;
			int j = TouchTest.screen_Y;
			android.graphics.Bitmap.Config config = android.graphics.Bitmap.Config.ARGB_8888;
			Bitmap bitmap = Bitmap.createBitmap(i, j, config);
			mBitmap = bitmap;
			Canvas canvas = new Canvas(bitmap);
			mCanvas = canvas;
			Path path = new Path();
			mPath = path;
			Paint paint = new Paint(4);
			mBitmapPaint = paint;
		}
	}

	public TouchTest() {
	}

	private void getScreenMetries() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screen_X = displaymetrics.widthPixels;
		screen_Y = displaymetrics.heightPixels;
	}

	private void setFullscreen() {
		requestWindowFeature(1);
		getWindow().setFlags(1024, 1024);
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setFullscreen();
		getScreenMetries();
		TouchView touchview = new TouchView(this);
		setContentView(touchview);
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
		mPaint.setStrokeWidth(1F);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.lcdmenuquit);
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
