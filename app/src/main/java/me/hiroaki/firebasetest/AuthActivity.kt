package me.hiroaki.firebasetest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    companion object {
        val TAG = AuthActivity::class.java.simpleName
    }

    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        login.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            signIn(email, password, object : OnResponseListener {
                override fun isSuccessful(flag: Boolean) {
                    Log.d(TAG, "isSuccessfull = $flag")
                    startActivity(MainActivity.startIntent(this@AuthActivity))
                }
            })
        }

        logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
        }

        start.setOnClickListener {
            startActivity(MainActivity.startIntent(this@AuthActivity))
        }

        signUp.setOnClickListener {
            val email = signUpEmail.text.toString()
            val password = signUpPassword.text.toString()
            signUp(email, password)
        }

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser;
            if (user != null) {
                status.text = "ログイン中"
                Log.d(MainActivity.TAG, "onAuthStateChanged:signed_in:" + user.uid);
                start.isEnabled = true

            } else {
                status.text = "未ログイン"
                Log.d(MainActivity.TAG, "onAuthStateChanged:signed_out");
                start.isEnabled = false
            }
        }


    }

    fun signUp(email: String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "emailまたはpasswordを入力してください",
                    Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "登録完了&ログイン！",
                                Toast.LENGTH_SHORT).show()
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext, "emailの形式を確認してください",
                                Toast.LENGTH_SHORT).show()
                    } else if (task.exception is FirebaseAuthWeakPasswordException) {
                        Toast.makeText(applicationContext, "パスワードは6文字以上にしてください",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(MainActivity.TAG, "createUserWithEmailAndPassword", task.exception);
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }

    }

    fun signIn(email: String, password: String, onResponseListener: OnResponseListener) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Log.d(TAG, "emailまたはpasswordを入力してください")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "ログイン！",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(MainActivity.TAG, "signInWithEmail", task.exception);
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                    onResponseListener.isSuccessful(task.isSuccessful)
                })
    }

    interface OnResponseListener {
        fun isSuccessful(flag: Boolean)
    }
}
