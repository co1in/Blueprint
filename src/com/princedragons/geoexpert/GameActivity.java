package com.princedragons.geoexpert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity
{
	ArrayList<State> states;
	EditText answerField;
	int numberWrong = 0;
	ImageView[] wrongViews;
	Button button;
	TextView titleView, questionText, scoreText, answerText;
	int questionNumber = 1, score = 0, newQuestionNumber;
	Random gen;
	boolean questionKind;
	boolean questionIsBeingAsked = true;
	ScoreDatabase db;
	ImageView mainImage;
	TextView capitalText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		db = new ScoreDatabase(this);
		setContentView(R.layout.activity_game);
		states = new ArrayList<State>();
		processInfoFile();
		answerField = (EditText)this.findViewById(R.id.answerField);
		addAnswerKeyListener();
		
		//Get the reference to all of the indicators for wrong views
		wrongViews = new ImageView[5];
		wrongViews[0] = (ImageView)findViewById(R.id.ind1);
		wrongViews[1] = (ImageView)findViewById(R.id.ind2);
		wrongViews[2] = (ImageView)findViewById(R.id.ind3);
		wrongViews[3] = (ImageView)findViewById(R.id.ind4);
		wrongViews[4] = (ImageView)findViewById(R.id.ind5);
		
		button = (Button)this.findViewById(R.id.doneButton);
		setupButtonListener();
		
		titleView = (TextView)this.findViewById(R.id.game_title_text);
		questionText = (TextView)this.findViewById(R.id.questionText);
		scoreText = (TextView)this.findViewById(R.id.scoreText);
		
		gen = new Random();
		
		
		mainImage = (ImageView)this.findViewById(R.id.main_question_image);
		capitalText = (TextView)this.findViewById(R.id.capital_question_text);
		answerText = (TextView)this.findViewById(R.id.answerText);
		
		updateQuestion();
	}
	
	private void addAnswerKeyListener()
	{
		answerField.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if(keyCode == event.KEYCODE_ENTER)
				{
		            correctAnswer();
		            answerField.setEnabled(false);
					button.setText("Next Question");
					questionIsBeingAsked = false;
					return true;
		        }
				return false;
			}
		});
	}
	
	public void setupButtonListener()
	{
		button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(questionIsBeingAsked)
				{
					correctAnswer();
					answerField.setEnabled(false);
					button.setText("Next Question");
				}
				else
				{
					updateQuestion();
					answerField.setEnabled(true);
					answerField.setText("");
					button.setText("Enter");
				}
				questionIsBeingAsked = !questionIsBeingAsked;
			}
		});
	}
	
	public void updateQuestion()
	{
		answerText.setVisibility(View.GONE);
		titleView.setText("Question " + questionNumber);
		titleView.setBackgroundColor(Color.TRANSPARENT);
		newQuestionNumber = gen.nextInt(states.size());
		questionKind = gen.nextBoolean();
		if(questionKind)
		{
			questionText.setText(states.get(newQuestionNumber).name);
			String imageLoc = states.get(newQuestionNumber).name.replace(" ", "_").toLowerCase() + "_state.png";
			Drawable mainImageD = null;
			try {
				mainImageD = Drawable.createFromStream(getAssets().open(imageLoc), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mainImage.setBackground(mainImageD);
			mainImage.setVisibility(View.VISIBLE);
			capitalText.setVisibility(View.GONE);
		}
		else
		{
			questionText.setText(states.get(newQuestionNumber).capital);
			capitalText.setVisibility(View.VISIBLE);
			mainImage.setVisibility(View.GONE);
		}
	}
	
	private void correctAnswer()
	{
		String answer = answerField.getText().toString();
		if(questionKind)
		{
			answerText.setText("Answer: " + states.get(newQuestionNumber).capital);
			if(Math.abs(answer.compareToIgnoreCase(states.get(newQuestionNumber).capital)) == 0)
			{
				answerCorrect();
			}
			else
			{
				answerWrong();
			}
		}
		else
		{
			answerText.setText("Answer: " + states.get(newQuestionNumber).name);
			if(Math.abs(answer.compareToIgnoreCase(states.get(newQuestionNumber).name)) == 0)
			{
				answerCorrect();
			}
			else
			{
				answerWrong();
			}
		}
		answerText.setVisibility(View.VISIBLE);
	}
	
	public void answerCorrect()
	{
		titleView.setText("Correct!");
		titleView.setBackgroundColor(Color.GREEN);
		score += 5;
		updateScoreView();
	}
	
	public void answerWrong()
	{
		titleView.setText("Wrong");
		titleView.setBackgroundColor(Color.RED);
		numberWrong++;
		if(numberWrong == 5)
		{
			gameOver();
		}
		updateWrongIcons();
	}
	
	public void answerClose()
	{
		titleView.setText("Answer is close");
		titleView.setBackgroundColor(Color.YELLOW);
	}
	
	private void updateScoreView()
	{
		scoreText.setText("Score: " + score);
	}
	
	private void updateWrongIcons()
	{
		for(int i = 0; i < numberWrong; i++)
		{
			wrongViews[i].setBackgroundResource(R.drawable.indicator_on);
		}
		for(int i = numberWrong; i < 5; i++)
		{
			wrongViews[i].setBackgroundResource(R.drawable.indicator_off);
		}
	}
	
	private void gameOver()
	{
		saveScore();
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Game Over");
		dialog.setMessage("Your Score: " + score);
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent i = new Intent(GameActivity.this, GameActivity.class);
				startActivity(i);
				finish();
			}
		});
		dialog.setNeutralButton("Score List", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent i = new Intent(GameActivity.this, ScoreActivity.class);
				startActivity(i);
				finish();
			}
		});
		
		dialog.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent i = new Intent(GameActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});
		dialog.show();
	}
	
	public void saveScore()
	{
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		String date = month + "/" + day + "/" + year;
		
		Score newScore = new Score(date, score);
		db.addScore(newScore);
	}
	
	private void processInfoFile()
	{
		AssetManager assets = this.getAssets();
		BufferedReader fileReader = null;
		try 
		{
			fileReader = new BufferedReader(new InputStreamReader(assets.open("StatesAndCapitols.txt")));
			String line;
			while((line = fileReader.readLine()) != null)
			{
				String[] split = line.split(":");
				states.add(new State(split[0], split[1], split[2]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
