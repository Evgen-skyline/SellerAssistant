package evgenskyline.sellerassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSeller extends AppCompatActivity {
    private Button mSaveBut;
    private EditText mEditText;
    public static final String KEY_ANSWER = "evgenskyline.sellerassistant.KEY_ANSWER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seller);
        mSaveBut = (Button)findViewById(R.id.buttonSaveName);
        mEditText = (EditText)findViewById(R.id.editTextInAddSeller);
    }

    public void clickForSaveSallerName(View view) {
        Intent answerIntent = new Intent();
        if(!(mEditText.getText().toString().equals("")) ) {
            answerIntent.putExtra(KEY_ANSWER, mEditText.getText().toString());
            setResult(RESULT_OK, answerIntent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(), "Введите имя или"
                    + " нажмите кнопку возрата", Toast.LENGTH_LONG).show();
        }
    }
}
