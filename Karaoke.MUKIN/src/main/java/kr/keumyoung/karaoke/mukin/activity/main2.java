package kr.keumyoung.karaoke.mukin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import kr.keumyoung.karaoke.mukin.R;
import kr.kymedia.karaoke.api.KPItem;

public class main2 extends main {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                play();
            }
        });

        ((EditText) findViewById(R.id.number)).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean handled = false;
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                        case KeyEvent.KEYCODE_NUMPAD_ENTER:
                            play();
                            handled = true;
                            break;
                    }
                }
                return handled;
            }
        });

        ((EditText) findViewById(R.id.number)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    play();
                    handled = true;
                }
                return handled;
            }
        });

        findViewById(R.id.number).requestFocus();
    }

    private void play() {
        String number = ((EditText) findViewById(R.id.number)).getText().toString();

        KPItem item = new KPItem();
        item.putValue("number", number);
        item.putValue("title", "음높이테스트(?)");
        item.putValue("artist", "남자(?)");
        item.putValue("composer", "(없어)(?)");
        item.putValue("lyricist", "(없나)(?)");

        final Intent intent = new Intent(main2.this, _play.class);
        intent.putExtra("item", item);

        startActivity(intent);
    }
}
