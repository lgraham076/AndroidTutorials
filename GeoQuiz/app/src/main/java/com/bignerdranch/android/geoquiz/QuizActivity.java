package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX= "index";
    private static final int REQUEST_CODE_CHEAT=0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mTextView;

    private final Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_america, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex=0;
    private boolean mIsACheater;

    /**
     *  Sets the text view to the current question
     */
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mTextView.setText(question);
    }

    /**
     * Checks if the user answer was correct and outputs the result as a toast
     *
     * @param userPressedTrue The true or false response to the question
     */
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;

        if(mIsACheater) { //Notify user if they cheated
            messageResId = R.string.judgement_toast;
        } else { //Check if user chose the correct answer
            if(userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        //Create response for user
        Toast.makeText(QuizActivity.this,messageResId,Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets result from child process
     *
     * @param requestCode Unique identifier for child process
     * @param resultCode Returns whether user submitted or cancelled
     * @param data The result from the child process
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data==null) {
                return;
            }
            //Determine if user is cheater
            mIsACheater = CheatActivity.wasAnswerShown(data);
        }
    }

    /**
     * Used to initialize QuizActivity
     * @param savedInstanceState Saved state from previous application run
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //Check for saved instance from previous run holding the question number
        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
        }

        //Text view for question
        mTextView = (TextView) findViewById(R.id.question_text_view);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=(mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        updateQuestion();

        //True and false buttons
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        //Previous Button
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsACheater=false;
                mCurrentIndex=(mCurrentIndex - 1) % mQuestionBank.length;
                if(mCurrentIndex < 0) {
                    mCurrentIndex=mQuestionBank.length-1;
                }
                updateQuestion();
            }
        });

        //Next Button
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsACheater=false;
                mCurrentIndex=(mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        //Cheat Button
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerISTrue=mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(QuizActivity.this, answerISTrue);
                startActivityForResult(i,REQUEST_CODE_CHEAT);
            }
        });
    }

    /**
     * Used to preserve state upon configuration changes
     * @param savedInstanceState The current state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState() Called");
        //Preserve index for current question
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
    }
}
