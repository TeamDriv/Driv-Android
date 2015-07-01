package accenture.driv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import com.melnykov.fab.FloatingActionButton;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.view_driv) View viewDriv;
    @InjectView(R.id.btnDriv) FloatingActionButton btnDriv;
    @InjectView(R.id.btnOne) ImageView btnOne;
    @InjectView(R.id.btnTwo) ImageView btnTwo;
    @InjectView(R.id.btnThree) ImageView btnThree;
    @InjectView(R.id.btnClose) ImageView btnClose;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        getSupportActionBar().hide();
        btnDriv.show();

        btnDriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDriv.hide(true);
                revealDown(viewDriv);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDriv.show(true);
                unvealDown(viewDriv);
            }
        });

        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PhonePrefActivity.class));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void unvealDown(final View myView){

        int cx = myView.getRight();
        int cy = myView.getBottom();

        int initialRadius = myView.getWidth();

        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void revealDown(final View myView){
        int cx = myView.getRight();
        int cy = myView.getBottom();

        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        myView.setVisibility(View.VISIBLE);
        anim.start();
    }
}
