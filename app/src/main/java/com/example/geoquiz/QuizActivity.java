package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String CURRENT_INDEX = "CURRENT_INDEX";
    private static final String LAST_INDEX = "LAST_INDEX";
    private static final String CORRECT_ANSWERS_COUNT = "CORRECT_ANSWERS_COUNT";
    private static final String IS_FIRST_QUESTION = "IS_FIRST_QUESTION";
    private static final String EXTRA_ANSWER_SHOWN = "IS_CHEATER";
    private static final String CHEAT_COUNT = "CHEAT_COUNT";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Toast mToast;
    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;

    private boolean mIsAnswerShown;
    private boolean mIsFirstQuestion = true;

    private int mCurrentIndex = 0;
    private int mLastIndex = 0;
    private int mCorrectAnswersCount = 0;
    private int mCheatCount = 0;


    private final Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d(TAG, "onCreate(Bundle) called");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX, 0);
            mLastIndex = savedInstanceState.getInt(LAST_INDEX, 0);
            mCorrectAnswersCount = savedInstanceState.getInt(CORRECT_ANSWERS_COUNT, 0);
            mIsFirstQuestion = savedInstanceState.getBoolean(IS_FIRST_QUESTION, true);
            mIsAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN, false);
            mCheatCount = savedInstanceState.getInt(CHEAT_COUNT, 0);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(1);
                mIsFirstQuestion = false;
            }
        });

        mQuestionTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                updateQuestion(2);
                mIsFirstQuestion = false;
                return true;
            }
        });


        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity
                        .newIntent(QuizActivity.this,
                                answerIsTrue, mIsAnswerShown, mCheatCount);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        ImageView nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(1);
                mIsFirstQuestion = false;
                mIsAnswerShown = false;
            }
        });

        ImageView prevButton = findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(2);
                mIsFirstQuestion = false;
            }
        });

        updateQuestion(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsAnswerShown = CheatActivity.wasAnswerShown(data);
            if (mIsAnswerShown) mCheatCount++;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(CURRENT_INDEX, mCurrentIndex);
        savedInstanceState.putInt(LAST_INDEX, mLastIndex);
        savedInstanceState.putInt(CORRECT_ANSWERS_COUNT, mCorrectAnswersCount);
        savedInstanceState.putInt(CHEAT_COUNT, mCheatCount);
        savedInstanceState.putBoolean(IS_FIRST_QUESTION, mIsFirstQuestion);
        savedInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(int var) {

        if (var == 1) { // next
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;

            if (mCurrentIndex > mLastIndex) {
                mTrueButton.setClickable(true);
                mFalseButton.setClickable(true);
            }

            if (mCurrentIndex > mLastIndex) mLastIndex = mCurrentIndex;

        } else if (var == 2) { // prev
            mCurrentIndex = (mCurrentIndex + mQuestionBank.length - 1) % mQuestionBank.length;

        } else mCurrentIndex = var;

        try {
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);

        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(TAG, "Array index was out of bounds - " + mCurrentIndex, ex);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {

        mTrueButton.setClickable(false);
        mFalseButton.setClickable(false);

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId;

        if (mIsAnswerShown) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mCorrectAnswersCount++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        makeToast(this, messageResId);

        if (mCurrentIndex == mQuestionBank.length - 1) {
            makeToast(QuizActivity.this, 0);
        }
    }

    private void makeToast(Context context, int text) {

        if (!(mToast == null)) mToast.cancel();

        if (text == 0) {

            double result = Math.round((double) mCorrectAnswersCount / (double) mQuestionBank.length * 100.0);
            String finishedText = result + "% current answers!";

            mToast = Toast.makeText(context, finishedText, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.TOP, 0, 0);
            mToast.show();

        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.TOP, 0, 0);
            mToast.show();
        }
    }
}
