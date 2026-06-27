package com.android.launcher3.standalone;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class StandaloneSmokeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setGravity(Gravity.CENTER);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(28), dp(28), dp(28));
        root.setBackgroundColor(getColor(R.color.smoke_background));

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_launcher_home);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(88), dp(88));
        iconParams.bottomMargin = dp(28);
        root.addView(icon, iconParams);

        TextView title = text(R.string.standalone_smoke_title, 24, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, widthWrapContent());

        TextView body = text(R.string.standalone_smoke_body, 15, Typeface.NORMAL);
        body.setGravity(Gravity.CENTER);
        body.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams bodyParams = widthWrapContent();
        bodyParams.topMargin = dp(12);
        root.addView(body, bodyParams);

        setContentView(root);
    }

    private TextView text(int resId, float textSizeSp, int typefaceStyle) {
        TextView view = new TextView(this);
        view.setText(resId);
        view.setTextColor(getColor(R.color.smoke_text));
        view.setTextSize(textSizeSp);
        view.setTypeface(Typeface.DEFAULT, typefaceStyle);
        view.setMaxWidth(dp(420));
        return view;
    }

    private LinearLayout.LayoutParams widthWrapContent() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
