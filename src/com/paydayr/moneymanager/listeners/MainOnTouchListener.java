package com.paydayr.moneymanager.listeners;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.activities.MainActivity;

public class MainOnTouchListener implements OnTouchListener {

	private float downXValue;
	private Activity activity;

	public MainOnTouchListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		// Get the action that was done on this touch event
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// store the X value when the user's finger was pressed down
			downXValue = arg1.getX();
			break;
		}

		case MotionEvent.ACTION_UP: {
			// Get the X value when the user released his/her finger
			float currentX = arg1.getX();

			// going backwards: pushing stuff to the right
			if (downXValue < currentX && (currentX - downXValue > 100)) {

				// Get a reference to the ViewFlipper
				ViewFlipper vf = (ViewFlipper) activity
						.findViewById(R.id.flipper);

				// if( arg0.getId() != R.id.expense_view && arg0.getId() !=
				// R.id.expenses_listView ){
				// Set the animation
				vf.setAnimation(AnimationUtils.loadAnimation(activity,
						R.anim.push_right_in));
				// vf.setAnimation(AnimationUtils.loadAnimation(activity,
				// R.anim.fadein));
				// Flip!
				vf.showPrevious();
				if (MainActivity.LAST_ABA == 0) {
					MainActivity.LAST_ABA = 2;
				} else {
					MainActivity.LAST_ABA--;
				}
			}

			// going forwards: pushing stuff to the left
			if (downXValue > currentX && (downXValue - currentX > 100)) {
				// Get a reference to the ViewFlipper
				ViewFlipper vf = (ViewFlipper) activity
						.findViewById(R.id.flipper);

				// Set the animation
				vf.setInAnimation(AnimationUtils.loadAnimation(activity,
						R.anim.push_left_in));
				// Flip!
				vf.showNext();
				if (MainActivity.LAST_ABA == 2) {
					MainActivity.LAST_ABA = 0;
				} else {
					MainActivity.LAST_ABA++;
				}
			}
			break;
		}
		}

		// if you return false, these actions will not be recorded
		return true;
	}

}
