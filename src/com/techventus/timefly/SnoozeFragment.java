package com.techventus.timefly;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockDialogFragment;

public class SnoozeFragment extends SherlockDialogFragment
{
//
//	public interface YesNoListener {
//		void onYes();
//
//		void onNo();
//	}
//
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		if (!(activity instanceof YesNoListener)) {
//			throw new ClassCastException(activity.toString() + " must implement YesNoListener");
//		}
//	}
////
////	View.OnClickListener endClick ;
////	View.OnClickListener fiveMinClick ;
////	View.OnClickListener tenMinClick;
////	View.OnClickListener endClick ;
//
//	public SnoozeFragment(View.OnClickListener endClick, View.OnClickListener fiveMinClick, View.OnClickListener tenMinClick,
//			View.OnClickListener halfHourClick)
//	{
//		    endClick
//	}
//
//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//
//		final Dialog dialog = new Dialog(getActivity());
//		dialog.setContentView(R.layout.dialog_snooze);
//		dialog.setTitle(getResources().getString(R.string.snooze));
//
//		Button end = (Button)dialog.findViewById(R.id.end);
//		Button five_mins = (Button)dialog.findViewById(R.id.five_mins);
//		Button ten_mins = (Button)dialog.findViewById(R.id.ten_mins);
//		Button half_hour = (Button)dialog.findViewById(R.id.half_hour);
//
//		end.setOnClickListener(end_click);
//		five_mins.setOnClickListener(snoozeTimeClick(5*60*1000L,dialog));
//		ten_mins.setOnClickListener(snoozeTimeClick(10*60*1000L,dialog));
//		half_hour.setOnClickListener(snoozeTimeClick(30*60*1000L,dialog));
//		return
//		dialog.show();
//
//
//
//
//		return new AlertDialog.Builder(getActivity())
//				.setTitle(R.string.snooze)
//
//				.setMessage(R.string.dialog_my_message)
//				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						((YesNoListener) getActivity()).onYes();
//					}
//				})
//				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						((YesNoListener) getActivity()).onNo();
//					}
//				})
//				.create();
//	}
}