package com.techventus.timefly;


import com.actionbarsherlock.app.SherlockFragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.techventus.timefly.R;


/**
 * @author Joseph Malone
 */
public class CreateNoteFragment extends SherlockFragment
{



	// Container Activity must implement this interface
	public interface NoteCreateListener
	{
		public void onNoteFinished(String note);

	}

	private NoteCreateListener mCallback;
	private Button finishButton;
	private EditText notesText;


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try
		{
			mCallback = (NoteCreateListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement TimerListener");
		}
	}


	/**
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if (container == null)
		{

			return null;

		}
		setRetainInstance(true);
		return (RelativeLayout) inflater.inflate(R.layout.fragment_create_note, container, false);
	}

	@Override
	public void onViewCreated(View page, Bundle savedInstanceState)
	{
		super.onViewCreated(page, savedInstanceState);

		notesText = (EditText) page.findViewById(R.id.enter_note);
		finishButton = (Button) page.findViewById(R.id.finish);
		finishButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				mCallback.onNoteFinished(notesText.getText().toString());
			}

		});
	}

}
