package evgenskyline.sellerassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SellerMenu extends AppCompatActivity {

    private TextView mTextViewSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);
        mTextViewSeller = (TextView)findViewById(R.id.textViewOnSellerMenu);
        mTextViewSeller.setText(getIntent().getStringExtra(MainActivity.KEY_INTENT_EXTRA_USER));
    }
}
