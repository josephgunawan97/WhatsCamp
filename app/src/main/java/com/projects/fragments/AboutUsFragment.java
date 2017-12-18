package com.projects.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.config.Config;
import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;
import com.libraries.utilities.MGUtilities;
import com.projects.whatscamp.R;

public class AboutUsFragment extends Fragment implements OnClickListener {
	
	private View viewInflate;
	
	public AboutUsFragment() { }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewInflate = inflater.inflate(R.layout.view_about_us, null);
		return viewInflate;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		Button btnContactUs = (Button) viewInflate.findViewById(R.id.btnContactUs);
		btnContactUs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email();
			}
		});

		btnContactUs.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showToolTip(v,"Contact us when you have trouble");
				return true;
			}
		});
	}

	@Override
	public void onClick(View v) { }
	
	private void email() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ Config.ABOUT_US_EMAIL } );
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				MGUtilities.getStringFromResource(getActivity(), R.string.email_subject_company) );
		
		emailIntent.putExtra(Intent.EXTRA_TEXT,
				MGUtilities.getStringFromResource(getActivity(), R.string.email_body_company) );
		emailIntent.setType("message/rfc822");
		getActivity().startActivity(Intent.createChooser(emailIntent,
				MGUtilities.getStringFromResource(getActivity(), R.string.choose_email_client)) );
	}

	private void showToolTip(View v, String text)
	{
		ToolTip toolTip = new ToolTip.Builder().withText(text).withTextSize(50). withPadding(10,10,10,10)
				.build();

		ToolTipView toolTipView = new ToolTipView.Builder(getActivity())
				.withAnchor(v)
				.withToolTip(toolTip)
				.withGravity(Gravity.TOP)

				.build();
		toolTipView.show();
	}

}
