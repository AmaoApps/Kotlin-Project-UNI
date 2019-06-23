package pe.applying.kotlinuni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import pe.applying.kotlinuni.ui.LoginActivity
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import pe.applying.kotlinuni.ui.LoginActivity.Companion.mGoogleSignInClient
import com.google.firebase.auth.UserInfo
import pe.applying.kotlinuni.viewmodels.LoginViewModel


class MainActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        showDataUser()


        btn_logout.setOnClickListener(View.OnClickListener {
            signOut()
        })
    }

    private fun showDataUser() {
        val photoProfile = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
        val name = FirebaseAuth.getInstance().currentUser!!.displayName
        val email = FirebaseAuth.getInstance().currentUser!!.email
        txtNombre.text = name
        txtEmail.text = email
        Glide.with(this)
            .load(getProvidePhoto())
            .into(pictureProfile)

    }

    private fun getProvidePhoto() : String {
        var photoProfile = ""
        for (profile in FirebaseAuth.getInstance().currentUser!!.getProviderData()) {
            println(profile.getProviderId())
            // check if the provider id matches "facebook.com"
            if (profile.getProviderId() == "facebook.com") {

                val facebookUserId = profile.getUid()

                val providerAUthFaceBook = profile.getProviderId()
                // construct the URL to the profile picture, with a custom height
                // alternatively, use '?type=small|medium|large' instead of ?height=

                photoProfile = "https://graph.facebook.com/$facebookUserId/picture?height=500"

            } else if (profile.getProviderId() == "google.com") {
                photoProfile = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
                photoProfile = photoProfile!!.replace("/s96-c/", "/s300-c/")
            }
        }

        return photoProfile
    }

    private fun signOut() {

        loginViewModel.deleteUsers()
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            OnCompleteListener<Void> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            })
    }
}
