package com.princedragons.geoexpert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
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
	TextView titleView, questionText, scoreText;
	int questionNumber = 1, score = 0, newQuestionNumber;
	Random gen;
	boolean questionKind;
	boolean questionIsBeingAsked = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
		
		updateQuestion();
	}
	
	private void addAnswerKeyListener()
	{
		answerField.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if(keyCode == event.KEYCODE_ENTER)
				{
		            correctAnswer();
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
		titleView.setText("Question " + questionNumber);
		titleView.setBackgroundColor(Color.WHITE);
		newQuestionNumber = gen.nextInt(states.size());
		questionKind = gen.nextBoolean();
		if(questionKind)
		{
			questionText.setText(states.get(newQuestionNumber).name);
		}
		else
		{
			questionText.setText(states.get(newQuestionNumber).capital);
		}
	}
	
	private void correctAnswer()
	{
		String answer = answerField.getText().toString();
		if(questionKind)
		{
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
			if(Math.abs(answer.compareToIgnoreCase(states.get(newQuestionNumber).name)) == 0)
			{
				answerCorrect();
			}
			else
			{
				answerWrong();
			}
		}
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
		Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
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
