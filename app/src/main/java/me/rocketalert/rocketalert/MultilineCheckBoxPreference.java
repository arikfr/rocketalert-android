package me.rocketalert.rocketalert;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MultilineCheckBoxPreference extends CheckBoxPreference {
    public MultilineCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MultilineCheckBoxPreference(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public MultilineCheckBoxPreference(Context ctx) {
        super(ctx);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView textView = (TextView) view.findViewById(android.R.id.title);
        if (textView != null) {
            textView.setSingleLine(false);
        }
    }
}
