package pe.applying.kotlinuni.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.runBlocking
import pe.applying.kotlinuni.viewmodels.LoginViewModel


class SplashScreenActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        var flagLogin = loginViewModel.isLogged()
        var numusers : Int = 0
        //var numusers = synchronized(loginViewModel.getCountUsuarios())

        runBlocking {
            numusers = loginViewModel.getCountUsuarios()
        }

        Handler().postDelayed({
            if(numusers>0){
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            }else{
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
