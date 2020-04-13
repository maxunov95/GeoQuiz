package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String IS_CHEATER = "IS_CHEATER";
    private static final String CHEAT_COUNT = "CHEAT_COUNT";

    private boolean mIsAnswerShown;
    private boolean mAnswerIsTrue;

    private int mCheatCount = 0;

    private Button mShowAnswerButton;
    private TextView mApiLevelInfo;
    private TextView mCheatCountText;
    private TextView mQuestionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            mIsAnswerShown = savedInstanceState.getBoolean(IS_CHEATER, false);
            if (mIsAnswerShown) {
                setAnswerShownResult();
            }
            mIsAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
            mCheatCount = savedInstanceState.getInt(CHEAT_COUNT, 0);
        } else {
            mIsAnswerShown = getIntent().getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
            mCheatCount = getIntent().getIntExtra(CHEAT_COUNT, 0);
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mQuestionText = findViewById(R.id.question_cheat_text);

        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIsAnswerShown = true;
                setAnswerShownResult();

                String cheatsUsed = getString(R.string.cheat_count_text) + " " + ++mCheatCount;
                mCheatCountText.setText(cheatsUsed);

                updateView();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    });
                    anim.start();

                    updateView();
                }
            }
        });

        updateView();

        mCheatCountText = findViewById(R.id.cheat_count);
        String cheatsUsed = getString(R.string.cheat_count_text) + " " + mCheatCount;
        mCheatCountText.setText(cheatsUsed);

        mApiLevelInfo = findViewById(R.id.api_level_text_view);
        String apiLevel = getString(R.string.api_level) + " " + Build.VERSION.SDK_INT;
        mApiLevelInfo.setText(apiLevel);

        if (mCheatCount >= 3) {
            mShowAnswerButton.setClickable(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_CHEATER, mIsAnswerShown);
        savedInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
        savedInstanceState.putInt(CHEAT_COUNT, mCheatCount);
        super.onSaveInstanceState(savedInstanceState);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean isAnswerShown, int cheatCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        intent.putExtra(CHEAT_COUNT, cheatCount);
        return intent;
    }

    private void setAnswerShownResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
        setResult(RESULT_OK, data);
    }

    private void updateView() {

        if (mIsAnswerShown) {
            if (mAnswerIsTrue) {
                mQuestionText.setText(R.string.true_button);
            } else {
                mQuestionText.setText(R.string.false_button);
            }

            mShowAnswerButton.setVisibility(View.INVISIBLE);
            mShowAnswerButton.setClickable(false);
        }
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
}
