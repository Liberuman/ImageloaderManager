package com.sxu.imageloadermanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

	private int imageLoaderType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View imageLoaderButton = findViewById(R.id.image_loader_button);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

		imageLoaderButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ImageLoaderActivity.class);
				intent.putExtra("type", imageLoaderType);
				startActivity(intent);
			}
		});

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (group.getCheckedRadioButtonId()) {
					case R.id.fresco_radio:
						imageLoaderType = 0;
						break;
					case R.id.url_radio:
						imageLoaderType = 1;
						break;
					case R.id.glide_radio:
						imageLoaderType = 2;
						break;
				}
			}
		});
	}
}
