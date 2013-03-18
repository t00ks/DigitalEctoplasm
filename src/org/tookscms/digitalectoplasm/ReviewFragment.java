package org.tookscms.digitalectoplasm;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReviewFragment extends Fragment {
	
	public ReviewFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
				
		new DataLoader().loadBulletins(rootView, "ReviewBulletins", getActivity());

		return rootView;
	}

}
