/*

package pt.ipca.projetopdm



import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth




class RegisterActivity: ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        auth = Firebase.auth

    }



    private fun signInWithEmailAndPassword(email:String, password:String) {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->

            if(task.isSuccessful) {

                Log.d(TAG, "signInWithEmailAndPassword:Success")
                val user = auth.currentUser
            } else {
                Log.d(TAG, "signInWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Authentication Failure", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {

        private var TAG = "EmailAndPassword"
    }


}

 */