import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val btnScience = findViewById<Button>(R.id.btnScience)
        val btnMath = findViewById<Button>(R.id.btnMath)

        btnEnglish.setOnClickListener {
            showToast("English Module Selected")
        }

        btnScience.setOnClickListener {
            showToast("Science Module Selected")
        }

        btnMath.setOnClickListener {
            showToast("Mathematics Module Selected")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}