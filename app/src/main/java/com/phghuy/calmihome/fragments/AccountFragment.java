package com.phghuy.calmihome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.phghuy.calmihome.PaymentMethodActivity;
import com.phghuy.calmihome.R;
import com.phghuy.calmihome.RenewPassword;
import com.phghuy.calmihome.SignInActivity;

public class AccountFragment extends Fragment {

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        LinearLayout payMethod= view.findViewById(R.id.toPaymentMethod);
        LinearLayout rePass= view.findViewById(R.id.toPass);
        ImageView logOutBtn = view.findViewById(R.id.logOut);

        logOutBtn.setOnClickListener(v->startActivity(new Intent(getActivity(), SignInActivity.class)));
        rePass.setOnClickListener(v -> startActivity(new Intent(getActivity(), RenewPassword.class)));
        payMethod.setOnClickListener(v -> startActivity(new Intent(getActivity(), PaymentMethodActivity.class)));
        return view;
    }
}
