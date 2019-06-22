package pe.applying.kotlinuni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import pe.applying.kotlinuni.ui.LoginActivity
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import pe.applying.kotlinuni.ui.LoginActivity.Companion.mGoogleSignInClient


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_logout.setOnClickListener(View.OnClickListener {
            signOut()
        })
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            OnCompleteListener<Void> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            })
    }
}
