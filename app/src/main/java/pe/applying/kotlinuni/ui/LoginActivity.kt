package pe.applying.kotlinuni.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import pe.applying.kotlinuni.MainActivity
import pe.applying.kotlinuni.R
import pe.applying.kotlinuni.data.model.Usuario
import pe.applying.kotlinuni.viewmodels.LoginViewModel
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class LoginActivity : AppCompatActivity() {


    lateinit var loginViewModel: LoginViewModel
    private var permissionNeeds = Arrays.asList(
        "email",
        "public_profile"
    )
    lateinit var callbackManager : CallbackManager

    companion object{
        private val RC_SIGN_IN = 777
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
        lateinit var mGoogleSignInClient: GoogleSignInClient
        lateinit var mGoogleSignInOptions: GoogleSignInOptions

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        //Instanciando Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        //Configurando Remote Config
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .setMinimumFetchIntervalInSeconds(10)
            .build()

        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.firebase_defaults)
        consutingRemoteConfig()

        //Configurando el Metodo de Google SignIN
        configureGoogleSignIn()

        //Configuracion de Facebook LoginBUtton
        callbackManager = CallbackManager.Factory.create()
        login_button.setReadPermissions(permissionNeeds)
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("FBFirebase", "facebook:onSuccess:$loginResult")
                var stringacesstoken = loginResult!!.accessToken.token

                var request = GraphRequest.newMeRequest(loginResult.accessToken, GraphRequest.GraphJSONObjectCallback{
                        jsonObject: JSONObject, graphResponse: GraphResponse ->
                    try {
                        val id = jsonObject.getString("id");
                        val profile_pic = URL(
                            "http://graph.facebook.com/" + id + "/picture?type=large");
                        Log.i("profile_pic",
                            profile_pic.toString() );

                    } catch ( e : MalformedURLException) {
                        e.printStackTrace();

                    }
                })

                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "Login Facebook Cancel", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@LoginActivity, "Login Facebook Error", Toast.LENGTH_LONG).show()
            }
        })

        btn_google.setOnClickListener(View.OnClickListener {
            signIn()
            /*
            loginViewModel.insert(Usuario(
                nombres = "Ulises",
                perfil = "Invitado",
                imagen = "Logo Default",
                calificacion = 5.0,
                token = "asdasda"))
            Toast.makeText(applicationContext, "Registrado", Toast.LENGTH_SHORT).show()*/
        })

        btn_facebook.setOnClickListener(View.OnClickListener {
            login_button.performClick()
            /*
            loginViewModel.deleteUsers()
            Toast.makeText(applicationContext, "Eliminados", Toast.LENGTH_SHORT).show()
            */
        })

        txtapp.setOnClickListener(View.OnClickListener {
            consutingRemoteConfig()
        })
    }

    private fun consutingRemoteConfig() {
        firebaseRemoteConfig.fetch(0).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Fetch Succeeded", Toast.LENGTH_SHORT).show()
                firebaseRemoteConfig!!.fetchAndActivate()
            } else {
                Toast.makeText(applicationContext, "Fetch Failed", Toast.LENGTH_SHORT).show()
            }
            updateValuesFromRemote()
        }
    }

    private fun updateValuesFromRemote() {
        var welcomeText = firebaseRemoteConfig.getString("WELCOME_TEXT")
        var backgroundLogin = firebaseRemoteConfig.getString("LOGIN_BACKFROUND")
        //Valida Mensaje Remote
        if (welcomeText.isNullOrEmpty()) {
            // No cambia nada
        } else {
            txtWelcome.text = welcomeText
        }
        //Valida PhotoBackground
        if (backgroundLogin.isNullOrEmpty() || backgroundLogin.equals("")) {
            //Valida la salida de la imagen
        } else {
            Glide.with(this)
                .load(backgroundLogin)
                .into(imgLogin)
        }

    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        // email -> acct.account.name -> uamao@applying.pe
        // nombre -> acct.displayName -> Ulises Amao
        // nombres -> acct.givenName -> Ulises
        // familyName -> acct.familyName -> Amao
        // foto URL -> acct.photoUrl -> Ruta de Foto
        acct.account
        val nombres = acct.displayName.toString()
        val foto = acct.photoUrl.toString()
        acct.email
        acct.idToken
        loginViewModel.insert(Usuario(
            nombres = nombres,
            perfil = "LoginGoogle",
            imagen = foto,
            calificacion = 5.0,
            token = "TokenExpected"))
        Toast.makeText(applicationContext, "Registrado", Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Google Susscefull!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                //startActivity(LoginActivity.getLaunchIntent(this))
            } else {
                Toast.makeText(this@LoginActivity, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FBFirebase", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FBFirebase", "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    loginViewModel.insert(Usuario(
                        nombres = user!!.displayName.toString(),
                        perfil = "LoginFacebook",
                        imagen = user!!.photoUrl.toString(),
                        calificacion = 5.0,
                        token = "TokenExpected"))
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    // FOTO FACEBOOK -> firebaseAuth.currentUser.photoUrl.toString()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FBFirebase", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }

                // ...
            }
    }

}
