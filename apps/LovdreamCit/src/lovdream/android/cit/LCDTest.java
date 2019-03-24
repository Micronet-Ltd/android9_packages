package lovdream.android.cit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import java.util.Timer;
import java.util.TimerTask;

public class LCDTest extends Activity {
	LCDView lcdView;
	Timer timer;

	class LCDView extends View {

		public LCDView(Context context) {

			super(context);
			m_nCurrentPage = 0;
		}

		private void OnKeypressRight() {
			if (m_nCurrentPage == 4) {
				finish();
			} else {
				m_nCurrentPage++;
				invalidate();
			}
		}

		private void onKeypressLeft() {
			if (m_nCurrentPage == 0) {
				finish();
			} else {
				m_nCurrentPage--;
				invalidate();
			}
		}

		private void palette_Display(Canvas canvas, int i) {
			switch (i) {
			case C_RED:
				canvas.drawColor(0xffff0000);
				break;
			case C_GREEN:
				canvas.drawColor(0xff00ff00);
				break;
			case C_BLUE:
				canvas.drawColor(0xff0000ff);
				break;
			case C_BLACK:
				canvas.drawColor(0xff000000);
				break;
			default:
				canvas.drawColor(-1);
				return;
			}
		}

		protected void onDraw(Canvas canvas) {
			if (m_nCurrentPage == 5) {
				timer.cancel();
				finish();
			}
			palette_Display(canvas, m_nCurrentPage);
		}

		private static final int C_BLACK = 3;
		private static final int C_BLUE = 2;
		private static final int C_GREEN = 1;
		private static final int C_RED = 0;
		private static final int C_WHITE = 4;
		private int m_nCurrentPage;

	}

	class timetask extends TimerTask {

		public void run() {
			try {
				Thread.sleep(2000L);
				Log.v("timetask", "run()");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			lcdView.postInvalidate();
			lcdView.m_nCurrentPage++;
		}

	}

	public LCDTest() {

		timer = new Timer();
		;
	}

	private void setFullscreen() {
		requestWindowFeature(1);
		getWindow().setFlags(1024, 1024);
	}

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setFullscreen();
		lcdView = new LCDView(this);
		setContentView(lcdView);
		timetask timetask1 = new timetask();
		timer.schedule(timetask1, 0, 1000L);
	}

	public boolean onKeyUp(int i, KeyEvent keyevent) {
		/*
		 * i; JVM INSTR lookupswitch 3: default 36 // 4: 63 // 21: 43 // 22: 53;
		 * goto _L1 _L2 _L3 _L4
		 */
		// _L1:
		//
		// _L3:
		switch (keyevent.getAction()) {
		case KeyEvent.ACTION_DOWN:
			lcdView.onKeypressLeft();
			break;
		// _L4:
		case KeyEvent.ACTION_UP:
			lcdView.OnKeypressRight();
			break;

		// finish();
		}
		return super.onKeyUp(i, keyevent);
		// if(true) goto _L1; else goto _L5
		// _L5:
	}

}
